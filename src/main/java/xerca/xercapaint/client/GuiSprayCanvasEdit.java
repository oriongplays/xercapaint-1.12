package xerca.xercapaint.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xerca.xercapaint.common.CanvasType;
import xerca.xercapaint.common.XercaPaint;

@SideOnly(Side.CLIENT)
public class GuiSprayCanvasEdit extends GuiCanvasEdit {
    private static final ResourceLocation SPRAY_TEXTURE =
            new ResourceLocation(XercaPaint.MODID, "textures/gui/spray_palette.png");

    protected GuiSprayCanvasEdit(EntityPlayer player, NBTTagCompound canvasTag, NBTTagCompound paletteTag,
                                 ITextComponent title, CanvasType canvasType) {
        super(player, canvasTag, paletteTag, title, canvasType);
    }

    @Override
    protected ResourceLocation getPaletteTexture() {
        return SPRAY_TEXTURE;
    }
}
