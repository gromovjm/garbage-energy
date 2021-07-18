package net.jmorg.garbageenergy.common.items;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.util.helpers.EnergyHelper;
import cofh.core.util.helpers.ReconfigurableHelper;
import cofh.core.util.helpers.RedstoneControlHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.blocks.generator.BlockGenerator;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;

public class ItemBlockGenerator extends ItemBlock
{
    public ItemBlockGenerator(Block block)
    {
        super(block);
    }

    @Override
    public void registerModels()
    {
        for (BlockGenerator.Types type : BlockGenerator.Types.values()) {
            ModelResourceLocation model = new ModelResourceLocation(GarbageEnergy.MODID + ":" + type.getName());
            ModelLoader.setCustomModelResourceLocation(this, type.getMetadata(), model);
        }
    }

    @Override
    public ItemStack setDefaultTag(ItemStack stack, int level)
    {
        ReconfigurableHelper.setFacing(stack, 1);
        RedstoneControlHelper.setControl(stack, IRedstoneControl.ControlMode.DISABLED);
        EnergyHelper.setDefaultEnergyTag(stack, 0);
        stack.getTagCompound().setByte("Level", (byte) level);

        return stack;
    }
}
