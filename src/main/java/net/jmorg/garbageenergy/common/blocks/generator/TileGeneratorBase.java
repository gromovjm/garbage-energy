package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.api.core.IAccelerable;
import cofh.api.item.IAugmentItem.AugmentType;
import cofh.api.tileentity.IEnergyInfo;
import cofh.api.tileentity.IReconfigurableFacing;
import cofh.core.block.TileInventory;
import cofh.core.init.CoreProps;
import cofh.core.network.PacketBase;
import cofh.core.util.TimeTracker;
import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.*;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import cofh.redstoneflux.impl.EnergyStorage;
import net.jmorg.garbageenergy.GEProperties;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator.Types;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashSet;

public abstract class TileGeneratorBase extends TileInventory implements ITickable, IAccelerable, IEnergyProvider, IReconfigurableFacing, ISidedInventory, IEnergyInfo
{
    protected static int type;
    protected static String name;

    // Genrator augments
    public static final HashSet<String> VALID_AUGMENTS_BASE = new HashSet<>();
    public static final HashSet<String>[] VALID_AUGMENTS = new HashSet[Types.values().length];
    protected static final byte[] AUGMENTS_NUM = {0, 1, 2, 4, 8};

    static {
        VALID_AUGMENTS_BASE.add(GEProperties.ENERGY_AMPLIFIER);
        VALID_AUGMENTS_BASE.add(GEProperties.ATTENUATE_MODIFIER);
    }

    boolean hasAttenuateAugment = false;
    boolean hasEnergyAugment = false;
    boolean hasModeAugment = false;
    public float augmentAttenuateModifierValue = 0F;
    public int augmentEnergyAmplifierValue = 1;

    // Sided inventory
    public byte[] sideCache = { 0, 0, 0, 0, 0, 0 };
    protected byte facing = 3;

    // Generator properties
    protected static boolean enableSecurity = true;
    protected static boolean enableUpgrades = true;
    boolean cached = false;
    protected TimeTracker tracker = new TimeTracker();
    IEnergyReceiver energyReceiver = null;
    static int energyAmplifier;
    static float attenuateModifier;
    float progress = 0F;
    float fuelValue = 0F;

    public static void configure()
    {
        String category = "Generator";
        String comment = "If TRUE, Generators are securable.";
        enableSecurity = GarbageEnergy.config.get(category, "Securable", enableSecurity, comment);

        comment = "If TRUE, Generators are upgradable. If disabled, be sure and change the Augment Progression.";
        enableUpgrades = GarbageEnergy.config.get(category, "Upgradable", enableUpgrades, comment);

        // Configure fuels map.
        category = "CustomItemFuelValues";
        comment = "Indicate the objects that can be placed in the generator for their conversion into RF energy as\n" +
                "the parameter name. You should provide this name. Its value indicate the value of the coefficient of energy generation when burning\n" +
                "this item. At the same time, this parameter is equal to the length of the burning process,\n" +
                "which will decrease by the value of AttenuateModifier.\n" +
                "\n" +
                "Name example: I:<ItemUnlocalizedName>.<metadata>=<energyModifier>\n" +
                "    I:ItemWheat.0=0.15";
        ConfigCategory configs = GarbageEnergy.config.getCategory(category);
        ItemFuelManager.registerFuels(GarbageEnergy.config.getCategoryKeys(category), configs);
        configs.setComment(comment);
    }

    /**
     * Determines that cans generate energy.
     */
    protected abstract boolean canGenerate();

    /**
     * Generate energy. Should calls attenuate merhod.
     */
    protected abstract void generate();

    /**
     * Calcs scaled duration.
     *
     * @param scale Scale of a proccess.
     * @return Duration.
     */
    public abstract int getScaledDuration(int scale);

    protected void attenuate()
    {
        float modifier = attenuateModifier;
        if (hasAttenuateAugment) {
            modifier = modifier + augmentAttenuateModifierValue;
        }

        if (timeCheck() && progress > 0) {
            progress -= modifier;
        }

        if (progress == 0) {
            progress = -1e10F;
        }
    }

    protected void transferEnergy()
    {
        if (energyReceiver == null) return;

        EnumFacing direction = EnumFacing.VALUES[facing];
        int energy = energyReceiver.receiveEnergy(direction, Math.min(getEnergyStorage().getMaxExtract(), getEnergyStored(direction)), false);

        getEnergyStorage().modifyEnergyStored(-energy);
    }

    public abstract EnergyConfig getEnergyConfig();

    public abstract EnergyStorage getEnergyStorage();

    protected int getEnergyModifier()
    {
        return energyAmplifier * augmentEnergyAmplifierValue;
    }

    protected int calcEnergy()
    {
        return MathHelper.round((getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored()) * fuelValue / getEnergyConfig().energyRamp);
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
    public void onNeighborTileChange(BlockPos pos)
    {
        super.onNeighborTileChange(pos);
        updateEnergyReceiver();
    }

    @Override
    public void update()
    {
        boolean flag1 = false;

        if (ServerHelper.isServerWorld(world)) {
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
                    tracker.markTime(world);
                    flag1 = true;
                }
            } else if (redstoneControlOrDisable() && canGenerate()) {
                // If it redstone mode disabled and can generate energy we activate it and generate energy.
                isActive = true;
                generate();
                transferEnergy();
                flag1 = true;
            }

            if (curActive != isActive) {
                flag1 = true;
                BlockGenerator.setState(isActive, world, pos);
            }
        }

