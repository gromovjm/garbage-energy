package net.jmorg.garbageenergy.utils;

import cofh.lib.util.helpers.StringHelper;
import net.jmorg.garbageenergy.common.items.ItemDataCard;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.*;

public class ItemDataCardManager
{
    public static int getMaxStoredItemsCount(ItemStack dataCard)
    {
        return ItemDataCard.cardSizes[dataCard.getItem().getDamage(dataCard)];
    }

    public static int getTotalStoredItemsCount(ItemStack dataCard)
    {
        return getNBTTag(dataCard).getTagList("Items", 10).tagCount();
    }

    public static List<HashMap<String, String>> getItems(ItemStack dataCard)
    {
        List<HashMap<String, String>> items = new ArrayList<HashMap<String, String>>();
        NBTTagList tagList = getNBTTag(dataCard).getTagList("Items", 10);

        for (int i = 0; i < getMaxStoredItemsCount(dataCard); i++) {
            NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
            if (!itemTag.hasNoTags()) {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put("Id", itemTag.getString("Id"));
                item.put("DisplayName", itemTag.getString("DisplayName"));
                item.put("EnergyModifier", itemTag.getString("EnergyModifier"));
                items.add(item);
            }
        }

        return items;
    }

    public static boolean hasItem(ItemStack dataCard, String itemId)
    {
        NBTTagList tagList = getNBTTag(dataCard).getTagList("Items", 10);
        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound itemTag = tagList.getCompoundTagAt(i);
            if (itemTag.getString("Id").equals(itemId)) return true;
        }
        return false;
    }

    public static boolean saveData(ItemStack dataCard, String itemId, String itemDisplayName, float itemEnergyModifier)
    {
        NBTTagCompound nbt = getNBTTag(dataCard);
        NBTTagList tagList = new NBTTagList();
        boolean result = false;

        if (nbt.hasKey("Items")) {
            tagList = nbt.getTagList("Items", 10);
        }

        if (tagList.tagCount() < getMaxStoredItemsCount(dataCard) && !hasItem(dataCard, itemId)) {
            NBTTagCompound tagListNBT = new NBTTagCompound();
            tagListNBT.setString("Id", itemId);
            tagListNBT.setString("DisplayName", itemDisplayName);
            tagListNBT.setString("EnergyModifier", String.valueOf(itemEnergyModifier));
            tagList.appendTag(tagListNBT);
            result = true;
        }

        nbt.setTag("Items", tagList);
        dataCard.setTagCompound(nbt);

        return result;
    }

    public static void addItemsInformation(ItemStack dataCard, List list)
    {
        list.add(StringHelper.getNoticeText("tooltip.dataCard.containInfoAbout"));
        List<HashMap<String, String>> items = ItemDataCardManager.getItems(dataCard);
        for (HashMap<String, String> item : items) {
            list.add(String.format(StringHelper.localize("tooltip.dataCard.itemRfGenerationFromItem"), item.get("EnergyModifier"), item.get("DisplayName")));
        }
    }

    protected static NBTTagCompound getNBTTag(ItemStack itemStack)
    {
        return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
    }
}
