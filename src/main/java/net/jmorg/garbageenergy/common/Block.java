package net.jmorg.garbageenergy.common;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.common.bloks.BlockGeneric;
import net.jmorg.garbageenergy.common.bloks.GeneratorCoreBlock;

public class Block
{
    public static final BlockGeneric generatorCoreBlock = new GeneratorCoreBlock();

    public static void registerBlocks()
    {
        GameRegistry.registerBlock(generatorCoreBlock, "generator_core_block");
    }
}
