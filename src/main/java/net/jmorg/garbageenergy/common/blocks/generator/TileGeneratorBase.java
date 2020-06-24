package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.*;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.TileAugmentable;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator.Types;
import net.jmorg.garbageenergy.utils.AugmentManager;
import net.jmorg.garbageenergy.utils.AugmentManager.Augments;
import net.jmorg.garbageenergy.utils.EnergyConfig;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileGeneratorBase extends TileAugmentable implements IEnergyInfo, IEnergyProvider, ISidedInventory
{
    protected final byte type;

    public static boolean enableSecurity = true;

    protected TimeTracker tracker = new TimeTracker();
    protected EnergyStorage energyStorage;
    protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockGenerator.Types.values().length];
    protected EnergyConfig config;
    IEnergyReceiver energyReceiver = null;
    static float[] attenuateModifier = new float[BlockGenerator.Types.values().length];
    static int[] energyAmplifier = new int[BlockGenerator.Types.values().length];
    float progress = 0F;
    float fuelValue;

    boolean cached = false;
    boolean wasActive = false;

    // Augments
    public boolean augmentAttenuateModifier = false;
    public float augmentAttenuateModifierValue = 0F;
    public boolean augmentEnergyAmplifier = false;
    public int augmentEnergyAmplifierValue = 1;

    public TileGeneratorBase(Types type)
    {
        this.type = ((byte) type.ordinal());

        config = defaultEnergyConfig[getType()];
        energyStorage = new EnergyStorage(config.maxEnergy, config.maxPower * 2);
        facing = (byte) ForgeDirection.NORTH.ordinal();
        augmentManager = new AugmentManager(this, 4);
    }

    public static void configure()
    {
        // Configure generator security
        String comment = "Enable this to allow for Garbage Generators to be securable.";
        enableSecurity = GarbageEnergy.config.get("Security", "Generator.All.Securable", enableSecurity, comment);

        // Configure fuels map.
        comment = "Indicate the objects that can be placed in the generator for their conversion into RF energy as\n" +
                "the parameter name. You should provide this name. Its value indicate the value of the coefficient of energy generation when burning\n" +
                "this item. At the same time, this parameter is equal to the length of the burning process,\n" +
                "which will decrease by the value of AttenuateModifier.\n" +
                "\n" +
                "Name example: I:<ItemUnlocalizedName>.<metadata>=<energyModifier>\n" +
                "    I:ItemWheat.0=0.15";
        ConfigCategory configs = GarbageEnergy.config.getCategory("ItemRfFuels");
        ItemFuelManager.registerFuels(GarbageEnergy.config.getCategoryKeys("ItemRfFuels"), configs);
        configs.setComment(comment);

        // Configure generator energy.
        for (int i = 0; i < BlockGenerator.Types.values().length; i++) {
            String generatorName = "Generator." + StringHelper.titleCase(BlockGenerator.NAMES[i]);
            comment = "Max power output.";
            int maxPower = GarbageEnergy.config.get(generatorName, "BasePower", 8, comment);
            defaultEnergyConfig[i] = new EnergyConfig();
            defaultEnergyConfig[i].setParams(maxPower);

            comment = "Reduces process time.";
            attenuateModifier[i] = (float) GarbageEnergy.config.get(generatorName, "AttenuateModifier", 0.01F, comment);

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

    protected int calcEnergy()
    {
        return MathHelper.round((energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) * fuelValue / config.energyRamp);
    }

    protected int getEnergyModifier()
    {
        return energyAmplifier[getType()] * augmentEnergyAmplifierValue;
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
        float modifier = attenuateModifier[getType()];
        if (augmentAttenuateModifier) {
            modifier = modifier + augmentAttenuateModifierValue;
        }

        if (timeCheck() && progress > 0) {
            progress -= modifier;
        }

        if (progress == 0) {
            progress = -1e10F;
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
                fuelValue = 0F;
                progress = 0F;
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
    // TileAugmentable
    @Override
    public boolean installAugment(IAugmentItem augment, ItemStack itemStack, int slot)
    {
        boolean installed = super.installAugment(augment, itemStack, slot);
        int energyAmplifierAugmentLevel = augment.getAugmentLevel(itemStack, Augments.ENERGY_AMPLIFIER_NAME);
        int attenuateAmplifierAugmentLevel = augment.getAugmentLevel(itemStack, Augments.ATTENUATE_MODIFIER_NAME);

        if (energyAmplifierAugmentLevel > 0) {
            if (augmentManager.hasDuplicateAugment(Augments.ENERGY_AMPLIFIER_NAME, energyAmplifierAugmentLevel, slot)) {
                return false;
            }
            if (augmentManager.hasAugmentChain(Augments.ENERGY_AMPLIFIER_NAME, energyAmplifierAugmentLevel)) {
                augmentEnergyAmplifierValue = Augments.ENERGY_AMPLIFIER[energyAmplifierAugmentLevel];
                installed = true;
            } else {
                return false;
            }
            augmentEnergyAmplifier = installed;
        }

        if (attenuateAmplifierAugmentLevel > 0) {
            if (augmentManager.hasDuplicateAugment(Augments.ATTENUATE_MODIFIER_NAME, attenuateAmplifierAugmentLevel, slot)) {
                return false;
            }
            if (augmentManager.hasAugmentChain(Augments.ATTENUATE_MODIFIER_NAME, attenuateAmplifierAugmentLevel)) {
                int percent = Augments.ATTENUATE_MODIFIER[attenuateAmplifierAugmentLevel];
                augmentAttenuateModifierValue = (attenuateModifier[getType()] * percent / 100);
                installed = true;
            } else {
                return false;
            }
            augmentAttenuateModifier = installed;
        }

        return installed;
    }

    @Override
    public void resetAugments()
    {
        super.resetAugments();

        augmentAttenuateModifierValue = 0F;
        augmentAttenuateModifier = false;
        augmentEnergyAmplifierValue = 1;
        augmentEnergyAmplifier = false;
    }

    //
    // IEnergyProvider
    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        if (canConnectEnergy(from)) {
            return energyStorage.extractEnergy(Math.min(config.maxPower * getEnergyModifier(), maxExtract), simulate);
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
        return fuelValue > 0 ? MathHelper.clamp(calcEnergy() * getEnergyModifier(), config.minPower, config.maxPower) : 0;
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
        progress = nbt.getFloat("Progress");
        fuelValue = nbt.getFloat("FuelValue");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        energyStorage.writeToNBT(nbt);
        nbt.setFloat("Progress", progress);
        nbt.setFloat("FuelValue", fuelValue);
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
        payload.addFloat(fuelValue);

        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase payload)
    {
        super.handleGuiPacket(payload);

        energyStorage.setCapacity(payload.getInt());
        energyStorage.setEnergyStored(payload.getInt());
        progress = payload.getFloat();
        fuelValue = payload.getFloat();
    }
}
