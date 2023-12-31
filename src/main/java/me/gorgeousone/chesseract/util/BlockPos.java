package me.gorgeousone.chesseract.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;

public class BlockPos {

	private World world;
	private int x;
	private int y;
	private int z;
	
	public BlockPos(Location loc) {
		this(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
	}
	
	public BlockPos(Block block) {
		this(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}
	
	public BlockPos(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Block getBlock() {
		return world.getBlockAt(x, y, z);
	}
	
	public Location getLocation() {
		return new Location(world, x, y, z);
	}
	
	public World getWorld() {
		return world;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
	
	public Chunk getChunk() {
		return world.getChunkAt(getChunkX(), getChunkZ());
	}
	
	public boolean isChunkLoaded() {
		return world.isChunkLoaded(getChunkX(), getChunkZ());
	}
	
	public int getChunkX() {
		return x >> 4;
	}
	
	public int getChunkZ() {
		return z >> 4;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof BlockPos)) {
			return false;
		}
		BlockPos blockPos = (BlockPos) o;
		return x == blockPos.x && y == blockPos.y && z == blockPos.z && Objects.equals(world, blockPos.world);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(world, x, y, z);
	}
	
	@Override
	public BlockPos clone() {
		return new BlockPos(world, x, y, z);
	}
	
	@Override
	public String toString() {
		return "[" +
		       world.getName() +
		       ", " + x +
		       ", " + y +
		       ", " + z +
		       ']';
	}
}
