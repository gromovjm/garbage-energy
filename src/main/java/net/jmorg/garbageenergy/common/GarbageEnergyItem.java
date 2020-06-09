package net.jmorg.garbageenergy.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.items.BaseItem;
import net.jmorg.garbageenergy.common.items.ItemDataCard;
import net.jmorg.garbageenergy.common.items.tool.WrenchTool;

public class GarbageEnergyItem
{
    public static final BaseItem wrench = new WrenchTool();
    public static final BaseItem dataCard = new ItemDataCard();

    public static void registerItems()
    {
        GameRegistry.registerItem(wrench, wrench.getName());
        GameRegistry.registerItem(dataCard, dataCard.getName());

        GarbageEnergy.log.info(GarbageEnergy.MODNAME + ": Items are registered.");
    }
}
