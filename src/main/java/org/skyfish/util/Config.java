package org.skyfish.util;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.awt.Color;
import java.io.File;

public class Config extends Vigilant {

    public Config() {
        super(new File("./config/SkyFish.toml"));
        initialize();
    }

    private static Config instance;    
    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        
        return instance;
    }

}