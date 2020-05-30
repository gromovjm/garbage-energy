package net.jmorg.garbageenergy.common.blocks;

import cofh.api.tileentity.IPortableData;
import cofh.core.block.TileCoFHBase;
import cofh.core.network.*;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.gui.GuiHandler;
import net.jmorg.garbageenergy.network.GarbageEnergyPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.nbt.NBTTagCompound;

public abstract class BaseTile extends TileCoFHBase implements ITileInfoPacketHandler, ITilePacketHandler, IPortableData
{
    public String tileName = "";

    public void setTileName(String name)
    {
        if (!name.isEmpty()) {
            tileName = name;
        }
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    //
    // Gui methods.
    public boolean hasGui()
    {
        return false;
    }

    @Override
    public int getInvSlotCount()
    {
        return 0;
    }

    @Override
    public boolean openGui(EntityPlayer entityPlayer)
    {
        if (hasGui()) {
            entityPlayer.openGui(GarbageEnergy.instance, GuiHandler.TILE_GUI, worldObj, xCoord, yCoord, zCoord);
            return true;
        }

        return false;
    }

    @Override
    public void sendGuiNetworkData(Container container, ICrafting iCrafting)
    {
        if (iCrafting instanceof EntityPlayer) {
            PacketCoFHBase guiPacket = getGuiPacket();
            if (guiPacket != null) {
                PacketHandler.sendTo(guiPacket, (EntityPlayer) iCrafting);
            }
        }
    }

    public void updateFromNBT(NBTTagCompound nbt)
    {
    }

    @Override
    public PacketCoFHBase getPacket()
    {
        PacketCoFHBase payload = super.getPacket();
        payload.addString(tileName);
        return payload;
    }

    public PacketCoFHBase getGuiPacket()
    {
        PacketCoFHBase payload = PacketTileInfo.newPacket(this);
        payload.addByte(GarbageEnergyPacket.PacketID.GUI.ordinal());
        return payload;
    }

    protected void handleGuiPacket(PacketCoFHBase payload)
    {
    }

    protected void handleModePacket(PacketCoFHBase payload)
    {
        markChunkDirty();
    }

    //
    // ITilePacketHandler
    @Override
    public void handleTilePacket(PacketCoFHBase payload, boolean isServer)
    {
        if (ServerHelper.isClientWorld(worldObj)) {
            tileName = payload.getString();
        } else {
            payload.getString();
        }
    }

    //
    // ITileInfoPacketHandler
    @Override
    public void handleTileInfoPacket(PacketCoFHBase payload, boolean isServer, EntityPlayer thePlayer)
    {
        switch (GarbageEnergyPacket.PacketID.values()[payload.getByte()]) {
            case GUI:
                handleGuiPacket(payload);
                return;
            case MODE:
                handleModePacket(payload);
                return;
            default:
        }
    }

    //
    // IPortableData
    protected boolean readPortableTagInternal(EntityPlayer entityPlayer, NBTTagCompound tag)
    {
        return false;
    }

    protected boolean writePortableTagInternal(EntityPlayer entityPlayer, NBTTagCompound tag)
    {
        return false;
    }

    @Override
    public String getDataType()
    {
        return getName();
    }

    @Override
    public void readPortableData(EntityPlayer player, NBTTagCompound tag)
    {
        if (!canPlayerAccess(player)) return;
        if (readPortableTagInternal(player, tag)) {
            markDirty();
            sendUpdatePacket(Side.CLIENT);
        }
    }

    @Override
    public void writePortableData(EntityPlayer player, NBTTagCompound tag)
    {
        if (writePortableTagInternal(player, tag)) {
            return;
        }
    }
}
