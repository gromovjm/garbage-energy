package net.jmorg.garbageenergy.common.blocks.scanner;

import cofh.api.energy.EnergyStorage;
import cofh.core.CoFHProps;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.TimeTracker;
import cofh.lib.util.helpers.ServerHelper;
import cofh.lib.util.helpers.StringHelper;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.TilePowered;
import net.jmorg.garbageenergy.utils.EnergyConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileScanner extends TilePowered implements ISidedInventory
{
    public final byte type;

    protected static final EnergyConfig[] defaultEnergyConfig = new EnergyConfig[BlockScanner.Types.values().length];
    public static final boolean[] enableSecurityConfig = new boolean[BlockScanner.Types.values().length];
    protected TimeTracker tracker = new TimeTracker();
    protected EnergyConfig energyConfig;
    // Energy consume coefficient.
    protected int ecc;

    public boolean finished = false;
    public long progressMax = 0;
    public long progress = 0;

    public TileScanner(BlockScanner.Types type)
    {
        this.type = (byte) type.ordinal();
        energyConfig = defaultEnergyConfig[this.type].copy();
        energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower);
        facing = (byte) ForgeDirection.NORTH.ordinal();
    }

    public static void configure()
    {
        for (int i = 0; i < BlockScanner.Types.values().length; i++) {
            String name = StringHelper.titleCase(BlockScanner.NAMES[i]);
            String comment = "Enable this to allow for " + name + "s to be securable.";
            enableSecurityConfig[i] = GarbageEnergy.config.get("Security", "Scanner." + name + ".Securable", true, comment);
        }
    }

    @Override
    public String getName()
    {
        return BlockScanner.getTileName(BlockScanner.NAMES[type]);
    }

    @Override
    public int getType()
    {
        return type;
    }

    @Override
    public boolean enableSecurity()
    {
        return enableSecurityConfig[type];
    }

    @Override
    public boolean canPlayerDismantle(EntityPlayer entityPlayer)
    {
        return false;
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
        if (hasChargeSlot() && inventory[getChargeSlot()] != null) {
            charge();
        }

        boolean wasActive = isActive;
        isActive = inventory[0] != null && !finished;
        boolean isFinished = finished;

        if (redstoneControlOrDisable() && isActive) {
            if (check()) resetResult(false);
            scan();
            consumeEnergy();
        } else {
            if (check()) resetResult(false);
            isActive = false;
        }

        if ((wasActive != isActive && !isFinished) || (isFinished && tracker.hasDelayPassed(worldObj, 10))) {
            sendUpdatePacket(Side.CLIENT);
        }
    }

    protected abstract boolean check();

    protected abstract void scan();

    public void resetResult(boolean send)
    {
        setFinished(false);
        if (ServerHelper.isClientWorld(worldObj) && send) {
            sendUpdatePacket(Side.SERVER);
        }
    }

    protected void consumeEnergy()
    {
        int energy = calcEnergy();
        energyStorage.modifyEnergyStored(-energy * ecc);
    }

    protected int calcEnergy()
    {
        if (!isActive) {
            return 0;
        }
        if (energyStorage.getEnergyStored() > energyConfig.maxPowerLevel) {
            return energyConfig.maxPower;
        }
        if (energyStorage.getEnergyStored() < energyConfig.minPowerLevel) {
            return energyConfig.minPower;
        }
        return energyStorage.getEnergyStored() / energyConfig.energyRamp;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    //
    // ISidedTexture
    @Override
    public IIcon getTexture(int side, int pass)
    {
        if (pass == 0) {
            if (side == 0) {
                return BlockScanner.bottomSide;
            } else if (side == 1) {
                return BlockScanner.topSide;
            }
            if (side == facing) {
                if (isActive) {
                    return BlockScanner.faceActive[type];
                } else if (finished) {
                    return BlockScanner.faceFinished[type];
                }
                return BlockScanner.face[type];
            }
            return BlockScanner.side;
        }
        return BlockScanner.side;
    }

    //
    // Gui methods
    public int getScaledProgress(int scale)
    {
        return 0;
    }

    //
    // IEnergyInfo
    @Override
    public int getInfoEnergyPerTick()
    {
        return calcEnergy() * ecc;
    }

    @Override
    public int getInfoMaxEnergyPerTick()
    {
        return energyConfig.maxPower * ecc;
    }

    @Override
    public int getInfoMaxEnergyStored()
    {
        return energyConfig.maxEnergy;
    }

    @Override
    public int getInfoEnergyStored()
    {
        return energyStorage.getEnergyStored();
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
        return true;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return side != facing && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot < inventory.length;
    }

    //
    // NBT methods
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
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        energyStorage.writeToNBT(nbt);
    }

    //
    // Network
    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase payload = super.getGuiPacket();

        payload.addInt(energyStorage.getMaxEnergyStored());
        payload.addInt(energyStorage.getEnergyStored());

        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase payload)
    {
        super.handleGuiPacket(payload);

        energyStorage.setCapacity(payload.getInt());
        energyStorage.setEnergyStored(payload.getInt());
    }
}
