package net.jmorg.garbageenergy.common.gui.client;

import cofh.core.gui.GuiContainerCore;
import cofh.core.gui.container.IAugmentableContainer;
import cofh.core.gui.element.tab.*;
import cofh.core.util.helpers.SecurityHelper;
import net.jmorg.garbageenergy.common.blocks.generator.TileGeneratorBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public abstract class GeneratorGui extends GuiContainerCore
{
    protected static String texturePath = "minecraft:textures/gui/demo_background.png";
    protected UUID playerUuid;

    protected TileGeneratorBase tile;

    protected TabBase augmentTab;
    protected TabBase redstoneTab;
    protected TabBase configTab;

    String myTutorial = "";

    public GeneratorGui(Container container, TileGeneratorBase tile, EntityPlayer entityPlayer)
    {
        super(container, new ResourceLocation(texturePath));

        this.tile = tile;

        playerUuid = SecurityHelper.getID(entityPlayer);
        name = tile.getName();
    }

    public void setTile(TileGeneratorBase tile)
    {
        this.tile = tile;
    }

    @Override
    public void initGui()
    {
        super.initGui();
        initCommonTabs();
    }

    protected void initCommonTabs()
    {
        augmentTab = addTab(new TabAugment(this, (IAugmentableContainer) inventorySlots));
        if (tile.enableSecurity() && tile.isSecured()) {
            addTab(new TabSecurity(this, tile, playerUuid));
        }
        redstoneTab = addTab(new TabRedstoneControl(this, tile));
        if (!myInfo.isEmpty()) {
            addTab(new TabInfo(this, myInfo));
        }
        addTab(new TabTutorial(this, myTutorial));
        //configTab = addTab(new TabConfigurationTransfer(this, (IReconfigurableSides) tile));
    }
}
