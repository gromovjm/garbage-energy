package net.jmorg.garbageenergy.common.gui.element;

import cofh.core.gui.element.TabConfiguration;
import cofh.lib.gui.GuiBase;
import cofh.lib.gui.element.TabBase;
import cofh.lib.render.RenderHelper;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.StringHelper;
import net.jmorg.garbageenergy.common.blocks.generator.TileGeneratorBase;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TabGenerator extends TabBase
{
    TileGeneratorBase tile;

    public TabGenerator(GuiBase gui, TileGeneratorBase tile)
    {
        super(gui, TabConfiguration.defaultSide);

        headerColor = TabConfiguration.defaultHeaderColor;
        subheaderColor = TabConfiguration.defaultSubHeaderColor;
        textColor = TabConfiguration.defaultTextColor;
        backgroundColor = TabConfiguration.defaultBackgroundColor;

        maxHeight = 92;
        maxWidth = 100;
        this.tile = tile;
    }

    @Override
    public void addTooltip(List<String> list)
    {
        if (!isFullyOpened()) {
            list.add(StringHelper.localize("info.cofh.configuration"));
        }
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int mouseButton)
    {
        if (!isFullyOpened()) {
            return false;
        }
        if (side == LEFT) {
            mouseX += currentWidth;
        }
        mouseX -= currentShiftX;
        mouseY -= currentShiftY;

        if (mouseX < 16 || mouseX >= 80 || mouseY < 20 || mouseY >= 84) {
            return false;
        }
        if (40 <= mouseX && mouseX < 56 && 24 <= mouseY && mouseY < 40) {
            handleSideChange(1, mouseButton);
        } else if (20 <= mouseX && mouseX < 36 && 44 <= mouseY && mouseY < 60) {
            handleSideChange(BlockHelper.SIDE_LEFT[tile.getFacing()], mouseButton);
        } else if (40 <= mouseX && mouseX < 56 && 44 <= mouseY && mouseY < 60) {
            handleSideChange(tile.getFacing(), mouseButton);
        } else if (60 <= mouseX && mouseX < 76 && 44 <= mouseY && mouseY < 60) {
            handleSideChange(BlockHelper.SIDE_RIGHT[tile.getFacing()], mouseButton);
        } else if (40 <= mouseX && mouseX < 56 && 64 <= mouseY && mouseY < 80) {
            handleSideChange(0, mouseButton);
        } else if (60 <= mouseX && mouseX < 76 && 64 <= mouseY && mouseY < 80) {
            handleSideChange(BlockHelper.SIDE_OPPOSITE[tile.getFacing()], mouseButton);
        }
        return true;
    }

    @Override
    protected void drawBackground()
    {
        super.drawBackground();

        if (!isFullyOpened()) {
            return;
        }
        float colorR = (backgroundColor >> 16 & 255) / 255.0F * 0.6F;
        float colorG = (backgroundColor >> 8 & 255) / 255.0F * 0.6F;
        float colorB = (backgroundColor & 255) / 255.0F * 0.6F;
        GL11.glColor4f(colorR, colorG, colorB, 1.0F);
        gui.drawTexturedModalRect(posX() + 16, posY + 20, 16, 20, 64, 64);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void drawForeground()
    {
        drawTabIcon("IconConfig");
        if (!isFullyOpened()) {
            return;
        }
        getFontRenderer().drawStringWithShadow(StringHelper.localize("info.cofh.configuration"), posXOffset() + 18, posY + 6, headerColor);
        RenderHelper.setBlockTextureSheet();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        gui.drawIcon(tile.getTexture(1, 0), posX() + 40, posY + 24, 0);
        gui.drawIcon(tile.getTexture(BlockHelper.SIDE_LEFT[tile.getFacing()], 0), posX() + 20, posY + 44, 0);
        gui.drawIcon(tile.getTexture(tile.getFacing(), 0), posX() + 40, posY + 44, 0);
        gui.drawIcon(tile.getTexture(BlockHelper.SIDE_RIGHT[tile.getFacing()], 0), posX() + 60, posY + 44, 0);
        gui.drawIcon(tile.getTexture(0, 0), posX() + 40, posY + 64, 0);
        gui.drawIcon(tile.getTexture(BlockHelper.SIDE_OPPOSITE[tile.getFacing()], 0), posX() + 60, posY + 64, 0);

        GL11.glDisable(GL11.GL_BLEND);
        RenderHelper.setDefaultFontTextureSheet();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    void handleSideChange(int side, int mouseButton)
    {
        if (GuiScreen.isShiftKeyDown()) {
            if (side == tile.getFacing()) {
                if (tile.resetSides()) {
                    GuiBase.playSound("random.click", 1.0F, 0.2F);
                }
            } else if (tile.setSide(side, 0)) {
                GuiBase.playSound("random.click", 1.0F, 0.4F);
            }
            return;
        }
        if (mouseButton == 0) {
            if (tile.incrSide(side)) {
                GuiBase.playSound("random.click", 1.0F, 0.8F);
            }
        } else if (mouseButton == 1) {
            if (tile.decrSide(side)) {
                GuiBase.playSound("random.click", 1.0F, 0.6F);
            }
        }
    }
}
