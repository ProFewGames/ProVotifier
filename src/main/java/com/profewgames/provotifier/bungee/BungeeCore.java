package com.profewgames.provotifier.bungee;

import com.profewgames.provotifier.ProVotifier;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCore extends Plugin {

	// TODO Check if this is where votifier will listen if not then delete

    @Override
    public void onEnable() {
        ProVotifier.initialize(true);
    }

    @Override
    public void onDisable() {
        if (ProVotifier.getInstance() != null)
            ProVotifier.getInstance().onDisable();
    }
}