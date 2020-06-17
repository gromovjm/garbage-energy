package net.jmorg.garbageenergy.common.gui.container.scanner;

import cofh.lib.gui.slot.ISlotValidator;
import cofh.lib.gui.slot.SlotEnergy;
import cofh.lib.gui.slot.SlotValidated;
import cpw.mods.fml.relauncher.Side;
import net.jmorg.garbageenergy.common.blocks.scanner.TileItemScanner;
import net.jmorg.garbageenergy.common.gui.container.BaseContainer;
import net.jmorg.garbageenergy.common.gui.element.SlotDataCard;
import net.jmorg.garbageenergy.utils.IDataCardManageable;
import net.jmorg.garbageenergy.utils.ItemDataCardManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ItemScannerContainer extends BaseContainer implements ISlotValidator, IDataCardManageable
{
    TileItemScanner itemScannerTile;

    public ItemScannerContainer(InventoryPlayer inventory, TileItemScanner tile)
    {
        super(inventory, tile);

        itemScannerTile = tile;
        addSlotToContainer(new SlotValidated(this, itemScannerTile, 0, 43, 38));
        addSlotToContainer(new SlotDataCard(itemScannerTile, 1, 188, 67));
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

    @Override
    public void writeDataCard()
    {
        ItemStack dataCard = itemScannerTile.getStackInSlot(1);
        String itemId = itemScannerTile.item[0];
        String itemDisplayName = itemScannerTile.item[1];
        float itemEnergyModifier = itemScannerTile.itemEnergyModifier;

        if (dataCard != null && itemScannerTile.finished
                && (itemId != null || itemDisplayName != null || itemEnergyModifier > 0)
                && ItemDataCardManager.saveData(dataCard, itemId, itemDisplayName, itemEnergyModifier)) {
            itemScannerTile.resetResult(false);
            itemScannerTile.sendUpdatePacket(Side.CLIENT);
        }
    }
}
