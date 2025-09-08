package xerca.xercapaint.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public abstract class Proxy {
    public abstract void init();
    public abstract void updateCanvas(NBTTagCompound data);
    public abstract void showCanvasGui(EntityPlayer player);
    public abstract void showSprayGui(EntityPlayer player, NBTTagCompound canvasTag, NBTTagCompound paletteTag, BlockPos pos);

    public abstract void preInit();

    public abstract void postInit();
}
