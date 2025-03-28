package org.skyfish.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.*;
import org.skyfish.util.helper.Rotation;

public class RotationHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private Rotation startRot = null;
    private Rotation endRot = null;
    private long startTime = -1;
    private long endTime = -1;
    private boolean done = true;

    public void easeToBlock(BlockPos block, long time) {
        Vec3 vec = new Vec3((block.getX() > 0) ? (block.getX() - 0.5) : (block.getX() + 0.5), (double) block.getY(), (block.getZ() > 0) ? (block.getZ() - 0.5) : (block.getZ() + 0.5));
        Vec3 playerPos = mc.thePlayer.getPositionVector();
        double diffX = vec.xCoord - playerPos.xCoord;
        double diffY = vec.yCoord - (playerPos.yCoord + mc.thePlayer.getEyeHeight()) + 0.5;
        double diffZ = vec.zCoord - playerPos.zCoord;
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0f;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float pitch = (float) (-(Math.toDegrees(Math.atan2(diffY, dist))));

        easeTo(
            normalizeYaw(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw)),
            clampPitch(pitch),
            time
        );
    }

    public void easeTo(float yaw, float pitch, long time) {
        done = false;
        startRot = new Rotation(normalizeYaw(mc.thePlayer.rotationYaw), clampPitch(mc.thePlayer.rotationPitch));
        endRot = new Rotation(normalizeYaw(yaw), clampPitch(pitch));
        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis() + time;
    }

    public void reset() {
        done = false;
    }

    public boolean isDone() {
        return done;
    }

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onWorldLastRender(RenderWorldLastEvent event) {
        if (System.currentTimeMillis() <= endTime) {
            mc.thePlayer.rotationYaw = normalizeYaw(interpolate(startRot.getYaw(), endRot.getYaw()));
            mc.thePlayer.rotationPitch = clampPitch(interpolate(startRot.getPitch(), endRot.getPitch()));
        } else if (!done) {
            done = true;
        }
    }

    private float interpolate(float start, float end) {
        return (end - start) * easeOutCubic((float) (System.currentTimeMillis() - startTime) / (endTime - startTime)) + start;
    }

    private float easeOutCubic(double number) {
        return (float) Math.max(0, Math.min(1, 1 - Math.pow(1 - number, 3)));
    }

    private float normalizeYaw(float yaw) {
        yaw = yaw % 360;
        if (yaw > 180) {
            yaw -= 360;
        } else if (yaw < -180) {
            yaw += 360;
        }
        return yaw;
    }

    private float clampPitch(float pitch) {
        return MathHelper.clamp_float(pitch, -90, 90);
    }

    private static RotationHandler instance;
    public static RotationHandler getInstance() {
        if (instance == null) {
            instance = new RotationHandler();
        }
        return instance;
    }
    
}
