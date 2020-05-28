package net.jmorg.garbageenergy.common.bloks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;

public class TileTransmitter extends TileGeneratorBase
{
    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileTransmitter.class, "GarbageEnergy.generator.transmitter");
    }

    public TileTransmitter()
    {
        super(BlockGenerator.Types.TRANSMITTER);
        inventory = new ItemStack[3];
    }
}
