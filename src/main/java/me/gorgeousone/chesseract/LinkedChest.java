package me.gorgeousone.chesseract;

import me.gorgeousone.chesseract.util.BlockPos;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LinkedChest {
	
	private final BlockPos pos;
	private String linkName;
	private long lastItemTravelTime;
	
	public LinkedChest(Chest chest) {
		this.pos = new BlockPos(chest.getBlock());
		this.linkName = "";
	}
	
	public BlockPos getPos() {
		return pos.clone();
	}
	
	/**
	 * Returns the inventory of the blocks current state. Which is important.
	 * Idk if this ever throws in case the block is not a chest anymore.
	 */
	public Inventory getInventory() {
		return ((Chest) pos.getBlock().getState()).getBlockInventory();
	}
	
	public void addItem(ItemStack item) {
		getInventory().addItem(item);
		lastItemTravelTime = System.currentTimeMillis();
	}
	
	public void removeItem(ItemStack item) {
		getInventory().removeItem(item);
		lastItemTravelTime = System.currentTimeMillis();
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
	public void setLinkName(String newLinkName) {
		linkName = formatLinkName(newLinkName);
	}
	
	public static String formatLinkName(String linkName) {
		return linkName.replaceAll("[^a-zA-Z0-9\\-_]", "");
	}
	
	public void spawnLinkedParticles(float dx, float dz) {
		if (!pos.isChunkLoaded()) {
			return;
		}
		Location loc = pos.getLocation().add(0.5, 0, 0.5);
		for (int i = 0; i < 5; i++) {
			loc.getWorld().spawnParticle(Particle.PORTAL, loc, 0, dx, 0f, dz, .75f);
			loc.getWorld().spawnParticle(Particle.PORTAL, loc, 0, -dx, 0f, -dz, .75f);
		}
	}
	/**
	 * Spawns fancy particles around the ches IF it is in a loaded chunk
	 */
	public void spawnUnlinkedParticles() {
		if (!pos.isChunkLoaded()) {
			return;
		}
		Location loc = pos.getLocation().add(0.5, .75, 0.5);
		loc.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, loc, 1, 0f, 0f, 0f, 0.5f);
	}
	
	public long getLastItemTravelTime() {
		return lastItemTravelTime;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof LinkedChest)) {
			return false;
		}
		LinkedChest chest = (LinkedChest) o;
		return pos.equals(chest.pos);
	}
	
	@Override
	public int hashCode() {
		return pos.hashCode();
	}
}
