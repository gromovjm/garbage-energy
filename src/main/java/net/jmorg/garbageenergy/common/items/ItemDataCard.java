package net.jmorg.garbageenergy.common.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemDataCard extends BaseItem
{
    private boolean subscribed = false;

    public String itemId;

    public String itemDisplayName;

    public float itemEnergyModifier;

    public String getName()
    {
        return "data_card";
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    public void setItemDisplayName(String itemDisplayName)
    {
        this.itemDisplayName = itemDisplayName;
    }

    public void setItemEnergyModifier(float itemEnergyModifier)
    {
        this.itemEnergyModifier = itemEnergyModifier;
    }

    public void setSubscribed(boolean subscribed)
    {
        this.subscribed = subscribed;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List information, boolean debug)
    {
        if (subscribed) {
            information.add(itemDisplayName);
            information.add(itemId);
            information.add(String.valueOf(itemEnergyModifier));
        }
    }
}
