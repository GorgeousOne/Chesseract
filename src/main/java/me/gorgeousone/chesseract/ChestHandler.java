package me.gorgeousone.chesseract;

import me.gorgeousone.chesseract.block.BlockPos;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads chests and manages them
 */
public class ChestHandler implements Listener {
	private final ChesseractPlugin chesseract;
	private BukkitRunnable chestSyncer;
	
	private final Map<LinkedChest, Set<ItemStack>> syncAdditions;
	private final Map<LinkedChest, Set<ItemStack>> syncRemovals;
	
	BidiMap<LinkedChest, LinkedChest> links;
	private final Map<BlockPos, LinkedChest> linkedChests;
	
	public ChestHandler(ChesseractPlugin chesseract) {
		this.chesseract = chesseract;
		linkedChests = new HashMap<>();
		links = new DualHashBidiMap<>();
		
		syncAdditions = new HashMap<>();
		syncRemovals = new HashMap<>();
		loadChests();
		startChestSyncing();
	}
	
	public void startChestSyncing() {
		chestSyncer = new BukkitRunnable() {
			@Override
			public void run() {
				for (LinkedChest chest : syncAdditions.keySet()) {
					for (ItemStack item : syncAdditions.get(chest)) {
						chest.getInventory().addItem(item);
					}
				}
				for (LinkedChest chest : syncRemovals.keySet()) {
					for (ItemStack item : syncRemovals.get(chest)) {
						chest.getInventory().removeItem(item);
					}
				}
				syncAdditions.clear();
				syncRemovals.clear();
			}
			
		};
		chestSyncer.runTaskTimer(chesseract, 1, 1);
	}
	
	public LinkedChest getChest(Block block) {
		BlockPos pos = new BlockPos(block);
		return linkedChests.get(pos);
	}
	
	public LinkedChest getChest(Chest chest) {
		Block block = chest.getBlock();
		return getChest(block);
	}
	
	public void addLinkedChest(Chest chest) {
		LinkedChest linkedChest = new LinkedChest(chest);
		linkedChests.put(linkedChest.getPos(), linkedChest);
		linkedChest.setLinkName("asdf");
		Bukkit.broadcastMessage("a new hand touches the beacon");
	}
	
	
	public void removeLinkedChest(LinkedChest chest) {
		linkedChests.remove(chest);
	}
	
	@EventHandler
	public void onChestRename(ChestRenameEvent event) {
		LinkedChest chest = event.getChest();
		links.removeValue(chest);
		links.remove(chest);
		
		for (LinkedChest possibleLink : linkedChests.values()) {
			if (possibleLink == chest) {
				continue;
			}
			if (possibleLink.getLinkName().equals(event.getNewName())) {
				links.put(chest, possibleLink);
				Bukkit.broadcastMessage("linking " + chest.getPos().toString() + " to " + possibleLink.getPos().toString());
				break;
			}
		}
	}
	
	/**
	 * @return chest linked to a linkedchest. returns null if the chest is not linked.
	 */
	public LinkedChest getLinkedChest(LinkedChest chest) {
		if (links.containsKey(chest)){
			return links.get(chest);
		}
		return links.getKey(chest);
	}
	
	public boolean funnelChestItem(LinkedChest chest, ItemStack movedItem) {
		LinkedChest linked = getLinkedChest(chest);

		if (linked == null) {
			return false;
		}
		syncAdditions.computeIfAbsent(linked, c -> new HashSet<>()).add(movedItem);
		return true;
	}
	
	public boolean suckChestItem(LinkedChest chest, ItemStack movedItem) {
		LinkedChest linked = getLinkedChest(chest);
		
		if (linked == null) {
			return false;
		}
		syncRemovals.computeIfAbsent(linked, c -> new HashSet<>()).add(movedItem);
		return true;
	}
	
	
	private void loadChests() {
	}
	
	public void disable() {
		chestSyncer.cancel();
	}
}
