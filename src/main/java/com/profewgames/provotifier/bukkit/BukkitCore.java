package com.profewgames.provotifier.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public class BukkitCore extends JavaPlugin {

    private VoteModule module;

    @Override
    public void onEnable() {
        module = new VoteModule(this);
    }

    @Override
    public void onDisable() {
        if (module != null) module.onDisable();
    }
}