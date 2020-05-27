package net.jmorg.garbageenergy.common.bloks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class TileCore extends TileGeneratorBase
{
    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileCore.class, "garbageEnergy.generator.core");
    }

    public TileCore()
    {
        inventory = new ItemStack[3];
    }
}
