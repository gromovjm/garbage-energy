package net.jmorg.garbageenergy.common.bloks.generator;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyStorage;
import cofh.api.tileentity.IEnergyInfo;
import cofh.core.CoFHProps;
import cofh.lib.util.helpers.MathHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.StringHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.bloks.TileRSControl;
import net.jmorg.garbageenergy.common.bloks.generator.BlockGenerator.Types;
import net.jmorg.garbageenergy.utils.EnergyConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileGeneratorBase extends TileRSControl implements IEnergyInfo, IEnergyProvider, ISidedInventory
{
    public static final int FUEL_MOD = 100;

    protected final byte type;
    byte facing = 1;

    public static boolean enableSecurity = true;

    protected EnergyConfig config;
    protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockGenerator.Types.values().length];
    protected EnergyStorage energyStorage = new EnergyStorage(0);
    int energyMod = 1;
    int fuelMod = FUEL_MOD;

    public TileGeneratorBase()
    {
        this(Types.ITEM_RF);
    }

    public TileGeneratorBase(Types type)
    {
        this.type = ((byte) type.ordinal());
    }

    public static void configure()
    {
        String comment = "Enable this to allow for Generators to be securable.";
        enableSecurity = GarbageEnergy.config.get("Security", "Generator.All.Securable", enableSecurity, comment);

        for (int i = 0; i < BlockGenerator.Types.values().length; i++) {
            String name = StringHelper.titleCase(BlockGenerator.NAMES[i]);
            int maxPower = MathHelper.clamp(GarbageEnergy.config.get("Generator." + name, "BasePower", 80), 10, 160);
            GarbageEnergy.config.set("Generator." + name, "BasePower", maxPower);
            maxPower /= 10;
            maxPower *= 10;
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

    protected int calcEnergy()
    {
        if (!isActive) {
            return 0;
        }
        if (energyStorage.getEnergyStored() < config.minPowerLevel) {
            return config.maxPower;
        }
        if (energyStorage.getEnergyStored() > config.maxPowerLevel) {
            return config.minPower;
        }
        return (energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored()) / config.energyRamp;
    }

    public IEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
    {
        return from.ordinal() != facing ? 0 : energyStorage.extractEnergy(Math.min(config.maxPower * 2, maxExtract), simulate);
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

    @Override
    public boolean canConnectEnergy(ForgeDirection from)
    {
        return from.ordinal() == facing;
    }

    /* IEnergyInfo */
    @Override
    public int getInfoEnergyPerTick()
    {
        return calcEnergy() * energyMod;
    }

    @Override
    public int getInfoMaxEnergyPerTick()
    {
        return config.maxPower * energyMod;
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
        return side != facing;
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

    public int getScaledDuration(int scale)
    {
        return 0;
    }

    //
    // NBT methods.
    public void updateFromNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Name")) {
            tileName = nbt.getString("Name");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (!tileName.isEmpty()) {
            nbt.setString("Name", tileName);
        }
    }

//    @Override
//    public PacketCoFHBase getPacket()
//    {
//        PacketCoFHBase payload = super.getPacket();
//
//        payload.addByte(facing);
//        payload.addBool(augmentRedstoneControl);
//
//        return payload;
//    }
//
//    @Override
//    public PacketCoFHBase getGuiPacket()
//    {
//
//        PacketCoFHBase payload = super.getGuiPacket();
//
//        payload.addInt(energyStorage.getMaxEnergyStored());
//        payload.addInt(energyStorage.getEnergyStored());
//        payload.addInt(fuelRF);
//
//        payload.addBool(augmentRedstoneControl);
//
//        return payload;
//    }
//
//    @Override
//    protected void handleGuiPacket(PacketCoFHBase payload)
//    {
//
//        super.handleGuiPacket(payload);
//
//        energyStorage.setCapacity(payload.getInt());
//        energyStorage.setEnergyStored(payload.getInt());
//        fuelRF = payload.getInt();
//
//        boolean prevControl = augmentRedstoneControl;
//        augmentRedstoneControl = payload.getBool();
//
//        if (augmentRedstoneControl != prevControl) {
//            onInstalled();
//            sendUpdatePacket(Side.SERVER);
//        }
//    }
//
//    /* ITilePacketHandler */
//    @Override
//    public void handleTilePacket(PacketCoFHBase payload, boolean isServer)
//    {
//
//        super.handleTilePacket(payload, isServer);
//
//        if (!isServer) {
//            facing = payload.getByte();
//            augmentRedstoneControl = payload.getBool();
//        } else {
//            payload.getByte();
//            payload.getBool();
//        }
//    }
}
