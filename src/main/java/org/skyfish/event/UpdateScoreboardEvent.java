package org.skyfish.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class UpdateScoreboardEvent extends Event {

    private final String line;
    
    public UpdateScoreboardEvent(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

}