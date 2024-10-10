package org.skyfish.feature.impl;

import org.skyfish.feature.Feature;

public class AutoFlare extends Feature {

    public AutoFlare() {
        super("AutoFlare");
    }

    private static AutoFlare instance;
    public static AutoFlare getInstance() {
        if (instance == null) {
            instance = new AutoFlare();
        }

        return instance;
    }

}