package org.skyfish.feature.impl;

import org.skyfish.feature.Feature;

public class AutoKill extends Feature {

    public AutoKill() {
        super("AutoKill");
    }

    private static AutoKill instance;
    public static AutoKill getInstance() {
        if (instance == null) {
            instance = new AutoKill();
        }

        return instance;
    }

}