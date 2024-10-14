package org.skyfish.failsafe.impl;

import net.minecraft.util.StringUtils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.skyfish.failsafe.*;
import org.skyfish.handler.GameStateHandler;
import org.skyfish.util.Config;

public class EvacuateFailsafe extends Failsafe {

    public EvacuateFailsafe() {
        super(Failsafe.Type.EVACUATE);
    }

    @Override
    public void onTick() {
        if (!Config.getInstance().FAILSAFE_EVACUATE) return;

        GameStateHandler.getInstance().getServerClosingSeconds().ifPresent(seconds -> {
            if (seconds < 30) {
                FailsafeManager.getInstance().possibleDetection(this);
            }
        });
    }
    
    @Override
    public void onChat(ClientChatReceivedEvent event) {
        if (!Config.getInstance().FAILSAFE_EVACUATE) return;
        
        String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (!msg.contains(":") && msg.startsWith("You can't use this when the server is about to")) {
            FailsafeManager.getInstance().possibleDetection(this);
        }
    }   

    private static EvacuateFailsafe instance;
    public static EvacuateFailsafe getInstance() {
        if (instance == null) {
            instance = new EvacuateFailsafe();
        }

        return instance;
    }

}
