package org.skyfish.feature.impl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.skyfish.feature.Feature;
import org.skyfish.util.LogUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoFlare extends Feature {

    private final Pattern ORB_PATTERN = Pattern.compile("[A-Za-z ]* (?<seconds>[0-9]*)s");
    private Orb orb = null;
    private Flare flare = null;
    
    public AutoFlare() {
        super("AutoFlare");
    }

    @Override
    public void onTick() {
        List<EntityArmorStand> armorstands =  mc.theWorld.loadedEntityList.stream().filter((e) -> e instanceof EntityArmorStand).map((e) -> (EntityArmorStand) e).collect(Collectors.toList());
        double playerX = mc.thePlayer.getPosition().getX();
        double playerY = mc.thePlayer.getPosition().getY();
        double playerZ = mc.thePlayer.getPosition().getZ();
        for (EntityArmorStand armorstand : armorstands) {
            double distance = Math.sqrt(Math.pow(armorstand.getPosition().getX() - playerX, 2) + Math.pow(armorstand.getPosition().getY() - playerY, 2) + Math.pow(armorstand.getPosition().getZ() - playerZ, 2));
            if (distance > 40 || armorstand.ticksExisted > 3600) continue;
            EntityLivingBase as = (EntityLivingBase) armorstand;
            ItemStack head = as.getEquipmentInSlot(4);
            if (head == null || !head.hasTagCompound()) continue;
            Flare.Type type = getFlareType(head);
            if (type == null || this.flare != null || !this.flare.isDead) continue;
            if (type == getFlareTypeHotbar()) this.flare = new Flare(armorstand, type);
        }
    }
    
    private Flare.Type getFlareType(ItemStack head) {
        String[] flareSkins = new String[] { "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzMwNjIyMywKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJlMmJmNmMxZWMzMzAyNDc5MjdiYTYzNDc5ZTU4NzJhYzY2YjA2OTAzYzg2YzgyYjUyZGFjOWYxYzk3MTQ1OCIKICAgIH0KICB9Cn0=", 
                                            "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzMyNjQzMiwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQyYmY5ODY0NzIwZDg3ZmQwNmI4NGVmYTgwYjc5NWM0OGVkNTM5YjE2NTIzYzNiMWYxOTkwYjQwYzAwM2Y2YiIKICAgIH0KICB9Cn0=", 
                                            "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzM0NzQ4OSwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzAwNjJjYzk4ZWJkYTcyYTZhNGI4OTc4M2FkY2VmMjgxNWI0ODNhMDFkNzNlYTg3YjNkZjc2MDcyYTg5ZDEzYiIKICAgIH0KICB9Cn0=" };
        for (int i = 0; i < flareSkins.length; i++) {
            if (head.hasTagCompound() && head.getTagCompound().toString().contains(flareSkins[i])) {
                if (i == 0) return Flare.Type.WARNING;
                if (i == 1) return Flare.Type.ALERT;
                if (i == 2) return Flare.Type.WARNING;
            }
        }
        return null;
    }
    
    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.entity;
    
        if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
            String nameTag = entity.getCustomNameTag();
            OrbType orb = OrbType.getByName(nameTag);
            
            if (orb != null && orb.isInRadius(entity.getDistanceSqToEntity(mc.thePlayer))) { 
                Matcher matcher = ORB_PATTERN.matcher(StringUtils.stripControlCodes(nameTag));
    
                if (matcher.matches()) {
                    List<EntityArmorStand> surroundingArmorStands = mc.theWorld.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(entity.posX - 0.1, entity.posY - 3, entity.posZ - 0.1, entity.posX + 0.1, entity.posY, entity.posZ + 0.1));
                    if (!surroundingArmorStands.isEmpty()) {
                        for (EntityArmorStand surroundingArmorStand : surroundingArmorStands) {
                            ItemStack helmet = surroundingArmorStand.getCurrentArmor(3);
                            if (helmet != null && (this.orb == null || this.orb.isDead) && orb == getOrbTypeHotbar()) {
                                this.orb = new Orb(surroundingArmorStand, orb);
                            }
                        }
                    }
                }
            }    
        }
    }

    public OrbType getOrbTypeHotbar() {
        OrbType orbType = null;
        int slot = InventoryUtils.searchItem("Orb");

        if (slot != -1) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
            String name = StringUtils.stripControlCodes(itemStack.getDisplayName()).toLowerCase();

            if (name.contains("plasmaflux")) orbType = OrbType.PLASMAFLUX;
            if (name.contains("overflux")) orbType = OrbType.OVERFLUX;
            if (name.contains("mana flux")) orbType = OrbType.MANA_FLUX;
            if (name.contains("radiant")) orbType = OrbType.RADIANT;
        }

        return orbType;
    }

    public Flare.Type getFlareTypeHotbar() {
        Flare.Type flareType = null;
        int slot = InventoryUtils.searchItem("Flare");

        if (slot != -1) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
            String name = StringUtils.stripControlCodes(itemStack.getDisplayName()).toLowerCase();

            if (name.contains("sos")) flareType = Flare.Type.SOS;
            if (name.contains("alert")) flareType = Flare.Type.ALERT;
            if (name.contains("warning")) flareType = Flare.Type.WARNING;
        }

        return flareType;
    }
    
    public boolean shouldPlace() {
        if (getOrbTypeHotbar() != null && (orb == null || orb.isDead)) return true;
        if (getFlareTypeHotbar() != null && (flare == null || flare.isDead)) return true;
        return false;
    }
    
    private static AutoFlare instance;
    public static AutoFlare getInstance() {
        if (instance == null) {
            instance = new AutoFlare();
        }

        return instance;
    }

    private class Orb {
        public final EntityArmorStand entity;
        public final OrbType type;
        
        public Orb(EntityArmorStand entity, OrbType type) {
            this.entity = entity;
            this.type = type;
        }

        public boolean isBetterThan(OrbType type) {
            if (this.type.priority < type.priority) {
                return true;
            }

            return false;
        }
    }
        
    private static enum OrbType {
        RADIANT("§aRadiant", 18*18),
        MANA_FLUX("§9Mana Flux", 18*18),
        OVERFLUX("§5Overflux", 18*18),
        PLASMAFLUX("§d§lPlasmaflux", 20*20);

        private String display;
        private int rangeSquared;
        
        private OrbType(String display, int rangeSquared) {
            this.display = display;
            this.rangeSquared = rangeSquared;
        }    
        
         public boolean isInRadius(double distanceSquared) {
            return distanceSquared <= rangeSquared;
        }

        public static OrbType getByName(String name) {
            for (OrbType orb : values()) {
                if (name.startsWith(orb.display)) {
                    return orb;
                }
            }
            return null;
        }
    }

    private class Flare {
        public final EntityArmorStand entity;
        public final Flare.Type type;
        
        public Flare(EntityArmorStand entity, Flare.Type type) {
            this.entity = entity;
            this.type = type;
        }
        
        private enum Type {
            SOS,
            ALERT,
            WARNING;
        }
    }

}
