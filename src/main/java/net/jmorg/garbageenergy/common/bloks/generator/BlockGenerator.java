package net.jmorg.garbageenergy.common.bloks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.bloks.BaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockGenerator extends BaseBlock
{
    public static ItemStack core;
    public static ItemStack receiver;
    public static ItemStack transmitter;

    public BlockGenerator()
    {
        super(Material.rock);
        setBlockName(GarbageEnergy.MODID + ".generator");
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        setHardness(15.0F);
        setResistance(25.0F);
    }

    @Override
    public boolean initialize()
    {
        TileCore.initialize();
        TileReceiver.initialize();
        TileTransmitter.initialize();

        core = ItemBlockGenerator.setDefaultTag(new ItemStack(this, 1, Types.CORE.ordinal()));
        receiver = ItemBlockGenerator.setDefaultTag(new ItemStack(this, 1, Types.RECEIVER.ordinal()));
        transmitter = ItemBlockGenerator.setDefaultTag(new ItemStack(this, 1, Types.TRANSMITTER.ordinal()));

        GameRegistry.registerCustomItemStack("core", core);
        GameRegistry.registerCustomItemStack("receiver", receiver);
        GameRegistry.registerCustomItemStack("transmitter", transmitter);

        return false;
    }

    @Override
    public boolean postInit()
    {
        return false;
    }

    public static void refreshItemStacks()
    {
        core = ItemBlockGenerator.setDefaultTag(core);
        receiver = ItemBlockGenerator.setDefaultTag(receiver);
        transmitter = ItemBlockGenerator.setDefaultTag(transmitter);
    }

    public static String getTileName(String tileName)
    {
        return BaseBlock.getTileName("generator." + tileName);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
        for (int i = 0; i < Types.values().length; i++) {
            list.add(ItemBlockGenerator.setDefaultTag(new ItemStack(item, 1, i)));
        }
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        if (metadata >= Types.values().length) return null;

        switch (Types.values()[metadata]) {
            case CORE:
                return new TileCore();
            case RECEIVER:
                return new TileReceiver();
            case TRANSMITTER:
                return new TileTransmitter();
            default:
                return null;
        }
    }

    @Override
    public int getRenderBlockPass()
    {
        return 1;
    }

    @Override
    public boolean canRenderInPass(int pass)
    {
        renderPass = pass;
        return pass < 2;
    }

    @Override
    public boolean isNormalCube(IBlockAccess world, int x, int y, int z)
    {
        return false;
    }

    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
    {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return true;
    }

    @Override
    public NBTTagCompound getItemStackTag(World world, int x, int y, int z)
    {
        NBTTagCompound tag = super.getItemStackTag(world, x, y, z);
        TileGeneratorBase tile = (TileGeneratorBase) getTile(world, x, y, z);

        if (tag != null && tile instanceof TileCore) {
            tag.setInteger("count", ((TileCore) tile).count);
        }

        return tag;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase living, ItemStack stack)
    {
        ((TileGeneratorBase) getTile(world, x, y, z)).updateFromNBT(stack.getTagCompound());
        super.onBlockPlacedBy(world, x, y, z, living, stack);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (((TileGeneratorBase) getTile(world, x, y, z)).onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ)) return true;
        return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
    }

    public static final String[] NAMES = {"core", "receiver", "transmitter"};

    public enum Types
    {
        CORE,
        RECEIVER,
        TRANSMITTER;
    }
}
