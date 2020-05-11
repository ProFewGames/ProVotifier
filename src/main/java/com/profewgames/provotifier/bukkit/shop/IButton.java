package com.profewgames.provotifier.bukkit.shop;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public abstract interface IButton {

	public abstract ItemStack getItem();

	public abstract void onClick(Player player, ClickType clickType);
}