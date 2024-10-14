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
            category = "Auto Kill"
    )
    public boolean AUTO_KILL = true; 

    @Property(
            type = PropertyType.SELECTOR,
            name = "Weapon",
            description = "Select which weapon to use",
            options = { "Wither Blade", "Fire Veil" },
            category = "Auto Kill"
    )
    public int AUTO_KILL_WEAPON = 0;

    @Property(
            type = PropertyType.SLIDER,
            name = "Hype Cap",
            description = "Limits how much you can hype (0 =  no limit)",
            category = "Auto Kill",
            min = 0,
            max = 20
    )
    public int AUTO_KILL_HYPE_CAP = 0;  
    
    @Property(
            type = PropertyType.SWITCH,
            name = "Hype Fishing",
            description = "Removes the extra step of rotating down",
            category = "Auto Kill"
    )
    public boolean AUTO_KILL_HYPE_FISHING = true; 

    @Property(
            type = PropertyType.SLIDER,
            name = "Mob Limit",
            description = "When to start killing mobs (0 = disable barn fishing)",
            category = "Auto Kill",
            subcategory = "Barn Fishing",
            min = 0,
            max = 20
    )
    public int AUTO_KILL_MOB_LIMIT = 0;  
    
    @Property(
            type = PropertyType.SWITCH,
            name = "Render Box",
            description = "Renders a box over mobs spawned",
            category = "Auto Kill",
            subcategory = "Render Box"
    )
    public boolean AUTO_KILL_RENDER_BOX = true;


    @Property(
            type = PropertyType.COLOR,
            name = "Render Box Color",
            description = "Color of the box being rendered",
            category = "Auto Kill",
            subcategory = "Render Box"
    )
    public Color AUTO_KILL_RENDER_BOX_COLOR = new Color(255, 255, 255, 155);

    // region Failsafes

    @Property(
            type = PropertyType.SWITCH,
            name = "Death Failsafe",
            description = "Enable/disable death failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_DEATH = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Teleport Failsafe",
            description = "Enable/disable teleport failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_TELEPORT = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Rotation Failsafe",
            description = "Enable/disable rotation failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_ROTATION = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Disconnect Failsafe",
            description = "Enable/disable disconnect failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_DISCONNECT = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Evacuate Failsafe",
            description = "Enable/disable evacuate failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_EVACUATE = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Item Change Failsafe",
            description = "Enable/disable item change failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_ITEM_CHANGE = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Knockback Failsafe",
            description = "Enable/disable knockback failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_KNOCKBACK = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Full Inventory Failsafe",
            description = "Enable/disable full inventory failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_FULL_INVENTORY = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "World Change Failsafe",
            description = "Enable/disable world change failsafe",
            category = "Failsafes"
    )
    public boolean FAILSAFE_WORLD_CHANGE = true;
    
    @Property(
            type = PropertyType.SLIDER,
            name = "Vertical Knockback Threshold",
            description = "Minimum vertical knockback to trigger failsafe (value * 1000)",
            category = "Failsafes",
            subcategory = "Options",
            min = 2,
            max = 10
    )
    public int FAILSAFE_VERITCAL_KNOCKBACK_THRESHOLD = 4;      

    @Property(
            type = PropertyType.SLIDER,
            name = "Rotation Sensitivity",
            description = "Change Sensitivity (lower = stricter)",
            category = "Failsafes",
            subcategory = "Options",
            min = 1,
            max = 30
    )
    public int FAILSAFE_ROTATION_SENSITIVITY = 7;

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

    public String[] getWeapon() {
        String[][] weapons = new String[][] { new String[] { "Hyperion", "Scylla", "Astraea", "Valkyrie" }, new String[] { "Fire Veil" }};
        return weapons[AUTO_KILL_WEAPON];
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
