package xerca.xercapaint.common;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = XercaPaint.MODID)
public final class Sounds {
    public static final SoundEvent SPRAY_SHAKE = create("sprayshake");
    public static final SoundEvent SPRAY = create("spray");

    private static SoundEvent create(String name) {
        ResourceLocation location = new ResourceLocation(XercaPaint.MODID, name);
        return new SoundEvent(location).setRegistryName(location);
    }

    @SubscribeEvent
    public static void registerSounds(final RegistryEvent.Register<SoundEvent> event) {
        event.getRegistry().registerAll(
                SPRAY_SHAKE,
                SPRAY
        );
    }
}