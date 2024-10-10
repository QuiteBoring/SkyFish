package org.skyfish.util;

public class Timer {

    private long lastMS;

    public Timer() {
        lastMS = System.currentTimeMillis();
    }

    public boolean hasElasped(long ms) {
        if ((System.currentTimeMillis() - lastMS) >= ms) {
            return true;
        }

        return false;
    }

    public void reset() {
        lastMS = System.currentTimeMillis();
    }

}