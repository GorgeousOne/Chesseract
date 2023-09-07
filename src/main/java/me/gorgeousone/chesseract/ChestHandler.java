package me.gorgeousone.chesseract;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.gorgeousone.chesseract.util.BlockPos;
import me.gorgeousone.chesseract.gson.ChestAdapter;
import me.gorgeousone.chesseract.util.BlockUtil;
import me.gorgeousone.chesseract.util.ItemUtil;
import net.wesjd.anvilgui.AnvilGUI;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads chests and manages them
 */
public class ChestHandler {
	private final ChesseractPlugin chesseract;
	private BukkitRunnable chestSyncer;
	private BukkitRunnable particleSpawner;
	
	private final Map<LinkedChest, Set<ItemStack>> syncAdditions;
	private final Map<LinkedChest, Set<ItemStack>> syncRemovals;
	
	BidiMap<LinkedChest, LinkedChest> links;
	private final Map<BlockPos, LinkedChest> chests;
	
	public ChestHandler(ChesseractPlugin chesseract) {
		this.chesseract = chesseract;
		chests = new HashMap<>();
		links = new DualHashBidiMap<>();
		
		syncAdditions = new HashMap<>();
		syncRemovals = new HashMap<>();
		startChestSyncing();
		startParticles();
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
	
	public void startParticles() {
		particleSpawner = new BukkitRunnable() {
			float second = 20;
			float phi = 0;
			@Override
			public void run() {
				phi += 180f / second;
				float dx = (float) Math.cos(Math.toRadians(phi));
				float dz = (float) Math.sin(Math.toRadians(phi));
				
				for (LinkedChest chest : chests.values()) {
					if (getLink(chest) == null) {
						chest.spawnUnlinkedParticles();
					} else {
						chest.spawnLinkedParticles(dx, dz);
					}
				}
			}
		};
		particleSpawner.runTaskTimer(chesseract, 1, 1);
	}
	
	public LinkedChest getChest(Block block) {
		return chests.get(new BlockPos(block));
	}
	
	public LinkedChest getChest(Chest chest) {
		Block block = chest.getBlock();
		return getChest(block);
	}
	
	/**
	 * Adds a chesseract if no other chests are around this chest
	 */
	public boolean addChest(Chest chest) {
		LinkedChest linkedChest = new LinkedChest(chest);
		
		if (BlockUtil.isChestNearby(chest.getBlock())) {
			return false;
		}
		chests.put(linkedChest.getPos(), linkedChest);
		saveChests(ChesseractPlugin.SAVES_FILE_PATH);
		return true;
	}
	
	private boolean addChest(LinkedChest chest) {
		if (BlockUtil.isChestNearby(chest.getPos().getBlock())) {
			return false;
		}
		chests.put(chest.getPos(), chest);
		return true;
	}
	
	public boolean isChesseractNearby(Block block) {
		for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
			if (getChest(block.getRelative(face)) != null) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Verifies if the given name is not already used by 2 chests.
	 * Establishes link if the new name is already used by 1 chest.
	 */
	public boolean renameChest(LinkedChest chest, String newName) {
		newName = LinkedChest.formatLinkName(newName);
		
		if (isNameTaken(newName) || newName.isEmpty()) {
			return false;
		}
		chest.setLinkName(newName);
		links.removeValue(chest);
		links.remove(chest);
		
		saveChests(ChesseractPlugin.SAVES_FILE_PATH);
		linkChest(chest);
		return true;
	}
	
	private void linkChest(LinkedChest chest) {
		if (chest.getLinkName().isEmpty()) {
			return;
		}
		for (LinkedChest possibleLink : chests.values()) {
			if (possibleLink.equals(chest)) {
				continue;
			}
			if (possibleLink.getLinkName().equals(chest.getLinkName())) {
				links.put(chest, possibleLink);
				break;
			}
		}
	}
	/**
	 * Unregisters a chest upon destruction.
	 * If it is linked the linked inventory will be cleared
	 * Items about to sync will be removed from the sync queue.
	 */
	public void destroyChest(LinkedChest chest) {
		syncRemovals.remove(chest);
		syncAdditions.remove(chest);
		unlinkChest(chest);
		chests.remove(chest.getPos());
		saveChests(ChesseractPlugin.SAVES_FILE_PATH);
	}
	
	/**
	 * Removes the link of a chesseract.
	 * The previous linked chest gets cleared.
	 * Items about to sync will be removed from the sync queue.
	 */
	private void unlinkChest(LinkedChest chest) {
		LinkedChest linked = getLink(chest);
		
		if (linked == null) {
			return;
		}
		linked.getInventory().clear();
		links.remove(chest);
		links.removeValue(chest);
		syncRemovals.remove(linked);
		syncAdditions.remove(linked);
	}
	
	/**
	 * Returns true if the given name is already used by any 2 linked chests
	 */
	private boolean isNameTaken(String name) {
		for (LinkedChest other : links.keySet()) {
			if (other.getLinkName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @return chest linked to a linkedchest. returns null if the chest is not linked.
	 */
	public LinkedChest getLink(LinkedChest chest) {
		if (links.containsKey(chest)){
			return links.get(chest);
		}
		return links.getKey(chest);
	}
	
	/**
	 * Sync chest item when a hooper moves an item into a chest
	 */
	public boolean funnelChestItem(LinkedChest chest, ItemStack movedItem) {
		LinkedChest linked = getLink(chest);

		if (linked == null) {
			return false;
		}
		syncAdditions.computeIfAbsent(linked, c -> new HashSet<>()).add(movedItem);
		return true;
	}
	
	/**
	 * Sync chest item when a hooper sucks an item from a chest
	 */
	public boolean suckChestItem(LinkedChest chest, ItemStack movedItem) {
		LinkedChest linked = getLink(chest);
		
		if (linked == null) {
			return false;
		}
		syncRemovals.computeIfAbsent(linked, c -> new HashSet<>()).add(movedItem);
		return true;
	}
	
	public void loadChests(String jsonFilePath) {
		if (!new File(jsonFilePath).isFile()) {
			return;
		}
		chests.clear();
		links.clear();
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LinkedChest.class, new ChestAdapter());
		Gson gson = gsonBuilder.create();
		
		try (FileReader fileReader = new FileReader(jsonFilePath)) {
			Set<LinkedChest> loadedChests = gson.fromJson(fileReader, new TypeToken<Set<LinkedChest>>(){}.getType());
			
			for (LinkedChest chest : loadedChests) {
				if (addChest(chest)) {
					linkChest(chest);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveChests(String jsonFilePath) {
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(LinkedChest.class, new ChestAdapter());
		Gson gson = gsonBuilder.create();
		
		try (Writer writer = new FileWriter(jsonFilePath)) {
			gson.toJson(chests.values(), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void disable() {
		chestSyncer.cancel();
		particleSpawner.cancel();
	}
	
	public void openRenameGUI(LinkedChest chest, Player player) {
		AnvilGUI.Builder renamingGui = new AnvilGUI.Builder()
				.plugin(chesseract)
				.itemLeft(new ItemStack(Material.NAME_TAG))
				.title("Set the linking name for this chesseract")
				.text(chest.getLinkName().isEmpty() ? " " : chest.getLinkName());
		
		renamingGui.onClick((slot, stateSnapshot) -> {
			if (slot != AnvilGUI.Slot.OUTPUT) {
				return Collections.emptyList();
			}
			String newName = LinkedChest.formatLinkName(stateSnapshot.getText());
			
			if (newName.isEmpty()) {
				player.sendMessage("Please enter a valid name");
				return Collections.emptyList();
			}
			boolean wasRenameSuccessful = renameChest(chest, newName);
			
			if (wasRenameSuccessful) {
				player.sendMessage("Renamed chesseract to " + ItemUtil.purpoil(newName));
				LinkedChest link = getLink(chest);
				
				if (link != null) {
					player.sendMessage("Created link to chesseract at " + ChatColor.LIGHT_PURPLE + link.getPos().toString());
				}
				return Arrays.asList(AnvilGUI.ResponseAction.close());
			}
			player.sendMessage("The name " + ItemUtil.purpoil(newName) + " is already taken");
			renamingGui.text(LinkedChest.formatLinkName(newName));
			return Collections.emptyList();
		});
		renamingGui.open(player);
	}
}
