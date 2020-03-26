package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class CanvasRenderer implements AutoCloseable {
    private final TextureManager textureManager;
    private final Map<String, Instance> loadedCanvases = Maps.newHashMap();

    public CanvasRenderer(TextureManager textureManagerIn) {
        this.textureManager = textureManagerIn;
    }
    public void updateMapTexture(NBTTagCompound textureData) {
        this.getMapRendererInstance(textureData).updateCanvasTexture(textureData);
    }

    public void renderCanvas(NBTTagCompound textureData) {
        if(textureData != null && textureData.hasKey("name")){
            this.getMapRendererInstance(textureData).render();
        }
    }

    private Instance getMapRendererInstance(NBTTagCompound textureData) {
        Instance instance = this.loadedCanvases.get(textureData.getString("name"));
        if (instance == null) {
            instance = new Instance(textureData);
            this.loadedCanvases.put(textureData.getString("name"), instance);
        }else{
            int currentVersion = textureData.getInteger("v");
            if(instance.version != currentVersion){
                instance.updateCanvasTexture(textureData);
            }
        }

        return instance;
    }

    @Nullable
    public Instance getMapInstanceIfExists(String name) {
        return this.loadedCanvases.get(name);
    }

    /**
     * Clears the currently loaded maps and removes their corresponding textures
     */
    public void clearLoadedMaps() {
        for(Instance instance : this.loadedCanvases.values()) {
            instance.close();
        }

        this.loadedCanvases.clear();
    }

    public void close() {
        this.clearLoadedMaps();
    }

    @SideOnly(Side.CLIENT)
    class Instance implements AutoCloseable {
        int version = 0;
        private final DynamicTexture canvasTexture;
        private final ResourceLocation location;

        private Instance(NBTTagCompound canvasData) {
            this.canvasTexture = new DynamicTexture(16, 16);
            this.location = CanvasRenderer.this.textureManager.getDynamicTextureLocation("canvas/" + canvasData.getString("name"), this.canvasTexture);

            updateCanvasTexture(canvasData);
        }

        private void updateCanvasTexture(NBTTagCompound textureData) {
            this.version = textureData.getInteger("v");

            int[] pixels = textureData.getIntArray("pixels");
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    int k = j + i * 16;

                    canvasTexture.getTextureData()[k] = pixels[k];
                }
            }

            canvasTexture.updateDynamicTexture();
        }

        public void render() {
            GlStateManager.disableLighting();
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
            float f = 0.0078125F;
            GlStateManager.scale(f, f, f);
            GlStateManager.translate(-64.0F, -64.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, -1.0F);


            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            textureManager.bindTexture(location);
            GlStateManager.disableAlpha();
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, 128.0D, (double) -0.01F).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(128.0D, 128.0D, (double) -0.01F).tex(1.0D, 1.0D).endVertex();
            bufferbuilder.pos(128.0D, 0.0D, (double) -0.01F).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, (double) -0.01F).tex(0.0D, 0.0D).endVertex();
            tessellator.draw();
            GlStateManager.enableAlpha();
        }

        public void close() {
//            this.canvasTexture.close();
        }
    }
}
