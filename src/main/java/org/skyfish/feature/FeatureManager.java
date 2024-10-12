package org.skyfish.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.PacketReceiveEvent;
import org.skyfish.feature.impl.*;
import org.skyfish.handler.MacroHandler;
import org.skyfish.util.*;

import java.util.ArrayList;
import java.util.Optional;

public class FeatureManager {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ArrayList<Feature> features = new ArrayList<>();
    
    public void initialize() {
        features.add(FishingMacro.getInstance());
        features.add(AutoKill.getInstance());
        features.add(AutoTotem.getInstance());
        features.add(AutoFlare.getInstance());
        features.add(AutoCrouch.getInstance());
        features.add(UngrabMouse.getInstance());
        features.add(AntiAFK.getInstance());
        features.forEach((feature) -> feature.initialize());
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void enableAll() {
        features.forEach((feature) -> feature.start());
    }

    public void disableAll() {
        features.forEach((feature) -> feature.stop());
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) return;

        features.forEach((feature) -> {
            if (MacroHandler.getInstance().isEnabled() && !MacroHandler.getInstance().isPaused()) feature.onTick();
        });
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        features.forEach((feature) -> {
            if (MacroHandler.getInstance().isEnabled() && !MacroHandler.getInstance().isPaused()) feature.onChat(event);
        });
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        features.forEach((feature) -> {
            if (MacroHandler.getInstance().isEnabled() && !MacroHandler.getInstance().isPaused()) feature.onPacketReceive(event);
        });
    }
    
    private static FeatureManager instance;
    public static FeatureManager getInstance() {
        if (instance == null) {
            instance = new FeatureManager();
        }

        return instance;
    }
  
}
