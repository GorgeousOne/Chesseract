package me.gorgeousone.chesseract.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import me.gorgeousone.chesseract.LinkedChest;

import java.io.IOException;

public class ChestAdapter extends TypeAdapter<LinkedChest> {
	
	@Override
	public void write(JsonWriter out, LinkedChest value) throws IOException {
	
	}
	
	@Override
	public LinkedChest read(JsonReader in) throws IOException {
		return null;
	}
	
}
