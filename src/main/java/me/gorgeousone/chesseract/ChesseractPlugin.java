package me.gorgeousone.chesseract;

import me.gorgeousone.chesseract.listener.ChestInteractListener;
import me.gorgeousone.chesseract.listener.EnderPearlListener;
import me.gorgeousone.chesseract.listener.HopperListener;
import me.gorgeousone.chesseract.listener.RenameListener;
import me.gorgeousone.chesseract.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;

public final class ChesseractPlugin extends JavaPlugin {
	
	public static String SAVES_FILE_PATH;
	private ChestHandler chestHandler;
	private ItemStack chesseractItem;
	
	@Override
	public void onEnable() {
		SAVES_FILE_PATH = getDataFolder().getAbsolutePath() + "/chesseracts.json";
		chesseractItem = createChesseractItem();
		
		chestHandler = new ChestHandler(this);
		registerListeners();
		
		createDataFolder();
		chestHandler.loadChests(SAVES_FILE_PATH);
		
		loadCraftingRecipe();
	}
	
	@Override
	public void onDisable() {
		chestHandler.disable();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!command.getName().equalsIgnoreCase("chesseract")) {
			return false;
		}
		if (!sender.isOp()) {
			return false;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
			return true;
		}
		if (args.length < 1 || !args[0].equalsIgnoreCase("give")) {
			sender.sendMessage(ChatColor.RED + "Usage: /chesseract give");
			return true;
		}
		((Player) sender).getInventory().addItem(chesseractItem);
		return true;
	}
	
	public ItemStack getChesseractItem() {
		return chesseractItem.clone();
	}
	
	private void createDataFolder() {
		if (!getDataFolder().exists()) {
			try {
				Files.createDirectory(getDataFolder().toPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	private void registerListeners() {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new ChestInteractListener(this, this.chestHandler), this);
		manager.registerEvents(new HopperListener(this, this.chestHandler), this);
		manager.registerEvents(new RenameListener(this.chestHandler), this);
		manager.registerEvents(new EnderPearlListener(this, this.chestHandler), this);
	}
	
	ItemStack createChesseractItem() {
		ItemStack item = new ItemStack(Material.CHEST);
		ItemUtil.addMagicGlow(item);
		ItemUtil.rename(item,
				ChatColor.LIGHT_PURPLE + "" +  ChatColor.BOLD + "Chesseract",
				ChatColor.DARK_PURPLE + "Funnels items to other placed in the world and even to other dimensions.",
				ChatColor.DARK_PURPLE + "Right click to link to another chesseract.");
		return item;
	}
	
	private void loadCraftingRecipe() {
		NamespacedKey recipeKey = new NamespacedKey(this, "chesseract");
		ShapedRecipe minerRecipe = new ShapedRecipe(recipeKey, chesseractItem);
		minerRecipe.shape("*#*", "oxo", "*#*");
		minerRecipe.setIngredient('*', Material.OBSIDIAN);
		minerRecipe.setIngredient('#', Material.QUARTZ_BLOCK);
		minerRecipe.setIngredient('o', Material.ENDER_PEARL);
		minerRecipe.setIngredient('x', Material.ENDER_CHEST);
		Bukkit.addRecipe(minerRecipe);
	}
}
