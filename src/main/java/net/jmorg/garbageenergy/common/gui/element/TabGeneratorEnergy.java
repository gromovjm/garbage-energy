package net.jmorg.garbageenergy.common.gui.element;

import cofh.api.tileentity.IEnergyInfo;
import cofh.core.gui.element.TabEnergy;
import cofh.lib.gui.GuiBase;
import cofh.lib.util.helpers.StringHelper;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TabGeneratorEnergy extends TabEnergy
{
    IEnergyInfo energyStorage;
    String perTick = " " + StringHelper.localize("info.GarbageEnergy.generator.rfFromItem");

    public TabGeneratorEnergy(GuiBase guiBase, IEnergyInfo iEnergyInfo, boolean b)
    {
        super(guiBase, iEnergyInfo, b);
        energyStorage = iEnergyInfo;
    }

    @Override
    protected void drawForeground() {
        this.drawTabIcon("IconEnergy");
        if (this.isFullyOpened()) {
            this.getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energy"), this.posXOffset() + 20, this.posY + 6, this.headerColor);
            this.getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyProduce") + ":", this.posXOffset() + 6, this.posY + 18, this.subheaderColor);
            this.getFontRenderer().drawString(energyStorage.getInfoEnergyPerTick() + perTick, this.posXOffset() + 14, this.posY + 30, this.textColor);
            this.getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.maxEnergyPerTick") + ":", this.posXOffset() + 6, this.posY + 42, this.subheaderColor);
            this.getFontRenderer().drawString(energyStorage.getInfoMaxEnergyPerTick() + perTick, this.posXOffset() + 14, this.posY + 54, this.textColor);
            this.getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyStored") + ":", this.posXOffset() + 6, this.posY + 66, this.subheaderColor);
            this.getFontRenderer().drawString(energyStorage.getInfoEnergyStored() + " RF", this.posXOffset() + 14, this.posY + 78, this.textColor);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public void addTooltip(List<String> var1) {
        if (!this.isFullyOpened()) {
            var1.add(energyStorage.getInfoEnergyPerTick() + perTick);
        }
    }
}
