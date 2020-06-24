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

        return augmentable.installAugment(augmentItem, augmentSlot, slot);
    }

    public boolean hasAugment(String type, int level)
    {
        for (ItemStack augment : augments) {
            if (Utils.isAugmentItem(augment) && ((IAugmentItem) augment.getItem()).getAugmentLevel(augment, type) == level) {
                return true;
            }
        }
        return false;
    }

    public boolean hasDuplicateAugment(String type, int level, int slot)
    {
        for (int i = 0; i < augments.length; i++) {
            if (i != slot && Utils.isAugmentItem(augments[i]) && ((IAugmentItem) augments[i].getItem()).getAugmentLevel(augments[i], type) == level) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAugmentChain(String type, int level)
    {
        boolean preReq = true;
        for (int i = 1; i < level; i++) {
            preReq = preReq && hasAugment(type, i);
        }
        return preReq;
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

    public static class Augments
    {
        public static final int NUM_ENERGY_AMPLIFIER = 3;
        public static final String ENERGY_AMPLIFIER_NAME = "energyAmplifier";
        public static final int[] ENERGY_AMPLIFIER = {1, 2, 3, 5};

        public static final int NUM_ATTENUATE_MODIFIER = 3;
        public static final String ATTENUATE_MODIFIER_NAME = "attenuateModifier";
        public static final int[] ATTENUATE_MODIFIER = {0, 40, 75, 160};
    }
}
