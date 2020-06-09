package net.jmorg.garbageenergy.utils;

import cofh.api.item.IToolHammer;
import cofh.api.transport.IItemDuct;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.InventoryHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class Utils
{
    public static boolean isHoldingUsableWrench(EntityPlayer player, int x, int y, int z)
    {
        Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
        if (equipped instanceof IToolHammer) {
            return ((IToolHammer) equipped).isUsable(player.getCurrentEquippedItem(), player, x, y, z);
        }
        return false;
    }

    public static void usedWrench(EntityPlayer player, int x, int y, int z)
    {
        Item equipped = player.getCurrentEquippedItem() != null ? player.getCurrentEquippedItem().getItem() : null;
        if (equipped instanceof IToolHammer) {
            ((IToolHammer) equipped).toolUsed(player.getCurrentEquippedItem(), player, x, y, z);
        }
    }

    public static boolean isAccessibleInput(TileEntity tile, int side)
    {
        if (tile instanceof ISidedInventory && ((ISidedInventory) tile).getAccessibleSlotsFromSide(BlockHelper.SIDE_OPPOSITE[side]).length <= 0) {
            return false;
        }
        return tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0;
    }

    public static boolean isAccessibleOutput(TileEntity tile, int side)
    {
        if (tile instanceof ISidedInventory && ((ISidedInventory) tile).getAccessibleSlotsFromSide(BlockHelper.SIDE_OPPOSITE[side]).length <= 0) {
            return false;
        }
        if (tile instanceof IInventory && ((IInventory) tile).getSizeInventory() > 0) {
            return true;
        }
        return tile instanceof IItemDuct;
    }

    public static int addToInsertion(TileEntity theTile, int from, ItemStack stack)
    {
        if (!(InventoryHelper.isInsertion(theTile))) {
            return stack.stackSize;
        }
        stack = InventoryHelper.addToInsertion(theTile, from, stack);

        return stack == null ? 0 : stack.stackSize;
    }

    public static int addToInsertion(int xCoord, int yCoord, int zCoord, World worldObj, int from, ItemStack stack)
    {
        TileEntity theTile = worldObj.getTileEntity(xCoord, yCoord, zCoord);

        if (!InventoryHelper.isInsertion(theTile)) {
            return stack.stackSize;
        }
        stack = InventoryHelper.addToInsertion(theTile, from, stack);

        return stack == null ? 0 : stack.stackSize;
    }

    public static int addToInsertion(IInventory tile, int from, ItemStack stack)
    {
        if (!InventoryHelper.isInsertion(tile)) {
            return stack.stackSize;
        }
        stack = InventoryHelper.addToInsertion(tile, from, stack);

        return stack == null ? 0 : stack.stackSize;
    }

    public static String getItemUniqueId(Item item)
    {
        return GameRegistry.findUniqueIdentifierFor(item).toString();
    }
}
