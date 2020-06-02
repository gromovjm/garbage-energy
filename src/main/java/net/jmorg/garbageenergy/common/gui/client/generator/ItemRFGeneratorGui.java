package net.jmorg.garbageenergy.common.gui.client.generator;

import cofh.core.gui.element.TabEnergy;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.util.helpers.StringHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.TileItemRFGenerator;
import net.jmorg.garbageenergy.common.gui.client.BaseGui;
import net.jmorg.garbageenergy.common.gui.container.generator.ItemRFGeneratorContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemRFGeneratorGui extends BaseGui
{
    protected static final String TEXTURE = GarbageEnergy.MODID + ":textures/gui/generator/itemRf.png";

    TileItemRFGenerator tile;
    ElementDualScaled duration;

    public ItemRFGeneratorGui(InventoryPlayer inventory, TileItemRFGenerator tile)
    {
        super(new ItemRFGeneratorContainer(inventory, tile), tile, inventory.player, new ResourceLocation(TEXTURE));

        this.tile = tile;

        // Block GUI information
        this.myInfo = StringHelper.localize("tab.info." + GarbageEnergy.MODID + ".generator.0");
        generateInfo("tab.info." + GarbageEnergy.MODID + ".generator.itemRf", 3);

        // Block GUI tutorial
        this.myTutorial += StringHelper.localize("info.cofh.tutorial.tabRedstone") + "\n\n";
        if (tile.enableSecurity() && tile.isSecured()) {
            this.myTutorial += StringHelper.localize("info.cofh.tutorial.tabSecurity") + "\n\n";
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        //addTab(new TabGenerator(this, tile));

        addElement(new ElementEnergyStored(this, 80, 18, tile.getEnergyStorage()));
        if (tile.getMaxEnergyStored(ForgeDirection.UNKNOWN) > 0) {
            addTab(new TabEnergy(this, tile, true));
        }

        duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!tile.canAccess()) {
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    protected void updateElementInformation()
    {
        super.updateElementInformation();

        redstoneTab.setVisible(true);
        duration.setQuantity(tile.getScaledDuration(SPEED));
    }
}
