package net.jmorg.garbageenergy.proxy;

import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.GarbageEnergyBlock;
import net.jmorg.garbageenergy.common.items.GarbageEnergyItem;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

//        MinecraftForge.EVENT_BUS.register(RenderEventHandler.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        GarbageEnergyBlock.registerRenders();
        GarbageEnergyItem.registerRenders();

        GarbageEnergy.log.info("Models are registered.");
    }
}
