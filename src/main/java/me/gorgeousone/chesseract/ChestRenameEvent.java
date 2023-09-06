package me.gorgeousone.chesseract;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LinkedChestRenameEvent extends Event {
	
	private static final HandlerList HANDLERS = new HandlerList();
	
	private LinkedChest chest;
	private String oldName;
	private String newName;
	
	public LinkedChestRenameEvent(LinkedChest chest, String oldName, String newName) {
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
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
