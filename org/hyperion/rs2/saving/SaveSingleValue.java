package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class SaveSingleValue extends SaveObject {

	public SaveSingleValue(String name) {
		super(name);
	}

	public abstract Object getValue(Player player);

	@Override
	public abstract boolean save(Player player, BufferedWriter writer) throws IOException;

	@Override
	public abstract void load(Player player, String values, BufferedReader reader) throws IOException;

}
