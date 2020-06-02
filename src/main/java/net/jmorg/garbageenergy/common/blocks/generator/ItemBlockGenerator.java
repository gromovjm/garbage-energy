package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

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

    @Override
    public void addInformation(ItemStack stack, EntityPlayer entityPlayer, List list, boolean check)
    {
        SecurityHelper.addOwnerInformation(stack, list);
        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
            list.add(StringHelper.shiftForDetails());
        }
        if (!StringHelper.isShiftKeyDown()) {
            return;
        }
        SecurityHelper.addAccessInformation(stack, list);

//        list.add(StringHelper.localize("info.thermalexpansion.dynamo.generate"));
//        list.add(StringHelper.getInfoText("info.thermalexpansion.dynamo." + BlockDynamo.NAMES[ItemHelper.getItemDamage(stack)]));
//
//        if (ItemHelper.getItemDamage(stack) == BlockDynamo.Types.STEAM.ordinal()) {
//            list.add(StringHelper.getNoticeText("info.thermalexpansion.dynamo.steam.0"));
//        }
//        RedstoneControlHelper.addRSControlInformation(stack, list);
    }
}
