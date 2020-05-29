package net.jmorg.garbageenergy.common.gui;

import cofh.core.block.TileCoFHBase;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler
{
    public static final int TILE_GUI = 0;

    @Override
    public Object getServerGuiElement(int gui, EntityPlayer player, World world, int x, int y, int z)
    {
        if (gui == TILE_GUI) {
            TileCoFHBase tile = getTile(world, x, y, z);
            if (tile != null) {
                return tile.getGuiServer(player.inventory);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int gui, EntityPlayer player, World world, int x, int y, int z)
    {
        if (gui == TILE_GUI) {
            TileCoFHBase tile = getTile(world, x, y, z);
            if (tile != null) {
                return tile.getGuiClient(player.inventory);
            }
        }
        return null;
    }

    private TileCoFHBase getTile(World world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof TileCoFHBase) {
            return (TileCoFHBase) tile;
        }
        return null;
    }
}
