package net.jmorg.garbageenergy.common.blocks;

import cofh.api.block.IConfigGui;
import cofh.core.block.BlockCoreTile;
import cofh.core.block.TileNameable;
import cofh.core.render.IModelRegister;
import cofh.core.util.RayTracer;
import cofh.core.util.helpers.ItemHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.core.util.helpers.WrenchHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

public abstract class GarbageEnergyBlock extends BlockCoreTile implements IConfigGui, IModelRegister
{
    protected boolean standardGui = true;
    protected boolean configGui = false;
    private static final List<GarbageEnergyBlock> blocks = new ArrayList<>();
    protected IBlockState state;

    public static BlockGenerator generator;

    public static void registerBlocks(IForgeRegistry<Block> registry)
    {
        generator = (BlockGenerator) registerBlock(registry, new BlockGenerator());

        GarbageEnergy.log.info("Blocks are registered.");
    }

    public static void registerRecipes()
    {
        for (GarbageEnergyBlock block : blocks) {
            block.initialize();
            block.addRecipes();
        }
    }

    protected abstract void addRecipes();

    @SideOnly(Side.CLIENT)
    public static void registerRenders()
    {
        for (GarbageEnergyBlock block : blocks) {
            block.registerModels();
        }
    }

    public static void postInit()
    {
        blocks.clear();
    }

    protected static Block registerBlock(IForgeRegistry<Block> registry, GarbageEnergyBlock block)
    {
        block.preInit();
        registry.register(block);
        blocks.add(block);
        return block;
    }

    protected GarbageEnergyBlock(Material material)
    {
        super(material, GarbageEnergy.MODID);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        state = getBlockState().getBaseState();
    }

    protected GarbageEnergyBlock(Material material, String modName)
    {
        super(material, modName);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
    }

    @Override
    public Block setUnlocalizedName(String name)
    {
        setRegistryName(name);
        return super.setUnlocalizedName(name);
    }

    public String getNameFromItemStack(ItemStack stack)
    {
        return null;
    }

    //
    // GUI methods
    @Override
    public boolean openConfigGui(World world, BlockPos pos, EnumFacing side, EntityPlayer player)
    {
        TileNameable tile = (TileNameable) world.getTileEntity(pos);

        if (tile == null || tile.isInvalid()) {
            return false;
        }
        if (configGui && ServerHelper.isServerWorld(world)) {
            return tile.openConfigGui(player);
        }
        return configGui;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase living, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileNameable) {
            ((TileNameable) tile).setCustomName(ItemHelper.getNameFromItemStack(stack));
        }
        super.onBlockPlacedBy(world, pos, state, living, stack);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        RayTraceResult traceResult = RayTracer.retrace(player);

        if (traceResult == null) {
            return false;
        }
        PlayerInteractEvent event = new PlayerInteractEvent.RightClickBlock(player, hand, pos, side, traceResult.hitVec);
        if (MinecraftForge.EVENT_BUS.post(event) || event.getResult() == Event.Result.DENY) {
            return false;
        }
        if (player.isSneaking() && WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
            if (ServerHelper.isServerWorld(world) && canDismantle(world, pos, state, player)) {
                dismantleBlock(world, pos, state, player, false);
                WrenchHelper.usedWrench(player, traceResult);
            }
            return true;
        }
        TileNameable tile = (TileNameable) world.getTileEntity(pos);

        if (tile == null || tile.isInvalid()) {
            return false;
        }
        if (WrenchHelper.isHoldingUsableWrench(player, traceResult)) {
            if (tile.canPlayerAccess(player)) {
                if (ServerHelper.isServerWorld(world)) {
                    tile.onWrench(player, side);
                }
                WrenchHelper.usedWrench(player, traceResult);
            }
            return true;
        }
        if (onBlockActivatedDelegate(world, pos, state, player, hand, side, hitX, hitY, hitZ)) {
            return true;
        }
        if (standardGui && ServerHelper.isServerWorld(world)) {
            return tile.openGui(player);
        }
        return standardGui;
    }

    public boolean onBlockActivatedDelegate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        return false;
    }
}
