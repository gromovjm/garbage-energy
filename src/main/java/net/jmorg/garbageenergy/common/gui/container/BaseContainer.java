package net.jmorg.garbageenergy.common.gui.container;

import cofh.lib.gui.container.ContainerBase;
import net.jmorg.garbageenergy.common.blocks.BaseTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;

public class BaseContainer extends ContainerBase
{
    public final BaseTile baseTile;

    protected boolean hasPlayerInvSlots = true;

    public BaseContainer()
    {
        baseTile = null;
    }

    public BaseContainer(BaseTile tile)
    {
        baseTile = tile;
    }

    public BaseContainer(InventoryPlayer inventory, BaseTile tile)
    {
        this(inventory, tile, true);
    }

    public BaseContainer(InventoryPlayer inventory, BaseTile tile, boolean playerInvSlots)
    {
        baseTile = tile;
        hasPlayerInvSlots = playerInvSlots;

        /* Player Inventory */
        if (hasPlayerInvSlots) {
            bindPlayerInventory(inventory);
        }
    }

    @Override
    protected int getPlayerInventoryVerticalOffset()
    {
        return 84;
    }

    @Override
    protected int getSizeInventory()
    {
        if (baseTile instanceof IInventory) {
            return ((IInventory) baseTile).getSizeInventory();
        }
        return 0;
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityPlayer)
    {
        return baseTile == null || baseTile.isUseable(entityPlayer);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (baseTile == null) {
            return;
        }
        for (Object crafter : crafters) {
            baseTile.sendGuiNetworkData(this, (ICrafting) crafter);
        }
    }

    @Override
    public void updateProgressBar(int i, int j)
    {
        if (baseTile == null) {
            return;
        }
        baseTile.receiveGuiNetworkData(i, j);
    }
}
