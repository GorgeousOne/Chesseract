package me.gorgeousone.chesseract.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.gorgeousone.chesseract.LinkedChest;
import me.gorgeousone.chesseract.util.BlockPos;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

import java.io.IOException;

public class ChestAdapter extends TypeAdapter<LinkedChest> {
	
	private final BlockPosAdapter blockPosAdapter = new BlockPosAdapter();
	
	@Override
	public void write(JsonWriter out, LinkedChest value) throws IOException {
		out.beginObject();
		out.name("location");
		blockPosAdapter.write(out, value.getPos());
		out.name("linkName");
		out.value(value.getLinkName());
		out.endObject();
	}
	
	@Override
	public LinkedChest read(JsonReader in) throws IOException {
		try {
			in.beginObject();
			BlockPos pos = null;
			String linkName = null;
			
			while (in.hasNext()) {
				switch (in.nextName()) {
					case "location":
						pos = blockPosAdapter.read(in);
						break;
					case "linkName":
						linkName = in.nextString();
						break;
				}
			}
			in.endObject();
			
			if (linkName == null) {
				throw new IllegalArgumentException("Could not Chest chest from json. Link name is missing.");
			}
			BlockState state = pos.getBlock().getState();
			
			if (!(state instanceof Chest)) {
				throw new IllegalArgumentException("Could not Chest chest from json. Block at " + pos + " is not a chest.");
			}
			LinkedChest chest = new LinkedChest((Chest) state);
			chest.setLinkName(linkName);
			return chest;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
	}
}
