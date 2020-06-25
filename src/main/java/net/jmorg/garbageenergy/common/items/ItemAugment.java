package net.jmorg.garbageenergy.common.items;

import cofh.api.item.IAugmentItem;
import cofh.core.item.ItemBase;
import cofh.lib.util.helpers.ItemHelper;
import cofh.lib.util.helpers.StringHelper;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.utils.AugmentManager.Augments;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

public class ItemAugment extends ItemBase implements IAugmentItem
{
    TIntObjectHashMap<AugmentEntry> augmentMap = new TIntObjectHashMap<AugmentEntry>();

    public ItemStack[] attenuateModifiers = new ItemStack[Augments.NUM_ATTENUATE_MODIFIER];
    public ItemStack[] energyAmplifiers = new ItemStack[Augments.NUM_ENERGY_AMPLIFIER];

    public ItemAugment()
    {
        super(GarbageEnergy.MODID);
        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        setUnlocalizedName("augment");
        setHasSubtypes(true);
    }

    public void registerAugments()
    {
        // Register attenuate amplifier augment
        for (int i = 0; i < Augments.NUM_ATTENUATE_MODIFIER; i++) {
            attenuateModifiers[i] = addItem(48 + i, Augments.ATTENUATE_MODIFIER_NAME, 1 + i, 0);
        }

        // Register energy amplifier augment
        for (int i = 0; i < Augments.NUM_ENERGY_AMPLIFIER; i++) {
            energyAmplifiers[i] = addItem(64 + i, Augments.ENERGY_AMPLIFIER_NAME, 1 + i, 0);
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
        return StringHelper.localize("info.GarbageEnergy.augment") + ": " + StringHelper.localize(getUnlocalizedName(stack) + ".name");
    }

    @Override
    public boolean isFull3D()
    {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check)
    {
        if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
            list.add(StringHelper.shiftForDetails());
        }
        if (!StringHelper.isShiftKeyDown()) {
            return;
        }
        boolean augmentChain = true;
        String type = getPrimaryType(stack);
        list.add(StringHelper.localize("info.GarbageEnergy.augment." + type));

        int level = getPrimaryLevel(stack);
        list.add(StringHelper.WHITE + StringHelper.localize("info.cofh.level") + " " + StringHelper.ROMAN_NUMERAL[level] + StringHelper.END);

        int numInfo = getNumInfo(stack);
        for (int i = 0; i < numInfo; i++) {
            list.add(StringHelper.BRIGHT_GREEN + StringHelper.localize("info.GarbageEnergy.augment." + type + "." + i) + StringHelper.END);
        }

        String info = StringHelper.localize("info.GarbageEnergy.augment." + type + ".info");
        if (type.equals(Augments.ENERGY_AMPLIFIER_NAME)) {
            list.add(StringHelper.BRIGHT_GREEN + String.format(info, Augments.ENERGY_AMPLIFIER[level]) + StringHelper.END);
        }
        if (type.equals(Augments.ATTENUATE_MODIFIER_NAME)) {
            list.add(StringHelper.BRIGHT_GREEN + String.format(info, Augments.ATTENUATE_MODIFIER[level]) + StringHelper.END);
        }

        if (level > 1 && augmentChain) {
            list.add(StringHelper.getNoticeText("info.GarbageEnergy.augment.levels.0"));
            list.add(StringHelper.getNoticeText("info.GarbageEnergy.augment.levels.1"));
        }
    }

    public ItemStack addItem(int metadata, String name, int level, int numInfo)
    {
        ItemStack itemStack = super.addItem(metadata, name + level);

        if (!augmentMap.containsKey(metadata)) {
            augmentMap.put(metadata, new AugmentEntry());
            augmentMap.get(metadata).primaryType = name;
            augmentMap.get(metadata).primaryLevel = level;
            augmentMap.get(metadata).numInfo = numInfo;
        }
        augmentMap.get(metadata).augmentTypeInfo.put(name, level);

        return itemStack;
    }

    private String getPrimaryType(ItemStack stack)
    {
        AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
        if (entry == null) {
            return "";
        }
        return entry.primaryType;
    }

    private int getPrimaryLevel(ItemStack stack)
    {
        AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
        if (entry == null) {
            return 0;
        }
        return entry.primaryLevel;
    }

    private int getNumInfo(ItemStack stack)
    {
        AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(stack));
        if (entry == null) {
            return 0;
        }
        return entry.numInfo;
    }

    @Override
    public int getAugmentLevel(ItemStack itemStack, String type)
    {
        AugmentEntry entry = augmentMap.get(ItemHelper.getItemDamage(itemStack));
        if (!entry.augmentTypeInfo.containsKey(type)) {
            return 0;
        }
        return entry.augmentTypeInfo.get(type);
    }

    @Override
    public Set<String> getAugmentTypes(ItemStack itemStack)
    {
        return augmentMap.get(ItemHelper.getItemDamage(itemStack)).augmentTypeInfo.keySet();
    }

    public class AugmentEntry
    {
        public String primaryType = "";
        public int primaryLevel = 0;
        public int numInfo = 1;
        public TObjectIntHashMap<String> augmentTypeInfo = new TObjectIntHashMap<String>();
    }
}
