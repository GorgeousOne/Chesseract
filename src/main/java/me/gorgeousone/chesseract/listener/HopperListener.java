package me.gorgeousone.chesseract.listener;


import me.gorgeousone.chesseract.ChesseractPlugin;
import me.gorgeousone.chesseract.ChestHandler;
import me.gorgeousone.chesseract.LinkedChest;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class HopperListener implements Listener {
	private ChestHandler chestHandler;
	
	public HopperListener(ChestHandler chestHandler) {
		this.chestHandler = chestHandler;
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
}
