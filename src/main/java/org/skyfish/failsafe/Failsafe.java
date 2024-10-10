package org.skyfish.failsafe;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.PacketReceiveEvent;

public class Failsafe {

    private final Failsafe.Type type;
  
    public Failsafe(Failsafe.Type type) {
        this.type = type;
    }

    public Failsafe.Type getType() {
        return type;
    }

    public void onTick() {}
    public void onChat(ClientChatReceivedEvent event) {}
    public void onPacketReceive(PacketReceiveEvent event) {}
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {}

    public int getPriority() {
        return type.getPriority();
    }

    public static enum Type {
        DEATH(6),
        TELEPORT(5),
        ROTATION(4),
        EVACUATE(1),
        DISCONNECT(1),
        ITEM_CHANGE(3),
        WORLD_CHANGE(2),
        FULL_INVENTORY(3);

        private final int priority;
        private Type(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
  
}
