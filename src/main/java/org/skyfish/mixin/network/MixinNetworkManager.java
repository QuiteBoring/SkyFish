package org.skyfish.mixin.network;

import io.netty.channel.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.*;
import net.minecraft.scoreboard.*;
import net.minecraft.util.*;
import net.minecraftforge.common.MinecraftForge;
import org.skyfish.event.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Unique
    private final Map<Integer, String> skyfish$cachedScoreboard = new HashMap<>();

    @Inject(method = "channelRead0*", at = @At("HEAD"))
    private void read(ChannelHandlerContext context, Packet<?> packet, CallbackInfo callback) {
        if (packet.getClass().getSimpleName().startsWith("S")) {
            MinecraftForge.EVENT_BUS.register(new PacketReceiveEvent(packet));
        }

        if (Minecraft.getMinecraft().thePlayer == null || Minecraft.getMinecraft().theWorld == null) return;
        if (packet instanceof S3DPacketDisplayScoreboard || packet instanceof S3CPacketUpdateScore || packet instanceof S3DPacketDisplayScoreboard || packet instanceof S3EPacketTeams) {
            Scoreboard scoreboard = Minecraft.getMinecraft().thePlayer.getWorldScoreboard();
            Collection<Score> scores;
            try {
                scores = scoreboard.getSortedScores(scoreboard.getObjectiveInDisplaySlot(1));
            } catch (NullPointerException e) {
                return;
            }
            scores.removeIf(score -> score.getPlayerName().startsWith("#"));

            int index = 0;
            for (Score score : scores) {
                ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
                String string = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName());
                String clean = skyfish$cleanSB(string);
                if (!clean.equals(skyfish$cachedScoreboard.get(index)) || !skyfish$cachedScoreboard.containsKey(index)) {
                    skyfish$cachedScoreboard.put(index, clean);
                    MinecraftForge.EVENT_BUS.register(new UpdateScoreboardEvent(clean));
                }
                index++;
                if (index > 15) break;
            }
        }
    }

    @Unique
    private String skyfish$cleanSB(String scoreboard) {
        StringBuilder cleaned = new StringBuilder();

        for (char c : StringUtils.stripControlCodes(scoreboard).toCharArray()) {
            if (c >= 32 && c < 127 || c == 'ൠ') {
                cleaned.append(c);
            }
        }

        return cleaned.toString();
    }

}
