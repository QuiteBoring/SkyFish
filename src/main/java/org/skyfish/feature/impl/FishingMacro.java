package org.skyfish.feature.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import org.skyfish.event.PacketReceiveEvent;
import org.skyfish.feature.Feature;
import org.skyfish.handler.*;
import org.skyfish.mixin.entity.EntityFishHookAccessor;
import org.skyfish.util.Timer;
import org.skyfish.util.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FishingMacro extends Feature {

    private Timer delayTimer = new Timer();
    private Timer lastTimeReeled = new Timer();
    private Timer onGroundTimer = new Timer();

    public FishingMacro() {
        super("Fishing Macro");
    }

    @Override
    public void start() {   
        rodSlot = -1;
        startingLocation = GameStateHandler.getInstance().getLocation();
        fishingHook = null;
        delayTimer.reset();
        lastTimeReeled.reset();
        onGroundTimer.reset(); 
    }

    @Override
    public void stop() {
        rodSlot = -1;
        startingLocation = null;
        fishingHook = null;
        delayTimer.reset();
        lastTimeReeled.reset();
        onGroundTimer.reset();  
    }

    public GameStateHandler.Location startingLocation = GameStateHandler.Location.CRIMSON_ISLE;
    public int rodSlot = -1;
    private EntityFishHook fishingHook = null;

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof S14PacketEntity.S17PacketEntityLookMove) {
            Entity entity = ((S14PacketEntity.S17PacketEntityLookMove) event.getPacket()).getEntity(mc.theWorld);
            if (!(entity instanceof EntityFishHook) || ((EntityFishHook) entity).angler != mc.thePlayer) return;
            fishingHook = (EntityFishHook) entity;
        }

        if (event.getPacket() instanceof S2APacketParticles) {
            if (fishingHook == null) return;
            if (((S2APacketParticles) event.getPacket()).getParticleType() != EnumParticleTypes.WATER_WAKE && ((S2APacketParticles) event.getPacket()).getParticleType() != EnumParticleTypes.FLAME) return;
            if (((S2APacketParticles) event.getPacket()).getParticleCount() != 6 || ((S2APacketParticles) event.getPacket()).getParticleSpeed() != 0.2f) return;
            double particlePosX = ((S2APacketParticles) event.getPacket()).getXCoordinate();
            double particlePosZ = ((S2APacketParticles) event.getPacket()).getZCoordinate();
            if (fishingHook.getDistance(particlePosX, fishingHook.posY, particlePosZ) < 0.1) {
                MacroHandler.getInstance().setStep(MacroHandler.Step.CATCH);
            }
        }
    }

    @Override
    public void onTick() {
        switch (MacroHandler.getInstance().getStep()) {
            default: {
                return;
            }
            
            case FIND_ROD: {
                if (delayTimer.hasElasped(Config.getInstance().getDelay(Config.getInstance().DELAYS_SWAP_SLOT))) {
                    onGroundTimer.reset();

                    if (rodSlot == -1) {
                        int slot = InventoryUtils.searchItem("Rod");

                        if (slot == -1) {
                            MacroHandler.getInstance().setEnabled(false);
                            LogUtils.sendError("No rod found in hotbar!");
                            return;
                        }

                        rodSlot = slot;
                    } 

                    this.mc.thePlayer.inventory.currentItem = rodSlot;
                    delayTimer.reset();
                    MacroHandler.getInstance().setStep(MacroHandler.Step.THROW_ROD);
                }

                return;
            }

            case THROW_ROD: {
                if (delayTimer.hasElasped(Config.getInstance().getDelay(Config.getInstance().DELAYS_RECAST))) {
                    if (mc.thePlayer.fishEntity == null) KeybindUtils.rightClick();
                    lastTimeReeled.reset();
                    MacroHandler.getInstance().setStep(MacroHandler.Step.WAIT_FOR_CATCH);
                }

                return;
            }

            case WAIT_FOR_CATCH: {
                if (lastTimeReeled.hasElaspedOnce(60000)) {
                    KeybindUtils.rightClick();
                    LogUtils.sendError("Recasting, rod has been casted for too long...");
                    MacroHandler.getInstance().setStep(MacroHandler.Step.FIND_ROD);
                }

                return;
            }

            case CATCH: {
                if (delayTimer.hasElasped(Config.getInstance().getDelay(Config.getInstance().DELAYS_RECAST))) {
                    KeybindUtils.rightClick();
                    delayTimer.reset();
                    MacroHandler.getInstance().setStep(MacroHandler.Step.THROW_ROD);
                }

                return;
            }
        }
    }


    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (mc.thePlayer == null) return;

        if (onGroundTimer.hasElaspedOnce(2000)) {
            if (fishingHook != null && (fishingHook.onGround || ((EntityFishHookAccessor) fishingHook).getInGround() || fishingHook.caughtEntity != null)) {
                if (mc.thePlayer.fishEntity != null) KeybindUtils.rightClick();
                LogUtils.sendError("Recasting due to problem (On ground, in ground, or hooked entity)...");
                MacroHandler.getInstance().setStep(MacroHandler.Step.FIND_ROD);;
            }
        }
    }

    private static FishingMacro instance;
    public static FishingMacro getInstance() {
        if (instance == null) {
            instance = new FishingMacro();
        }

        return instance;
    }

}