package net.jmorg.garbageenergy.common.blocks.scanner;

import cofh.api.tileentity.ISidedTexture;
import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.common.blocks.BaseTile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

public class TileItemScanner extends BaseTile implements ISidedTexture
{
    public static int type = BlockScanner.Types.ITEM.ordinal();

    boolean isActive = false;
    byte facing = 3;

    public TileItemScanner()
    {
        super();
    }

    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileItemScanner.class, BlockScanner.NAME + ".Item");
    }

    @Override
    public String getName()
    {
        return BlockScanner.getTileName(BlockScanner.NAMES[type]);
    }

    @Override
    public int getType()
    {
        return type;
    }

    @Override
    public IIcon getTexture(int side, int pass)
    {
        if (pass == 0) {
            if (side == 0) {
                return BlockScanner.bottomSide;
            } else if (side == 1) {
                return BlockScanner.topSide;
            }
            if (side == facing) {
                return isActive ? BlockScanner.faceActive[type] : BlockScanner.face[type];
            }
            return BlockScanner.side;
        }
        return BlockScanner.side;
    }

    @Override
    public boolean canPlayerDismantle(EntityPlayer entityPlayer)
    {
        return false;
    }
}
