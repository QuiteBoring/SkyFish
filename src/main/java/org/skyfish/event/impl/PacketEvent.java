package org.skyfish.event.impl;

import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.skyfish.event.Event;

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
    
}
