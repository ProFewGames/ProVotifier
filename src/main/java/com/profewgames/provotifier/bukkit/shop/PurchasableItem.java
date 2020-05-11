package com.profewgames.provotifier.bukkit.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.ufactions.api.Module;

public abstract class PurchasableItem<PluginType extends Module> extends ShopItem<PluginType> {

	private CurrencyType type;
	private int cost = 0;

	public PurchasableItem(PluginType plugin, CurrencyType type, int cost, Material material, String name, int position,
			String... lore) {
		super(plugin, material, name, position, lore);

		this.type = type;
		this.cost = cost;

		ItemMeta meta = getItem().getItemMeta();
		List<String> lore1 = meta.getLore();
		if (lore1 == null) {
			lore1 = new ArrayList<>();
		}
		meta.setLore(lore1);
		getItem().setItemMeta(meta);
	}

	public PurchasableItem(PluginType plugin, CurrencyType type, int cost, Material material, String name,
			int position) {
		super(plugin, material, name, position);

		this.type = type;
		this.cost = cost;

		ItemMeta meta = getItem().getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList<>();
		}
		meta.setLore(lore);
		getItem().setItemMeta(meta);
	}

	public boolean additionalPurchaseChecks(Player player) {
		return true;
	}

	@Override
	public void onClick(Player player, ClickType clickType) {
		// TODO Add more types of cost
	}

	public int getCost() {
		return cost;
	}

	public CurrencyType getType() {
		return type;
	}

	public void purchaseDeclined(Player player, ClickType clickType) {
	}

	public abstract void purchaseSuccessful(Player player, ClickType clickType);
}