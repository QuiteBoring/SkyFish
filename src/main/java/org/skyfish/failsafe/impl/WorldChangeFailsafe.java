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
            FailsafeManager.getInstance().possibleDetection(this);
        }
    }

    @Override
    public void onChat(ClientChatReceivedEvent event) {
        if (!Config.getInstance().FAILSAFE_WORLD_CHANGE) return;
        if (FailsafeManager.getInstance().getTriggeredFailsafe().isPresent() && FailsafeManager.getInstance().getTriggeredFailsafe().get().getType() != Failsafe.Type.WORLD_CHANGE) return;

        String message = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (message.contains(":")) return;
        if (message.contains("You were spawned in Limbo.") || message.contains("/limbo") || message.startsWith("A kick occurred in your connection")) {
            FailsafeManager.getInstance().possibleDetection(this);
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
