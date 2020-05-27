package net.jmorg.garbageenergy.common.bloks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.bloks.BaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
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
        super(Material.iron);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        setHardness(15.0F);
        setResistance(25.0F);
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
        TileGeneratorBase tile = (TileGeneratorBase) world.getTileEntity(x, y, z);

        if (tile != null) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
        }

        return tag;
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

    public enum Types {
        CORE, RECEIVER, TRANSMITTER
    }

    public static final String[] NAMES = {"core", "receiver", "transmitter"};
    public static ItemStack[] defaultAugments = new ItemStack[3];
}
