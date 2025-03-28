package org.skyfish.util;

public class Clock {

    private long remainingTime;
    private boolean paused;
    private boolean scheduled;
    private long endTime;

    public void schedule(long milliseconds) {
        this.endTime = System.currentTimeMillis() + milliseconds;
        this.remainingTime = milliseconds;
        this.scheduled = true;
        this.paused = false;
    }

    public void schedule(double milliseconds) {
        this.endTime = (System.currentTimeMillis() + (long) milliseconds);
        this.remainingTime = (long) milliseconds;
        this.scheduled = true;
        this.paused = false;
    }

    public long getRemainingTime() {
        if (paused) {
            return remainingTime;
        }
        if (endTime - System.currentTimeMillis() < 0) {
            return 0;
        }
        return endTime - System.currentTimeMillis();
    }

    public boolean passed() {
        return System.currentTimeMillis() >= endTime;
    }

    public void pause() {
        if (scheduled && !paused) {
            remainingTime = endTime - System.currentTimeMillis();
            paused = true;
        }
    }

    public void resume() {
        if (scheduled && paused) {
            endTime = System.currentTimeMillis() + remainingTime;
            paused = false;
        }
    }

    public void reset() {
        scheduled = false;
        paused = false;
        endTime = 0;
        remainingTime = 0;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public long getEndTime() {
        return endTime;
    }

}