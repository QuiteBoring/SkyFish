package org.skyfish.feature.impl;

import net.minecraft.client.settings.KeyBinding;
import org.skyfish.feature.Feature;
import org.skyfish.util.Config;

public class AutoCrouch extends Feature {

    public AutoCrouch() {
        super("AutoCrouch");
    }

    @Override
    public void onTick() {
        if (mc.currentScreen == null && !mc.gameSettings.keyBindSneak.isPressed() && Config.getInstance().FEATURE_AUTO_CROUCH) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        }
    }

    @Override
    public void start() {
        if (Config.getInstance().FEATURE_AUTO_CROUCH) KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }

    private static AutoCrouch instance;
    public static AutoCrouch getInstance() {
        if (instance == null) {
            instance = new AutoCrouch();
        }

        return instance;
    }

}