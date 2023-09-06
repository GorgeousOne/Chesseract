package me.gorgeousone.chesseract.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;

public class WorldAdapter extends TypeAdapter<World> {
	
	public void write(JsonWriter out, World value) throws IOException {
		out.value(value.getName());
	}
	
	public World read(JsonReader in) throws IOException {
		String worldName = in.nextString();
		World world = Bukkit.getWorld(worldName);
		
		if (world == null) {
			throw new IllegalArgumentException("Could not find world with name: " + worldName);
		}
		return world;
	}
}