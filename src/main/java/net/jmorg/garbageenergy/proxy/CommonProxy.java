package net.jmorg.garbageenergy.proxy;

import cofh.core.Proxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import net.jmorg.garbageenergy.common.GarbageEnergyBlock;
import net.jmorg.garbageenergy.common.Item;

public class CommonProxy extends Proxy
{
    @Override
    public void preInit()
    {
        super.preInit();
        GarbageEnergyBlock.registerBlocks();
        Item.registerItems();
    }

    public void init(FMLInitializationEvent event)
    {
        // null
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        // null
    }
}
