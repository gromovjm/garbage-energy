package net.jmorg.garbageenergy.common.gui.container;

import cofh.core.gui.container.ContainerTileAugmentable;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.slot.ISlotValidator;
import cofh.core.gui.slot.SlotValidated;
import net.jmorg.garbageenergy.common.blocks.generator.TileCrucibleGenerator;
import net.jmorg.garbageenergy.common.gui.element.SlotDataCard;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class CrucibleGeneratorContainer extends ContainerTileAugmentable implements IAugmentableContainer, ISlotValidator
{
    public CrucibleGeneratorContainer(InventoryPlayer inventory, TileCrucibleGenerator tile)
    {
        super(inventory, tile);

        addSlotToContainer(new SlotValidated(this, tile, 0, 44, 35));
        addSlotToContainer(new SlotDataCard(tile, 1, 152, 53));
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return ((TileCrucibleGenerator) baseTile).isItemValidForSlot(0, itemStack);
    }
}
