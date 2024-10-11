package org.skyfish;

import gg.essential.api.EssentialAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.skyfish.failsafe.FailsafeManager;
import org.skyfish.feature.FeatureManager;
import org.skyfish.handler.*;
import org.skyfish.util.Config;

@Mod(modid = "skyfish", useMetadata=true)
public class SkyFish {
    
    private final Minecraft mc = Minecraft.getMinecraft();
    private final KeyBinding settingsKeybind = new KeyBinding("Open Settings", Keyboard.KEY_P, "SkyFish");
    private final KeyBinding macroKeybind = new KeyBinding("Toggle Macro", Keyboard.KEY_O, "SkyFish");

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(settingsKeybind);
        ClientRegistry.registerKeyBinding(macroKeybind);

        FailsafeManager.getInstance().initialize();
        FeatureManager.getInstance().initialize();

        GameStateHandler.getInstance().initialize();
        RotationHandler.getInstance().initialize();
        BaritoneHandler.getInstance().initialize();
        MacroHandler.getInstance().initialize();

        MinecraftForge.EVENT_BUS.register(new SkyFish());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || mc.currentScreen != null) return;

        if (settingsKeybind.isPressed()) {
            EssentialAPI.getGuiUtil().openScreen(Config.getInstance().gui());
        } else if (macroKeybind.isPressed()) {
            MacroHandler.getInstance().toggleMacro();
        } 
    }

}
