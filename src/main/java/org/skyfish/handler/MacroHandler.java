package org.skyfish.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.util.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.skyfish.failsafe.FailsafeManager;
import org.skyfish.feature.FeatureManager;
import org.skyfish.util.LogUtils;
import org.skyfish.util.helper.Rotation;

public class MacroHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private MovingObjectPosition mainLookAtBlock = null;

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null || !isEnabled()) return;
        if (mc.currentScreen != null) {
            if (!isPaused()) {
                lastStep = getStep();
                pauseMacro();
                setStep(Step.NONE);
            }
        } else if ((mc.currentScreen == null) ) {
            if (isPaused()) {
                unpauseMacro();
                setStep(lastStep);
            };
        }
    }

    public Rotation getAngle() {
        if (mainLookAtBlock == null) {
            mainLookAtBlock  = mc.thePlayer.rayTrace(100.0, 1.0f);
        }

        BlockPos blockPos = new BlockPos(mainLookAtBlock.getBlockPos().getX() + 1, mainLookAtBlock.getBlockPos().getY(), mainLookAtBlock.getBlockPos().getZ());
        Vec3 playerPos = mc.thePlayer.getPositionVector();
        float pitchOffset = (float) ((Math.random() * (2.5 - -2.5)) + -2.5);
        float yawOffset = (float) ((Math.random() * (2.5 - -2.5)) + -2.5);
        double diffX = blockPos.getX() - playerPos.xCoord - 0.5;
        double diffY = blockPos.getY() - (playerPos.yCoord + mc.thePlayer.getEyeHeight()) + 0.5;
        double diffZ = blockPos.getZ() - playerPos.zCoord + 0.5;
        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX))) - 90.0f;
        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float pitch = (float) (-(Math.toDegrees(Math.atan2(diffY, dist))));
        return new Rotation(
                mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw) + yawOffset,
                mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch) + pitchOffset
        );
    }

    public void onEnable() {
        AudioHandler.getInstance().setSoundBeforeChange(mc.gameSettings.getSoundLevel(SoundCategory.MASTER));
        FailsafeManager.getInstance().reset();
        mainLookAtBlock = mc.thePlayer.rayTrace(100.0, 1.0f);
        LogUtils.sendSuccess("Macro Enabled");
        unpauseMacro();
    }

    public void onDisable() {
        AudioHandler.getInstance().resetSound();
        FailsafeManager.getInstance().reset();
        mainLookAtBlock = null;
        LogUtils.sendSuccess("Macro Disabled");
        pauseMacro();
    }

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public boolean isEnabled() {
        return currentStep != Step.NONE;
    }

    public void setEnabled(boolean enabled) {
        if (enabled) {
            onEnable();
            setStep(Step.FIND_ROD);
        } else {
            onDisable();
            setStep(Step.NONE);
        }
    }

    public void toggleMacro() {
        setEnabled(!isEnabled());
    }

    public void pauseMacro()  {
        paused = true;
        FeatureManager.getInstance().disableAll();
    }

    public void unpauseMacro()  {
        paused = false; 
        FeatureManager.getInstance().enableAll();
    }

    public boolean isPaused() {
        return paused;
    }

    private boolean paused = false;
    private Step currentStep = Step.NONE;
    private Step lastStep = Step.NONE;

    public Step getStep() {
        return currentStep;
    }

    public void setStep(Step currentStep) {
        this.currentStep = currentStep;
    }

    public static enum Step {
        NONE,
        SUSPEND,
        FIND_ROD,
        THROW_ROD,
        WAIT_FOR_CATCH,
        CATCH,
        FIND_WEAPON,
        ROTATE_DOWN,
        KILL,
        ROTATE_BACK;
    }

    private static MacroHandler instance;
    public static MacroHandler getInstance() {
        if (instance == null) {
            instance = new MacroHandler();
        }

        return instance;
    }

}
