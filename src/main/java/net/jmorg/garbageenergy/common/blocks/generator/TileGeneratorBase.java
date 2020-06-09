package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.*;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.TileReconfigurable;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator.Types;
import net.jmorg.garbageenergy.utils.EnergyConfig;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.jmorg.garbageenergy.utils.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileGeneratorBase extends TileReconfigurable implements IEnergyInfo, IEnergyProvider, ISidedInventory
{
    protected final byte type;

    /*protected static final SideConfig[] defaultSideConfig = new SideConfig[BlockGenerator.Types.values().length];
    protected SideConfig sideConfig;*/
    public static boolean enableSecurity = true;

    protected TimeTracker tracker = new TimeTracker();
    protected EnergyStorage energyStorage;
    protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockGenerator.Types.values().length];
    protected EnergyConfig config;
    IEnergyReceiver energyReceiver = null;
    static float[] attenuateModifier = new float[BlockGenerator.Types.values().length];
    static int[] energyAmplifier = new int[BlockGenerator.Types.values().length];
    protected int energyModifier;
    float progress = 0F;
    float fuelValue;

    boolean cached = false;
    boolean wasActive = false;

    public TileGeneratorBase(Types type)
    {
        this.type = ((byte) type.ordinal());

        config = defaultEnergyConfig[getType()];
        energyStorage = new EnergyStorage(config.maxEnergy, config.maxPower * 2);
        energyModifier = (int) (attenuateModifier[getType()] * energyAmplifier[getType()] * 200);
        facing = (byte) ForgeDirection.NORTH.ordinal();
        /*sideConfig = defaultSideConfig[getType()];
        setDefaultSides();*/
    }

    public static void configure()
    {
        // Configure generator security
        String comment = "Enable this to allow for Garbage Generators to be securable.";
        enableSecurity = GarbageEnergy.config.get("Security", "Generator.All.Securable", enableSecurity, comment);

        // Configure fuels map.
        comment = "Indicate the objects that can be placed in the generator for their conversion into RF energy as\n" +
                "the parameter name. Its value indicate the value of the coefficient of energy generation when burning\n" +
                "this item. At the same time, this parameter is equal to the length of the burning process,\n" +
                "which will decrease by the value of AttenuateModifier.";
        ConfigCategory configs = GarbageEnergy.config.getCategory("ItemRfFuels");
        ItemFuelManager.registerFuels(GarbageEnergy.config.getCategoryKeys("ItemRfFuels"), configs);
        configs.setComment(comment);

        // Configure generator energy.
        for (int i = 0; i < BlockGenerator.Types.values().length; i++) {
            String generatorName = "Generator." + StringHelper.titleCase(BlockGenerator.NAMES[i]);
            int maxPower = MathHelper.clamp(GarbageEnergy.config.get(generatorName, "BasePower", 8), 1, 16);
            GarbageEnergy.config.set(generatorName, "BasePower", maxPower);
            defaultEnergyConfig[i] = new EnergyConfig();
            defaultEnergyConfig[i].setParamsPower(maxPower);

            comment = "Reduces process time.";
            attenuateModifier[i] = (float) GarbageEnergy.config.get(generatorName, "AttenuateModifier", 0.005F, comment);
            comment = "Increases output energy.";
            energyAmplifier[i] = GarbageEnergy.config.get(generatorName, "EnergyAmplifier", 1, comment);
        }

    }

    @Override
    public String getName()
    {
        return BlockGenerator.getTileName(BlockGenerator.NAMES[type]);
    }

    @Override
    public int getType()
    {
        return type;
    }

    @Override
    public int getLightValue()
    {
        return isActive ? 5 : 0;
    }

    public static float getEnergyValue(ItemStack stack)
    {
        if (stack == null) {
            return 0F;
        }

        String item = Utils.getItemUniqueId(stack.getItem());
        if (ItemFuelManager.isBurnable(item)) {
            return (float) ItemFuelManager.getBurningTime(item);
        }
        return 0F;
    }

    protected int calcEnergy()
    {
        int energy = 0;

        if (isActive) {
            if (energyStorage.getEnergyStored() < config.minPowerLevel) {
                energy = config.maxPower;
            } else if (energyStorage.getEnergyStored() > config.maxPowerLevel) {
                energy = config.minPower;
            } else {
                energy = (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / config.energyRamp;
            }
        }

        return (int) (energy * fuelValue);
    }

    public IEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    public final void setEnergyStored(int quantity)
    {
        energyStorage.setEnergyStored(quantity);
    }

    public abstract int getScaledDuration(int scale);

    protected abstract boolean canGenerate();

    protected abstract void generate();

    protected void transferEnergy()
    {
        if (energyReceiver == null) return;

        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[facing];
        int energy = energyReceiver.receiveEnergy(direction, Math.min(energyStorage.getMaxExtract(), getEnergyStored(direction)), false);

        energyStorage.modifyEnergyStored(-energy);
    }

    protected void attenuate()
    {
        if (timeCheck() && progress > 0) {
            progress -= attenuateModifier[getType()];

            if (progress < 0) {
                progress = 0;
            }
        }
    }

    @Override
    public void invalidate()
    {
        cached = false;
        super.invalidate();
    }

    @Override
    public void onNeighborBlockChange()
    {
        super.onNeighborBlockChange();
        updateEnergyReceiver();
    }

    @Override
    public void onNeighborTileChange(int tileX, int tileY, int tileZ)
    {
        super.onNeighborTileChange(tileX, tileY, tileZ);
        updateEnergyReceiver();
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    public void updateEntity()
    {
        if (ServerHelper.isClientWorld(worldObj)) {
            return;
        }
        if (!cached) {
            onNeighborBlockChange();
        }
        boolean curActive = isActive;

        if (isActive) {
            // Generate an energy, if redstone mode enabled and can generate,
            // else disable generation and mark that it was active.
            if (redstoneControlOrDisable() && canGenerate()) {
                generate();
                transferEnergy();
            } else {
                isActive = false;
                wasActive = true;
                tracker.markTime(worldObj);
            }
        } else if (redstoneControlOrDisable() && canGenerate()) {
            // If it redstone mode disabled and can generate energy we activate it and generate energy.
            isActive = true;
            generate();
            transferEnergy();
        }

        // Send update packets if it is enabled.
        if (curActive != isActive && !wasActive) {
            updateLighting();
            sendUpdatePacket(Side.CLIENT);
        } else if (wasActive && tracker.hasDelayPassed(worldObj, 10)) {
            wasActive = false;
            updateLighting();
            sendUpdatePacket(Side.CLIENT);
        }
    }

    @Override
    public void blockPlaced()
    {
        for (int i = facing + 1, e = facing + 6; i < e; i++) {
            if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, i % 6)) {
                updateEnergyReceiver();
                markDirty();
                sendUpdatePacket(Side.CLIENT);
            }
        }
    }

    protected void updateEnergyReceiver()
    {
        if (ServerHelper.isClientWorld(worldObj)) {
            return;
        }
        if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, ForgeDirection.OPPOSITES[facing])) {
            energyReceiver = (IEnergyReceiver) BlockHelper.getAdjacentTileEntity(this, ForgeDirection.OPPOSITES[facing]);
        } else {
            energyReceiver = null;
        }
        cached = true;
    }

    //
    // IEnergyProvider
    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        if (canConnectEnergy(from)) {
            return energyStorage.extractEnergy(Math.min(config.maxPower * energyModifier, maxExtract), simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection from)
    {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection from)
    {
        return energyStorage.getMaxEnergyStored();
    }

    //
    // IEnergyConnection
    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from.ordinal() == ForgeDirection.OPPOSITES[facing];
    }

    protected int getEnergyOfItem()
    {
        return isActive ? MathHelper.clamp(calcEnergy() * energyModifier, config.minPower, config.maxPower) : 0;
    }

    //
    // IEnergyInfo
    @Override
    public int getInfoEnergyPerTick()
    {
        return getEnergyOfItem();
    }

    @Override
    public int getInfoMaxEnergyPerTick()
    {
        return config.maxPower;
    }

    @Override
    public int getInfoEnergyStored()
    {
        return energyStorage.getEnergyStored();
    }

    @Override
    public int getInfoMaxEnergyStored()
    {
        return config.maxEnergy;
    }

    //
    // ISidedInventory
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return CoFHProps.EMPTY_INVENTORY;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return side != facing && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (slot > inventory.length) return false;
        return getEnergyValue(stack) > 0;
    }

    @Override
    protected boolean readPortableTagInternal(EntityPlayer player, NBTTagCompound tag)
    {
        rsMode = RedstoneControlHelper.getControlFromNBT(tag);
        return true;
    }

    @Override
    protected boolean writePortableTagInternal(EntityPlayer player, NBTTagCompound tag)
    {
        RedstoneControlHelper.setItemStackTagRS(tag, this);
        return true;
    }

    //
    // ISidedTexture
    @Override
    public IIcon getTexture(int side, int pass)
    {
        if (pass == 0) {
            if (side == 0) {
                return BlockGenerator.bottomSide;
            } else if (side == 1) {
                return BlockGenerator.topSide;
            }
            if (side == ForgeDirection.OPPOSITES[facing]) {
                return isActive ? BlockGenerator.activeOppositeSide : BlockGenerator.oppositeSide;
            }
            if (side == facing) {
                return isActive ? BlockGenerator.faceActive[type] : BlockGenerator.face[type];
            }
            return BlockGenerator.side;
        }
        return BlockGenerator.side;
    }

    //
    // NBT methods.
    public void updateFromNBT(NBTTagCompound nbt)
    {
        super.updateFromNBT(nbt);

        if (nbt != null) {
            setEnergyStored(nbt.getInteger("Energy"));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        energyStorage.readFromNBT(nbt);
        progress = nbt.getInteger("Fuel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        energyStorage.writeToNBT(nbt);
        nbt.setFloat("Fuel", progress);
    }

    //
    // Network
    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase payload = super.getGuiPacket();

        payload.addInt(energyStorage.getMaxEnergyStored());
        payload.addInt(energyStorage.getEnergyStored());
        payload.addFloat(progress);

        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase payload)
    {
        super.handleGuiPacket(payload);

        energyStorage.setCapacity(payload.getInt());
        energyStorage.setEnergyStored(payload.getInt());
        progress = payload.getFloat();
    }
}
