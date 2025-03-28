package org.skyfish.feature.impl;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import org.skyfish.feature.Feature;
import org.skyfish.handler.MacroHandler;
import org.skyfish.handler.RotationHandler;
import org.skyfish.util.*;
import org.skyfish.util.helper.Rotation;

import java.util.List;

public class AutoTotem extends Feature {

    public AutoTotem() {
        super("AutoTotem");
    }

    public void placeTotem() {
        if (!Config.getInstance().FEATURE_AUTO_TOTEM) return;

        Multithreading.runAsync(() -> {
            try {
                int slot = InventoryUtils.searchItem("Totem");

                if (slot == -1) {
                    LogUtils.sendError("No totem found in hotbar");
                    MacroHandler.getInstance().setEnabled(false);
                } else {
                    BlockPos block = findProperBlock();
                    if (block != null) {
                        RotationHandler.getInstance().easeToBlock(block, 500L);
                        Thread.sleep(750);
                        mc.thePlayer.inventory.currentItem = slot;
                        Thread.sleep(100);        
                        KeybindUtils.rightClick();
                        Thread.sleep(100);
                    }

                    Rotation data = MacroHandler.getInstance().getAngle();
                    RotationHandler.getInstance().easeTo(data.getYaw(), data.getPitch(), 200);
                    Thread.sleep(200);
                    MacroHandler.getInstance().setStep(MacroHandler.Step.FIND_WEAPON);
                }
            } catch (Exception error) {}
        });
    }

    public BlockPos findProperBlock() {
        for (int offsetX = -2; offsetX <= 2; offsetX++) {
            for (int offsetZ = -2; offsetZ <= 2; offsetZ++) {
                if (offsetX == 0 && offsetZ == 0) continue;
                BlockPos blockPos = new BlockPos(
                    mc.thePlayer.posX + offsetX,
                    mc.thePlayer.posY - 1,
                    mc.thePlayer.posZ + offsetZ
                );
                Block blockAtBlockPos = mc.theWorld.getChunkFromBlockCoords(blockPos).getBlock(blockPos);
                Block blockOverBlockPos = mc.theWorld.getChunkFromBlockCoords(blockPos).getBlock(blockPos.add(0, 1, 0));
                if (blockAtBlockPos != Blocks.air && blockAtBlockPos != Blocks.water && 
                    blockAtBlockPos != Blocks.flowing_water && blockAtBlockPos != Blocks.lava && 
                    blockAtBlockPos != Blocks.flowing_lava && blockOverBlockPos == Blocks.air) {
                    return blockPos;
                }
            }
        }
        return null;
    }    

    private boolean isTotemInRange() {
        List<Entity> loadedEntityList = mc.theWorld.loadedEntityList;

        for (Entity entity : loadedEntityList) {
            if (mc.thePlayer.getDistanceToEntity(entity) > 10) continue;
            if (!entity.hasCustomName()) continue;
            if (!StringUtils.stripControlCodes(entity.getCustomNameTag()).contains("Totem of Corruption")) continue;
            
            return true;
        }

        return false;
    }

    private static AutoTotem instance;
    public static AutoTotem getInstance() {
        if (instance == null) {
            instance = new AutoTotem();
        }

        return instance;
    }

}
