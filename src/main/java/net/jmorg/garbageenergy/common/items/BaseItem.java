package net.jmorg.garbageenergy.common.items;

import net.jmorg.garbageenergy.GarbageEnergy;
import net.minecraft.item.Item;

public class BaseItem extends Item
{
    public BaseItem()
    {
        super();

        String name = getName();

        setCreativeTab(GarbageEnergy.garbageEnergyTab);
        setTextureName(GarbageEnergy.MODID + ":" + name);
        setUnlocalizedName(GarbageEnergy.MODID + "." + name);
    }

    public String getName()
    {
        return "generic_item";
    }
}
