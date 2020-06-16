package net.jmorg.garbageenergy.common.items;

import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.StringHelper;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.utils.ItemDataCardManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemDataCard extends ItemBase
{
    public static final int[] cardSizes = {1, 9, 15, 32};

    public ItemDataCard()
    {
        super(GarbageEnergy.MODID);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        setUnlocalizedName("dataCard");
        setHasSubtypes(true);
    }

    public void registerCards()
    {
        makeInstance(EnumRarity.common);
        makeInstance(EnumRarity.uncommon);
        makeInstance(EnumRarity.rare);
        makeInstance(EnumRarity.epic);
    }

    private void makeInstance(EnumRarity enumRarity)
    {
        addItem(enumRarity.ordinal(), enumRarity.rarityName + "Card", enumRarity.ordinal(), true);
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check)
    {
        list.add(String.format(StringHelper.getInfoText("item.GarbageEnergy.dataCard.info"),
                ItemDataCardManager.getTotalStoredItemsCount(stack) + "/" + ItemDataCardManager.getMaxStoredItemsCount(stack)));

        if (ItemDataCardManager.getTotalStoredItemsCount(stack) > 0) {
            if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
                list.add(StringHelper.shiftForDetails());
            }
            if (!StringHelper.isShiftKeyDown()) {
                return;
            }
            ItemDataCardManager.addItemsInformation(stack, list);
        }
    }
}
