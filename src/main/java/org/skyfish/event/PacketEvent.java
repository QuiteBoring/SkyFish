package org.skyfish.event;

import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketEvent extends Event {

    public final Packet<?> packet;

    public PacketEvent(final Packet<?> packet) {
        this.packet = packet;
    }

    public static class Receive extends PacketEvent {
        public Receive(final Packet<?> packet) {
            super(packet);
        }
    }

    public static class Send extends PacketEvent {
        public Send(final Packet<?> packet) {
            super(packet);
        }
    }

    public boolean post() {
        MinecraftForge.EVENT_BUS.post(this);
        return isCancelable() && isCanceled();
    }
    
}
