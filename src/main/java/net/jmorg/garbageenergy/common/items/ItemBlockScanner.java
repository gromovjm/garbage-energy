package net.jmorg.garbageenergy.common.items;

import cofh.api.tileentity.IRedstoneControl;
import cofh.core.item.ItemBlockBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.RedstoneControlHelper;
import net.jmorg.garbageenergy.common.blocks.scanner.BlockScanner;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockScanner extends ItemBlockBase
{
    public ItemBlockScanner(Block block)
    {
        super(block);
        setHasSubtypes(true);
        setMaxDamage(0);
        setNoRepair();
    }

    public static ItemStack setDefaultTag(ItemStack container)
    {
        RedstoneControlHelper.setControl(container, IRedstoneControl.ControlMode.DISABLED);
//        EnergyHelper.setDefaultEnergyTag(container, 0);

//        container.stackTagCompound.setByte("Facing", (byte) ForgeDirection.NORTH.ordinal());

        return container;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return BlockScanner.getTileName(BlockScanner.NAMES[ItemHelper.getItemDamage(stack)]);
    }
}
