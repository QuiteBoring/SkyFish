package org.skyfish.failsafe.impl;

import net.minecraft.network.play.server.*;
import net.minecraft.util.Vec3;
import org.skyfish.SkyFish;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.failsafe.*;
import org.skyfish.util.Config;

public class TeleportFailsafe extends Failsafe {
    
    public TeleportFailsafe() {
        super(Failsafe.Type.TELEPORT);
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Config.getInstance().FAILSAFE_TELEPORT) return;
        if (!(event.packet instanceof S08PacketPlayerPosLook)) return;
        
        S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) event.packet;
        Vec3 currentPlayerPos = mc.thePlayer.getPositionVector();
        Vec3 packetPlayerPos = new Vec3(
                packet.getX() + (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X) ? currentPlayerPos.xCoord : 0),
                packet.getY() + (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y) ? currentPlayerPos.yCoord : 0),
                packet.getZ() + (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z) ? currentPlayerPos.zCoord : 0)
        );
        
        double distance = currentPlayerPos.distanceTo(packetPlayerPos);  
        if (distance >= 3) FailsafeManager.getInstance().possibleDetection(this);
    }
    
    private static TeleportFailsafe instance;
    public static TeleportFailsafe getInstance() {
        if (instance == null) {
            instance = new TeleportFailsafe();
        }

        return instance;
    }

}
