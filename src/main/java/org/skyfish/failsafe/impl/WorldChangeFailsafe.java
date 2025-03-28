package org.skyfish.failsafe.impl;

import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.skyfish.failsafe.*;
import org.skyfish.feature.impl.FishingMacro;
import org.skyfish.handler.*;
import org.skyfish.util.Config;

public class WorldChangeFailsafe extends Failsafe {

    public WorldChangeFailsafe() {
        super(Failsafe.Type.WORLD_CHANGE);
    }
    
    @Override
    public void onTick() {
        if (!Config.getInstance().FAILSAFE_WORLD_CHANGE) return;
        if (FishingMacro.getInstance().startingLocation != GameStateHandler.getInstance().getLocation()) {
            FailsafeManager.getInstance().detection(this);
        }
    }

    private static WorldChangeFailsafe instance;
    public static WorldChangeFailsafe getInstance() {
        if (instance == null) {
            instance = new WorldChangeFailsafe();
        }

        return instance;
    }

}
