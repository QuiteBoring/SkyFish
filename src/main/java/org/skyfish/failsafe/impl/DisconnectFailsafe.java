package org.skyfish.failsafe.impl;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.failsafe.*;
import org.skyfish.util.Config;

public class DisconnectFailsafe extends Failsafe {

    public DisconnectFailsafe() {
        super(Failsafe.Type.DISCONNECT);
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        if (!Config.getInstance().FAILSAFE_DISCONNECT) return;
        FailsafeManager.getInstance().possibleDetection(this);
    }

    private static DisconnectFailsafe instance;
    public static DisconnectFailsafe getInstance() {
        if (instance == null) {
            instance = new DisconnectFailsafe();
        }

        return instance;
    }
  
}
