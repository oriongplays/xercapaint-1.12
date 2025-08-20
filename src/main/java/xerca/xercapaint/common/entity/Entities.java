package xerca.xercapaint.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityTransparentCanvas;


@GameRegistry.ObjectHolder(XercaPaint.MODID)
public class Entities {
//    public static final EntityType<EntityCanvas> CANVAS = null;
    private static int entityID = 80;

    private static void registerEntity(final Class<? extends Entity> entityClass, final String entityName, final int trackingRange, final int updateFrequency, final boolean sendsVelocityUpdates) {
        final ResourceLocation registryName = new ResourceLocation(XercaPaint.MODID, entityName);
        EntityRegistry.registerModEntity(registryName, entityClass, registryName.toString(), entityID++, XercaPaint.instance, trackingRange, updateFrequency, sendsVelocityUpdates);
    }

    public static void init() {
        registerEntity(EntityCanvas.class, "canvas", 64, 2147483647, false);
        registerEntity(EntityTransparentCanvas.class, "transparent_canvas", 64, 2147483647, false);
    }

}
