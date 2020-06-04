package net.jmorg.garbageenergy.common;

import cofh.api.core.IInitializer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.BaseBlock;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator;
import net.jmorg.garbageenergy.common.blocks.scanner.BlockScanner;
import net.jmorg.garbageenergy.common.items.ItemBlockGenerator;
import net.jmorg.garbageenergy.common.items.ItemBlockScanner;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

import java.util.ArrayList;

public class GarbageEnergyBlock
{
    public static ArrayList<IInitializer> blockList = new ArrayList<IInitializer>();

    public static Block generator;
    public static Block scanner;

    public static void registerBlocks()
    {
        generator = addBlock("Generator", new BlockGenerator(), ItemBlockGenerator.class);
        scanner = addBlock("Scanner", new BlockScanner(), ItemBlockScanner.class);

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

    private static Block addBlock(String name, BaseBlock block)
    {
        return addBlock(name, block, ItemBlock.class);
    }

    private static Block addBlock(String name, BaseBlock block, Class<? extends ItemBlock> itemClass)
    {
        GameRegistry.registerBlock(block, itemClass, name);
        blockList.add(block);
        return block;
    }

    private GarbageEnergyBlock()
    {
    }
}
