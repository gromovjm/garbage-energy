package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockGenerator extends ItemBlockBase
{
    public ItemBlockGenerator(Block block)
    {
        super(block);
    }

    public static ItemStack setDefaultTag(ItemStack container)
    {
        RedstoneControlHelper.setControl(container, IRedstoneControl.ControlMode.DISABLED);
        return container;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return BlockGenerator.getTileName(BlockGenerator.NAMES[ItemHelper.getItemDamage(stack)]);
    }
}
