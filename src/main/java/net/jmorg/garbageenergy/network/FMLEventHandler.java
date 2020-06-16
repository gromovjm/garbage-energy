package net.jmorg.garbageenergy.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class FMLEventHandler extends cofh.core.util.FMLEventHandler
{
    public static FMLEventHandler instance = new FMLEventHandler();

    public static void initialize()
    {
        FMLCommonHandler.instance().bus().register(instance);
    }

    @Override
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent playerLoggedInEvent)
    {
        super.onPlayerLogin(playerLoggedInEvent);
        GarbageEnergyPacket.sendConfigSyncPacketToClient(playerLoggedInEvent.player);
    }
}
