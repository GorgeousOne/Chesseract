package me.gorgeousone.chesseract;

import java.util.Collection;
import java.util.HashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class MinerEvents
		implements Listener
{
	private JavaPlugin plugin;
	private MinerHandler handler;
	private HashMap<Block, Boolean> poweredMiners;
	private boolean autoMiningEnabled;
	
	public MinerEvents(JavaPlugin plugin, MinerHandler handler) {
		this.plugin = plugin;
		this.handler = handler;
		
		this.autoMiningEnabled = this.plugin.getConfig().getBoolean("auto_mining_enabled");
		
		if (!this.autoMiningEnabled) {
			this.poweredMiners = new HashMap();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onRedstonePower(BlockPhysicsEvent event) {
		if (this.autoMiningEnabled) {
			return;
		}
		if (this.plugin.isMiner(event.getBlock())) {
			Block miner = event.getBlock();
			
			if (miner.getBlockPower() == 0) {
				this.poweredMiners.put(miner, Boolean.valueOf(false));
				
				return;
			}
			if (this.poweredMiners.containsKey(miner) && ((Boolean)this.poweredMiners.get(miner)).booleanValue()) {
				return;
			}
			this.poweredMiners.put(miner, Boolean.valueOf(true));
			this.handler.mine(miner);
		}
	}
	
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onItemTravel(InventoryMoveItemEvent event) {
		if (this.plugin.isMiner(event.getSource()) || this.plugin.isMiner(event.getDestination()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDispense(BlockDispenseEvent event) {
		if (this.plugin.isMiner(event.getBlock()))
			event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (this.plugin.isMiner(event.getBlock())) {
			this.handler.addMiner(event.getBlock());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent event) {
		final Block block = event.getBlock();
		
		if (!this.plugin.isMiner(block)) {
			return;
		}
		this.handler.removeMiner(block);
		block.getDrops().clear();
		
		(new BukkitRunnable()
		{
			public void run() {
				Collection<Entity> entities = block.getWorld().getNearbyEntities(block.getLocation().add(0.5D, 0.5D, 0.5D), 0.5D, 0.5D, 0.5D);
				
				for (Entity entity : entities) {
					
					if (!(entity instanceof Item)) {
						continue;
					}
					ItemStack drop = ((Item)entity).getItemStack();
					
					if (drop.getAmount() == 1 && drop.getType() == Material.DISPENSER) {
						
						ItemMeta meta = drop.getItemMeta();
						drop.setItemMeta(meta);
						return;
					}
				}
			}
		}).runTask(this.plugin);
	}
}
