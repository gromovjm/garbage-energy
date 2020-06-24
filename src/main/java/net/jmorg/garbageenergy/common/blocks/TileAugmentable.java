package net.jmorg.garbageenergy.common.blocks;

import cofh.api.item.IAugmentItem;
import cofh.api.tileentity.IAugmentable;
import cofh.core.network.PacketCoFHBase;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.utils.AugmentManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileAugmentable extends TileReconfigurable implements IAugmentable
{
    public AugmentManager augmentManager;

    public boolean augmentRedstoneControl = false;

    @Override
    public void installAugments()
    {
        resetAugments();
        augmentManager.install();

        if (worldObj != null && ServerHelper.isServerWorld(worldObj)) {
            updateRsMode();
            sendUpdatePacket(Side.CLIENT);
        }
    }

    protected void updateRsMode()
    {
        if (!augmentRedstoneControl) {
            this.rsMode = ControlMode.DISABLED;
        }
    }

    public boolean installAugment(IAugmentItem augment, ItemStack itemStack, int slot)
    {
        boolean installed = false;

        if (augment.getAugmentLevel(itemStack, "generalRedstoneControl") > 0) {
            augmentRedstoneControl = true;
            installed = true;
        }

        return installed;
    }

    public void resetAugments()
    {
        augmentRedstoneControl = false;
    }

    @Override
    public ItemStack[] getAugmentSlots()
    {
        return augmentManager.getAugmentSlots();
    }

    @Override
    public boolean[] getAugmentStatus()
    {
        return augmentManager.getAugmentStatus();
    }

    //
    // NBT methods.
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        augmentManager.readAugmentsFromNBT(nbt);
        installAugments();
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        augmentManager.writeAugmentsToNBT(nbt);
    }

    //
    // Network
    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase payload = super.getPacket();

        payload.addBool(augmentRedstoneControl);

        return payload;
    }

    @Override
    public void handleTilePacket(PacketCoFHBase payload, boolean isServer)
    {
        super.handleTilePacket(payload, isServer);

        if (!isServer) {
            augmentRedstoneControl = payload.getBool();
        } else {
            payload.getBool();
        }
    }

    @Override
    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase payload = super.getGuiPacket();

        payload.addBool(augmentRedstoneControl);

        return payload;
    }

    @Override
    protected void handleGuiPacket(PacketCoFHBase payload)
    {
        boolean prevControl = augmentRedstoneControl;
        augmentRedstoneControl = payload.getBool();

        if (augmentRedstoneControl != prevControl) {
            updateRsMode();
        }
    }
}
