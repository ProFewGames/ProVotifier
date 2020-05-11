package com.profewgames.provotifier.model;

import net.md_5.bungee.api.plugin.Event;

public class BungeeVotifierEvent extends Event {

	private Vote vote;

	public BungeeVotifierEvent(final Vote vote) {
		this.vote = vote;
	}

	public Vote getVote() {
		return this.vote;
	}
}
