package org.skyfish.feature.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraftforge.common.util.Constants;
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

    @Override
    public void onTick() {
        List<EntityArmorStand> armorstands =  mc.theWorld.loadedEntityList.stream().map(EntityArmorStand::new).collect(Collectors.toList());

        double playerX = Player.getX();
        double playerY = Player.getY();
        double playerZ = Player.getZ();

        for (EntityArmorStand armorstand : armorstands) {
            double distance = Math.sqrt(Math.pow(armorstand.getPosX() - playerX, 2) + Math.pow(armorstand.getPosY() - playerY, 2) + Math.pow(armorstand.getPosZ() - playerZ, 2));
            if (distance > 40 || armorstand.ticksExisted > 3600) continue;
            EntityLivingBase as = (EntityLivingBase) armorstand;
            ItemStack head = as.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            if (head.isEmpty()) {
                continue;
            }
            int type = getFlareType(head);

            if (type == -1) {
                continue;
            }
        }
    }
    
    private int getFlareType(ItemStack head) {
        for (int i = 0; i < Flare.length; i++) {
            if (head.hasTagCompound() && head.getTagCompound().toString().contains(Flare[i])) {
                return i;
            }
        }
        return -1;
    }
    
    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.entity;
    
        if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
            detectOrb((EntityArmorStand) entity);
        }
    }

    public boolean shouldPlace() {
        return flare != null && !flare.isDead;
    }
    
    public void detectOrb(EntityArmorStand entity) {
        String nameTag = entity.getCustomNameTag();
        Orb orb = Orb.getByName(nameTag);
        
        if (orb != null && orb.isInRadius(entity.getDistanceSqToEntity(mc.thePlayer))) { 
            Matcher matcher = ORB_PATTERN.matcher(StringUtils.stripControlCodes(nameTag));

            if (matcher.matches()) {
                ItemStack helmet = entity.getCurrentArmor(3);
                
                if (helmet != null && (flare == null || flare.isDead)) {
                    flare = entity;
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
