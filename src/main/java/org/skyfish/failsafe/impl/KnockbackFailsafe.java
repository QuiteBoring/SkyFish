package org.skyfish.failsafe.impl;

import net.minecraft.network.play.server.*;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.failsafe.*;
import org.skyfish.util.Config;
import org.skyfish.SkyFish;

public class KnockbackFailsafe extends Failsafe {

    public KnockbackFailsafe() {
        super(Failsafe.Type.KNOCKBACK);
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Config.getInstance().FAILSAFE_KNOCKBACK) return;

        if (event.packet instanceof S12PacketEntityVelocity) {
            if (((S12PacketEntityVelocity) event.packet).getEntityID() != this.mc.thePlayer.getEntityId()) return;
            if (((S12PacketEntityVelocity) event.packet).getMotionY() < (Config.getInstance().FAILSAFE_VERITCAL_KNOCKBACK_THRESHOLD * 1000)) return;

            FailsafeManager.getInstance().possibleDetection(this);
        }
    }
  
    private static KnockbackFailsafe instance;
    public static KnockbackFailsafe getInstance() {
        if (instance == null) {
            instance = new KnockbackFailsafe();
        }

        return instance;
    }

}
