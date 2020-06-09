package net.jmorg.garbageenergy.common.blocks;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.EnergyHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TilePowered extends TileReconfigurable implements IEnergyReceiver
{
    protected EnergyStorage energyStorage;

    public int getChargeSlot()
    {
        return inventory.length - 1;
    }

    public boolean hasChargeSlot()
    {
        return true;
    }

    protected boolean hasEnergy(int energy)
    {
        return energyStorage.getEnergyStored() >= energy;
    }

    protected boolean drainEnergy(int energy)
    {
        return hasEnergy(energy) && energyStorage.extractEnergy(energy, false) == energy;
    }

    protected void charge()
    {
        int chargeSlot = getChargeSlot();

        if (hasChargeSlot() && EnergyHelper.isEnergyContainerItem(inventory[chargeSlot])) {
            int energyRequest = Math.min(energyStorage.getMaxReceive(), energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored());
            IEnergyContainerItem item = (IEnergyContainerItem) inventory[chargeSlot].getItem();
            energyStorage.receiveEnergy(item.extractEnergy(inventory[chargeSlot], energyRequest, false), false);
            if (inventory[chargeSlot].stackSize <= 0) {
                inventory[chargeSlot] = null;
            }
        }
    }

    public final void setEnergyStored(int quantity)
    {
        energyStorage.setEnergyStored(quantity);
    }

    public IEnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    //
    // IEnergyReceiver
    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
    {
        return energyStorage.receiveEnergy(maxReceive, simulate);
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
        return energyStorage.getMaxEnergyStored() > 0;
    }

    //
    // NBT methods.
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
    // Network.
    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase payload = super.getPacket();

        payload.addInt(energyStorage.getEnergyStored());

        return payload;
    }

    @Override
    public void handleTilePacket(PacketCoFHBase payload, boolean isServer)
    {
        super.handleTilePacket(payload, isServer);

        int energy = payload.getInt();

        if (!isServer) {
            energyStorage.setEnergyStored(energy);
        }
    }
}
