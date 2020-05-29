package net.jmorg.garbageenergy.common.bloks.generator;

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
        inventory = new ItemStack[3];
    }

    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileItemRFGenerator.class, GarbageEnergy.MODID + ".Generator.ItemRFGenerator");
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

    public static int getEnergyValue(ItemStack stack)
    {
        return 1;
    }
}
