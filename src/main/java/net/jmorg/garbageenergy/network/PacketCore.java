package net.jmorg.garbageenergy.network;

import cofh.core.network.PacketBase;
import cofh.core.network.PacketHandler;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.GEProperties;
import net.minecraft.entity.player.EntityPlayer;

public class PacketCore extends PacketBase
{
    public static void initialize()
    {
        PacketHandler.INSTANCE.registerPacket(PacketCore.class);
    }

    public enum Packet
    {
        CONFIG_SYNC;
    }

    @Override
    public void handlePacket(EntityPlayer player, boolean isServer)
    {
        try {
            int type = getByte();
            if (Packet.values()[type] == Packet.CONFIG_SYNC) {
                GEProperties.handleConfigSync(this);
            } else {
                GarbageEnergy.log.error("Unknown Packet! Internal: " + GarbageEnergy.MODID + "; " + type);
            }
        } catch (Exception e) {
            GarbageEnergy.log.error("Packet payload failure! Please check your config files!", e);
        }
    }

    public static void sendConfigSyncPacketToClient(EntityPlayer player)
    {
        PacketHandler.sendTo(GEProperties.getConfigSync(), player);
    }

    public static PacketBase getPacket(Packet type)
    {
        return new PacketCore().addByte(type.ordinal());
    }
}
