package org.skyfish.feature.impl;

import org.skyfish.feature.Feature;
import org.skyfish.util.Config;

public class UngrabMouse extends Feature {

    private boolean toggled;

    public UngrabMouse() {
        super("Ungrab Mouse");
    }

    @Override
    public void start() {
        try {
            if (Config.getInstance().FEATURE_UNGRAB_MOUSE) {
                ungrabMouse();
                toggled = true;
            }
        } catch (Exception ignored) {}

        super.start();
    }

    @Override
    public void stop() {
        try {
            if (toggled) {
                regrabMouse();
                toggled = false;
            } 
        } catch (Exception ignored) {}
        
        super.stop();
    }

    private static UngrabMouse instance;
    public static UngrabMouse getInstance() {
        if (instance == null) {
            instance = new UngrabMouse();
        }

        return instance;
    }

}