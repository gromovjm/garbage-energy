package net.jmorg.garbageenergy.common;

import cofh.api.core.IInitializer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.common.bloks.generator.BlockGenerator;
import net.jmorg.garbageenergy.common.bloks.generator.ItemBlockGenerator;
import net.minecraft.block.Block;

import java.util.ArrayList;

public class GEBlock
{
    public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();

    public static Block blockGenerator;

    private GEBlock() {}

    public static void registerBlocks()
    {
        blockGenerator = addBlock(new BlockGenerator());

        GameRegistry.registerBlock(blockGenerator, ItemBlockGenerator.class, "Generator");
    }

    public static Block addBlock(Block block) {

        blockList.add((IInitializer) block);
        return block;
    }
}
