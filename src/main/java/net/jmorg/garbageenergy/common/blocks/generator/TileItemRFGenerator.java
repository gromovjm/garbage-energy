package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.gui.client.generator.ItemRFGeneratorGui;
import net.jmorg.garbageenergy.common.gui.container.generator.ItemRFGeneratorContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class TileItemRFGenerator extends TileGeneratorBase
{
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
        fuelValue = getEnergyValue(inventory[0]);

        if (fuelValue > 0 && progress == 0) {
            progress = fuelValue;
        }

        if (progress <= 0 && inventory[0] != null) {
            energyStorage.modifyEnergyStored(getEnergyOfItem());
            inventory[0] = ItemHelper.consumeItem(inventory[0]);
            progress = 0;
        } else {
            attenuate();
        }
    }

    @Override
    public int getScaledDuration(int scale)
    {
        return fuelValue > 0 ? MathHelper.round(progress * scale / fuelValue) : 0;
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

    //
    // ISidedInventory
    @Override
    public int getNumConfig(int side)
    {
        return 3;
    }
}
