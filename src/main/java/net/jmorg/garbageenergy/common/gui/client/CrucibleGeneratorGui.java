package net.jmorg.garbageenergy.common.gui.client;

import cofh.core.gui.element.ElementDualScaled;
import cofh.core.gui.element.ElementEnergyStored;
import cofh.core.util.helpers.StringHelper;
import net.jmorg.garbageenergy.GEProperties;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.TileCrucibleGenerator;
import net.jmorg.garbageenergy.common.gui.container.CrucibleGeneratorContainer;
import net.jmorg.garbageenergy.common.gui.element.TabGeneratorEnergy;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;

public class CrucibleGeneratorGui extends GeneratorGui
{
    static {
        texturePath = GEProperties.TEXTURES_GUI_PATH + "generator/crucible.png";
    }

    ElementDualScaled duration;

    public CrucibleGeneratorGui(InventoryPlayer inventory, TileCrucibleGenerator tile)
    {
        super(new CrucibleGeneratorContainer(inventory, tile), tile, inventory.player);

        // Block GUI information
        this.myInfo = StringHelper.localize("info.tab." + GarbageEnergy.MODID + ".generator") + "\n\n";
        generateInfo("info.tab." + GarbageEnergy.MODID + ".generator.crucible");

        // Block GUI tutorial
        this.myTutorial += StringHelper.localize("info.cofh.tutorial.tabRedstone") + "\n\n";
        if (this.tile.enableSecurity() && this.tile.isSecured()) {
            this.myTutorial += StringHelper.localize("info.cofh.tutorial.tabSecurity") + "\n\n";
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();
        tabs.clear();

        addElement(new ElementEnergyStored(this, 80, 18, tile.getEnergyStorage()));
        duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 115, 35).setSize(16, 16).setTexture(TEX_FLAME, 32, 16));

        if (tile.getMaxEnergyStored(EnumFacing.VALUES[tile.getFacing()].getOpposite()) > 0) {
            addTab(new TabGeneratorEnergy(this, tile, true));
        }

        initCommonTabs();
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (!tile.canAccess()) {
            this.mc.player.closeScreen();
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
