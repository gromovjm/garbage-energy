package net.jmorg.garbageenergy.common.gui.container.scanner;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotSpecificItem;
import cofh.lib.gui.slot.SlotValidated;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.scanner.TileItemScanner;
import net.jmorg.garbageenergy.common.gui.container.BaseContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ItemScannerContainer extends BaseContainer implements ISlotValidator
{
    TileItemScanner itemScannerTile;

    public ItemScannerContainer(InventoryPlayer inventory, TileItemScanner tile)
    {
        super(inventory, tile);

        itemScannerTile = tile;
        addSlotToContainer(new SlotValidated(this, itemScannerTile, 0, 43, 38));
        addSlotToContainer(new SlotSpecificItem(itemScannerTile, 1,188, 67, GameRegistry.findItemStack(GarbageEnergy.MODID, "data_card", 1)));
        addSlotToContainer(new SlotEnergy(itemScannerTile, itemScannerTile.getChargeSlot(), 8, 67));
    }

    @Override
    public boolean isItemValid(ItemStack itemStack)
    {
        return itemScannerTile.isItemValidForSlot(0, itemStack);
    }

    @Override
    protected int getPlayerInventoryHorizontalOffset()
    {
        return 26;
    }

    @Override
    protected int getPlayerInventoryVerticalOffset()
    {
        return 98;
    }
}
