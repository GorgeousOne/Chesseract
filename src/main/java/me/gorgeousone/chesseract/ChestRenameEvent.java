package me.gorgeousone.chesseract;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * An event that gets called when a chest is renamed to a new, different name.
 */
public class ChestRenameEvent extends Event implements Cancellable {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private LinkedChest chest;
	private String oldName;
	private String newName;
	private boolean isCancelled;
	
	public ChestRenameEvent(LinkedChest chest, String oldName, String newName) {
		this.chest = chest;
		this.oldName = oldName;
		this.newName = newName;
	}
	
	public LinkedChest getChest() {
		return chest;
	}
	
	public String getOldName() {
		return oldName;
	}
	
	public String getNewName() {
		return newName;
	}
	
	@Override
	public boolean isCancelled() {
		return isCancelled;
	}
	
	@Override
	public void setCancelled(boolean cancel) {
		isCancelled = cancel;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
