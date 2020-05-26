package net.jmorg.garbageenergy.common.bloks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.jmorg.garbageenergy.GarbageEnergy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

public class BlockGeneric extends Block
{
    @SideOnly(Side.CLIENT)
    protected IIcon[] icons;
    protected final String[] types;

    public BlockGeneric(Material material, String... types)
    {
        super(material);

        setCreativeTab(GarbageEnergy.garbageEnergyTab);

        this.types = types;
    }
}
