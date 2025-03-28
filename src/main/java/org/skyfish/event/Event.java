package org.skyfish.event;

import net.minecraftforge.common.MinecraftForge;

public class Event extends net.minecraftforge.fml.common.eventhandler.Event {

    public boolean post() {
        MinecraftForge.EVENT_BUS.post(this);
        return isCancelable() && isCanceled();
    }

}