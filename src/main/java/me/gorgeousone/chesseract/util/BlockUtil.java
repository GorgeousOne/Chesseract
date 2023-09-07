package me.gorgeousone.chesseract.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Arrays;

public class BlockUtil {
	
	public static boolean isChestNearby(Block block) {
		for (BlockFace face : Arrays.asList(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)) {
			if (block.getRelative(face).getType() == Material.CHEST) {
				return true;
			}
		}
		return false;
	}
}
