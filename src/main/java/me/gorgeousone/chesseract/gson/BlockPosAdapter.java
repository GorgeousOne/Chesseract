package me.gorgeousone.chesseract.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.gorgeousone.chesseract.util.BlockPos;
import org.bukkit.World;

import java.io.IOException;

public class BlockPosAdapter extends TypeAdapter<BlockPos> {
	
	private final WorldAdapter worldAdapter = new WorldAdapter();
	
	public void write(JsonWriter out, BlockPos value) throws IOException {
		out.beginObject();
		out.name("world");
		worldAdapter.write(out, value.getWorld());
		out.name("x");
		out.value(value.getX());
		out.name("y");
		out.value(value.getY());
		out.name("z");
		out.value(value.getZ());
		out.endObject();
	}
	
	public BlockPos read(JsonReader in) throws IOException {
		World world = null;
		int x = 0;
		int y = 0;
		int z = 0;
		in.beginObject();
		
		while (in.hasNext()) {
			switch (in.nextName()) {
				case "world":
					world = worldAdapter.read(in);
					break;
				case "x":
					x = in.nextInt();
					break;
				case "y":
					y = in.nextInt();
					break;
				case "z":
					z = in.nextInt();
			}
		}
		in.endObject();
		return new BlockPos(world, x, y, z);
	}
}
