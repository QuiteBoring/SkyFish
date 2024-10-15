package org.skyfish.feature.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.network.play.server.*;
import net.minecraft.util.*;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.feature.Feature;
import org.skyfish.handler.*;
import org.skyfish.mixin.entity.EntityFishHookAccessor;
import org.skyfish.util.Timer;
import org.skyfish.util.helper.Rotation;
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
        startedRotating = false;  
        shouldCast = mc.thePlayer.fishEntity == null;
        rodSlot = -1;
        weaponSlot = -1;
        startingLocation = GameStateHandler.getInstance().getLocation();
        fishingHook = null;
        delayTimer.reset();
        lastTimeReeled.reset();
        onGroundTimer.reset(); 
    }

    @Override
    public void stop() {
        startedRotating = false;
        shouldCast = false;
        rodSlot = -1;
        weaponSlot = -1;
        fishingHook = null;
        delayTimer.reset();
        lastTimeReeled.reset();
        onGroundTimer.reset();  
    }

    public GameStateHandler.Location startingLocation = GameStateHandler.Location.CRIMSON_ISLE;
    public int rodSlot = -1;
    public int weaponSlot = -1;
    private boolean shouldCast = false;
    private boolean startedRotating = false;
    private EntityFishHook fishingHook = null;

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof S14PacketEntity.S17PacketEntityLookMove) {
            Entity entity = ((S14PacketEntity.S17PacketEntityLookMove) event.packet).getEntity(mc.theWorld);
            if (entity == null) return;
            if (!(entity instanceof EntityFishHook) || ((EntityFishHook) entity).angler != mc.thePlayer) return;
            fishingHook = (EntityFishHook) entity;
        }

        if (event.packet instanceof S2APacketParticles) {
            if (fishingHook == null) return;
            if (((S2APacketParticles) event.packet).getParticleType() != EnumParticleTypes.WATER_WAKE && ((S2APacketParticles) event.packet).getParticleType() != EnumParticleTypes.FLAME) return;
            if (((S2APacketParticles) event.packet).getParticleCount() != 6 || ((S2APacketParticles) event.packet).getParticleSpeed() != 0.2f) return;
            double particlePosX = ((S2APacketParticles) event.packet).getXCoordinate();
            double particlePosZ = ((S2APacketParticles) event.packet).getZCoordinate();
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
                            LogUtils.sendError("No rod found in hotbar!");
                            MacroHandler.getInstance().setEnabled(false);
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
                    if (shouldCast) KeybindUtils.rightClick();
                    shouldCast = true;
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

            case FIND_WEAPON: {
                if (delayTimer.hasElasped(Config.getInstance().getDelay(Config.getInstance().DELAYS_SWAP_SLOT))) {
                    if (weaponSlot == -1) {
                        int slot = InventoryUtils.searchItems(Config.getInstance().getWeapon());

                        if (slot == -1) {
                            MacroHandler.getInstance().setEnabled(false);
                            LogUtils.sendError("No weapon found in hotbar!");
                            return;
                        }

                        weaponSlot = slot;
                    } 

                    this.mc.thePlayer.inventory.currentItem = weaponSlot;
                    MacroHandler.getInstance().setStep((Config.getInstance().AUTO_KILL_HYPE_FISHING || Config.getInstance().getWeapon()[0].contains("Fire Veil")) ? MacroHandler.Step.KILL : MacroHandler.Step.ROTATE_DOWN);
                }

                return;
            }

            case ROTATE_DOWN: {
                if (!startedRotating) {
                    RotationHandler.getInstance().easeTo(mc.thePlayer.rotationYaw, 90F, 200);
                    startedRotating = true;
                }

                if (!RotationHandler.getInstance().isDone()) return;
                if (mc.thePlayer.rotationPitch != 90F) {
                    startedRotating = false;
                } else {
                    startedRotating = false;
                    MacroHandler.getInstance().setStep(MacroHandler.Step.KILL);
                }
                return;
            }


            case ROTATE_BACK: {
                if (!startedRotating) {
                    Rotation data = MacroHandler.getInstance().getAngle();
                    RotationHandler.getInstance().easeTo(data.getYaw(), data.getPitch(), 200);
                    startedRotating = true;
                }

                if (!RotationHandler.getInstance().isDone()) return;
                startedRotating = false;
                MacroHandler.getInstance().setStep(MacroHandler.Step.FIND_ROD);
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
