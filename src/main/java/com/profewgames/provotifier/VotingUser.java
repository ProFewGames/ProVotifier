package com.profewgames.provotifier;

import java.util.UUID;

public class VotingUser {

	public UUID uuid;
	public int total = 0;
	public int usable = 0;

	@Override
	public String toString() {
		return "VotingUser{uuid=" + uuid + ",total=" + total + ",usable=" + usable + "}";
	}
}