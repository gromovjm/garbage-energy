package net.jmorg.garbageenergy.proxy;

import cofh.core.proxy.Proxy;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.GarbageEnergyBlock;
import net.jmorg.garbageenergy.network.PacketCore;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

@Mod.EventBusSubscriber
public class CommonProxy extends Proxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(FMLEventHandler.INSTANCE);
    }

    @Override
    public void initialize(FMLInitializationEvent event)
    {
        super.initialize(event);

        NetworkRegistry.INSTANCE.registerGuiHandler(GarbageEnergy.instance, GarbageEnergy.guiHandler);
//        MinecraftForge.EVENT_BUS.register(RegistryEventHandler.INSTANCE);

        // Packets
        PacketCore.initialize();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);

        GarbageEnergyBlock.postInit();
    }
}
