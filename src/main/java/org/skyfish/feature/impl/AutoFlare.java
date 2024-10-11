package org.skyfish.feature.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.skyfish.feature.Feature;
import org.skyfish.util.LogUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoFlare extends Feature {

    private final Pattern ORB_PATTERN = Pattern.compile("[A-Za-z ]* (?<seconds>[0-9]*)s");
    private EntityArmorStand flare = null;
    
    public AutoFlare() {
        super("AutoFlare");
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.entity;
    
        if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
            detectOrb(entity);
            LogUtils.sendSuccess("WOW");

            if (flare == null || flare.isDead) {
                detectFlare((EntityArmorStand) entity);
            }
        }
    }

    public boolean shouldPlace() {
        return flare != null && !flare.isDead;
    }
    
    public void detectFlare(EntityArmorStand entity) {
        if (entity.getDistanceToEntity(mc.thePlayer) > 40 || entity.ticksExisted > 3600) return;
        ItemStack helm = entity.getCurrentArmor(3);
        if (helm != null) {
            flare = entity;
            LogUtils.sendSuccess("Found Flare");
        }
    }
    
    public void detectOrb(Entity entity) {
        String nameTag = entity.getCustomNameTag();
        Orb orb = Orb.getByName(nameTag);
        
        if (orb != null && orb.isInRadius(entity.getDistanceSqToEntity(mc.thePlayer))) { 
            Matcher matcher = ORB_PATTERN.matcher(StringUtils.stripControlCodes(nameTag));

            if (matcher.matches()) {
                List<EntityArmorStand> armorStands = mc.theWorld.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(entity.posX - 0.1, entity.posY - 3, entity.posZ - 0.1, entity.posX + 0.1, entity.posY, entity.posZ + 0.1));
                if (!armorStands.isEmpty()) {
                    EntityArmorStand orbStand = null;

                    for (EntityArmorStand stand : armorStands) {
                        ItemStack helmet = stand.getCurrentArmor(3);
                        if (helmet != null) {
                            orbStand = stand;
                            LogUtils.sendSuccess("Found Orb");
                        }
                    }

                    if (flare == null || flare.isDead) {
                        flare = orbStand;
                    }
                }
            }
        }       
    }
    
    private static AutoFlare instance;
    public static AutoFlare getInstance() {
        if (instance == null) {
            instance = new AutoFlare();
        }

        return instance;
    }

    public static enum Orb {
        RADIANT("§aRadiant", 18*18),
        MANA_FLUX("§9Mana Flux", 18*18),
        OVERFLUX("§5Overflux", 18*18),
        PLASMAFLUX("§d§lPlasmaflux", 20*20);

        private String display;
        private int rangeSquared;
        
        private Orb(String display, int rangeSquared) {
            this.display = display;
            this.rangeSquared = rangeSquared;
        }    
        
         public boolean isInRadius(double distanceSquared) {
            return distanceSquared <= rangeSquared;
        }

        public static Orb getByName(String name) {
            for (Orb orb : values()) {
                if (name.startsWith(orb.display)) {
                    return orb;
                }
            }
            return null;
        }
    }

}
