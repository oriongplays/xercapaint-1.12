package xerca.xercapaint.client;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityTransparentCanvas;

import java.util.Arrays;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class RenderTransparentCanvas extends Render<EntityTransparentCanvas> {
    private final TextureManager textureManager;
    private static final ResourceLocation BACKGROUND = new ResourceLocation(XercaPaint.MODID, "textures/canvas/canvabackground.png");
    private final Map<String, Instance> loadedCanvases = Maps.newHashMap();

    protected RenderTransparentCanvas(RenderManager renderManager) {
        super(renderManager);
        this.textureManager = Minecraft.getMinecraft().getTextureManager();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTransparentCanvas entity) {
        return getMapRendererInstance(entity).location;
    }

    @Override
    public void doRender(EntityTransparentCanvas canvas, double x, double y, double z, float yaw, float partialTick) {
        NBTTagCompound tag = canvas.getCanvasNBT();
        if (tag != null && tag.hasKey("name")) {
            getMapRendererInstance(canvas).render(x, y, z, yaw, partialTick, canvas.getHorizontalFacing());
        }
        super.doRender(canvas, x, y, z, yaw, partialTick);
    }

    public static class Factory implements IRenderFactory<EntityTransparentCanvas> {
        @Override
        public Render<? super EntityTransparentCanvas> createRenderFor(RenderManager manager) {
            return new RenderTransparentCanvas(manager);
        }
    }

    private Instance getMapRendererInstance(EntityTransparentCanvas canvas) {
        NBTTagCompound textureData = canvas.getCanvasNBT();
        Instance instance = this.loadedCanvases.get(textureData.getString("name"));
        if (instance == null) {
            instance = new Instance(canvas);
            this.loadedCanvases.put(textureData.getString("name"), instance);
        } else {
            int currentVersion = textureData.getInteger("v");
            if (instance.version != currentVersion) {
                instance.updateCanvasTexture(textureData);
            }
        }
        return instance;
    }

    class Instance {
        int version = 0;
        private final DynamicTexture canvasTexture;
        private final ResourceLocation location;
        private final int width;
        private final int height;

        Instance(EntityTransparentCanvas canvas) {
            this.width = canvas.getWidthPixels();
            this.height = canvas.getHeightPixels();
            this.canvasTexture = new DynamicTexture(width, height);
            this.location = textureManager.getDynamicTextureLocation("canvas/" + canvas.getCanvasNBT().getString("name"), this.canvasTexture);
            updateCanvasTexture(canvas.getCanvasNBT());
        }

        void updateCanvasTexture(NBTTagCompound textureData) {
            this.version = textureData.getInteger("v");
            int[] pixels = textureData.getIntArray("pixels");
            int[] data = canvasTexture.getTextureData();
            int len = Math.min(pixels.length, data.length);
            Arrays.fill(data, 0);
            for (int i = 0; i < len; i++) {
                int color = pixels[i];
                if ((color & 0xFFFFFF) != 0 && (color & 0xFF000000) == 0) {
                    color |= 0xFF000000;
                }
                data[i] = color;
            }
            canvasTexture.updateDynamicTexture();
        }

        void render(double x, double y, double z, float yaw, float partialTick, EnumFacing facing) {
            GlStateManager.pushMatrix();
            float wScale = width / 16.0F;
            float hScale = height / 16.0F;
            GlStateManager.translate(x + facing.getFrontOffsetZ() * 0.5D * wScale,
                    y - 0.5D * hScale,
                    z - facing.getFrontOffsetX() * 0.5D * wScale);
            GlStateManager.rotate(180.0F - yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.enableRescaleNormal();
            float f = 1.0f / 32.0f;
            GlStateManager.scale(f, f, f);
            GlStateManager.disableLighting();
            GlStateManager.color(0.8F, 0.8F, 0.8F, 0.85F);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            textureManager.bindTexture(BACKGROUND);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, 32.0D * hScale, -1.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D * wScale, 32.0D * hScale, -1.0D).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D * wScale, 0.0D, -1.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -1.0D).tex(1.0D, 1.0D).endVertex();
            tessellator.draw();
            textureManager.bindTexture(location);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(0.0D, 32.0D * hScale, -1.0D).tex(1.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D * wScale, 32.0D * hScale, -1.0D).tex(0.0D, 0.0D).endVertex();
            bufferbuilder.pos(32.0D * wScale, 0.0D, -1.0D).tex(0.0D, 1.0D).endVertex();
            bufferbuilder.pos(0.0D, 0.0D, -1.0D).tex(1.0D, 1.0D).endVertex();
            tessellator.draw();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
        }
    }
}