package me.gorgeousone.chesseract.listener;

import me.gorgeousone.chesseract.ChesseractPlugin;
import me.gorgeousone.chesseract.ChestHandler;
import me.gorgeousone.chesseract.LinkedChest;
import me.gorgeousone.chesseract.util.BlockUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.List;

public class ChestInteractListener implements Listener {
	
	private final ChesseractPlugin chesseract;
	private final ChestHandler chestHandler;
	
	public ChestInteractListener(ChesseractPlugin chesseract, ChestHandler chestHandler) {
		this.chesseract = chesseract;
		this.chestHandler = chestHandler;
	}
	
	/**
	 * Regsiters when a chesseract is placed, but also cancel if any other chests are nearby
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChestPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		
		if (item.getType() != Material.CHEST) {
			return;
		}
		if (item.isSimilar(chesseract.getChesseractItem())) {
			if (BlockUtil.isChestNearby(event.getBlock())) {
				event.setCancelled(true);
				return;
			}
			registerChesseract(event.getBlock());
		} else {
			if (chestHandler.isChesseractNearby(event.getBlock())) {
				event.setCancelled(true);
			}
		}
	}
	
	private void registerChesseract(Block block) {
		new BukkitRunnable() {
			@Override
			public void run() {
				BlockState state = block.getState();
				
				if (state instanceof Chest) {
					chestHandler.addChest((Chest) state);
				}
			}
		}.runTaskLater(chesseract, 1);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();
		removeChesseracts(Collections.singletonList(block));
		event.setDropItems(false);
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChestExplode(EntityExplodeEvent event) {
		removeChesseracts(event.blockList());
	}
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChestExplode(BlockExplodeEvent event) {
		removeChesseracts(event.blockList());
	}
	
	private void removeChesseracts(List<Block> blocks)  {
		for (Block block : blocks) {
			LinkedChest chest = chestHandler.getChest(block);
			
			if (chest != null) {
				chestHandler.destroyChest(chest);
			}
		}
	}
}
