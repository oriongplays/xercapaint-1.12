package xerca.xercapaint.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xerca.xercapaint.common.entity.EntityTransparentCanvas;

@SideOnly(Side.CLIENT)
public class SprayCanvasUpdatePacketHandler implements IMessageHandler<SprayCanvasUpdatePacket, IMessage> {
    @Override
    public IMessage onMessage(SprayCanvasUpdatePacket message, MessageContext ctx) {
        Minecraft.getMinecraft().addScheduledTask(() -> {
            World world = Minecraft.getMinecraft().player.world;
            Entity entity = world.getEntityByID(message.getEntityId());
            if (entity instanceof EntityTransparentCanvas) {
                ((EntityTransparentCanvas) entity).setCanvasNBT(message.getCanvas());
            }
        });
        return null;
    }
}