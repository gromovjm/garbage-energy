package net.jmorg.garbageenergy.common;

import cofh.api.core.IInitializer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator;
import net.jmorg.garbageenergy.common.items.ItemBlockGenerator;
import net.minecraft.block.Block;

import java.util.ArrayList;

public class GarbageEnergyBlock
{
    public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();

    public static Block generator;

    public static void registerBlocks()
    {
        generator = addBlock(new BlockGenerator());

        GameRegistry.registerBlock(generator, ItemBlockGenerator.class, "Generator");

        for (IInitializer initializer : blockList) {
            initializer.initialize();
        }

        GarbageEnergy.log.info(GarbageEnergy.MODNAME + ": Blocks are registered.");
    }

    public static void postInit()
    {
        for (IInitializer initializer : blockList) {
            initializer.postInit();
        }
        blockList.clear();
    }

    private static Block addBlock(Block block)
    {
        blockList.add((IInitializer) block);
        return block;
    }

    private GarbageEnergyBlock()
    {
    }
}
