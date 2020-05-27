package net.jmorg.garbageenergy.common.bloks.generator;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.AugmentHelper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockGenerator extends ItemBlockBase
{
    public static ItemStack setDefaultTag(ItemStack container)
    {
        return setDefaultTag(container, (byte) 0);
    }

    public static ItemStack setDefaultTag(ItemStack container, byte level)
    {
        RedstoneControlHelper.setControl(container, IRedstoneControl.ControlMode.DISABLED);
        EnergyHelper.setDefaultEnergyTag(container, 0);
        container.stackTagCompound.setByte("Level", level);
        AugmentHelper.writeAugments(container, BlockGenerator.defaultAugments);

        return container;
    }

    public ItemBlockGenerator(Block block)
    {
        super(block);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return "tile." + GarbageEnergy.MODID + ".generator." + BlockGenerator.NAMES[ItemHelper.getItemDamage(stack)] + ".name";
    }
}
