package net.jmorg.garbageenergy.common.bloks;

import cofh.core.block.TileCoFHBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class BaseTile extends TileCoFHBase
{
    public String tileName = "";

    public boolean setTileName(String name)
    {
        if (name.isEmpty()) {
            return false;
        }
        tileName = name;
        return true;
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        return false;
    }

    public boolean hasGui()
    {
        return false;
    }
}
