package net.jmorg.garbageenergy.common.bloks.generator;

import cofh.core.block.TileCoFHBase;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.jmorg.garbageenergy.common.bloks.generator.BlockGenerator.Types;
import net.minecraft.item.ItemStack;

public abstract class TileGeneratorBase extends TileCoFHBase
{
    protected final byte type;
    public ItemStack[] inventory = new ItemStack[0];

    public TileGeneratorBase()
    {
        this(Types.CORE);
    }

    public TileGeneratorBase(Types type)
    {
        this.type = (byte) type.ordinal();
    }

    @Override
    public String getName()
    {
        return "tile." + GarbageEnergy.MODID + ".generator." + BlockGenerator.NAMES[type];
    }

    @Override
    public int getType()
    {
        return type;
    }
}
