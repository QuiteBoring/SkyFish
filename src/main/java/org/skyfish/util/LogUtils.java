package org.skyfish.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class LogUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public synchronized static void sendLog(ChatComponentText chat) {
        if (mc.thePlayer != null) {
            mc.thePlayer.addChatMessage(chat);
        } else if (mc.thePlayer == null) {
            System.out.println("[SkyFish] " + chat.getUnformattedText());
        }
    }

    public static void sendSuccess(String message) {
        sendLog(new ChatComponentText("§2§lSkyFish §8» §a" + message));
    }

    public static void sendError(String message) {
        sendLog(new ChatComponentText("§4§lSkyFish §8» §c" + message));
    }

}
