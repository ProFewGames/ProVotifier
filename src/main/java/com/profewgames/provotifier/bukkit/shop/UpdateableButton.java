package com.profewgames.provotifier.bukkit.shop;

import org.bukkit.inventory.ItemStack;

import xyz.ufactions.api.Module;
import xyz.ufactions.updater.UpdateType;

public abstract class UpdateableButton<PluginType extends Module> implements IButton {

	protected PluginType Plugin;
	private UpdateType updateType;
	private int position;

	public UpdateableButton(PluginType plugin, UpdateType updateType, int position) {
		this.Plugin = plugin;
		this.updateType = updateType;
		this.position = position;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	@Override
	public ItemStack getItem() {
		return constructItem();
	}

	public void updated() {
	}

	public int getPosition() {
		return position;
	}

	public abstract ItemStack constructItem();
}