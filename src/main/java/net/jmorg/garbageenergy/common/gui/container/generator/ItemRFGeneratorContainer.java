package net.jmorg.garbageenergy.common.gui.container.generator;

import cofh.core.gui.slot.SlotAugment;
import cofh.lib.gui.container.IAugmentableContainer;
import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import cofh.lib.util.helpers.ServerHelper;
import net.jmorg.garbageenergy.common.blocks.generator.TileItemRFGenerator;
import net.jmorg.garbageenergy.common.gui.container.BaseContainer;
import net.jmorg.garbageenergy.common.gui.element.SlotDataCard;
import net.jmorg.garbageenergy.network.GarbageEnergyPacket;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ItemRFGeneratorContainer extends BaseContainer implements ISlotValidator, IAugmentableContainer
{
    TileItemRFGenerator itemRFGeneratorTile;
    SlotAugment[] augmentSlots;
    boolean augmentLock = true;

    public ItemRFGeneratorContainer(InventoryPlayer inventory, TileItemRFGenerator tile)
    {
        super(inventory, tile);

        itemRFGeneratorTile = tile;
        addSlotToContainer(new SlotValidated(this, itemRFGeneratorTile, 0, 44, 35));
        addSlotToContainer(new SlotDataCard(itemRFGeneratorTile, 1, 152, 53));
        addAugmentSlotsToContainer();
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return itemRFGeneratorTile.isItemValidForSlot(0, itemStack);
    }

    @Override
    public Slot[] getAugmentSlots()
    {
        return augmentSlots;
    }

    @Override
    public void setAugmentLock(boolean lock)
    {
        augmentLock = lock;

        if (ServerHelper.isClientWorld(itemRFGeneratorTile.getWorldObj())) {
            GarbageEnergyPacket.sendTabAugmentPacketToServer(lock);
        }
    }

    protected void addAugmentSlotsToContainer()
    {
        augmentSlots = itemRFGeneratorTile.augmentManager.getSlots();

        for (SlotAugment slot : augmentSlots) {
            addSlotToContainer(slot);
        }
    }
}
