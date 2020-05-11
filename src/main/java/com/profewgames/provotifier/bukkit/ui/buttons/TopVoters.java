package com.profewgames.provotifier.bukkit.ui.buttons;

import java.util.ArrayList;
import java.util.List;

import com.profewgames.provotifier.VotingUser;
import com.profewgames.provotifier.bukkit.shop.ShopItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import com.profewgames.provotifier.bukkit.VoteModule;

public class TopVoters extends ShopItem<VoteModule> {

	public TopVoters(VoteModule module) {
		super(module, Material.PAPER, C.mHead + "Top Voters", 12, a(module));
	}

	private static List<String> a(VoteModule module) {
		List<String> list = new ArrayList<>();
		List<VotingUser> users = module.getTopVoters();
		for (int i = 0; i < users.size(); i++) {
			VotingUser user = users.get(i);
			list.add(C.cGray + "#" + F.elem(String.valueOf(Integer.valueOf(i + 1))) + C.cGray + " "
					+ Bukkit.getOfflinePlayer(user.uuid).getName() + " (Votes " + F.elem(String.valueOf(user.total))
					+ C.cGray + ")");
		}
		return list;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
	}
}