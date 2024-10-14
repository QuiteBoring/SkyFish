package org.skyfish.failsafe.impl;

import org.skyfish.failsafe.*;
import org.skyfish.SkyFish;
import org.skyfish.util.Config;
import org.skyfish.util.InventoryUtils;

public class FullInventoryFailsafe extends Failsafe {

    public FullInventoryFailsafe() {
        super(Failsafe.Type.FULL_INVENTORY);
    }

    @Override
    public void onTick() {
        if (!Config.getInstance().FAILSAFE_FULL_INVENTORY) return;

        if (InventoryUtils.isInventoryFull(this.mc.thePlayer)) {
            FailsafeManager.getInstance().possibleDetection(this);
        }
    }

    private static FullInventoryFailsafe instance;
    public static FullInventoryFailsafe getInstance() {
        if (instance == null) {
            instance = new FullInventoryFailsafe();
        }

        return instance;
    }
    
}

