package xerca.xercapaint.common.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xerca.xercapaint.common.PaletteUtil;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.entity.EntityTransparentCanvas;
import xerca.xercapaint.common.item.ItemSprayCan;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.packets.SprayCanvasUpdatePacket;

public class CanvasUpdatePacketHandler implements IMessageHandler<CanvasUpdatePacket, IMessage> {

    @Override
    public IMessage onMessage(CanvasUpdatePacket message, MessageContext ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return null;
        }
        EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when CanvasUpdatePacket was received");
            return null;
        }

        final IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world;
        mainThread.addScheduledTask(() -> processMessage(message, sendingPlayer));

        return null;
    }

    private static void processMessage(CanvasUpdatePacket msg, EntityPlayerMP pl) {
        ItemStack canvas = pl.getHeldItemMainhand();
        ItemStack palette = pl.getHeldItemOffhand();
        if(canvas.getItem() == Items.ITEM_PALETTE || canvas.getItem() == Items.ITEM_SPRAY_PALETTE){
            ItemStack temp = canvas;
            canvas = palette;
            palette = temp;
        }

        if (!canvas.isEmpty() && canvas.getItem() instanceof ItemCanvas) {
            NBTTagCompound comp = canvas.getTagCompound();
            if(comp == null){
                comp = new NBTTagCompound();
                canvas.setTagCompound(comp);
            }

            comp.setIntArray("pixels", msg.getPixels());
            comp.setString("name", msg.getName());
            comp.setInteger("v", msg.getVersion());
            if (msg.getSigned()) {
                comp.setString("author", pl.getName());
                comp.setString("title", msg.getTitle().trim());
            }

            if (!palette.isEmpty() && palette.getItem() == Items.ITEM_PALETTE) {
                NBTTagCompound paletteComp = palette.getTagCompound();
                PaletteUtil.writeCustomColorArrayToNBT(paletteComp, msg.getPaletteColors());
            }

            XercaPaint.LOGGER.debug("Handling canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
        }
        else if(!canvas.isEmpty() && canvas.getItem() instanceof ItemSprayCan) {
            EntityTransparentCanvas target = null;
            for(EntityTransparentCanvas ent : pl.world.getEntities(EntityTransparentCanvas.class, e -> true)) {
                NBTTagCompound tag = ent.getCanvasNBT();
                if(tag != null && msg.getName().equals(tag.getString("name"))) {
                    target = ent;
                    break;
                }
            }
            if(target != null) {
                NBTTagCompound comp = target.getCanvasNBT();
                comp.setIntArray("pixels", msg.getPixels());
                comp.setString("name", msg.getName());
                comp.setInteger("v", msg.getVersion());
                if(msg.getSigned()) {
                    comp.setString("author", pl.getName());
                    comp.setString("title", msg.getTitle().trim());
                }
                if(!palette.isEmpty() && (palette.getItem() == Items.ITEM_PALETTE || palette.getItem() == Items.ITEM_SPRAY_PALETTE)) {
                    PaletteUtil.writeCustomColorArrayToNBT(palette.getTagCompound(), msg.getPaletteColors());
                }
                XercaPaint.network.sendToAllTracking(new SprayCanvasUpdatePacket(target.getEntityId(), comp), target);
                XercaPaint.LOGGER.debug("Handling spray canvas update: Name: " + msg.getName() + " V: " + msg.getVersion());
            }
        }
    }
}
