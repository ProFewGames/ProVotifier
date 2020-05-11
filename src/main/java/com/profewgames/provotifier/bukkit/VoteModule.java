package com.profewgames.provotifier.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import com.profewgames.provotifier.ProVotifier;
import com.profewgames.provotifier.VotingSorter;
import com.profewgames.provotifier.VotingUser;
import com.profewgames.provotifier.bukkit.commands.FakeVoteCommand;
import com.profewgames.provotifier.bukkit.commands.VoteCommand;
import com.profewgames.provotifier.repository.Repository;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.F;
import xyz.ufactions.libs.UtilPlayer;
import xyz.ufactions.libs.UtilServer;
import xyz.ufactions.updater.UpdateType;
import xyz.ufactions.updater.event.UpdateEvent;

public class VoteModule extends Module {

    private List<VotingUser> topVoters = new ArrayList<>();
    private List<VotingUser> users = new ArrayList<>();
    private final Repository repository;
    private final int delta = 3;
    private File locale;
    private HashMap<String, List<String>> rewards = new HashMap<>();

    public VoteModule(JavaPlugin plugin) {
        super("Vote", plugin);

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        locale = new File(plugin.getDataFolder(), "locale.yml");
        if (!locale.exists()) {
            try {
                locale.createNewFile();
                FileConfiguration config = YamlConfiguration.loadConfiguration(locale);
                config.set("locale.no redeem", "&cYou cannot redeem votes on this server.");
                config.set("locale.redeeming", "Please wait, contacting database...");
                config.set("locale.no usable", "&cYou have not voted yet...");
                config.save(locale);
                plugin.getLogger().log(Level.INFO, "locale file created.");
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING,
                        "error creating locale file \"" + e.getLocalizedMessage() + "\".");
            }
        }
        File rewards = new File(plugin.getDataFolder(), "rewards.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(rewards);
        if (!rewards.exists()) {
            try {
                rewards.createNewFile();
                config.set("rewards.default.commands", Arrays.asList("broadcast %player% redeemed %amount% votes."));
                config.set("rewards.donator.permission", "donator.vote");
                config.set("rewards.donator.commands",
                        Arrays.asList("broadcast %player% redeemed votes as a donator."));
                config.save(rewards);
                plugin.getLogger().log(Level.INFO, "rewards file created.");
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING,
                        "error creating rewards file \"" + e.getLocalizedMessage() + "\".");
            }
        }
        if (config.isConfigurationSection("rewards")) {
            for (String name : config.getConfigurationSection("rewards").getKeys(false)) {
                String permission = config.getString("rewards." + name + ".permission");
                if (permission == null)
                    permission = "NONE";
                this.rewards.put(permission, config.getStringList("rewards." + name + ".commands"));
            }
        }

        repository = new Repository(false);

        sortTopVoters();
        for (Player player : UtilServer.getPlayers()) {
            login(player.getUniqueId());
        }

        File enableVoteListener = new File(plugin.getDataFolder(), "mainframe.dat");
        System.out.println("Checking " + enableVoteListener.getAbsolutePath());
        if (enableVoteListener.exists()) {
            System.out.println("Yes");
            ProVotifier.initialize(false);
        }
    }

    @Override
    public void disable() {
        if (ProVotifier.getInstance() != null)
            ProVotifier.getInstance().onDisable();
    }

    public void fakeVote(Player caller, Player player) {
        UtilPlayer.message(caller, F.main(getName(), "Connecting to database..."));
        runAsync(() -> {
            getRepository().updateVotes(player.getUniqueId(), player.getName(), 1);
            UtilPlayer.message(caller, F.main(Plugin.getName(), "Fake vote sent"));
        });
    }

    public String getFromLocale(String path) {
        return ChatColor.translateAlternateColorCodes('&',
                YamlConfiguration.loadConfiguration(locale).getString("locale." + path));
    }

    public HashMap<String, List<String>> getRewards() {
        return rewards;
    }

    @EventHandler
    public void update(UpdateEvent e) {
        if (e.getType() != UpdateType.MIN_01)
            return;

        sortTopVoters();
    }

    private final void sortTopVoters() {
        runAsync(new Runnable() {

            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                List<VotingUser> list = repository.loadVotes();
                Collections.sort(list, new VotingSorter());
                topVoters.clear();
                for (int i = 0; i < delta && list.size() > i; i++) {
                    topVoters.add(list.get(i));
                }
            }
        });
    }

    public List<VotingUser> getTopVoters() {
        return topVoters;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        login(e.getPlayer().getUniqueId());
    }

    private final void login(UUID uuid) {
        runAsync(new Runnable() {

            @Override
            public void run() {
                VotingUser user = repository.loadVote(uuid);
                if (user == null) {
                    user = new VotingUser();
                    user.uuid = uuid;
                }
                users.add(user);
            }
        });
    }

    public VotingUser getUser(UUID uuid) {
        for (VotingUser user : users) {
            if (user.uuid.equals(uuid)) {
                return user;
            }
        }
        login(uuid);
        VotingUser user = new VotingUser();
        user.uuid = uuid;
        return user;
    }

    @Override
    public void addCommands() {
        addCommand(new FakeVoteCommand(this));
        addCommand(new VoteCommand(this));
    }

    public Repository getRepository() {
        return repository;
    }
}