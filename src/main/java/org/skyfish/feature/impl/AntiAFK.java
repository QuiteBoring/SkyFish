package org.skyfish.feature.impl;

import org.skyfish.feature.Feature;
import org.skyfish.handler.MacroHandler;
import org.skyfish.handler.RotationHandler;
import org.skyfish.util.*;
import org.skyfish.util.helper.Rotation;

import java.util.Random;

public class AntiAFK extends Feature {

    private Timer delayTimer = new Timer();

    public AntiAFK() {
        super("AntiAFK");
    }

    @Override
    public void onTick() {
        if (MacroHandler.getInstance().getStep() != MacroHandler.Step.WAIT_FOR_CATCH || !delayTimer.hasElasped(10000) || !Config.getInstance().FEATURE_ANTI_AFK) return;
        Rotation rotation = null;

        if (Config.getInstance().AUTO_KILL_HYPE_FISHING) {
            float yawOffset = new Random().nextInt(8) - 4;
            float newYaw = AngleUtils.normalizeAngle((float) AngleUtils.wrapAngleTo180(mc.thePlayer.rotationYaw + yawOffset));
            rotation = new Rotation(newYaw, mc.thePlayer.rotationPitch);
        } else {
            rotation = MacroHandler.getInstance().getAngle();
        }

        if (RotationHandler.getInstance().isDone()) {
            RotationHandler.getInstance().easeTo(rotation.getYaw(), rotation.getPitch(), 300L);
            delayTimer.reset();
        }
    }

    private MovementState currentMovement = MovementState.LEFT;
    private enum MovementState {
        LEFT,
        RIGHT;
    }

    private static AntiAFK instance;
    public static AntiAFK getInstance() {
        if (instance == null) {
            instance = new AntiAFK();
        }

        return instance;
    }

}