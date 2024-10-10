package org.skyfish.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

public class InventoryUtils {

    private static final Minecraft mc = Minecraft.getMinecraft();

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