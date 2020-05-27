package net.jmorg.garbageenergy.common.bloks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class TileReceiver extends TileGeneratorBase
{
    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileReceiver.class, "garbageEnergy.generator.receiver");
    }

    public TileReceiver()
    {
        super(BlockGenerator.Types.RECEIVER);
        inventory = new ItemStack[3];
    }
}
