package net.jmorg.garbageenergy.utils;

import cofh.api.item.IAugmentItem;
import cofh.core.gui.slot.SlotAugment;
import net.jmorg.garbageenergy.common.blocks.TileAugmentable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class AugmentManager
{
    public TileAugmentable augmentable;
    public boolean[] augmentStatus;
    public ItemStack[] augments;

    public AugmentManager(TileAugmentable augmentable, int augments)
    {
        this.augmentable = augmentable;
        this.augments = new ItemStack[augments];
        this.augmentStatus = new boolean[augments];
    }

    public ItemStack[] getAugmentSlots()
    {
        return augments;
    }

    public boolean[] getAugmentStatus()
    {
        return augmentStatus;
    }

    public void install()
    {
        for (int i = 0; i < augments.length; i++) {
            augmentStatus[i] = false;
            if (Utils.isAugmentItem(augments[i])) {
                augmentStatus[i] = installAugment(i);
            }
        }
    }

    protected boolean installAugment(int slot)
    {
        ItemStack augmentSlot = augments[slot];
        IAugmentItem augmentItem = (IAugmentItem) augments[slot].getItem();

        return augmentable.installAugment(augmentItem, augmentSlot);
    }

    public SlotAugment[] getSlots()
    {
        int slots = augments.length;
        SlotAugment[] augmentSlots = new SlotAugment[slots];

        for (int i = 0; i < slots; i++) {
            augmentSlots[i] = new SlotAugment(augmentable, null, i, 0, 0);
        }

        return augmentSlots;
    }

    public void readAugmentsFromNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList("Augments", 10);

        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound tag = list.getCompoundTagAt(i);
            int slot = tag.getInteger("Slot");
            if (slot >= 0 && slot < augments.length) {
                augments[slot] = ItemStack.loadItemStackFromNBT(tag);
            }
        }
    }

    public void writeAugmentsToNBT(NBTTagCompound nbt)
    {
        if (augments.length <= 0) {
            return;
        }
        NBTTagList list = new NBTTagList();
        for (int i = 0; i < augments.length; i++) {
            if (augments[i] != null) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setInteger("Slot", i);
                augments[i].writeToNBT(tag);
                list.appendTag(tag);
            }
        }
        nbt.setTag("Augments", list);
    }
}
