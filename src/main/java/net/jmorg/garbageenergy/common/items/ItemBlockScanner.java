package net.jmorg.garbageenergy.common.items;

import cofh.core.block.ItemBlockCore;
import net.minecraft.block.Block;

public class ItemBlockScanner extends ItemBlockCore
{
    public ItemBlockScanner(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
        setNoRepair();
    }
}
