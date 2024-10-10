package org.skyfish.failsafe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.PacketReceiveEvent;
import org.skyfish.util.*;

import java.util.ArrayList;
import java.util.Optional;

public class FailsafeManager {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ArrayList<Failsafe> failsafes = new ArrayList<>();
    private final ArrayList<Failsafe> emergencyQueue = new ArrayList<>();
    private Optional<Failsafe> triggeredFailsafe = Optional.empty();

    private final Timer checkTimer = new Timer();
    
    public void initialize() {
        // failsafes.add(NewFailsafe.getInstance());
        failsafes.forEach((failsafe) -> failsafe.initialize());
    }

    public Failsafe getHighestPriority() {
        Failsafe highestPriority = failsafes.get(0);
        
        for (Failsafe failsafe : failsafes) {
            if (failsafe.getPriority() > highestPriority.getPriority()) highestPriority = failsafe;
        }
        
        return highestPriority;
    }
    
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        failsafes.forEach((failsafe) -> failsafe.onTick());
        if (mc.theWorld == null || mc.thePlayer == null || emergencyQueue.isEmpty()) return;
        if (checkTimer.hasElasped(2000) && !triggeredFailsafe.isPresent()) {
            triggeredFailsafe = Optional.of(getHighestPriority());
            LogUtils.sendError(triggeredFailsafe.get().getName() +  " failsafe has been triggered!");
            checkTimer.reset();
            
            if (Config.getInstance().FAILSAFE_PLAY_SOUND) {
                try {
                    Multithreading.runAsync(() -> {
                        float prevSound = mc.gameSettings.getSoundLevel(SoundCategory.MASTER);
                        mc.gameSettings.setSoundLevel(SoundCategory.MASTER, 100.0F);
                        Thread.sleep(1000);
                        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "random.anvil_land", 10.0F, 1.0F, false);
                        Thread.sleep(1000);
                        mc.gameSettings.setSoundLevel(SoundCategory.MASTER, prevSound);
                    });
                } catch (Exception ignored) {}
            }
        }
        
        if (triggeredFailsafe.isPresent()) {
            triggeredFailsafe.get().onFailsafeTrigger();
        }
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        failsafes.forEach((failsafe) -> failsafe.onChat(event));
    }

    @SubscribeEvent
    public void onPacketReceive(PacketReceiveEvent event) {
        failsafes.forEach((failsafe) -> failsafe.onPacketReceive(event));
    }

    @SubscribeEvent 
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        failsafes.forEach((failsafe) -> failsafe.onDisconnect(event));
    }
    
    private static FailsafeManager instance;
    public static FailsafeManager getInstance() {
        if (instance == null) {
            instance = new FailsafeManager();
        }

        return instance;
    }
  
}
