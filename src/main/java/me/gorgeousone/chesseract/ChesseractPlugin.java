package me.gorgeousone.chesseract;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;

public final class ChesseractPlugin extends JavaPlugin {
	
	public static String SAVES_FILE_PATH;
    private ChestHandler chestHandler;
    
	@Override
	public void onEnable() {
		SAVES_FILE_PATH = getDataFolder().getAbsolutePath() + "/chesseracts.json";
		chestHandler = new ChestHandler(this);
		registerListeners();
		
		createDataFolder();
		chestHandler.loadChests(SAVES_FILE_PATH);
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
		manager.registerEvents(new HopperListener(this, this.chestHandler), this);
		manager.registerEvents(new RenameListener(this.chestHandler), this);
		manager.registerEvents(this.chestHandler, this);
	}
	
	@Override
	public void onDisable() {
		chestHandler.saveChests(SAVES_FILE_PATH);
        chestHandler.disable();
	}
	
//	private void loadCraftingRecipe() {
//		if (!getConfig().getBoolean("crafting_recipe_enabled")) {
//			return;
//		}
//		ShapedRecipe minerRecipe = new ShapedRecipe(getMinerItem());
//		minerRecipe.shape(new String[]{"*=*", "o#o", "*T*"});
//		minerRecipe.setIngredient('*', Material.REDSTONE);
//		minerRecipe.setIngredient('=', Material.IRON_TRAPDOOR);
//		minerRecipe.setIngredient('o', Material.IRON_BLOCK);
//		minerRecipe.setIngredient('#', Material.DISPENSER);
//		minerRecipe.setIngredient('T', Material.IRON_PICKAXE);
//		Bukkit.addRecipe(minerRecipe);
//	}
}
