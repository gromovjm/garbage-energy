package net.jmorg.garbageenergy.common.gui.client.scanner;

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
        scanningResult = (ElementTextField) addElement(new ElementTextField(this, 79, 19, 104, 64)
                .setMultiline(true)
                .setFocusable(false)
                .setMaxLength((short) 255)
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

        saveResultButton.setEnabled(tile.finished);
        resetButton.setEnabled(tile.finished);
        redstoneTab.setVisible(true);
        duration.setQuantity(tile.getScaledProgress(SPEED));

        String scanningText = "";
        String divider = "----------------------------------\n";
        String itemName = tile.item[1];
        double energyModifier = Math.floor(tile.energyModifier * 100) / 100;
        int progress = 0;
        if (tile.progressMax > 0) {
            progress = (int) (tile.progress * 100 / tile.progressMax);
        }

        if (tile.finished && !itemName.equals("") && energyModifier > 0) {
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.scanned") + "\n";
            scanningText += divider;
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.item") + ": " + itemName + "\n";
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.energyModifier") + ": " + energyModifier + "\n";
        } else if (!tile.finished && tile.isActive) {
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.scanning") + "\n";
            scanningText += divider;
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.process") + ": " + progress + "%\n";
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.leftTime") + ": " + getLeftTime() + "\n";
        } else {
            scanningText += StringHelper.localize("info.GarbageEnergy.scanner.idle") + "\n";
            scanningText += divider;
            if (!tile.isActive) {
                scanningText += StringHelper.localize("info.GarbageEnergy.scanner.idle.disabled") + "\n";
            } else if (tile.inventory[0] != null) {
                scanningText += StringHelper.localize("info.GarbageEnergy.scanner.idle.nei") + "\n";
            }
        }

        scanningResult.setText(scanningText);
    }

    private String getLeftTime()
    {
        String leftTime = "";
        long leftSeconds = (tile.progressMax - tile.progress) / 20;
        int seconds = (int) (leftSeconds % 59);
        long leftMinutes = (leftSeconds - seconds) / 59;
        int minutes = (int) (leftMinutes % 59);
        int hours = (int) ((leftMinutes - minutes) / 23);

        if (String.valueOf(hours).length() < 2) {
            leftTime += "0" + hours + ":";
        } else {
            leftTime += hours + ":";
        }
        if (String.valueOf(minutes).length() < 2) {
            leftTime += "0" + minutes + ":";
        } else {
            leftTime += minutes + ":";
        }
        if (String.valueOf(seconds).length() < 2) {
            leftTime += "0" + seconds;
        } else {
            leftTime += seconds;
        }

        return leftTime;
    }
}
