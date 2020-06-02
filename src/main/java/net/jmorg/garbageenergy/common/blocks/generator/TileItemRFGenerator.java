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
    private static final int NUM_CONFIG = 3;
    private static final int[] SLOTS = new int[] {0};

    public TileItemRFGenerator()
    {
        super(BlockGenerator.Types.ITEM_RF);
        inventory = new ItemStack[SLOTS.length];
    }

    public static void initialize()
    {
        /*int type = BlockGenerator.Types.ITEM_RF.ordinal();

        defaultSideConfig[type] = new SideConfig();
        defaultSideConfig[type].numConfig = NUM_CONFIG;
        defaultSideConfig[type].slotGroups = new int[][] { {}, { 0 }, { 1 } };
        defaultSideConfig[type].allowInsertionSide = new boolean[] { false, true, false };
        defaultSideConfig[type].allowExtractionSide = new boolean[] { false, false, false };
        defaultSideConfig[type].allowInsertionSlot = new boolean[] { true, false };
        defaultSideConfig[type].allowExtractionSlot = new boolean[] { false, false };
        defaultSideConfig[type].sideTex = new int[] { 0, 1, 4, 7 };
        defaultSideConfig[type].defaultSides = new byte[] { 1, 1, 2, 2, 2, 2 };*/

        GameRegistry.registerTileEntity(TileItemRFGenerator.class, GarbageEnergy.MODID + ".Generator.ItemRFGenerator");
    }

    @Override
    public boolean canUpdate()
    {
        return true;
    }

    @Override
    protected boolean canGenerate()
    {
        return getEnergyValue(inventory[0]) > 0;
    }

    @Override
    protected void generate()
    {
        if (progress <= 0 && inventory[0] != null) {
            energyStorage.modifyEnergyStored(getEnergyOfItem());
            inventory[0] = ItemHelper.consumeItem(inventory[0]);
            progress = fuelValue = getEnergyValue(inventory[0]);
        } else {
            attenuate();
        }
    }

    @Override
    public int getScaledDuration(int scale)
    {
        fuelValue = getEnergyValue(inventory[0]);
        if (fuelValue <= 0) {
            return 0;
        }
        if (progress <= 0) {
            progress = fuelValue;
        }

        return MathHelper.round(progress * scale / fuelValue);
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
        return SLOTS;
    }

    //
    // ISidedInventory
    @Override
    public int getNumConfig(int side)
    {
        return NUM_CONFIG;
    }
}
