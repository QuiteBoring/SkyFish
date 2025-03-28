package org.skyfish.failsafe.impl;

import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.failsafe.*;
import org.skyfish.handler.MacroHandler;
import org.skyfish.SkyFish;
import org.skyfish.util.helper.Rotation;
import org.skyfish.util.*;

public class RotationFailsafe extends Failsafe {

    public RotationFailsafe() {
        super(Failsafe.Type.ROTATION);
    }

    private final Clock triggerCheck = new Clock();
    private Rotation rotationBeforeReacting = null;

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Config.getInstance().FAILSAFE_ROTATION) return;
        
        if (event.packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.packet;
            double packetYaw = packet.getYaw();
            double packetPitch = packet.getPitch();
            double playerYaw = mc.thePlayer.rotationYaw;
            double playerPitch = mc.thePlayer.rotationPitch;

            if (shouldTriggerCheck(packetYaw, packetPitch)) {
                if (rotationBeforeReacting == null) rotationBeforeReacting = new Rotation((float) playerYaw, (float) playerPitch);
            }

            triggerCheck.schedule(500);
        }
    }
    
    @Override
    public void onTick() {
        if (MacroHandler.getInstance().isEnabled()) {
            rotationBeforeReacting = null;
            return;
        }

        if (triggerCheck.passed() && triggerCheck.isScheduled()) {
            evaluateRotation();
            triggerCheck.reset();
        }
    }

    private void evaluateRotation() {
        if (rotationBeforeReacting == null) return;

        if (shouldTriggerCheck(rotationBeforeReacting.getYaw(), rotationBeforeReacting.getPitch())) {
            FailsafeManager.getInstance().possibleDetection(this);
        }

        rotationBeforeReacting = null;
    }

    private boolean shouldTriggerCheck(double newYaw, double newPitch) {
        double yawDiff = Math.abs(newYaw - mc.thePlayer.rotationYaw) % 360;
        double pitchDiff = Math.abs(newPitch - mc.thePlayer.rotationPitch) % 360;

        if (yawDiff >= Config.getInstance().FAILSAFE_ROTATION_SENSITIVITY || pitchDiff >= Config.getInstance().FAILSAFE_ROTATION_SENSITIVITY) {
            return true;
        }

        return false;
    }
  

    private static RotationFailsafe instance;
    public static RotationFailsafe getInstance() {
        if (instance == null) {
            instance = new RotationFailsafe();
        }

        return instance;
    }

}
