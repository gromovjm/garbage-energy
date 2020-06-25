package net.jmorg.garbageenergy.common.blocks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;

public class TileItemAnnihilationGenerator extends TileGeneratorBase
{
    public TileItemAnnihilationGenerator()
    {
        super(BlockGenerator.Types.ITEM_ANNIHILATION);
    }

    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileItemAnnihilationGenerator.class, GarbageEnergy.MODID + ".Generator.ItemAnnihilationGenerator");
    }

    @Override
    protected boolean canGenerate()
    {
        return false;
    }

    @Override
    protected void generate()
    {

    }

    @Override
    public int getScaledDuration(int scale)
    {
        return 0;
    }

    @Override
    public int getNumConfig(int i)
    {
        return 0;
    }
}
