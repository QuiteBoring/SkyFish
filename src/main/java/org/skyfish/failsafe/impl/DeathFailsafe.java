package org.skyfish.failsafe.impl;

import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.skyfish.failsafe.*;
import org.skyfish.util.Config;

public class DeathFailsafe extends Failsafe {

    public DeathFailsafe( ) {
        super(Failsafe.Type.DEATH);
    }

    @Override
    public void onChat(ClientChatReceivedEvent event) {
        if (!Config.getInstance().FAILSAFE_DEATH) return;
        String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (msg.contains(":")) return;
        if (msg.contains("☠ You")) {
            FailsafeManager.getInstance().possibleDetection(this);
        }
    }

    private static DeathFailsafe instance;
    public static DeathFailsafe getInstance() {
        if (instance == null) {
            instance = new DeathFailsafe();
        }

        return instance;
    }

}
