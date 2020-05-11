package com.profewgames.provotifier.bukkit.commands;

import com.profewgames.provotifier.bukkit.ui.VoteUI;
import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import com.profewgames.provotifier.bukkit.VoteModule;

public class VoteCommand extends CommandBase<VoteModule> {

	public VoteCommand(VoteModule module) {
		super(module, "vote");
	}

	@Override
	public void execute(Player caller, String[] args) {
			new VoteUI(Plugin).openInventory(caller);
	}
}