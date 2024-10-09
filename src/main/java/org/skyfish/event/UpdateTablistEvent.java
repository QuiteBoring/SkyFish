package org.skyfish.event;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public class UpdateTablistEvent extends Event {

    private final List<String> tablist;
    private final long timestamp;

    public UpdateTablistEvent(List<String> tablist, long timestamp) {
        this.tablist = tablist;
        this.timestamp = timestamp;
    }  

    public List<String> getTablist() {
        return tablist;
    }

    public long getTimestamp() {
        return timestamp;
    }

}