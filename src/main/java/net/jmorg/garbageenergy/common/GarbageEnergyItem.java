package net.jmorg.garbageenergy.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.items.ItemGeneric;
import net.jmorg.garbageenergy.common.items.tool.WrenchTool;

public class GarbageEnergyItem
{
    public static final ItemGeneric wrenchItem = new WrenchTool();

    public static void registerItems()
    {
        GameRegistry.registerItem(wrenchItem, wrenchItem.getName());

        GarbageEnergy.log.info(GarbageEnergy.MODNAME + ": Items are registered.");
    }
}
