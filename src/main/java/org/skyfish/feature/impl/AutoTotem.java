package org.skyfish.feature.impl;

import org.skyfish.feature.Feature;

public class AutoTotem extends Feature {

    public AutoTotem() {
        super("AutoTotem");
    }

    private static AutoTotem instance;
    public static AutoTotem getInstance() {
        if (instance == null) {
            instance = new AutoTotem();
        }

        return instance;
    }

}