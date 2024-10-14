package org.skyfish.failsafe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.failsafe.impl.*;
import org.skyfish.handler.*;
import org.skyfish.util.*;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Optional;

public class FailsafeManager {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ArrayList<Failsafe> failsafes = new ArrayList<>();
    private final ArrayList<Failsafe> emergencyQueue = new ArrayList<>();
    private Optional<Failsafe> triggeredFailsafe = Optional.empty();

    private final Timer checkTimer = new Timer();
    
    public void initialize() {
        failsafes.add(DeathFailsafe.getInstance());
        failsafes.add(DisconnectFailsafe.getInstance());
        failsafes.add(EvacuateFailsafe.getInstance());
        failsafes.add(FullInventoryFailsafe.getInstance());
        failsafes.add(ItemChangeFailsafe.getInstance());
        failsafes.add(KnockbackFailsafe.getInstance());
        failsafes.add(RotationFailsafe.getInstance());
        failsafes.add(TeleportFailsafe.getInstance());
        failsafes.add(WorldChangeFailsafe.getInstance());
        failsafes.forEach((failsafe) -> failsafe.initialize());
        MinecraftForge.EVENT_BUS.register(this);
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
        if (mc.theWorld == null || mc.thePlayer == null || !MacroHandler.getInstance().isEnabled()) return;
        failsafes.forEach((failsafe) -> failsafe.onTick());
        if (emergencyQueue.isEmpty()) return;
        
        if (checkTimer.hasElasped(2000) && !triggeredFailsafe.isPresent()) {
            triggeredFailsafe = Optional.of(getHighestPriority());
            LogUtils.sendError(triggeredFailsafe.get().getName() +  " failsafe has been triggered!");
            checkTimer.reset();
            
            if (Config.getInstance().FAILSAFE_PLAY_SOUND) AudioHandler.getInstance().playSound();
        }
        
        if (triggeredFailsafe.isPresent()) {
            triggeredFailsafe.get().onTrigger();
        }
    }

    public void clear() {
        emergencyQueue.clear();
        triggeredFailsafe = Optional.empty();
    }

    public Optional<Failsafe> getTriggeredFailsafe() {
        return triggeredFailsafe;
    }

    public void detection(Failsafe failsafe) {
        triggeredFailsafe = Optional.of(failsafe);
        LogUtils.sendError(triggeredFailsafe.get().getName() +  " failsafe has been triggered!");
        checkTimer.reset();
        if (Config.getInstance().FAILSAFE_PLAY_SOUND) AudioHandler.getInstance().playSound();
    }

    public void possibleDetection(Failsafe failsafe) {
        if (emergencyQueue.contains(failsafe)) return;
        this.emergencyQueue.add(failsafe);
        checkTimer.reset();
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        failsafes.forEach((failsafe) -> failsafe.onChat(event));
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
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
