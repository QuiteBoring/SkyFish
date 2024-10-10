package org.skyfish.failsafe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
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
        features.forEach((feature) -> feature.initialize());
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
            if (feature.isRunning() && !MacroHandler.getInstance().isPaused()) feature.onTick();
        });
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        features.forEach((feature) -> {
            if (feature.isRunning() && !MacroHandler.getInstance().isPaused()) feature.onChat(event);
        });
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        features.forEach((feature) -> {
            if (feature.isRunning() && !MacroHandler.getInstance().isPaused()) feature.onPacketReceive(event);
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
