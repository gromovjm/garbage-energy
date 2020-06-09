package net.jmorg.garbageenergy.common.gui.client.scanner;

import cofh.lib.gui.GuiColor;
import cofh.lib.gui.element.ElementButton;
import cofh.lib.gui.element.ElementDualScaled;
import cofh.lib.gui.element.ElementEnergyStored;
import cofh.lib.gui.element.ElementTextField;
import cofh.lib.util.helpers.StringHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.scanner.TileItemScanner;
import net.jmorg.garbageenergy.common.gui.client.BaseGui;
import net.jmorg.garbageenergy.common.gui.container.scanner.ItemScannerContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class ItemScannerGui extends BaseGui
{
    protected static final String TEXTURE = GarbageEnergy.MODID + ":textures/gui/scanner/item.png";

    TileItemScanner tile;
    ElementDualScaled duration;
    ElementButton saveResultButton;
    ElementButton resetButton;
    ElementTextField scanningResult;

    public ItemScannerGui(InventoryPlayer inventory, TileItemScanner tile)
    {
        super(new ItemScannerContainer(inventory, tile), tile, inventory.player, new ResourceLocation(TEXTURE));

        this.tile = tile;
        xSize = 212;
        ySize = 180;

        // Block GUI information
        generateInfo("info.tab." + GarbageEnergy.MODID + ".scanner.item", 3);

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
        addElement(new ElementEnergyStored(this, 8, 22, tile.getEnergyStorage()));
        duration = (ElementDualScaled) addElement(new ElementDualScaled(this, 43, 63)
                .setSize(16, 16)
                .setTexture(TEX_ALCHEMY, 32, 16)
        );
        saveResultButton = (ElementButton) addElement(new ElementButton(this, 186, 40, "SaveResult",
                216, 0,
                216, 20,
                216, 40,
                20, 20,
                TEXTURE)
                .setToolTip(StringHelper.localize("info.GarbageEnergy.scanner.saveResult"))
        );
        resetButton = (ElementButton) addElement(new ElementButton(this, 186, 18, "ResetResult",
                236, 0,
                236, 20,
                236, 40,
                20, 20,
                TEXTURE)
                .setToolTip(StringHelper.localize("info.GarbageEnergy.scanner.reset"))
        );
        scanningResult = (ElementTextField) addElement(new ElementTextField(this, 79, 18, 104, 64)
                .setBackgroundColor(5, 5, 5)
                .setTextColor(new GuiColor(245, 245, 245).getColor(), null)
                .setMultiline(true)
                .setFocusable(false)
                .setMaxLength((short) 100)
        );
    }

    public void handleElementButtonClick(String buttonName, int mouseButton)
    {
        if (buttonName.equals("SaveResult")) {

        }
        if (buttonName.equals("ResetResult")) {
            tile.reset();
        }
        playSound("random.click", 1.0F, 0.8F);
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
        duration.setQuantity(tile.getScaledProgress(SPEED));

        int progress = 0;
        if (tile.progressMax > 0) {
            progress = (int) (tile.progress * 100 / tile.progressMax);
        }

        saveResultButton.setEnabled(tile.finished);
        resetButton.setEnabled(tile.finished);
        if (tile.finished && !tile.item[1].equals("") && tile.energyModifier > 0) {
            scanningResult.setText("Item:\n  " + tile.item[1] + "\nEnergy modifier:\n  " + tile.getItemEnergyModifier());
        } else {
            scanningResult.setText("Scanning...\n" + tile.progress + " / " + tile.progressMax + "\n" + progress + "%");
        }
    }
}
