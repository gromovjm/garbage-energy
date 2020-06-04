package net.jmorg.garbageenergy.common.blocks;

import cofh.api.tileentity.IRedstoneControl;
import cofh.api.tileentity.ISecurable;
import cofh.core.block.BlockCoFHBase;
import cofh.core.util.CoreUtils;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import cofh.lib.util.helpers.SecurityHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.utils.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.util.ArrayList;

public abstract class BaseBlock extends BlockCoFHBase
{
    protected boolean basicGui = false;

    public static IIcon bottomSide;
    public static IIcon topSide;
    public static IIcon side;

    public BaseBlock(Material material)
    {
        super(material);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
    }

    public TileEntity getTile(World world, int x, int y, int z)
    {
        return world.getTileEntity(x, y, z);
    }

    public int getMetadata(World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x, y, z);
    }

    public static String getTileName(String tileName)
    {
        return "tile." + GarbageEnergy.MODID + "." + tileName + ".name";
    }

    @Override
    public IIcon getIcon(int blockSide, int metadata)
    {
        if (blockSide == 0) {
            return bottomSide;
        }
        if (blockSide == 1) {
            return topSide;
        }
        return side;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegistry)
    {
        bottomSide = iconRegistry.registerIcon(GarbageEnergy.MODID + ":bottom_side");
        topSide = iconRegistry.registerIcon(GarbageEnergy.MODID + ":top_side");
        side = iconRegistry.registerIcon(GarbageEnergy.MODID + ":side");
    }

    @Override
    public NBTTagCompound getItemStackTag(World world, int x, int y, int z)
    {
        TileEntity tile = getTile(world, x, y, z);

        NBTTagCompound retTag = null;

        if (tile instanceof BaseTile && (!((BaseTile) tile).tileName.isEmpty())) {
            retTag = ItemHelper.setItemStackTagName(retTag, ((BaseTile) tile).tileName);
        }
        if (tile instanceof TileInventory && ((TileInventory) tile).isSecured()) {
            retTag = SecurityHelper.setItemStackTagSecure(retTag, (ISecurable) tile);
        }
        if (tile instanceof IRedstoneControl) {
            retTag = RedstoneControlHelper.setItemStackTagRS(retTag, (IRedstoneControl) tile);
        }

        return retTag;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        BaseTile tile = (BaseTile) getTile(world, x, y, z);

        if (tile != null) {
            tile.setTileName(ItemHelper.getNameFromItemStack(stack));
            tile.updateFromNBT(stack.getTagCompound());
        }

        super.onBlockPlacedBy(world, x, y, z, living, stack);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int hitSide, float hitX, float hitY, float hitZ)
    {
        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, hitSide, world);
        if (MinecraftForge.EVENT_BUS.post(event)
                || event.getResult() == Event.Result.DENY
                || event.useBlock == Event.Result.DENY) {
            return false;
        }

        if (player.isSneaking()) {
            if (Utils.isHoldingUsableWrench(player, x, y, z)) {
                if (ServerHelper.isServerWorld(world) && canDismantle(player, world, x, y, z)) {
                    dismantleBlock(player, world, x, y, z, false);
                }
                Utils.usedWrench(player, x, y, z);
                return true;
            }
            return false;
        }

        BaseTile tile = (BaseTile) getTile(world, x, y, z);
        if (tile == null) {
            return false;
        }

        if (Utils.isHoldingUsableWrench(player, x, y, z)) {
            if (ServerHelper.isServerWorld(world)) {
                tile.onWrench(player, hitSide);
            }
            Utils.usedWrench(player, x, y, z);
            return true;
        }

        if (basicGui) {
            if (ServerHelper.isServerWorld(world)) {
                return tile.openGui(player);
            }
            return tile.hasGui();
        }

        return false;
    }

    @Override
    public ArrayList<ItemStack> dismantleBlock(EntityPlayer player, NBTTagCompound nbt, World world, int x, int y, int z, boolean returnDrops, boolean simulate)
    {
        TileEntity tile = getTile(world, x, y, z);
        int metadata = getMetadata(world, x, y, z);
        ItemStack dropBlock = new ItemStack(this, 1, metadata);

        if (nbt != null && !nbt.hasNoTags()) {
            dropBlock.setTagCompound(nbt);
        }
        if (!simulate) {
            if (tile instanceof BaseTile) {
                ((BaseTile) tile).blockDismantled();
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
                    CoreUtils.dismantleLog(player.getCommandSenderName(), this, metadata, x, y, z);
                }
            }
        }

        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(dropBlock);

        return ret;
    }
}
