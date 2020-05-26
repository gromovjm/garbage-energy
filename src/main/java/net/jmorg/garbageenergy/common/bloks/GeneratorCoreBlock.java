package net.jmorg.garbageenergy.common.bloks;

import net.minecraft.block.material.Material;

public class GeneratorCoreBlock extends BlockGeneric
{
    public GeneratorCoreBlock()
    {
        super(Material.rock);

        setBlockName("GeneratorCore");
        setBlockTextureName("garbageenergy:generator_core_block");
    }
}
