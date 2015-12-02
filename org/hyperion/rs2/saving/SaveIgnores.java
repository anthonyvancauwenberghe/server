package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveIgnores extends SaveObject {

	public SaveIgnores(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean save(Player player, BufferedWriter writer)
			throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void load(Player player, String values, BufferedReader reader)
			throws IOException {
		// TODO Auto-generated method stub

	}

}