        if (flag1)
        {
            this.markDirty();
        }
    }

    @Override
    public void blockPlaced()
    {
        for (int i = facing + 1, e = facing + 6; i < e; i++) {
            if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, EnumFacing.VALUES[i % 6])) {
                updateEnergyReceiver();
                markDirty();
                sendTilePacket(Side.CLIENT);
            }
        }
    }

    protected void updateEnergyReceiver()
    {
        if (ServerHelper.isClientWorld(world)) {
            return;
        }
        if (EnergyHelper.isAdjacentEnergyReceiverFromSide(this, EnumFacing.VALUES[facing].getOpposite())) {
            energyReceiver = (IEnergyReceiver) BlockHelper.getAdjacentTileEntity(this, EnumFacing.VALUES[facing].getOpposite());
        } else {
            energyReceiver = null;
        }
        cached = true;
    }

    //
    // TileAugmentableSecure
    @Override
    protected int getNumAugmentSlots(int level)
    {
        return AUGMENTS_NUM[level];
    }

    @Override
    protected void preAugmentInstall()
    {
        hasModeAugment = false;
    }

    @Override
    protected boolean isValidAugment(AugmentType type, String id)
    {
        if (type == AugmentType.CREATIVE && !isCreative) {
            return false;
        }
        if (type == AugmentType.MODE && hasModeAugment) {
            return false;
        }
        return VALID_AUGMENTS_BASE.contains(id) || VALID_AUGMENTS[getType()].contains(id) || super.isValidAugment(type, id);
    }

    //
    // IEnergyProvider
    @Override
    public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate)
    {
        if (canConnectEnergy(from)) {
            return getEnergyStorage().extractEnergy(Math.min(getEnergyConfig().maxPower * getEnergyModifier(), maxExtract), simulate);
        }
        return 0;
    }

    @Override
    public int getEnergyStored(EnumFacing from)
    {
        if (canConnectEnergy(from)) {
            return getEnergyStorage().getEnergyStored();
        }
        return 0;
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from)
    {
        if (canConnectEnergy(from)) {
            return getEnergyStorage().getMaxEnergyStored();
        }
        return 0;
    }

    //
    // IEnergyConnection
    @Override
    public boolean canConnectEnergy(EnumFacing from)
    {
        return from != null && from.getIndex() == EnumFacing.VALUES[facing].getOpposite().getIndex();
    }

    protected int getEnergyOfItem()
    {
        return fuelValue > 0 ? MathHelper.clamp(calcEnergy() * getEnergyModifier(), getEnergyConfig().minPower, getEnergyConfig().maxPower) : 0;
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
        return getEnergyConfig().maxPower;
    }

    @Override
    public int getInfoEnergyStored()
    {
        return getEnergyStorage().getEnergyStored();
    }

    public void setEnergyStored(int energy)
    {
        getEnergyStorage().setEnergyStored(energy);
    }

    //
    // ISidedInventory
    @Override
    public int[] getSlotsForFace(EnumFacing direction)
    {
        int side = direction.getIndex();

        if (side != EnumFacing.VALUES[facing].getOpposite().getIndex() || side != facing) {
            return new int[inventory.length];
        }

        return new int[0];
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing direction)
    {
        return direction.getIndex() != facing && isItemValidForSlot(slot, stack);
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

    // IReconfigurableFacing
    @Override
    public int getFacing()
    {
        return facing;
    }

    @Override
    public boolean allowYAxisFacing()
    {
        return false;
    }

    @Override
    public boolean rotateBlock()
    {
        return false;
    }

    @Override
    public boolean setFacing(int side, boolean alternate)
    {
        if (side < 0 || side > 5) {
            return false;
        }

        if (!allowYAxisFacing() && side < 2) {
            return false;
        }

        facing = (byte) side;

        markChunkDirty();
        sendTilePacket(Side.CLIENT);

        return true;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    //
    // NBT methods.
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        getEnergyStorage().readFromNBT(nbt);
        facing = (byte) (nbt.getByte(CoreProps.FACING) % 6);
        progress = nbt.getFloat("Progress");
        fuelValue = nbt.getFloat("FuelValue");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        getEnergyStorage().writeToNBT(nbt);
        nbt.setByte(CoreProps.FACING, facing);
        nbt.setFloat("Progress", progress);
        nbt.setFloat("FuelValue", fuelValue);

        return nbt;
    }

    //
    // Network
    @Override
    public PacketBase getGuiPacket()
    {
        PacketBase payload = super.getGuiPacket();

        payload.addInt(getEnergyStorage().getMaxEnergyStored());
        payload.addInt(getEnergyStorage().getEnergyStored());
        payload.addFloat(progress);
        payload.addFloat(fuelValue);

        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketBase payload)
    {
        super.handleGuiPacket(payload);

        getEnergyStorage().setCapacity(payload.getInt());
        getEnergyStorage().setEnergyStored(payload.getInt());
        progress = payload.getFloat();
        fuelValue = payload.getFloat();
    }

    //
    // Common
    @Override
    protected Object getMod()
    {
        return GarbageEnergy.instance;
    }

    @Override
    protected String getModVersion()
    {
        return GarbageEnergy.VERSION;
    }

    @Override
    protected String getTileName()
    {
        return "tile." + name.replace(":", ".generator.") + ".name";
    }

    @Override
    public int getType()
    {
        return type;
    }
}
