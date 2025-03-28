package org.skyfish.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

public class InventoryUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

    public static int searchItem(String name) {
        int slotId = -1;

        for (int slot = 0; slot <= 8; slot++) {
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
            if (itemStack == null) continue;
            if (StringUtils.stripControlCodes(itemStack.getDisplayName()).toLowerCase().contains(name.toLowerCase())) slotId = slot;
        }
        
        return slotId;
    }

    public static int searchItems(String[] names) {
        int slotId = -1;

        for (String name : names) {
            int slot = searchItem(name);

            if (slot != -1) {
                slotId = slot;
                break;
            }
        }
        
        return slotId;
    }

    public static boolean checkSlot(String name, int slot) {
        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
        if (itemStack == null) return false;
        if (StringUtils.stripControlCodes(itemStack.getDisplayName()).toLowerCase().contains(name.toLowerCase())) return true;
        
        return false;
    }

    public static boolean checkSlots(String[] names, int slot) {
        boolean isThere = false;
        ItemStack itemStack = mc.thePlayer.inventory.mainInventory[slot];
        if (itemStack == null) return false;
        String item = StringUtils.stripControlCodes(itemStack.getDisplayName()).toLowerCase();

        for (String name : names) {
            if (item.contains(name.toLowerCase())) {
                isThere = true;
            }
        }
        
        return isThere;
    }

    public static boolean isInventoryEmpty(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != null) {
                return false;
            }
        }
        return true;
    }

    public static boolean isInventoryFull(EntityPlayer player) {
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) == null) {
                return false;
            }
        }
        return true;
    }

}