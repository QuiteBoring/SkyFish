package org.skyfish.feature;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.PacketReceiveEvent;

public class Feature {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
    private boolean running;
  
    public Feature(String name) {
        this.name = name;
        this.running = false;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    public void onTick() {}
    public void onChat(ClientChatReceivedEvent event) {}
    public void onPacketReceive(PacketReceiveEvent event) {}

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public String getName() {
        return name;
    }
  
    public boolean isRunning() {
        return running;
    }

}
