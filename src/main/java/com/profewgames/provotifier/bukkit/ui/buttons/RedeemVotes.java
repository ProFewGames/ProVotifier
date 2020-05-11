package com.profewgames.provotifier.bukkit.ui.buttons;

import java.util.HashMap;
import java.util.List;

import com.profewgames.provotifier.VotingUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import xyz.ufactions.libs.C;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import com.profewgames.provotifier.bukkit.VoteModule;
import com.profewgames.provotifier.bukkit.shop.ShopItem;

public class RedeemVotes extends ShopItem<VoteModule> {

    public RedeemVotes(VoteModule module, Player player) {
        super(module, Material.PAPER, C.mHead + "Redeem Vote", 14, C.cGray + C.Italics + "Redeemable votes: "
                + F.elem(String.valueOf(module.getUser(player.getUniqueId()).usable)));
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        player.closeInventory();
        HashMap<String, List<String>> rewards = Plugin.getRewards();
        if (rewards.isEmpty()) {
            UtilPlayer.message(player, F.main(Plugin.getName(), Plugin.getFromLocale("no redeem")));
        } else {
            UtilPlayer.message(player, F.main(Plugin.getName(), Plugin.getFromLocale("redeeming")));
            Plugin.runAsync(new Runnable() {

                @Override
                public void run() {
                    VotingUser user = Plugin.getRepository().loadVote(player.getUniqueId());
                    if (user == null) {
                        UtilPlayer.message(player, F.error(Plugin.getName(), Plugin.getFromLocale("no usable")));
                    } else if (user.usable <= 0) {
                        UtilPlayer.message(player, F.error(Plugin.getName(), Plugin.getFromLocale("no usable")));
                    } else {
                        Plugin.getRepository().updateVotes(player.getUniqueId(), player.getName(), -1);
                        for (String permission : rewards.keySet()) {
                            if (player.hasPermission(permission) || permission.equals("NONE")) {
                                for (String command : rewards.get(permission)) {
                                    command = command.replaceAll("%player%", player.getName());
                                    command = command.replaceAll("%amount%", String.valueOf(1));
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}