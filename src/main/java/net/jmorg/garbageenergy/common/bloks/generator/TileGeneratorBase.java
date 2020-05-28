package net.jmorg.garbageenergy.common.bloks.generator;

import net.jmorg.garbageenergy.common.bloks.TileRSControl;
import net.jmorg.garbageenergy.common.bloks.generator.BlockGenerator.Types;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileGeneratorBase extends TileRSControl
{
    protected final byte type;
    public static final boolean[] enableSecurity = new boolean[BlockGenerator.Types.values().length];

    @Override
    public String getName()
    {
        return tileName;
    }

    @Override
    public int getType()
    {
        return type;
    }

    public TileGeneratorBase()
    {
        this(Types.CORE);
    }

    public TileGeneratorBase(Types type)
    {
        this.type = ((byte) type.ordinal());
    }

    public void updateFromNBT(NBTTagCompound nbt)
    {
    }

    //
    // NBT methods.
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Name")) {
            tileName = nbt.getString("Name");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        if (!tileName.isEmpty()) {
            nbt.setString("Name", tileName);
        }
    }
}
