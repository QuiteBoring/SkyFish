package org.skyfish.handler;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.skyfish.event.impl.*;
import org.skyfish.util.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameStateHandler {
    
    private final Minecraft mc = Minecraft.getMinecraft();
    private final Pattern areaPattern = Pattern.compile("Area:\\s(.+)");
    private final Pattern serverClosingPattern = Pattern.compile("Server closing: (?<minutes>\\d+):(?<seconds>\\d+) .*");
    
    private Location lastLocation = Location.TELEPORTING;
    private Location location = Location.TELEPORTING;
    private BuffState cookieBuffState = BuffState.UNKNOWN;
    private BuffState godPotState = BuffState.UNKNOWN;
    private Optional<Integer> serverClosingSeconds = Optional.empty();
    private String serverIP;

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event) {
        lastLocation = location;
        location = Location.TELEPORTING;
        serverClosingSeconds = Optional.empty();
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (mc.getCurrentServerData() != null && mc.getCurrentServerData().serverIP != null) {
            serverIP = mc.getCurrentServerData().serverIP;
        }
    }

    @SubscribeEvent
    public void onUpdateTabFooter(UpdateTablistFooterEvent event) {
        if (mc.theWorld == null || mc.thePlayer == null) {
            return;
        }

        checkBuffsTabList(event.getFooter());
    }
    
    @SubscribeEvent
    public void onTablistUpdate(UpdateTablistEvent event) {
        if (event.getTablist().isEmpty()) return;

        List<String> tabList = new ArrayList<>(event.getTablist());
        List<String> scoreboardLines = ScoreboardUtils.getScoreboardLines(true);
        boolean foundLocation = false;

        if (tabList.size() == 1 && InventoryUtils.isInventoryEmpty(mc.thePlayer)) {
            lastLocation = location;
            location = Location.LIMBO;
            return;
        }

        for (String cleanedLine : tabList) {
            if (cleanedLine.matches(areaPattern.pattern())) {
                Matcher matcher = areaPattern.matcher(cleanedLine);
                
                if (matcher.find()) {
                    String area = matcher.group(1);

                    for (Location island : Location.values()) {
                        if (area.equals(island.getName())) {
                            lastLocation = location;
                            location = island;
                            foundLocation = true;
                            break;
                        }
                    }

                    if (foundLocation) continue;
                }
            }
        }

        if (foundLocation) return;

        if (!ScoreboardUtils.getScoreboardTitle().contains("SKYBLOCK") && !scoreboardLines.isEmpty() && scoreboardLines.get(0).contains("www.hypixel.net")) {
            lastLocation = location;
            location = Location.LOBBY;
            return;
        }


        if (location != Location.TELEPORTING) {
            lastLocation = location;
        }

        location = Location.TELEPORTING;
    }

    @SubscribeEvent
    public void onUpdateScoreboardLine(UpdateScoreboardEvent event) {
        Matcher serverClosingMatcher = serverClosingPattern.matcher(event.getLine());

        if (serverClosingMatcher.find()) {
            int minutes = Integer.parseInt(serverClosingMatcher.group("minutes"));
            int seconds = Integer.parseInt(serverClosingMatcher.group("seconds"));
            serverClosingSeconds = Optional.of(minutes * 60 + seconds);
        }
    }

    public void checkBuffsTabList(List<String> footerString) {
        boolean foundGodPotBuff = false;
        boolean foundCookieBuff = false;
        boolean loaded = false;

        for (String line : footerString) {
            if (line.contains("Active Effects")) {
                loaded = true;
            }
            if (line.contains("You have a God Potion active!")) {
                foundGodPotBuff = true;
                continue;
            }
            if (line.contains("Cookie Buff")) {
                foundCookieBuff = true;
                continue;
            }
            if (foundCookieBuff) {
                if (line.contains("Not active")) {
                    foundCookieBuff = false;
                }
                break;
            }
        }

        if (!loaded) {
            cookieBuffState = BuffState.UNKNOWN;
            godPotState = BuffState.UNKNOWN;
            return;
        }

        cookieBuffState = foundCookieBuff ? BuffState.ACTIVE : BuffState.NOT_ACTIVE;
        godPotState = foundGodPotBuff ? BuffState.ACTIVE : BuffState.NOT_ACTIVE;
    }

    public Optional<Integer> getServerClosingSeconds() {
        return serverClosingSeconds;
    }

    public Location getLocation() {
        return location;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public String getServerIP() {
        return serverIP;
    }

    public BuffState getCookieBuffState() {
        return cookieBuffState;
    }

    public BuffState getGodPotState() {
        return godPotState;
    }

    public enum BuffState {
        ACTIVE,
        NOT_ACTIVE,
        UNKNOWN;
    }

    public enum Location {

        PRIVATE_ISLAND("Private Island"),
        HUB("Hub"),
        THE_PARK("The Park"),
        THE_FARMING_ISLANDS("The Farming Islands"),
        SPIDER_DEN("Spider's Den"),
        THE_END("The End"),
        CRIMSON_ISLE("Crimson Isle"),
        GOLD_MINE("Gold Mine"),
        DEEP_CAVERNS("Deep Caverns"),
        DWARVEN_MINES("Dwarven Mines"),
        CRYSTAL_HOLLOWS("Crystal Hollows"),
        JERRY_WORKSHOP("Jerry's Workshop"),
        DUNGEON_HUB("Dungeon Hub"),
        LIMBO("UNKNOWN"),
        LOBBY("PROTOTYPE"),
        GARDEN("Garden"),
        DUNGEON("Dungeon"),
        UNKNOWN(""),
        TELEPORTING("Teleporting");

        private final String name;

        Location(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public void initialize() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static GameStateHandler instance;
    public static GameStateHandler getInstance() {
        if (instance == null) {
            instance = new GameStateHandler();
        }
        return instance;
    }

}