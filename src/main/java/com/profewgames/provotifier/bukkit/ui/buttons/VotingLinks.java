package com.profewgames.provotifier.bukkit.ui.buttons;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.UtilPlayer;
import com.profewgames.provotifier.bukkit.VoteModule;
import com.profewgames.provotifier.bukkit.shop.ShopItem;

public class VotingLinks extends ShopItem<VoteModule> {

	public VotingLinks(VoteModule module) {
		super(module, Material.PAPER, C.mHead + "Voting Links", 13);
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		player.closeInventory();
		UtilPlayer.message(player, " ");
		UtilPlayer.message(player, C.mHead + "Voting Links");
		UtilPlayer.message(player, C.mBody + C.Italics + "More coming soon...");
		UtilPlayer.message(player, " ");
	}
}