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
