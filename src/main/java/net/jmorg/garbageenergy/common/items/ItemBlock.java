package net.jmorg.garbageenergy.common.items;

import cofh.api.item.ICreativeItem;
import cofh.core.block.ItemBlockCore;
import cofh.core.init.CoreProps;
import cofh.core.render.IModelRegister;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

public abstract class ItemBlock extends ItemBlockCore implements IModelRegister, ICreativeItem
{
    public ItemBlock(Block block)
    {
        super(block);
        if (block.getRegistryName() != null) {
            setRegistryName(block.getRegistryName());
        }
        setHasSubtypes(true);
        setMaxDamage(0);
        setNoRepair();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return StringHelper.localize(getUnlocalizedName(stack)) + " (" + StringHelper.localize("info.garbageenergy.level." + (isCreative(stack) ? "creative" : getLevel(stack))) + ")";
    }

    @Override
    public EnumRarity getRarity(ItemStack stack)
    {
        if (isCreative(stack)) {
            return EnumRarity.EPIC;
        }
        switch (getLevel(stack)) {
            case 4:
                return EnumRarity.RARE;
            case 3:
            case 2:
                return EnumRarity.UNCOMMON;
            default:
                return EnumRarity.COMMON;
        }
    }

    @Override
    public int getMaxLevel(ItemStack stack)
    {
        return CoreProps.LEVEL_MAX;
    }
}
