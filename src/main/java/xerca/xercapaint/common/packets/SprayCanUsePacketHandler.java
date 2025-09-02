package xerca.xercapaint.common.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xerca.xercapaint.common.item.Items;

public class SprayCanUsePacketHandler implements IMessageHandler<SprayCanUsePacket, IMessage> {
    @Override
    public IMessage onMessage(SprayCanUsePacket message, MessageContext ctx) {
        EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
        if (sendingPlayer == null) {
            return null;
        }
        IThreadListener mainThread = (WorldServer) sendingPlayer.world;
        mainThread.addScheduledTask(() -> processMessage(sendingPlayer));
        return null;
    }

    private static void processMessage(EntityPlayerMP player) {
        ItemStack stack = ItemStack.EMPTY;
        EnumHand hand = EnumHand.MAIN_HAND;
        if (player.getHeldItemMainhand().getItem() == Items.ITEM_SPRAY_CAN) {
            stack = player.getHeldItemMainhand();
            hand = EnumHand.MAIN_HAND;
        } else if (player.getHeldItemOffhand().getItem() == Items.ITEM_SPRAY_CAN) {
            stack = player.getHeldItemOffhand();
            hand = EnumHand.OFF_HAND;
        }

        if (!stack.isEmpty()) {
            stack.damageItem(1, player);
            if (stack.getCount() <= 0) {
                player.setHeldItem(hand, ItemStack.EMPTY);
            }
        }
    }
}