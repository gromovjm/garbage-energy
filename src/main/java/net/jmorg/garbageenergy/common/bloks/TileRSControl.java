package net.jmorg.garbageenergy.common.bloks;

import cofh.api.tileentity.IRedstoneControl;
import cofh.asm.relauncher.CoFHSide;
import cofh.asm.relauncher.Strippable;
import cofh.lib.audio.ISoundSource;
import cofh.lib.audio.SoundTile;
import cofh.lib.util.helpers.ServerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.audio.ISound;
import net.minecraft.nbt.NBTTagCompound;

@Strippable(value="cofh.lib.audio.ISoundSource", side=CoFHSide.SERVER)
public abstract class TileRSControl extends TileInventory implements IRedstoneControl, ISoundSource
{
    public boolean isActive;
    protected boolean isPowered;
    protected boolean wasPowered;

    protected ControlMode rsMode = ControlMode.DISABLED;

    @Override
    public void onNeighborBlockChange()
    {
        wasPowered = isPowered;
        isPowered = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);

        if (wasPowered != isPowered) {
            onRedstoneUpdate();
        }
    }

    public final boolean redstoneControlOrDisable()
    {
        return rsMode.isDisabled() || isPowered == rsMode.getState();
    }

    public void onRedstoneUpdate()
    {
    }

    //
    // RedstoneControl methods.
    @Override
    public final void setPowered(boolean isPowered)
    {
        wasPowered = this.isPowered;
        this.isPowered = isPowered;
        if (ServerHelper.isClientWorld(worldObj)) {
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    @Override
    public final boolean isPowered()
    {
        return isPowered;
    }

    @Override
    public final void setControl(ControlMode control)
    {
        rsMode = control;
        if (!ServerHelper.isClientWorld(worldObj)) {
            sendUpdatePacket(Side.CLIENT);
        }
    }

    @Override
    public final ControlMode getControl()
    {
        return rsMode;
    }

    //
    // Sound methods.
    @Override
    @SideOnly(Side.CLIENT)
    public ISound getSound()
    {
        return new SoundTile(this, getSoundName(), 1.0F, 1.0F, true, 0, xCoord, yCoord, zCoord);
    }

    public String getSoundName()
    {
        return "";
    }

    @Override
    public boolean shouldPlaySound()
    {
        return !tileEntityInvalid && isActive;
    }

    //
    // NBT methods.
    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);

        isActive = nbt.getBoolean("Active");
        NBTTagCompound rsTag = nbt.getCompoundTag("RS");

        isPowered = rsTag.getBoolean("Power");
        rsMode = ControlMode.values()[rsTag.getByte("Mode")];
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);

        nbt.setBoolean("Active", isActive);
        NBTTagCompound rsTag = new NBTTagCompound();

        rsTag.setBoolean("Power", isPowered);
        rsTag.setByte("Mode", (byte) rsMode.ordinal());
        nbt.setTag("RS", rsTag);
    }
}