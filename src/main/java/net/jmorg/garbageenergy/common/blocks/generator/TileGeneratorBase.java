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
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.TileRSControl;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator.Types;
import net.jmorg.garbageenergy.utils.EnergyConfig;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileGeneratorBase extends TileRSControl implements IEnergyInfo, IEnergyProvider, ISidedInventory
{
    protected final byte type;

    public static boolean enableSecurity = true;

    protected TimeTracker tracker = new TimeTracker();
    protected EnergyStorage energyStorage;
    protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockGenerator.Types.values().length];
    protected EnergyConfig config;
    IEnergyReceiver energyReceiver = null;
    float attenuateModifier = 0.005F;
    int energyModifier = (int) (attenuateModifier * 200);
    float progress = 0F;
    float fuelValue;

    boolean cached = false;
    boolean wasActive = false;
    byte facing = (byte) ForgeDirection.NORTH.ordinal();

    public TileGeneratorBase()
    {
        this(Types.ITEM_RF);
    }

    public TileGeneratorBase(Types type)
    {
        this.type = ((byte) type.ordinal());

        config = defaultEnergyConfig[getType()];
        energyStorage = new EnergyStorage(config.maxEnergy, config.maxPower * 2);
    }

    public static void configure()
    {
        // Configure generator security
        String comment = "Enable this to allow for Garbage Generators to be securable.";
        enableSecurity = GarbageEnergy.config.get("Security", "Generator.All.Securable", enableSecurity, comment);

        // Configure fuels map.
        comment = "You can specify fuels for the Item Generators in this section. Instead of common divider\nbetween modname and item use dot.";
        ConfigCategory configs = GarbageEnergy.config.getCategory("ItemRfFuels");
        ItemFuelManager.registerFuels(GarbageEnergy.config.getCategoryKeys("ItemRfFuels"), configs);
        configs.setComment(comment);

        // Configure generator energy.
        for (int i = 0; i < BlockGenerator.Types.values().length; i++) {
            String name = StringHelper.titleCase(BlockGenerator.NAMES[i]);
            int maxPower = MathHelper.clamp(GarbageEnergy.config.get("Generator." + name, "BasePower", 8), 1, 16);
            GarbageEnergy.config.set("Generator." + name, "BasePower", maxPower);
            defaultEnergyConfig[i] = new EnergyConfig();
            defaultEnergyConfig[i].setParamsDefault(maxPower);
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

    public static float getEnergyValue(ItemStack stack)
    {
        if (stack == null) {
            return 0F;
        }

        String item = GameRegistry.findUniqueIdentifierFor(stack.getItem()).toString();
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

    protected boolean hasStoredEnergy()
    {
        return energyStorage.getEnergyStored() > 0;
    }

    public int getScaledEnergyStored(int scale)
    {
        return energyStorage.getEnergyStored() * scale / energyStorage.getMaxEnergyStored();
    }

    public final void setEnergyStored(int quantity)
    {
        energyStorage.setEnergyStored(quantity);
    }

    public abstract int getScaledDuration(int scale);

    protected abstract boolean canGenerate();

    protected abstract void generate();

    protected void transferEnergy(int from)
    {
        if (energyReceiver == null) return;

        ForgeDirection direction = ForgeDirection.VALID_DIRECTIONS[from ^ 1];
        int energy = energyReceiver.receiveEnergy(direction, Math.min(energyStorage.getMaxExtract(), getEnergyStored(direction)), false);

        energyStorage.modifyEnergyStored(-energy);
    }

    protected void attenuate()
    {
        if (timeCheck() && progress > 0) {
            progress -= attenuateModifier;

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
    public boolean onWrench(EntityPlayer player, int hitSide)
    {
        rotateBlock();
        return true;
    }

    public void rotateBlock()
    {
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
                transferEnergy(facing);
            } else {
                isActive = false;
                wasActive = true;
                tracker.markTime(worldObj);
            }
        } else if (redstoneControlOrDisable() && canGenerate()) {
            // If it redstone mode disabled and can generate energy we activate it and generate energy.
            isActive = true;
            generate();
            transferEnergy(facing);
        }

        // Send update packets if it is enabled.
        if (curActive != isActive && !wasActive) {
            updateLighting();
            sendUpdatePacket(Side.CLIENT);
        } else if (wasActive && tracker.hasDelayPassed(worldObj, 100)) {
            wasActive = false;
            updateLighting();
            sendUpdatePacket(Side.CLIENT);
        }
    }

    protected void updateEnergyReceiver()
    {
        if (ServerHelper.isClientWorld(worldObj)) {
            return;
        }
        TileEntity tile = BlockHelper.getAdjacentTileEntity(this, facing);

        if (EnergyHelper.isEnergyReceiverFromSide(tile, ForgeDirection.VALID_DIRECTIONS[facing ^ 1])) {
            energyReceiver = (IEnergyReceiver) tile;
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
        if (from.ordinal() == facing) {
            return energyStorage.extractEnergy(Math.min(config.maxPower * 2, maxExtract), simulate);
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
        return from.ordinal() == facing;
    }

    //
    // IEnergyInfo
    @Override
    public int getInfoEnergyPerTick()
    {
        return calcEnergy() * energyModifier;
    }

    @Override
    public int getInfoMaxEnergyPerTick()
    {
        return config.maxPower * energyModifier;
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
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return side != facing && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return false;
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
    // NBT methods.
    public void updateFromNBT(NBTTagCompound nbt)
    {
        super.updateFromNBT(nbt);
        setEnergyStored(nbt.getInteger("Energy"));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        energyStorage.readFromNBT(nbt);

        facing = (byte) (nbt.getByte("Facing") % 6);
        isActive = nbt.getBoolean("Active");
        progress = nbt.getInteger("Fuel");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        energyStorage.writeToNBT(nbt);

        nbt.setByte("Facing", facing);
        nbt.setBoolean("Active", isActive);
        nbt.setFloat("Fuel", progress);
    }

    //
    // Network
    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase payload = super.getPacket();

        payload.addByte(facing);

        return payload;
    }

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

    @Override
    public void handleTilePacket(PacketCoFHBase payload, boolean isServer)
    {
        super.handleTilePacket(payload, isServer);

        if (!isServer) {
            facing = payload.getByte();
        } else {
            payload.getByte();
        }
    }
}
