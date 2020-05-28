package net.jmorg.garbageenergy.common.bloks.generator;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class TileCore extends TileGeneratorBase
{
    public int count;

    public TileCore()
    {
        inventory = new ItemStack[3];
    }

    public static void initialize()
    {
        GameRegistry.registerTileEntity(TileCore.class, "garbageenergy.generator.core");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote) {
            switch (side) {
                case 0://Bottom
                    decrementCount();
                    break;

                case 1://Top
                    incrementCount();
                    break;

                default:
                    break;
            }

            player.addChatMessage(new ChatComponentTranslation("tile.GarbageEnergy.generator.core.count", getCount()));
        }

        return true;
    }

    public int getCount()
    {
        return this.count;
    }

    public void incrementCount()
    {
        this.count++;
        this.markDirty();
    }

    public void decrementCount()
    {
        this.count--;
        this.markDirty();
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public void updateFromNBT(NBTTagCompound nbt)
    {
        count = nbt.getInteger("count");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setInteger("count", count);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        count = nbt.getInteger("count");
    }
}
