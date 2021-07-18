package net.jmorg.garbageenergy.utils;

import cofh.api.item.IAugmentItem;
import net.minecraft.item.ItemStack;

public class Utils
{
    public static boolean isAugmentItem(ItemStack container)
    {
        return container != null && container.getItem() instanceof IAugmentItem;
    }
}
