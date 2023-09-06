package me.gorgeousone.chesseract;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChesseractPlugin extends JavaPlugin {
	
    ChestHandler chestHandler;
    
	@Override
	public void onEnable() {
		this.chestHandler = new ChestHandler(this);
		registerListeners();
	}
	
	private void registerListeners() {
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new HopperListener(this, this.chestHandler), this);
		manager.registerEvents(new RenameListener(this.chestHandler), this);
		manager.registerEvents(this.chestHandler, this);
	}
	
	@Override
	public void onDisable() {
        this.chestHandler.disable();
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
