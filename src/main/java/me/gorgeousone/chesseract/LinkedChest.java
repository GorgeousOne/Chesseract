package me.gorgeousone.chesseract;

import me.gorgeousone.chesseract.block.BlockPos;
import org.bukkit.inventory.Inventory;

public class ChestLink {
	
	private BlockPos chest1;
	private BlockPos chest2;
	
	private Inventory inventory;
	
	public ChestLink(BlockPos chest1, BlockPos chest2) {
		this.chest1 = chest1;
		this.chest2 = chest2;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	
}
