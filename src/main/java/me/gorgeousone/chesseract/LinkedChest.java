package me.gorgeousone.chesseract;

import me.gorgeousone.chesseract.block.BlockPos;
import org.bukkit.Bukkit;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

public class LinkedChest {
	
	private BlockPos pos;
	private Chest chest;
	private String linkName;
	
	public LinkedChest(Chest chest) {
		this.pos = new BlockPos(chest.getBlock());
		this.chest = chest;
		this.linkName = "";
	}
	
	public Chest getChest() {
		return chest;
	}
	
	public BlockPos getPos() {
		return pos;
	}
	
	public Inventory getInventory() {
		return chest.getInventory();
	}
	
	/**
	 * Get the string used for linking two chests with the same name;
	 */
	public String getLinkName() {
		return linkName;
	}
	
	/**
	 * Set the string used for linking two chests with the same name
	 */
	public boolean setLinkName(String newLinkName) {
		newLinkName = formatLinkName(newLinkName);
		
		if (linkName.equals(newLinkName)) {
			Bukkit.broadcastMessage("Suck cess");
			return true;
		}
		ChestRenameEvent event = new ChestRenameEvent(this, linkName, newLinkName);
		Bukkit.getPluginManager().callEvent(event);
		
		if (!event.isCancelled()) {
			linkName = newLinkName;
			return true;
		}
		return false;
	}
	
	public static String formatLinkName(String linkName) {
		return linkName.toLowerCase().replaceAll("[^a-zA-Z0-9\\-_]", "");
	}
}
