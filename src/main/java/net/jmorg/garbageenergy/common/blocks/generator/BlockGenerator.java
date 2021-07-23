package net.jmorg.garbageenergy.common.blocks.generator;

import cofh.core.init.CoreProps;
import cofh.core.util.helpers.ItemHelper;
import net.jmorg.garbageenergy.GEProperties;
import net.jmorg.garbageenergy.common.blocks.GarbageEnergyBlock;
import net.jmorg.garbageenergy.common.items.GarbageEnergyItem;
import net.jmorg.garbageenergy.common.items.ItemBlockGenerator;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nullable;

public class BlockGenerator extends GarbageEnergyBlock implements ITileEntityProvider
{
    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyEnum<Types> VARIANT = PropertyEnum.create("type", Types.class);
    public static final PropertyBool ACTIVE = PropertyBool.create("active");

    public static boolean[] enable = new boolean[Types.values().length];
    private static boolean keepInventory;

    public ItemStack crucible;

    public BlockGenerator()
    {
        super(Material.IRON);

        setUnlocalizedName("generator");
        setHardness(15.0F);
        setResistance(25.0F);

        setDefaultState(state
                .withProperty(FACING, EnumFacing.NORTH)
                .withProperty(VARIANT, Types.CRUCIBLE)
                .withProperty(ACTIVE, false)
        );
    }

    @Override
    public boolean preInit()
    {
        TileGeneratorBase.configure();

        // Initialize generators
        TileCrucibleGenerator.initialize();
        enable[Types.CRUCIBLE.getMetadata()] = TileCrucibleGenerator.enable;

        return true;
    }

    @Override
    public boolean initialize()
    {
        crucible = GarbageEnergyItem.blockGenerator.setDefaultTag(new ItemStack(this, 1, Types.CRUCIBLE.getMetadata()));

        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, VARIANT, ACTIVE);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        ItemBlockGenerator itemBlock = GarbageEnergyItem.blockGenerator;
        int i;

        for (Types type : Types.values()) {
            i = type.getMetadata();

            if (!enable[i]) continue;

            if (GEProperties.creativeTabShowAllBlockLevels) {
                for (int j = 0; j <= CoreProps.LEVEL_MAX; j++) {
                    items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i), j));
                }
            } else {
                items.add(itemBlock.setDefaultTag(new ItemStack(this, 1, i), GEProperties.creativeTabLevel));
            }
            if (GEProperties.creativeTabShowCreative) {
                items.add(itemBlock.setCreativeTag(new ItemStack(this, 1, i)));
            }
        }
    }


    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        if (Types.CRUCIBLE.getMetadata() == meta) {
            return new TileCrucibleGenerator();
        }

        return null;
    }

    @Override
    public void registerModels()
    {
        for (Types type : Types.values()) {
            ModelResourceLocation model = new ModelResourceLocation(modName + ":" + type);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.getMetadata(), model);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return getUnlocalizedName() + "." + getNameFromItemStack(stack) + ".name";
    }

    @Override
    public String getNameFromItemStack(ItemStack stack)
    {
        return Types.values()[ItemHelper.getItemDamage(stack)].getName();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    public static void setState(boolean active, World worldIn, BlockPos pos)
    {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        keepInventory = true;

        worldIn.setBlockState(pos, GarbageEnergyBlock.generator.getDefaultState().withProperty(ACTIVE, active));

        keepInventory = false;

        if (tileEntity != null)
        {
            tileEntity.validate();
            worldIn.setTileEntity(pos, tileEntity);
        }
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack)
    {
        if (stack.getTagCompound() != null) {
            TileGeneratorBase tile = (TileGeneratorBase) world.getTileEntity(pos);

//            tile.setLevel(stack.getTagCompound().getByte("Level"));
            tile.readAugmentsFromNBT(stack.getTagCompound());
            tile.updateAugmentStatus();
            tile.setEnergyStored(stack.getTagCompound().getInteger(CoreProps.ENERGY));
        }

        super.onBlockPlacedBy(world, pos, state, living, stack);
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!keepInventory)
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileGeneratorBase)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (TileGeneratorBase) tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Override
    protected void addRecipes()
    {
        // null
    }

    public enum Types implements IStringSerializable
    {
        CRUCIBLE(0, "crucible"),
        ;

        Types(int metadata, String name)
        {
            this.metadata = metadata;
            this.name = name;
        }

        public int getMetadata()
        {
            return this.metadata;
        }

        @Override
        public String getName()
        {
            return this.name;
        }

        private final int metadata;
        private final String name;
    }
}
