package xerca.xercapaint.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SpraySoundPacket implements IMessage {
    public enum SoundType {SPRAY(0), SHAKE(1);
        private final int id;
        SoundType(int id) {this.id = id;}
        public int getId() {return id;}
        public static SoundType fromId(int id) {return id == 1 ? SHAKE : SPRAY;}
    }

    private SoundType type;

    public SpraySoundPacket() {}
    public SpraySoundPacket(SoundType type) { this.type = type; }
    public SoundType getType() { return type; }

    @Override
    public void fromBytes(ByteBuf buf) {
        type = SoundType.fromId(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(type.getId());
    }
}