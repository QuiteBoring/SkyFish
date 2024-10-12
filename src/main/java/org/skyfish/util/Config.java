package org.skyfish.util;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.awt.Color;
import java.io.File;

public class Config extends Vigilant {

    // region Auto Kill

    @Property(
            type = PropertyType.SWITCH,
            name = "AutoKill",
            description = "Enable feature to automatically kill mobs spawned",
            category = "AutoKill"
    )
    public boolean AUTO_KILL = true; 

    @Property(
            type = PropertyType.SWITCH,
            name = "Hype Fishing",
            description = "Removes the extra step of rotating down",
            category = "AutoKill"
    )
    public boolean AUTO_KILL_HYPE_FISHING = true; 

    @Property(
            type = PropertyType.SWITCH,
            name = "Barn Fishing",
            description = "Begins to kill mobs after a certain amount of mobs is spawned",
            category = "AutoKill",
            subcategory = "Barn Fishing"
    )
    public boolean AUTO_KILL_BARN_FISHING = true;

    @Property(
            type = PropertyType.SLIDER,
            name = "Mob Limit",
            description = "When to start killing mobs",
            category = "Auto Kill",
            subcategory = "Barn Fishing",
            min = 0,
            max = 20
    )
    public int AUTO_KILL_MOB_LIMIT = 7;  
    
    @Property(
            type = PropertyType.SWITCH,
            name = "Render Box",
            description = "Renders a box over mobs spawned",
            category = "AutoKill",
            subcategory = "Render Box"
    )
    public boolean AUTO_KILL_RENDER_BOX = true;


    @Property(
            type = PropertyType.SWITCH,
            name = "Render Box Color",
            description = "Color of the box being rendered",
            category = "AutoKill",
            subcategory = "Render Box"
    )
    public boolean AUTO_KILL_RENDER_BOX_COLOR = true;

    // region Failsafes

    @Property(
            type = PropertyType.SWITCH,
            name = "Play Sound",
            description = "Play an anvil sound when a failsafe is triggered",
            category = "Failsafes"
    )
    public boolean FAILSAFE_PLAY_SOUND = true;  

    // region Features

    @Property(
            type = PropertyType.SWITCH,
            name = "Ungrab Mouse",
            description = "Ungrabs mouse upon starting the macro",
            category = "Features"
    )
    public boolean FEATURE_UNGRAB_MOUSE = true;  

    @Property(
            type = PropertyType.SWITCH,
            name = "AutoCrouch",
            description = "Crouches while using the macro",
            category = "Features"
    )
    public boolean FEATURE_AUTO_CROUCH = true; 

    @Property(
            type = PropertyType.SWITCH,
            name = "AutoFlare",
            description = "Uses a flare or power orb when possible",
            category = "Features"
    )
    public boolean FEATURE_AUTO_FLARE = true;  

    @Property(
            type = PropertyType.SWITCH,
            name = "AutoTotem",
            description = "Uses Totem of Corruption when possible",
            category = "Features"
    )
    public boolean FEATURE_AUTO_TOTEM = true; 
    
    @Property(
            type = PropertyType.SWITCH,
            name = "AntiAFK",
            description = "Rotates mouse occasionally",
            category = "Features"
    )
    public boolean FEATURE_ANTI_AFK = false;

    // region Delay

    @Property(
            type = PropertyType.SLIDER,
            name = "Swap Slot (ms)",
            description = "Delay when swapping slots",
            category = "Delays",
            min = 0,
            max = 500
    )
    public int DELAYS_SWAP_SLOT = 100;  

    @Property(
            type = PropertyType.SLIDER,
            name = "Recast (ms)",
            description = "Delay when recasting",
            category = "Delays",
            min = 0,
            max = 500
    )
    public int DELAYS_RECAST = 100;  

    @Property(
            type = PropertyType.SLIDER,
            name = "CPS",
            description = "Hypes at the rate of which you click",
            category = "Delays",
            min = 5,
            max = 20
    )
    public int DELAYS_CPS = 10;  

    public Config() {
        super(new File("./config/SkyFish.toml"));
        initialize();
    }

    public int getDelay() {
        return getDelay((1000 / DELAYS_CPS) - 50);
    }

    public int getDelay(int inital) {
        return inital + (int) (Math.random() * 20) + 1;
    }

    private static Config instance;    
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        
        return instance;
    }

}
