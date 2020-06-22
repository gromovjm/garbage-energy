package net.jmorg.garbageenergy.network;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.network.PacketCoFHBase;
import cofh.core.network.PacketHandler;
import cofh.lib.gui.container.IAugmentableContainer;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.utils.IDataCardManageable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GarbageEnergyPacket extends PacketCoFHBase
{
    public static void initialize()
    {
        PacketHandler.instance.registerPacket(GarbageEnergyPacket.class);
    }

    public enum PacketTypes
    {
        RS_POWER_UPDATE, RS_CONFIG_UPDATE, CONFIG_SYNC, TAB_AUGMENT, DATA_CARD_WRITE
    }

    public enum PacketID
    {
        GUI, FLUID, MODE
    }

    @Override
    public void handlePacket(EntityPlayer player, boolean isServer)
    {
        try {
            int type = getByte();

            switch (PacketTypes.values()[type]) {
                case RS_POWER_UPDATE:
                    handleRsPowerUpdate(player);
                    return;
                case RS_CONFIG_UPDATE:
                    handleRsConfigUpdate(player);
                    return;
                case TAB_AUGMENT:
                    if (player.openContainer instanceof IAugmentableContainer) {
                        ((IAugmentableContainer) player.openContainer).setAugmentLock(getBool());
                    }
                    return;
                case DATA_CARD_WRITE:
                    handleDataCardWriteUpdate(player);
                    return;
                case CONFIG_SYNC:
                    GarbageEnergy.instance.handleConfigSync(this);
                    return;
                default:
                    GarbageEnergy.log.error("[Unknown Packet] PacketId: " + type);
            }
        } catch (Exception e) {
            GarbageEnergy.log.error("Packet payload failure! Please check your config files!");
            e.printStackTrace();
        }
    }

    public static void sendRSPowerUpdatePacketToClients(IRedstoneControl rs, World world, int x, int y, int z)
    {
        PacketHandler.sendToAllAround(getPacket(PacketTypes.RS_POWER_UPDATE).addCoords(x, y, z).addBool(rs.isPowered()), world, x, y, z);
    }

    public static void sendRSConfigUpdatePacketToServer(IRedstoneControl rs, int x, int y, int z)
    {
        PacketHandler.sendToServer(getPacket(PacketTypes.RS_CONFIG_UPDATE).addCoords(x, y, z).addByte(rs.getControl().ordinal()));
    }

    public static void sendConfigSyncPacketToClient(EntityPlayer player)
    {
        PacketHandler.sendTo(GarbageEnergy.instance.getConfigSync(), player);
    }

    public static void sendTabAugmentPacketToServer(boolean lock)
    {
        PacketHandler.sendToServer(getPacket(PacketTypes.TAB_AUGMENT).addBool(lock));
    }

    public static void sendDataCardWritePacketToServer()
    {
        PacketHandler.sendToServer(getPacket(PacketTypes.DATA_CARD_WRITE));
    }

    public static PacketCoFHBase getPacket(PacketTypes packetType)
    {
        return new GarbageEnergyPacket().addByte(packetType.ordinal());
    }

    private boolean isValidPacketData(EntityPlayer player, int x, int y, int z)
    {
        if (!player.worldObj.blockExists(x, y, z)) {
            return false;
        }
        return player.getDistanceSq(x, y, z) < 64.0D;
    }

    protected void handleRsPowerUpdate(EntityPlayer player)
    {
        int[] coords = getCoords();
        if (isValidPacketData(player, coords[0], coords[1], coords[2])) {
            TileEntity rs = player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
            if (rs instanceof IRedstoneControl) {
                ((IRedstoneControl) rs).setPowered(getBool());
            }
        }
    }

    protected void handleRsConfigUpdate(EntityPlayer player)
    {
        int[] coords = getCoords();
        if (isValidPacketData(player, coords[0], coords[1], coords[2])) {
            TileEntity rs = player.worldObj.getTileEntity(coords[0], coords[1], coords[2]);
            if (rs instanceof IRedstoneControl) {
                ((IRedstoneControl) rs).setControl(IRedstoneControl.ControlMode.values()[getByte()]);
            }
        }
    }

    protected void handleDataCardWriteUpdate(EntityPlayer player)
    {
        if (player.openContainer instanceof IDataCardManageable) {
            ((IDataCardManageable) player.openContainer).writeDataCard();
        }
    }
}
