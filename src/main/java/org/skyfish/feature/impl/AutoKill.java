package org.skyfish.feature.impl;

import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.skyfish.feature.Feature;
import org.skyfish.feature.impl.*;
import org.skyfish.handler.MacroHandler;
import org.skyfish.util.Timer;
import org.skyfish.util.*;

import java.util.*;

public class AutoKill extends Feature {

    private Map<javax.swing.text.html.parser.Entity, javax.swing.text.html.parser.Entity> fishingMobs = new HashMap();
    private ArrayList<String> fishedUpMobs = new ArrayList<>();
    private Timer delayTimer = new Timer();

    public AutoKill() {
        super("AutoKill");
    }

    private boolean killing = false;
    private boolean placeFlare = false;
    private boolean placeTotem = false;

    @Override
    public void onTick() {
        switch(MacroHandler.getInstance().getStep()) {
            default: {}
            case KILL: {
                if (!placeTotem) {
                    placeTotem = true;
                    AutoTotem.getInstance().placeTotem();
                }

                if (killing) {
                    if (Config.getInstance().getWeapon()[0].equals("Fire Veil")) {
                        KeybindUtils.rightClick();
                        killing = false;
                    } else {
                        boolean allDead = true;

                        for (Entity mob : fishingMobs.keySet()) {
                            if (mob.isDead || !delayTimer.hasElasped(Config.getInstance().getDelay())) continue;
                            if (allDead) allDead = false;
                            if (mob.getDistanceToEntity(mc.thePlayer) < 6) {
                                KeybindUtils.rightClick();
                                delayTimer.reset();
                            }
                        }

                        if (allDead) killing = false;
                    }

                    return;
                }

                if (!placeFlare) {
                    placeFlare = true;
                    AutoFlare.getInstance().placeFlare(() -> {
                        MacroHandler.getInstance().setStep(MacroHandler.Step.FIND_ROD);  
                    });
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldRender(RenderWorldLastEvent event) {
        if (!MacroHandler.getInstance().isEnabled()|| !Config.getInstance().AUTO_KILL) return;
        
        if (!fishingMobs.isEmpty() && Config.getInstance().AUTO_KILL_RENDER_BOX) {
            for (Map.Entry<Entity, Entity> mob : fishingMobs.entrySet()) {
                RenderUtils.renderBoundingBox(mob.getValue(), event.partialTicks, Config.getInstance().AUTO_KILL_RENDER_BOX_COLOR.getRGB());
            }
        }

        checkMobs();
    }

    private void checkMobs() {
        List<Entity> entities = getEntity();

        if (!entities.isEmpty()) {
            for (Entity mob : entities) {
                if (fishingMobs.keySet().stream().anyMatch((entity) ->  entity.getUniqueID().equals(mob.getUniqueID()))) continue;
                if (!mobs.values().stream().anyMatch((e) -> mob.getCustomNameTag().contains(e)) || !fishedUpMobs.stream().anyMatch((e) -> mob.getCustomNameTag().contains(e))) continue;
                fishingMobs.put(mob, getEntityCuttingOtherEntity(mob, null));
            } 
        }
    }
    
    private List<Entity> getEntity() {
        return mc.theWorld.getEntitiesInAABBexcluding(mc.thePlayer,
                mc.thePlayer.getEntityBoundingBox().expand(6, (6 >> 1), 6), e -> e instanceof EntityArmorStand).stream()
                .filter((v) -> v.getDistanceToEntity(mc.thePlayer) < 6 &&
                !v.getName().contains(mc.thePlayer.getName()) &&
                !v.isDead &&
                ((EntityLivingBase) v).getHealth() > 0)
                .filter((entity) -> mc.thePlayer.canEntityBeSeen(entity))
                .collect(Collectors.toList());
    }

    private Entity getEntityCuttingOtherEntity(Entity e, Class<?> entityType) {
        List<Entity> possible = Minecraft.getMinecraft().theWorld.getEntitiesInAABBexcluding(e, e.getEntityBoundingBox().expand(0.3D, 2.0D, 0.3D), a -> {
            boolean flag1 = (!a.isDead && !a.equals(Minecraft.getMinecraft().thePlayer));
            boolean flag2 = !(a instanceof EntityArmorStand);
            boolean flag3 = !(a instanceof net.minecraft.entity.projectile.EntityFireball);
            boolean flag4 = !(a instanceof net.minecraft.entity.projectile.EntityFishHook);
            boolean flag5 = (entityType == null || entityType.isInstance(a));
            return flag1 && flag2 && flag3 && flag4 && flag5;
        });
        if (!possible.isEmpty()) return Collections.min(possible, Comparator.comparing(e2 -> e2.getDistanceToEntity(e)));
        return null;
    }

    private Pattern doubleHookPattern = Pattern.compile("doublehook", "§eIt's a §r§aDouble Hook§r§e!(?: Woot woot!)?");
    private boolean doubleHook = false;

    @Override
    public void onChat(ClientChatReceivedEvent event) {
        if (!MacroHandler.getInstance().isEnabled()) return;
        String msg = StringUtils.stripControlCodes(event.message.getUnformattedText());
        if (msg.contains(":")) return;

        
        if (doubleHookPattern.matches(event.message) || msg.toLowerCase().contains("double hook")) {
            doubleHook = true;
        }

        for (String mobMsg : mobs.keySet()) {
            String mob = mobs.get(mobMsg);

            if (msg.contains(mobMsg) || msg.contains(mob)) {
                fishedUpMobs.add(mob);
                if (doubleHook) fishedUpMobs.add(mob);
                doubleHook = false;
            }
        }

        if (Config.getInstance().AUTO_KILL_BARN_FISHING || fishedUpMobs.size() >= Config.getInstance().AUTO_KILL_MOB_LIMIT) {
            fishedUpMobs.clear();
            killing = true;
            MacroHandler.getInstance().setStep(MacroHandler.Step.FIND_WEAPON);
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        fishingMobs.clear();
        fishedUpMobs.clear();
    }

    private static AutoKill instance;
    public static AutoKill getInstance() {
        if (instance == null) {
            instance = new AutoKill();
        }
        
        return instance;
    }

    public static Map<String, String> mobs = new HashMap<String, String>() {{
        put("A Squid appeared.", "Squid");
        put("You caught a Sea Walker.", "Sea Walker");
        put("You stumbled upon a Sea Guardian.", "Sea Guardian");
        put("It looks like you've disrupted the Sea Witch's brewing session. Watch out, she's furious!", "Sea Witch");
        put("You reeled in a Sea Archer.", "Sea Archer");
        put("The Rider of the Deep has emerged.", "Rider Of The Deep");
        put("Huh? A Catfish!", "Catfish");
        put("Is this even a fish? It's the Carrot King!", "Carrot King");
        put("Gross! A Sea Leech!", "Sea Leech");
        put("You've discovered a Guardian Defender of the sea.", "Guardian Defender");
        put("You have awoken the Deep Sea Protector, prepare for a battle!", "Deep Sea Protector");
        put("The Water Hydra has come to test your strength.", "Water Hydra");
        put("The Sea Emperor arises from the depths.", "Sea Emperor");
        put("Phew! It's only a Scarecrow.", "Scarecrow");
        put("You hear trotting from beneath the waves, you caught a Nightmare.", "Nightmare");
        put("It must be a full moon, a Werewolf appears.", "Werewolf");
        put("The spirit of a long lost Phantom Fisher has come to haunt you.", "Phantom Fisher");
        put("This can't be! The manifestation of death himself!", "Grim Reaper");
        put("Frozen Steve fell into the pond long ago, never to resurface...until now!", "Frozen Steve");
        put("It's a snowman! He looks harmless", "Frosty The Snowman");
        put("The Grinch stole Jerry's Gifts...get them back!", "Grinch");
        put("What is this creature!?", "Yeti");
        put("You found a forgotten Nutcracker laying beneath the ice.", "Nutcracker");
        put("A Reindrake forms from the depths.", "Reindrake");
        put("A tiny fin emerges from the water, you've caught a Nurse Shark.", "Nurse Shark");
        put("You spot a fin as blue as the water it came from, it's a Blue Shark.", "Blue Shark");
        put("A striped beast bounds from the depths, the wild Tiger Shark!", "Tiger Shark");
        put("Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!", "Great White Shark");
        put("From beneath the lava appears a Magma Slug.", "Magma Slug");
        put("You hear a faint Moo from the lava... A Moogma appears.", "Moogma");
        put("A small but fearsome Lava Leech emerges.", "Lava Leech");
        put("You feel the heat radiating as a Pyroclastic Worm surfaces.", "Pyroclastic Worm");
        put("A Lava Flame flies out from beneath the lava.", "Lava Flame");
        put("A Fire Eel slithers out from the depths.", "Fire Eel");
        put("Taurus and his steed emerge.", "Taurus");
        put("You hear a massive rumble as Thunder emerges.", "Thunder");
        put("You have angered a legendary creature... Lord Jawbus has arrived", "Lord Jawbus");
        put("A Water Worm surfaces!", "Water Worm");
        put("A Poisoned Water Worm surfaces!", "Poisoned Water Worm");
        put("A Zombie miner surfaces!", "Zombie Miner");
        put("A flaming worm surfaces from the depths!", "Flaming Worm");
        put("A Lava Blaze has surfaced from the depths!", "Lava Blaze");
        put("A Lava Pigman arose from the depths!", "Lava Pigman");
    }};

}