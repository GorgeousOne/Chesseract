package me.gorgeousone.chesseract.listener;

import me.gorgeousone.chesseract.ChestHandler;
import me.gorgeousone.chesseract.LinkedChest;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

public class RenameListener implements Listener {
	
	private final ChestHandler chestHandler;
	
	public RenameListener(ChestHandler chestHandler) {
		this.chestHandler = chestHandler;
	}
	
	@EventHandler
	public void onChestOpen(InventoryOpenEvent event) {
		InventoryHolder holder = event.getInventory().getHolder();
		
		if (!(holder instanceof Chest)) {
			return;
		}
		LinkedChest chest = chestHandler.getChest((Chest) holder);
		
		if (chest == null) {
			return;
		}
		event.setCancelled(true);
		chestHandler.openRenameGUI(chest, (Player) event.getPlayer());
	}
}
