package net.jmorg.garbageenergy.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.items.ItemDataCard;
import net.jmorg.garbageenergy.common.items.tool.Wrench;
import net.minecraft.item.Item;

public class GarbageEnergyItem
{
    public static final Item wrench = new Wrench();
    public static final ItemDataCard dataCard = new ItemDataCard();

    public static void registerItems()
    {
        GameRegistry.registerItem(wrench, "wrench");
        dataCard.registerCards();

        GarbageEnergy.log.info(GarbageEnergy.MODNAME + ": Items are registered.");
    }
}
