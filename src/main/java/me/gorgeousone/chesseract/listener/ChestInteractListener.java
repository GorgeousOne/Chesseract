package me.gorgeousone.chesseract.listener;

import me.gorgeousone.chesseract.ChesseractPlugin;
import me.gorgeousone.chesseract.ChestHandler;
import me.gorgeousone.chesseract.LinkedChest;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestInteractListener implements Listener {
	
	private final ChesseractPlugin chesseract;
	private final ChestHandler chestHandler;
	
	public ChestInteractListener(ChesseractPlugin chesseract, ChestHandler chestHandler) {
		this.chesseract = chesseract;
		this.chestHandler = chestHandler;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChestPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		
		if (item.isSimilar(chesseract.getChesseractItem())) {
			registerChesseract(event.getBlock());
		}
	}
	
	private void registerChesseract(Block block) {
		new BukkitRunnable() {
			@Override
			public void run() {
				BlockState state = block.getState();
				
				if (state instanceof Chest) {
					chestHandler.addLinkedChest((Chest) state);
				}
			}
		}.runTaskLater(chesseract, 1);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		LinkedChest chest = chestHandler.getChest(event.getBlock());
		
		if (chest != null) {
			chestHandler.destroyChest(chest);
			event.setDropItems(false);
			//TODO think about removing lore from chesseract so this can be removed
			Location blockLoc = event.getBlock().getLocation();
			blockLoc.add(0.5, 0.5, 0.5);
			blockLoc.getWorld().dropItemNaturally(blockLoc, chesseract.getChesseractItem());
		}
	}
}
