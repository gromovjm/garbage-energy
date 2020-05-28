package net.jmorg.garbageenergy.common.items.tool;

import cofh.api.block.IDismantleable;
import cofh.api.item.IToolHammer;
import cofh.lib.util.helpers.BlockHelper;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.common.eventhandler.Event.Result;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class WrenchTool extends ToolGeneric implements IToolHammer
{
    public WrenchTool()
    {
        setHarvestLevel("wrench", 1);
    }

    @Override
    public String getName()
    {
        return "wrench";
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        Block block = world.getBlock(x, y, z);
        if (block == null) return false;

        PlayerInteractEvent event = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, side, world);
        if (MinecraftForge.EVENT_BUS.post(event)
                || event.getResult() == Result.DENY
                || event.useBlock == Result.DENY
                || event.useItem == Result.DENY) {
            return false;
        }

        if (ServerHelper.isServerWorld(world) && player.isSneaking() && block instanceof IDismantleable
                && ((IDismantleable) block).canDismantle(player, world, x, y, z)) {
            ((IDismantleable) block).dismantleBlock(player, world, x, y, z, false);
            return true;
        }

        if (BlockHelper.canRotate(block)) {
            if (player.isSneaking()) {
                world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlockAlt(world, block, x, y, z), 3);
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.6F);
            } else {
                world.setBlockMetadataWithNotify(x, y, z, BlockHelper.rotateVanillaBlock(world, block, x, y, z), 3);
                world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, block.stepSound.getBreakSound(), 1.0F, 0.8F);
            }
            return ServerHelper.isServerWorld(world);
        } else if (!player.isSneaking() && block.rotateBlock(world, x, y, z, ForgeDirection.getOrientation(side))) {
            player.swingItem();
            return ServerHelper.isServerWorld(world);
        }

        return false;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int hitSide, float hitX, float hitY, float hitZ)
    {
        return true;
    }

    @Override
    public boolean isUsable(ItemStack itemStack, EntityLivingBase entityLivingBase, int i, int i1, int i2)
    {
        return false;
    }

    @Override
    public void toolUsed(ItemStack itemStack, EntityLivingBase entityLivingBase, int i, int i1, int i2)
    {

    }
}
