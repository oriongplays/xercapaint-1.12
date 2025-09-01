package xerca.xercapaint.common.packets;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import xerca.xercapaint.common.Sounds;

public class SpraySoundPacketHandler implements IMessageHandler<SpraySoundPacket, IMessage> {
    @Override
    public IMessage onMessage(SpraySoundPacket message, MessageContext ctx) {
        EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
        if (sendingPlayer == null) {
            return null;
        }
        IThreadListener mainThread = (WorldServer) sendingPlayer.world;
        mainThread.addScheduledTask(() -> processMessage(message, sendingPlayer));
        return null;
    }

    private static void processMessage(SpraySoundPacket msg, EntityPlayerMP player) {
        switch (msg.getType()) {
            case SHAKE:
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        Sounds.SPRAY_SHAKE, SoundCategory.PLAYERS, 1.0F, 1.0F);
                break;
            case SPRAY:
            default:
                player.world.playSound(null, player.posX, player.posY, player.posZ,
                        Sounds.SPRAY, SoundCategory.PLAYERS, 1.0F, 1.0F);
                break;
        }
    }
}