package net.jmorg.garbageenergy.common.bloks;

import cofh.api.tileentity.ISecurable;
import cofh.core.block.BlockCoFHBase;
import cofh.core.block.TileCoFHBase;
import cofh.core.util.CoreUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class BaseBlock extends BlockCoFHBase
{
    public BaseBlock(Material material)
    {
        super(material);
    }

    public TileEntity getTileEntity(World world, int x, int y, int z)
    {
        return world.getTileEntity(x, y, z);
    }

    public int getMetadata(World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, World world, int x, int y, int z, boolean returnDrops, boolean simulate)
    {
        TileEntity tile = getTileEntity(world, x, y, z);
        int bMeta = getMetadata(world, x, y, z);

        ItemStack dropBlock = new ItemStack(this, 1, bMeta);

        if (nbt != null && !nbt.hasNoTags()) {
            dropBlock.setTagCompound(nbt);
        }

        if (!simulate) {
            if (tile instanceof TileCoFHBase) {
                ((TileCoFHBase) tile).blockDismantled();
            }
            world.setBlockToAir(x, y, z);

            if (!returnDrops) {
                float f = 0.3F;
                double x2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double y2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                double z2 = world.rand.nextFloat() * f + (1.0F - f) * 0.5D;
                EntityItem item = new EntityItem(world, x + x2, y + y2, z + z2, dropBlock);
                item.delayBeforeCanPickup = 10;
                if (tile instanceof ISecurable && !((ISecurable) tile).getAccess().isPublic()) {
                    item.func_145797_a(player.getCommandSenderName());
                    // set owner (not thrower) - ensures wrenching player can pick it up first
                }
                world.spawnEntityInWorld(item);

                if (player != null) {
                    CoreUtils.dismantleLog(player.getCommandSenderName(), this, bMeta, x, y, z);
                }
            }
        }

        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(dropBlock);

        return ret;
    }
}
