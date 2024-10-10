package org.skyfish.failsafe;

public class FailsafeManager {

    private static FailsafeManager instance;
    public static FailsafeManager getInstance() {
        if (instance == null) {
            instance = new FailsafeManager();
        }

        return instance;
    }
  
}
