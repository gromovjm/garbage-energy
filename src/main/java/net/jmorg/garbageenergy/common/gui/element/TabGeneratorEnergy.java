package net.jmorg.garbageenergy.common.gui.element;

import cofh.api.tileentity.IEnergyInfo;
import cofh.core.gui.element.tab.TabEnergy;
import cofh.core.init.CoreTextures;
import cofh.core.util.helpers.StringHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.jmorg.garbageenergy.common.gui.client.GeneratorGui;

import java.util.List;

public class TabGeneratorEnergy extends TabEnergy
{
    IEnergyInfo energyStorage;
    String perTick = " " + StringHelper.localize("info.garbageenergy.generator.rfFromItem");

    public TabGeneratorEnergy(GeneratorGui guiBase, IEnergyInfo iEnergyInfo, boolean b)
    {
        super(guiBase, iEnergyInfo, b);
        energyStorage = iEnergyInfo;
    }

    @Override
    protected void drawForeground()
    {
        drawTabIcon(CoreTextures.ICON_ENERGY);
        if (!isFullyOpened()) {
            return;
        }

        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energy"), sideOffset() + 20, 6, headerColor);
        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyProduce") + ":", sideOffset() + 6, 18, subheaderColor);
        getFontRenderer().drawString(energyStorage.getInfoEnergyPerTick() + perTick, sideOffset() + 14, 30, textColor);
        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyMax") + ":", sideOffset() + 6, 42, subheaderColor);
        getFontRenderer().drawString(energyStorage.getInfoMaxEnergyPerTick() + perTick, sideOffset() + 14, 54, textColor);
        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.energyStored") + ":", sideOffset() + 6, 66, subheaderColor);
        getFontRenderer().drawString(energyStorage.getInfoEnergyStored() + " RF", sideOffset() + 14, 78, textColor);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void addTooltip(List<String> list)
    {
        if (!isFullyOpened()) {
            list.add(energyStorage.getInfoEnergyPerTick() + perTick);
        }
    }
}
