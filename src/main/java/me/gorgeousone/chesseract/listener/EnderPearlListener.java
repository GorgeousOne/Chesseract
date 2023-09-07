package me.gorgeousone.chesseract.listener;

import me.gorgeousone.chesseract.ChestHandler;
import me.gorgeousone.chesseract.LinkedChest;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderPearlListener implements Listener {
	
	private final JavaPlugin plugin;
	private final ChestHandler chestHandler;
	
	public EnderPearlListener(JavaPlugin plugin, ChestHandler chestHandler) {
		this.plugin = plugin;
		this.chestHandler = chestHandler;
	}
	
	@EventHandler
	public void onEnderPearlLand(ProjectileHitEvent event) {
		Projectile projectile = event.getEntity();
		
		if (!(projectile instanceof org.bukkit.entity.EnderPearl)) {
			return;
		}
		Block block = event.getHitBlock();
		
		if (block == null) {
			return;
		}
		LinkedChest chest = chestHandler.getChest(block);
		
		if (chest == null) {
			return;
		}
		LinkedChest link = chestHandler.getLink(chest);
		
		if (link == null) {
			return;
		}
		Player player = (Player) projectile.getShooter();
		Block tpBlock = link.getPos().getBlock();
		BlockFace chestFacing = ((Directional) tpBlock.getBlockData()).getFacing();
		Location tpLoc = tpBlock.getRelative(chestFacing).getLocation().add(0.5, 0, 0.5);
		tpLoc.setDirection(chestFacing.getOppositeFace().getDirection());
		new BukkitRunnable() {
			@Override
			public void run() {
				player.teleport(tpLoc);
			}
		}.runTaskLater(plugin, 2);
	}
}
