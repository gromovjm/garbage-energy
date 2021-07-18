package net.jmorg.garbageenergy.common.items;

import cofh.core.render.IModelRegister;
import cofh.core.util.core.IInitializer;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.GarbageEnergyBlock;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public class GarbageEnergyItem
{
    private static final List<Item> items = new ArrayList<>();

    public static ItemBlockGenerator blockGenerator;

    public static void registerItems(IForgeRegistry<Item> registry)
    {
        blockGenerator = (ItemBlockGenerator) registerItem(registry, new ItemBlockGenerator(GarbageEnergyBlock.generator));

        GarbageEnergy.log.info("Items are registered.");
    }

    public static void registerRecipes()
    {
        for (Item item : items) {
            if (item instanceof IInitializer) {
                ((IInitializer) item).initialize();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerRenders()
    {
        for (Item item : items) {
            if (item instanceof IModelRegister) {
                ((IModelRegister) item).registerModels();
            }
        }
    }

    protected static Item registerItem(IForgeRegistry<Item> registry, Item item)
    {
        registry.register(item);

        if (item instanceof IInitializer) {
            ((IInitializer) item).preInit();
        }

        items.add(item);

        return item;
    }
}
