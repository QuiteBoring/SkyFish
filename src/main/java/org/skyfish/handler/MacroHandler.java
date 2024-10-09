package org.skyfish.handler;

import net.minecraftforge.common.MinecraftForge;
import org.skyfish.util.LogUtils;

public class MacroHandler {

    public void onEnable() {
        LogUtils.sendSuccess("Macro Enabled");
    }

    public void onDisable() {
        LogUtils.sendSuccess("Macro Disabled");
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
    public Step currentStep = Step.NONE;
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