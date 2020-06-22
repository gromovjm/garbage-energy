package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.gui.client.generator.ItemRFGeneratorGui;
import net.jmorg.garbageenergy.common.gui.container.generator.ItemRFGeneratorContainer;
import net.jmorg.garbageenergy.crafting.RecipeManager;
import net.jmorg.garbageenergy.utils.ItemDataCardManager;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.List;

public class TileItemRFGenerator extends TileGeneratorBase
{
    private ItemStack lastItem;

    public TileItemRFGenerator()
    {
        super(BlockGenerator.Types.ITEM_RF);
        inventory = new ItemStack[2];
    }

    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileItemRFGenerator.class, GarbageEnergy.MODID + ".Generator.ItemRFGenerator");
    }

    @Override
    protected boolean canGenerate()
    {
        return getEnergyValue(inventory[0]) > 0;
    }

    @Override
    protected void generate()
    {
        if (lastItem != null && !lastItem.isItemEqual(inventory[0]) || progress == 0) {
            progress = fuelValue = getEnergyValue(inventory[0]);
        }

        if (progress <= 0) {
            energyStorage.modifyEnergyStored(getEnergyOfItem());
            inventory[0] = ItemHelper.consumeItem(inventory[0]);
            progress = 0;
        } else {
            attenuate();
        }

        lastItem = inventory[0];
    }

    private float getEnergyValue(ItemStack stack)
    {
        if (stack == null) {
            return 0F;
        }

        String itemId = RecipeManager.itemName(stack);

        if (ItemFuelManager.isBurnable(itemId)) {
            return (float) ItemFuelManager.getBurningTime(itemId);
        }

        if (inventory[1] != null) {
            List<HashMap<String, String>> items = ItemDataCardManager.getItems(inventory[1]);
            for (HashMap<String, String> item : items) {
                if (item.get("Id").equals(itemId)) {
                    return Float.parseFloat(item.get("EnergyModifier"));
                }
            }
        }

        return 0F;
    }

    @Override
    public int getScaledDuration(int scale)
    {
        return fuelValue > 0 && progress > 0 ? MathHelper.round(progress * scale / fuelValue) : 0;
    }

    //
    // Gui methods.
    @Override
    public Object getGuiClient(InventoryPlayer inventory)
    {
        return new ItemRFGeneratorGui(inventory, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory)
    {
        return new ItemRFGeneratorContainer(inventory, this);
    }

    //
    // IInventory
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[] {0};
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return getEnergyValue(stack) > 0;
    }

    //
    // ISidedInventory
    @Override
    public int getNumConfig(int side)
    {
        return 3;
    }
}
