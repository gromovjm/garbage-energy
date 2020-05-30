package net.jmorg.garbageenergy.common;

import cofh.api.core.IInitializer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator;
import net.jmorg.garbageenergy.common.blocks.generator.ItemBlockGenerator;
import net.minecraft.block.Block;

import java.util.ArrayList;

public class GarbageEnergyBlock
{
    public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();

    public static Block blockGenerator;

    private GarbageEnergyBlock()
    {
    }

    public static void registerBlocks()
    {
        blockGenerator = addBlock(new BlockGenerator());

        GameRegistry.registerBlock(blockGenerator, ItemBlockGenerator.class, "Generator");

        for (IInitializer initializer : blockList) {
            initializer.initialize();
        }

        GarbageEnergy.log.info(GarbageEnergy.MODNAME + ": Blocks are registered.");
    }

    public static Block addBlock(Block block)
    {
        blockList.add((IInitializer) block);
        return block;
    }
}
