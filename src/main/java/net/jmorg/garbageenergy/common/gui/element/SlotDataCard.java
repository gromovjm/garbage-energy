package net.jmorg.garbageenergy.common.gui.element;

import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotDataCard extends SlotValidated
{
    public SlotDataCard(IInventory iInventory, int i, int i1, int i2)
    {
        super(new ISlotValidator()
        {
            @Override
            public boolean isItemValid(ItemStack itemStack)
            {
                //return itemStack.getItem() instanceof ItemDataCard;
                return false;
            }
        }, iInventory, i, i1, i2);
    }
}
