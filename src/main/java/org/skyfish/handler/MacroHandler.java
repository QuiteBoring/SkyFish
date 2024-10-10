package org.skyfish.handler;

import net.minecraftforge.common.MinecraftForge;
import org.skyfish.feature.FeatureManager;
import org.skyfish.util.LogUtils;

public class MacroHandler {

    @SubscribeEvent
    public void onTick() {
        if (mc.theWorld == null || mc.thePlayer == null || !isEnabled()) return;

        if (mc.currentScreen != null) {
            if (!isPaused()) pauseMacro();
        } else if ((mc.currentScreen == null) ) {
            if (isPaused()) unpauseMacro();
        }
    }

    public void onEnable() {
        setStep(Step.FIND_ROD);
        LogUtils.sendSuccess("Macro Enabled");
        FeatureManager.getInstance().enableAll();
    }

    public void onDisable() {
        setStep(Step.NONE);
        LogUtils.sendSuccess("Macro Disabled");
        FeatureManager.getInstance().disableAll();
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
        } else {
            onDisable();
        }
    }

    public void toggleMacro() {
        setEnabled(!isEnabled());
    }

    public void pauseMacro()  {
        paused = true;
    }

    public void unpauseMacro()  {
        paused = false; 
    }

    public boolean isPaused() {
        return paused;
    }

    private boolean paused = false;
    private Step currentStep = Step.NONE;

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
        CATCH;
    }

    private static MacroHandler instance;
    public static MacroHandler getInstance() {
        if (instance == null) {
            instance = new MacroHandler();
        }

        return instance;
    }

}