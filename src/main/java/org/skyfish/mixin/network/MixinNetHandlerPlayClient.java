package org.skyfish.mixin.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.*;
import net.minecraft.util.StringUtils;
import net.minecraftforge.common.MinecraftForge;
import org.skyfish.event.impl.*;
import org.skyfish.util.TablistUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient {

    @Unique
    private final List<String> skyfish$previousTablist = new ArrayList<>();
    @Unique
    private final List<String> skyfish$previousFooter = new ArrayList<>();

    @Inject(method = "handlePlayerListItem", at = @At(value = "RETURN"))
    public void handlePlayerListItem(S38PacketPlayerListItem packetIn, CallbackInfo ci) {
        List<String> tablist = new ArrayList<>();
        List<NetworkPlayerInfo> players = TablistUtils.playerOrdering.sortedCopy(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap());
        GuiPlayerTabOverlay tabOverlay = Minecraft.getMinecraft().ingameGUI.getTabList();

        for (NetworkPlayerInfo info : players) {
            tablist.add(StringUtils.stripControlCodes(tabOverlay.getPlayerName(info)));
        }

        if (tablist.equals(skyfish$previousTablist)) return;
        skyfish$previousTablist.clear();
        skyfish$previousTablist.addAll(tablist);
        new UpdateTablistEvent(tablist, System.currentTimeMillis()).post();
    }

    @Inject(method = "handlePlayerListHeaderFooter", at = @At("RETURN"))
    public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn, CallbackInfo ci) {
        List<String> footer = new ArrayList<>();
        if (packetIn.getFooter() == null) return;
        for (String s : packetIn.getFooter().getFormattedText().split("\n")) {
            footer.add(StringUtils.stripControlCodes(s));
        }
        if (footer.equals(skyfish$previousFooter)) return;
        skyfish$previousFooter.clear();
        skyfish$previousFooter.addAll(footer);
        new UpdateTablistFooterEvent(footer).post();
    }
    
}