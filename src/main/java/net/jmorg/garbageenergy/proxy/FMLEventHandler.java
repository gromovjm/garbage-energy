package net.jmorg.garbageenergy.proxy;

import net.jmorg.garbageenergy.network.PacketCore;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class FMLEventHandler
{
    public static final FMLEventHandler INSTANCE = new FMLEventHandler();

    @SubscribeEvent
    public void handlePlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent)
    {
        PacketCore.sendConfigSyncPacketToClient(playerLoggedInEvent.player);
    }
}
