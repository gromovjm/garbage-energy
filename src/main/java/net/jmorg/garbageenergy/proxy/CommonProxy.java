package net.jmorg.garbageenergy.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.jmorg.garbageenergy.common.Block;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        Block.registerBlocks();
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
