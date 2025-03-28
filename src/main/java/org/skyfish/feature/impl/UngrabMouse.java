package org.skyfish.feature.impl;

import net.minecraft.util.MouseHelper;
import org.lwjgl.input.Mouse;
import org.skyfish.feature.Feature;
import org.skyfish.util.Config;

public class UngrabMouse extends Feature {

    public UngrabMouse() {
        super("Ungrab Mouse");
    }

    @Override
    public void start() {
        try {
            if (Config.getInstance().FEATURE_UNGRAB_MOUSE) {
                ungrabMouse();
            }
        } catch (Exception ignored) {}

        super.start();
    }

    @Override
    public void stop() {
        try {
            if (mouseUngrabbed) {
                regrabMouse();
            } 
        } catch (Exception ignored) {}
        
        super.stop();
    }

    private boolean mouseUngrabbed;
    private MouseHelper oldMouseHelper;

    public void ungrabMouse() {
        if (!Mouse.isGrabbed() || mouseUngrabbed) return;
        mc.gameSettings.pauseOnLostFocus = false;
        oldMouseHelper = mc.mouseHelper;
        oldMouseHelper.ungrabMouseCursor();
        mc.inGameHasFocus = true;
        mc.mouseHelper = new MouseHelper() {
            @Override
            public void mouseXYChange() {
            }

            @Override
            public void grabMouseCursor() {
            }

            @Override
            public void ungrabMouseCursor() {
            }
        };
        mouseUngrabbed = true;
    }

    public void regrabMouse() {
        regrabMouse(false);
    }

    public void regrabMouse(boolean force) {
        if (!mouseUngrabbed && !force) return;
        mc.mouseHelper = oldMouseHelper;
        if (mc.currentScreen == null || force) {
            mc.mouseHelper.grabMouseCursor();
        }
        mouseUngrabbed = false;
    }
    
    private static UngrabMouse instance;
    public static UngrabMouse getInstance() {
        if (instance == null) {
            instance = new UngrabMouse();
        }

        return instance;
    }

}
