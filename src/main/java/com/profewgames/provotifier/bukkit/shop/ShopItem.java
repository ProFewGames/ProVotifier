package com.profewgames.provotifier.bukkit.shop;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import xyz.ufactions.api.Module;
import xyz.ufactions.libs.ItemBuilder;

public abstract class ShopItem<PluginType extends Module> implements IButton {

	private ItemStack item;
	private int position;
	protected PluginType Plugin;

	public ShopItem(PluginType plugin, int position, ItemStack item) {
		this.Plugin = plugin;
		this.position = position;
		this.item = item;
	}

	public ShopItem(PluginType plugin, String owner, String name, int position, String... lore) {
		Plugin = plugin;
		this.item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(owner);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		List<String> tempLore = new ArrayList<>();
		for (String loreLine : lore) {
			tempLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
		}
		meta.setLore(tempLore);
		item.setItemMeta(meta);
		this.position = position;
	}

	public ShopItem(PluginType plugin, String owner, String name, int position, List<String> lore) {
		Plugin = plugin;
		this.item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(owner);
		meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		List<String> tempLore = new ArrayList<>();
		for (String loreLine : lore) {
			tempLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
		}
		meta.setLore(tempLore);
		item.setItemMeta(meta);
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, String name, int position) {
		Plugin = plugin;
		this.item = new ItemBuilder(material).name(name).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, int data, String name, int position) {
		Plugin = plugin;
		this.item = new ItemBuilder(material, data).name(name).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, String name, int position, List<String> lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material).name(name).lore(lore).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, String name, int position, String... lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material).name(name).lore(lore).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, int data, String name, int position, List<String> lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material, data).name(name).lore(lore).build();
		this.position = position;
	}

	public ShopItem(PluginType plugin, Material material, int data, String name, int position, String... lore) {
		Plugin = plugin;
		this.item = new ItemBuilder(material, data).name(name).lore(lore).build();
		this.position = position;
	}

	public ItemStack getItem() {
		return item;
	}

	public int getPosition() {
		return position;
	}
}