package xerca.xercapaint.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class SprayCanvasUpdatePacket implements IMessage {
    private int entityId;
    private NBTTagCompound canvas;

    public SprayCanvasUpdatePacket() {
    }

    public SprayCanvasUpdatePacket(int entityId, NBTTagCompound canvas) {
        this.entityId = entityId;
        this.canvas = canvas;
    }

    public int getEntityId() {
        return entityId;
    }

    public NBTTagCompound getCanvas() {
        return canvas;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        canvas = ByteBufUtils.readTag(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeTag(buf, canvas);
    }
}