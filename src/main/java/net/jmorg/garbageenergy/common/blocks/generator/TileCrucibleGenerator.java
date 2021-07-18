package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.core.util.core.EnergyConfig;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.MathHelper;
import cofh.redstoneflux.impl.EnergyStorage;
import net.jmorg.garbageenergy.GEProperties;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.gui.client.CrucibleGeneratorGui;
import net.jmorg.garbageenergy.common.gui.container.CrucibleGeneratorContainer;
import net.jmorg.garbageenergy.crafting.RecipeManager;
import net.jmorg.garbageenergy.utils.ItemFuelManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.HashSet;

public class TileCrucibleGenerator extends TileGeneratorBase
{
    public static boolean enable = true;

    ItemStack lastItem = ItemStack.EMPTY;
    static EnergyConfig energyConfig = new EnergyConfig();
    EnergyStorage energyStorage;

    public TileCrucibleGenerator()
    {
        // Energy storage
        energyStorage = new EnergyStorage(energyConfig.maxEnergy, energyConfig.maxPower * 2);

        // Inventory
        inventory = new ItemStack[2];
        Arrays.fill(inventory, ItemStack.EMPTY);

        // Augments
        Arrays.fill(augments, ItemStack.EMPTY);
    }

    protected static void config()
    {
        VALID_AUGMENTS[type] = new HashSet<>();

        enable = GarbageEnergy.config.get(GEProperties.CRUCIBLE_GENERATOR_CAT, "Enable", true);

        String comment = "Adjust this value to change the Process modifier (in one per tick) for a Crucible Generator. Reduces process time.";
        attenuateModifier = (float) GarbageEnergy.config.get(GEProperties.CRUCIBLE_GENERATOR_CAT, "AttenuateModifier", 0.01F, comment);

        comment = "Adjust this value to change the Energy amplifier (in RF) for a Crucible Generator. Increases output energy.";
        energyAmplifier = GarbageEnergy.config.get(GEProperties.CRUCIBLE_GENERATOR_CAT, "EnergyAmplifier", 1, comment);

        comment = "Adjust this value to change the Energy generation (in RF per item) for a Crucible Generator. This max value will scale with Augments.";
        int maxPower = GarbageEnergy.config.get(GEProperties.CRUCIBLE_GENERATOR_CAT, "MaxPower", 8, comment);
        energyConfig.setDefaultParams(maxPower);
    }

    public static void initialize()
    {
        type = BlockGenerator.Types.CRUCIBLE.getMetadata();
        name = GarbageEnergy.MODID + ":" + BlockGenerator.Types.CRUCIBLE.getName();

        register(name, TileCrucibleGenerator.class);

        config();
    }

    @Override
    protected boolean canGenerate()
    {
        return getEnergyValue(inventory[0]) > 0;
    }

    @Override
    protected void generate()
    {
        if (lastItem != ItemStack.EMPTY && !lastItem.isItemEqual(inventory[0]) || progress == 0) {
            progress = fuelValue = getEnergyValue(inventory[0]);
        }

        if (progress <= 0) {
            getEnergyStorage().modifyEnergyStored(getEnergyOfItem());
            inventory[0] = ItemHelper.consumeItem(inventory[0]);
            progress = 0;
        } else {
            attenuate();
        }

        lastItem = inventory[0];
    }

    private float getEnergyValue(ItemStack stack)
    {
        if (stack == ItemStack.EMPTY) {
            return 0F;
        }

        String itemId = RecipeManager.itemName(stack);

        if (ItemFuelManager.isBurnable(itemId)) {
            return (float) ItemFuelManager.getBurningTime(itemId);
        }

//        if (inventory[1] != null) {
//            List<HashMap<String, String>> items = ItemDataCardManager.getItems(inventory[1]);
//            for (HashMap<String, String> item : items) {
//                if (item.get("Id").equals(itemId)) {
//                    return Float.parseFloat(item.get("EnergyModifier"));
//                }
//            }
//        }

        return 0F;
    }

    @Override
    public int getScaledDuration(int scale)
    {
        if (fuelValue <= 0 && progress <= 0) {
            return 0;
        }

        return Math.max(MathHelper.round(progress * scale / fuelValue), 4);
    }

    @Override
    public EnergyStorage getEnergyStorage()
    {
        return energyStorage;
    }

    @Override
    public EnergyConfig getEnergyConfig()
    {
        return energyConfig;
    }

    //
    // Gui methods.
    @Override
    public Object getGuiClient(InventoryPlayer inventory)
    {
        return new CrucibleGeneratorGui(inventory, this);
    }

    @Override
    public Object getGuiServer(InventoryPlayer inventory)
    {
        return new CrucibleGeneratorContainer(inventory, this);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return getEnergyValue(stack) > 0;
    }

    //
    // IAccelerable
    @Override
    public int updateAccelerable()
    {
        return 0;
    }
}
