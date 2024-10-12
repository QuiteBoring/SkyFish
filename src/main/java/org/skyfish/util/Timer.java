package org.skyfish.util;

public class Timer {

    private long lastMS;
    private boolean happened;

    public Timer() {
        this.lastMS = System.currentTimeMillis();
        this.happened = false;
    }

    public boolean hasElasped(long ms) {
        if ((System.currentTimeMillis() - this.lastMS) >= ms) {
            return true;
        }

        return false;
    }
    
    public boolean hasElaspedOnce(long ms) {
        if (this.happened) return false;
        if ((System.currentTimeMillis() - this.lastMS) >= ms) {
            this.happened = true;
            return true;
        }

        return false;
    }

    public void reset() {
        this.lastMS = System.currentTimeMillis();
        this.happened = false;
    }

}