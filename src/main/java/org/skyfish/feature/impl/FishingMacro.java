package org.skyfish.feature.impl;

import org.skyfish.event.PacketReceiveEvent;
import org.skyfish.feature.Feature;

public class FishingMacro extends Feature {

    public FishingMacro() {
        super("Fishing Macro");
    }

    @Override
    public void onTick() {
        
    }

    private static FishingMacro instance;
    public static FishingMacro getInstance() {
        if (instance == null) {
            instance = new FishingMacro();
        }

        return instance;
    }

}