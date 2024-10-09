package org.skyfish.event;

import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public class UpdateTablistFooterEvent extends Event {

    private final List<String> footer;

    public UpdateTablistFooterEvent(List<String> footer) {
        this.footer = footer;
    }

    public List<String> getFooter() {
        return footer;
    }

}