package net.jmorg.garbageenergy.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.common.items.ItemGeneric;
import net.jmorg.garbageenergy.common.items.tool.WrenchTool;

public class Item
{
    public static final ItemGeneric wrenchItem = new WrenchTool();

    public static void registerItems()
    {
        GameRegistry.registerItem(wrenchItem, wrenchItem.getName());
    }
}
