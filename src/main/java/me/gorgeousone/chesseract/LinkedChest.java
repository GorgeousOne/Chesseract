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
	 * Set the string used for linking two chests with the same name;
	 */
	public void setLinkName(String newLinkName) {
		String oldName = linkName;
		linkName = newLinkName;
		Bukkit.getPluginManager().callEvent(new ChestRenameEvent(this, oldName, newLinkName));
	}
}
