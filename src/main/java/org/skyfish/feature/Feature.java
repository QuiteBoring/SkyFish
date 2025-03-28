package org.skyfish.feature;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.skyfish.event.impl.PacketEvent;

public class Feature {

    protected final Minecraft mc = Minecraft.getMinecraft();
    private final String name;
  
    public Feature(String name) {
        this.name = name;
    }

    public void start() {}
    public void stop() {}

    public void onTick() {}
    public void onChat(ClientChatReceivedEvent event) {}
    public void onPacketReceive(PacketEvent.Receive event) {}

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public String getName() {
        return name;
    }

}
