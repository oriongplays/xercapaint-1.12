package xerca.xercapaint.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.client.event.RenderItemInFrameEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import xerca.xercapaint.common.Proxy;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;
import xerca.xercapaint.common.entity.EntityTransparentCanvas;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.ItemSprayPalette;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.CanvasType;

public class ClientProxy extends Proxy {
    private static CanvasRenderer canvasRenderer;

    @Override
    public void updateCanvas(NBTTagCompound data) {
        canvasRenderer.updateMapTexture(data);
    }

    public void showCanvasGui(EntityPlayer player){
        final ItemStack heldItem = player.getHeldItemMainhand();
        final ItemStack offhandItem = player.getHeldItemOffhand();

        Minecraft minecraft = Minecraft.getMinecraft();

        if(heldItem.isEmpty() || !minecraft.player.getGameProfile().getId().equals(player.getGameProfile().getId())){
            return;
        }

        if(heldItem.getItem() instanceof ItemCanvas){
            if(offhandItem.isEmpty()){
                minecraft.displayGuiScreen(new GuiCanvasView(heldItem.getTagCompound(), new TextComponentTranslation("item.item_canvas.name"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }else if(offhandItem.getItem() instanceof ItemPalette){
                minecraft.displayGuiScreen(new GuiCanvasEdit(minecraft.player,
                        heldItem.getTagCompound(), offhandItem.getTagCompound(), new TextComponentTranslation("item.item_canvas.name"), ((ItemCanvas)heldItem.getItem()).getCanvasType()));
            }
        }
        else if(heldItem.getItem() instanceof ItemPalette){
            if(offhandItem.isEmpty()){
                minecraft.displayGuiScreen(new GuiPalette(heldItem.getTagCompound(), new TextComponentTranslation("item.item_palette.name")));
            }else if(offhandItem.getItem() instanceof ItemCanvas){
                minecraft.displayGuiScreen(new GuiCanvasEdit(minecraft.player,
                        offhandItem.getTagCompound(), heldItem.getTagCompound(), new TextComponentTranslation("item.item_canvas.name"), ((ItemCanvas)offhandItem.getItem()).getCanvasType()));
            }
        }
                else if(heldItem.getItem() instanceof ItemSprayPalette){
            if(offhandItem.isEmpty()){
                minecraft.displayGuiScreen(new GuiSprayPalette(heldItem.getTagCompound(), new TextComponentTranslation("item.item_spray_palette.name")));
            }
        }
    }

    @Override
    public void preInit() {
        RenderingRegistry.registerEntityRenderingHandler(EntityCanvas.class, new RenderEntityCanvas.RenderEntityCanvasFactory());
        RenderingRegistry.registerEntityRenderingHandler(EntityTransparentCanvas.class, new RenderTransparentCanvas.Factory());
    }

    @Override
    public void init() {

    }
    
    @Override
    public void showSprayGui(EntityPlayer player, NBTTagCompound canvasTag, NBTTagCompound paletteTag) {
        Minecraft minecraft = Minecraft.getMinecraft();
        minecraft.displayGuiScreen(new GuiSprayCanvasEdit(minecraft.player, canvasTag, paletteTag,
                new TextComponentTranslation("item.item_canvas.name"), CanvasType.SMALL));
    }

    @Override
    public void postInit() {

    }

    @Mod.EventBusSubscriber(modid = XercaPaint.MODID, value=Side.CLIENT)
    static class ForgeBusSubscriber {
        @SubscribeEvent
        public static void renderItemInFrameEvent(RenderItemInFrameEvent ev) {
            if(ev.getItem().getItem() == Items.ITEM_CANVAS){
                if(canvasRenderer == null){
                    // Can't put this in setup handler because it needs to be in the main thread
                    canvasRenderer = new CanvasRenderer(Minecraft.getMinecraft().getTextureManager());
                }

                ev.setCanceled(true);

                canvasRenderer.renderCanvas(ev.getItem().getTagCompound());
            }
        }
    }
}
