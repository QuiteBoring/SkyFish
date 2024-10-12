package org.skyfish.event.impl;

import org.skyfish.event.Event;

public class UpdateScoreboardEvent extends Event {

    private final String line;
    
    public UpdateScoreboardEvent(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }

}