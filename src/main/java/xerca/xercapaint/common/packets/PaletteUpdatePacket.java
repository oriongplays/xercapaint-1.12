package xerca.xercapaint.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import xerca.xercapaint.common.PaletteUtil;

import java.util.Arrays;

public class PaletteUpdatePacket implements IMessage {
    private PaletteUtil.CustomColor[] paletteColors;
    private boolean messageIsValid;

    public PaletteUpdatePacket(PaletteUtil.CustomColor[] paletteColors) {
        this.paletteColors = Arrays.copyOfRange(paletteColors, 0, 12);
    }

    public PaletteUpdatePacket() {
        this.messageIsValid = false;
    }

    public PaletteUtil.CustomColor[] getPaletteColors() {
        return paletteColors;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            paletteColors = new PaletteUtil.CustomColor[12];
            for(int i=0; i < paletteColors.length; i++){
                paletteColors[i] = new PaletteUtil.CustomColor(buf);
            }
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return;
        }
        messageIsValid = true;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        for(PaletteUtil.CustomColor color : paletteColors){
            color.writeToBuffer(buf);
        }
    }
}
