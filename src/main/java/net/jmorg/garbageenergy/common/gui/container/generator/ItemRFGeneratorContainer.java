package net.jmorg.garbageenergy.common.gui.container.generator;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotValidated;
import net.jmorg.garbageenergy.common.blocks.generator.TileItemRFGenerator;
import net.jmorg.garbageenergy.common.gui.container.BaseContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ItemRFGeneratorContainer extends BaseContainer implements ISlotValidator
{
    TileItemRFGenerator itemRFGeneratorTile;

    public ItemRFGeneratorContainer(InventoryPlayer inventory, TileItemRFGenerator tile)
    {
        super(inventory, tile);

        itemRFGeneratorTile = tile;
        addSlotToContainer(new SlotValidated(this, itemRFGeneratorTile, 0, 44, 35));
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return TileItemRFGenerator.getEnergyValue(itemStack) > 0;
    }
}
