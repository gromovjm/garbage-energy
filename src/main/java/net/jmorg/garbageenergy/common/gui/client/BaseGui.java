package net.jmorg.garbageenergy.common.gui.client;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.gui.GuiBaseAdv;
import cofh.core.gui.element.TabInfo;
import cofh.core.gui.element.TabRedstone;
import cofh.core.gui.element.TabSecurity;
import cofh.core.gui.element.TabTutorial;
import cofh.lib.gui.element.TabBase;
import cofh.lib.util.helpers.SecurityHelper;
import net.jmorg.garbageenergy.common.blocks.TileInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class BaseGui extends GuiBaseAdv
{
    protected TileInventory tile;
    protected UUID playerUuid;

    public String myTutorial = "";

    protected TabBase redstoneTab;

    public BaseGui(Container container, TileInventory tile, EntityPlayer entityPlayer, ResourceLocation texture)
    {
        super(container, texture);

        this.tile = tile;
        name = tile.getInventoryName();
        playerUuid = SecurityHelper.getID(entityPlayer);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        if (tile.enableSecurity() && tile.isSecured()) {
            addTab(new TabSecurity(this, tile, playerUuid));
        }
        if (tile instanceof IRedstoneControl) {
            redstoneTab = addTab(new TabRedstone(this, (IRedstoneControl) tile));
        }
        if (!myInfo.isEmpty()) {
            addTab(new TabInfo(this, myInfo));
        }
        addTab(new TabTutorial(this, myTutorial));
    }
}
