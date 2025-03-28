package org.skyfish.failsafe;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.handler.MacroHandler;

public class Failsafe {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final Failsafe.Type type;
  
    public Failsafe(Failsafe.Type type) {
        this.type = type;
    }

    public Failsafe.Type getType() {
        return type;
    }

    public void onTrigger() {
        MacroHandler.getInstance().setEnabled(false);
        FailsafeManager.getInstance().reset();
    }
    
    public void onTick() {}
    public void onChat(ClientChatReceivedEvent event) {}
    public void onPacketReceive(PacketEvent.Receive event) {}
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {}

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public String getName() {
        return type.getName();
    }
    
    public int getPriority() {
        return type.getPriority();
    }

    public static enum Type {
        DEATH("Death", 3),
        TELEPORT("Teleport", 5),
        ROTATION("Rotation", 4),
        EVACUATE("Evacuate", 1),
        KNOCKBACK("Knockback", 4),
        DISCONNECT("Disconnect", 1),
        ITEM_CHANGE("Item Change", 3),
        WORLD_CHANGE("World Change", 2),
        FULL_INVENTORY("Full Inventory", 3);

        private final String name;
        private final int priority;
        
        private Type(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }

        public String getName() {
            return name;
        }
        
        public int getPriority() {
            return priority;
        }
    }
  
}
