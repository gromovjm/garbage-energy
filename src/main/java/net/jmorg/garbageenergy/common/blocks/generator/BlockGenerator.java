package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.api.tileentity.ISidedTexture;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.BaseBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class BlockGenerator extends BaseBlock
{
    public static IIcon[] face = new IIcon[Types.values().length];
    public static IIcon[] faceActive = new IIcon[Types.values().length];
    public static IIcon generatorBottomSide;
    public static IIcon generatorTopSide;
    public static IIcon generatorSide;
    public static IIcon generatorOppositeSide;

    public static ItemStack itemRF;
    public static ItemStack receiver;
    public static ItemStack transmitter;

    public BlockGenerator()
    {
        super(Material.rock);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        setHardness(15.0F);
        setResistance(25.0F);

        basicGui = true;
    }

    @Override
    public boolean initialize()
    {
        TileGeneratorBase.configure();
        TileItemRFGenerator.initialize();

        itemRF = ItemBlockGenerator.setDefaultTag(new ItemStack(this, 1, Types.ITEM_RF.ordinal()));
        GameRegistry.registerCustomItemStack("item_rf", itemRF);

        return false;
    }

    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side)
    {
        ISidedTexture tile = (ISidedTexture) world.getTileEntity(x, y, z);
        return tile == null ? null : tile.getTexture(side, renderPass);
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        if (side == 0) {
            return generatorBottomSide;
        }
        if (side == 1) {
            return generatorTopSide;
        }
        if (side == 2) {
            return generatorOppositeSide;
        }
        return side != 3 ? generatorSide : face[metadata % Types.values().length];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister ir)
    {
        // Base Textures
        generatorBottomSide = ir.registerIcon(GarbageEnergy.MODID + ":generator/bottom_side");
        generatorTopSide = ir.registerIcon(GarbageEnergy.MODID + ":generator/top_side");
        generatorSide = ir.registerIcon(GarbageEnergy.MODID + ":generator/side");
        generatorOppositeSide = ir.registerIcon(GarbageEnergy.MODID + ":generator/opposite_side");

        // Face Textures
        for (int i = 0; i < Types.values().length; i++) {
            face[i] = ir.registerIcon(GarbageEnergy.MODID + ":generator/face_" + NAMES[i]);
            faceActive[i] = ir.registerIcon(GarbageEnergy.MODID + ":generator/active_face_" + NAMES[i]);
        }
    }

    @Override
    public boolean postInit()
    {
        return false;
    }

    public static void refreshItemStacks()
    {
        itemRF = ItemBlockGenerator.setDefaultTag(itemRF);
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
            case ITEM_RF:
                return new TileItemRFGenerator();
            case RECEIVER:
                return null;
            case TRANSMITTER:
                return null;
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
        return pass < 1;
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

        if (tag != null && tile != null) {
            tag.setInteger("Energy", tile.getEnergyStored(ForgeDirection.UNKNOWN));
        }

        return tag;
    }

    public static final String[] NAMES = {"itemRf", "receiver", "transmitter"};

    public enum Types
    {
        ITEM_RF,
        RECEIVER,
        TRANSMITTER;
    }
}
