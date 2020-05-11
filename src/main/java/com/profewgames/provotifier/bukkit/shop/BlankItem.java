package com.profewgames.provotifier.bukkit.shop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class BlankItem implements IButton {

	private int position;

	public BlankItem(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	@Override
	public ItemStack getItem() {
		return new ItemStack(Material.AIR);
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
	}
}