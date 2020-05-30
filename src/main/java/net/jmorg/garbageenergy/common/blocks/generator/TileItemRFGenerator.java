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
        inventory = new ItemStack[1];
        attenuateModifier = 0.05F;
    }

    public static void initialize()
    {
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
            progress = fuelValue = getEnergyValue(inventory[0]);

            energyStorage.modifyEnergyStored(MathHelper.clamp(calcEnergy() * energyModifier, 1, config.maxPower));
            inventory[0] = ItemHelper.consumeItem(inventory[0]);
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
}
