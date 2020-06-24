package net.jmorg.garbageenergy.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.items.ItemAugment;
import net.jmorg.garbageenergy.common.items.ItemDataCard;
import net.jmorg.garbageenergy.common.items.tool.Wrench;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class GarbageEnergyItem
{
    public static final ItemAugment augment = new ItemAugment();
    public static final ItemDataCard dataCard = new ItemDataCard();
    public static final Item wrench = new Wrench();

    public static void registerItems()
    {
        augment.registerAugments();
        dataCard.registerCards();
        GameRegistry.registerItem(wrench, "wrench");

        GarbageEnergy.log.info(GarbageEnergy.MODNAME + ": Items are registered.");
    }

    public static void postInit()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(dataCard.common, "BRB", "BDB", "BBB", 'B', "dyeBlack", 'R', "dustRedstone", 'D', "gemDiamond"));
        GameRegistry.addRecipe(dataCard.uncommon, "III", "DCD", "III", 'I', Items.iron_ingot, 'C', dataCard.common, 'D', Items.diamond);
        GameRegistry.addRecipe(dataCard.rare, " D ", "DCD", " D ", 'C', dataCard.uncommon, 'D', Items.diamond);
        GameRegistry.addRecipe(dataCard.epic, "IDI", "DCD", "IDI", 'I', Items.iron_ingot, 'C', dataCard.rare, 'D', Items.diamond);
    }
}
