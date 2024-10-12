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
import org.skyfish.util.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AutoFlare extends Feature {

    private final Pattern ORB_PATTERN = Pattern.compile("[A-Za-z ]* (?<seconds>[0-9]*)s");
    private Flare flare = null;
    private boolean running = false;
    
    public AutoFlare() {
        super("AutoFlare");
    }

    @Override
    public void start() {
        flare = null;
        running = false;
    }

    @Override 
    public void stop() {
        flare = null;
        running = false;
    }

    public void placeFlare(Runnable runnable) {
        if (flare != null || !flare.isDead || !Config.getInstance().FEATURE_AUTO_FLARE || running) return;
        running = true;
        
        Multithreading.runAsync(() -> {
            try {
                int slot = InventoryUtils.searchItem("Flare") == -1 ? InventoryUtils.searchItem("Flux") : InventoryUtils.searchItem("Flare");

                if (slot == -1) {
                    LogUtils.sendDebug("No flux or flare found in hotbar");
                } else {
                    Thread.sleep(Config.getInstance().getDelay());
                    mc.thePlayer.inventory.currentItem = slot;
                    Thread.sleep(100);        
                    mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.heldItem);
                    Thread.sleep(100);
                }
            } catch (Exception error) {}
            runnable.run();
            running = false;
        });
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
            Type type = getFlareType(head);
            if (type == null || this.flare != null || !this.flare.isDead) continue;
            if (type == getTypeHotbar()) this.flare = new Flare(armorstand, type);
        }
    }
    
    private Type getFlareType(ItemStack head) {
        String[] flareSkins = new String[] { "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzMwNjIyMywKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjJlMmJmNmMxZWMzMzAyNDc5MjdiYTYzNDc5ZTU4NzJhYzY2YjA2OTAzYzg2YzgyYjUyZGFjOWYxYzk3MTQ1OCIKICAgIH0KICB9Cn0=", 
                                            "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzMyNjQzMiwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQyYmY5ODY0NzIwZDg3ZmQwNmI4NGVmYTgwYjc5NWM0OGVkNTM5YjE2NTIzYzNiMWYxOTkwYjQwYzAwM2Y2YiIKICAgIH0KICB9Cn0=", 
                                            "ewogICJ0aW1lc3RhbXAiIDogMTY0NjY4NzM0NzQ4OSwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzAwNjJjYzk4ZWJkYTcyYTZhNGI4OTc4M2FkY2VmMjgxNWI0ODNhMDFkNzNlYTg3YjNkZjc2MDcyYTg5ZDEzYiIKICAgIH0KICB9Cn0=" };
        for (int i = 0; i < flareSkins.length; i++) {
            if (head.hasTagCompound() && head.getTagCompound().toString().contains(flareSkins[i])) {
                if (i == 0) return Type.WARNING;
                if (i == 1) return Type.ALERT;
                if (i == 2) return Type.WARNING;
            }
        }
        
        return null;
    }
    
    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent event) {
        Entity entity = event.entity;
    
        if (entity instanceof EntityArmorStand && entity.hasCustomName()) {
            String nameTag = entity.getCustomNameTag();
            Type orb = Type.getOrb(nameTag);
            
            if (orb != null && orb.isInRadius(entity.getDistanceSqToEntity(mc.thePlayer))) { 
                Matcher matcher = ORB_PATTERN.matcher(StringUtils.stripControlCodes(nameTag));
    
                if (matcher.matches()) {
                    List<EntityArmorStand> surroundingArmorStands = mc.theWorld.getEntitiesWithinAABB(EntityArmorStand.class, new AxisAlignedBB(entity.posX - 0.1, entity.posY - 3, entity.posZ - 0.1, entity.posX + 0.1, entity.posY, entity.posZ + 0.1));
                    if (!surroundingArmorStands.isEmpty()) {
                        for (EntityArmorStand surroundingArmorStand : surroundingArmorStands) {
                            ItemStack helmet = surroundingArmorStand.getCurrentArmor(3);
                            if (helmet != null && (this.flare == null || this.flare.isDead) && flare == getTypeHotbar()) {
                                this.flare = new Flare(surroundingArmorStand, orb);
                            }
                        }
                    }
                }
            }    
        }
    }
    
    public Type getTypeHotbar() {
        Type type = null;
        int slot = InventoryUtils.searchItem("Orb") == -1 ? InventoryUtils.searchItem("Flare") : InventoryUtils.searchItem("Orb");

        if (slot != -1) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
            String name = StringUtils.stripControlCodes(itemStack.getDisplayName()).toLowerCase();

            if (name.contains("plasmaflux")) type = Type.PLASMAFLUX;
            if (name.contains("overflux")) type = Type.OVERFLUX;
            if (name.contains("mana flux")) type = Type.MANA_FLUX;
            if (name.contains("radiant")) type = Type.RADIANT;
            if (name.contains("sos")) type = Type.SOS;
            if (name.contains("alert")) type = Type.ALERT;
            if (name.contains("warning")) type = Type.WARNING;
        } 

        return orbType;
    }
    
    private static AutoFlare instance;
    public static AutoFlare getInstance() {
        if (instance == null) {
            instance = new AutoFlare();
        }

        return instance;
    }

    private class Flare {
        public final EntityArmorStand entity;
        public final Type type;
        
        public Flare(EntityArmorStand entity, Type type) {
            this.entity = entity;
            this.type = type;
        }
    }
        
    private static enum Type {
        RADIANT("§aRadiant", 18*18),
        MANA_FLUX("§9Mana Flux", 18*18),
        OVERFLUX("§5Overflux", 18*18),
        PLASMAFLUX("§d§lPlasmaflux", 20*20),
        SOS("SOS", -1),
        Alert("Alert", -1),
        Warning("Warning", -1);

        private String display;
        private int rangeSquared;
        
        private Type(String display, int rangeSquared) {
            this.display = display;
            this.rangeSquared = rangeSquared;
        }    
        
         public boolean isInRadius(double distanceSquared) {
            return distanceSquared <= rangeSquared;
        }

        public static Type getOrb(String name) {
            for (Type orb : values()) {
                if (name.startsWith(orb.display)) {
                    return orb;
                }
            }
            return null;
        }
    }

}
