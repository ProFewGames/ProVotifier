package com.profewgames.provotifier.bukkit.ui;

import com.profewgames.provotifier.bukkit.ui.buttons.RedeemVotes;
import com.profewgames.provotifier.bukkit.ui.buttons.VotingLinks;
import org.bukkit.entity.Player;

import xyz.ufactions.libs.C;
import com.profewgames.provotifier.bukkit.VoteModule;
import com.profewgames.provotifier.bukkit.shop.Shop;
import com.profewgames.provotifier.bukkit.ui.buttons.TopVoters;

public class VoteUI extends Shop {

	private VoteModule module;

	public VoteUI(VoteModule module) {
		super(module.getPlugin(), C.mHead + "Voting UI", 27, ShopFiller.PANE, new VotingLinks(module), new TopVoters(module));

		this.module = module;
	}

	@Override
	public void onPreInventoryOpen(Player player) {
		addButton(new RedeemVotes(module, player));
	}
}