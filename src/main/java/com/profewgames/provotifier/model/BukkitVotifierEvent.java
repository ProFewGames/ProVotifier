package com.profewgames.provotifier.model;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BukkitVotifierEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Vote vote;

    public BukkitVotifierEvent(final Vote vote) {
        this.vote = vote;
    }

    public Vote getVote() {
        return vote;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}