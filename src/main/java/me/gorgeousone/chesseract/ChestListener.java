package me.gorgeousone.chesseract;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;


public class ChestListener implements Listener {
	private ChesseractPlugin chesseract;
	private ChestHandler chestHandler;
	
	public ChestListener(ChesseractPlugin chesseract, ChestHandler chestHandler) {
		this.chesseract = chesseract;
		this.chestHandler = chestHandler;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onChestPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		
		if (item.getType() == Material.CHEST) {
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
	
	@EventHandler //(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemTravel(InventoryMoveItemEvent event) {
		Inventory source = event.getSource();
		Inventory destination = event.getDestination();
		ItemStack item = event.getItem();
		
		if (source.getHolder() instanceof Chest) {
			LinkedChest chest = chestHandler.getChest((Chest) source.getHolder());
			
			if (chest != null) {
				event.setCancelled(!chestHandler.suckChestItem(chest, item));
			}
		} else if (destination.getHolder() instanceof Chest) {
			LinkedChest chest = chestHandler.getChest((Chest) destination.getHolder());
			
			if (chest != null) {
				event.setCancelled(!chestHandler.funnelChestItem(chest, item));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		LinkedChest chest = chestHandler.getChest(event.getBlock());
		
		if (chest != null) {
			this.chestHandler.removeLinkedChest(chest);
		}
	}
}
