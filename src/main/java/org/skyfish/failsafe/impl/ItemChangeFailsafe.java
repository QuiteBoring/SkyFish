package org.skyfish.failsafe.impl;

import net.minecraft.network.play.server.S2FPacketSetSlot;
import org.skyfish.event.impl.PacketEvent;
import org.skyfish.failsafe.*;
import org.skyfish.feature.impl.FishingMacro;
import org.skyfish.SkyFish;
import org.skyfish.util.Config;
import org.skyfish.util.InventoryUtils;

public class ItemChangeFailsafe extends Failsafe {

    public ItemChangeFailsafe() {
        super(Failsafe.Type.ITEM_CHANGE);
    }

    @Override
    public void onPacketReceive(PacketEvent.Receive event) {
        if (!Config.getInstance().FAILSAFE_ITEM_CHANGE) return;
        if (FishingMacro.getInstance().weaponSlot == -1 || FishingMacro.getInstance().rodSlot == -1) return;

        if (event.packet instanceof S2FPacketSetSlot) {
            if (!InventoryUtils.checkSlot("Rod", FishingMacro.getInstance().rodSlot)) FailsafeManager.getInstance().possibleDetection(this);
            if (!InventoryUtils.checkSlots(Config.getInstance().getWeapon(), FishingMacro.getInstance().weaponSlot)) FailsafeManager.getInstance().possibleDetection(this);
        }
    }

    private static ItemChangeFailsafe instance;
    public static ItemChangeFailsafe getInstance() {
        if (instance == null) {
            instance = new ItemChangeFailsafe();
        }

        return instance;
    }

}
