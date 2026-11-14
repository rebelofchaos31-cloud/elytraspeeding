package client;

import com.fenix.elytraspeeding.ElytraSpeeding;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ElytraSpeeding.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static KeyBinding LIMIT_UP;
    public static KeyBinding LIMIT_DOWN;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        LIMIT_UP = new KeyBinding("key.elytraspeeding.limit_up", GLFW.GLFW_KEY_LEFT_ALT, "key.categories.elytraspeeding");
        LIMIT_DOWN = new KeyBinding("key.elytraspeeding.limit_down", GLFW.GLFW_KEY_CAPS_LOCK, "key.categories.elytraspeeding");
        ClientRegistry.registerKeyBinding(LIMIT_UP);
        ClientRegistry.registerKeyBinding(LIMIT_DOWN);
    }
}