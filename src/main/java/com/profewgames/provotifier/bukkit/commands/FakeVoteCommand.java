package com.profewgames.provotifier.bukkit.commands;

import org.bukkit.entity.Player;

import xyz.ufactions.commands.CommandBase;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import com.profewgames.provotifier.bukkit.VoteModule;

public class FakeVoteCommand extends CommandBase<VoteModule> {

	public FakeVoteCommand(VoteModule module) {
		super(module, "fakevote");
	}

	@Override
	public void execute(Player caller, String[] args) {
		if (!caller.hasPermission("mega.command.fakevote")) {
			UtilPlayer.message(caller, F.error("Permissions", "You do not have access to this command."));
			return;
		}
		if (args.length == 1) {
			Player player = UtilPlayer.searchOnline(caller, args[0], true);
			if (player == null)
				return;
			Plugin.fakeVote(caller, player);
			return;
		}
		UtilPlayer.message(caller, F.help("/fakevote <player>", "Send a fake vote as player."));
	}
}