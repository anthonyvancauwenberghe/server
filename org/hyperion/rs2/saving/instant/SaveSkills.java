package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveSkills extends SaveObject {

	public static final int DEFAULT_EXP = 0;

	public SaveSkills(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean save(Player player, BufferedWriter writer)
			throws IOException {
		writer.newLine();
		writer.write(getName());
		writer.newLine();
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			int exp = player.getSkills().getExperience(i);
			if (exp != DEFAULT_EXP) {
				int lvl = player.getSkills().getLevel(i);
				writer.write(i + " " + lvl + " " + exp);
				writer.newLine();
			}
		}
		return true;
	}

	@Override
	public void load(Player player, String values, BufferedReader reader)
			throws IOException {
		String line;
		while ((line = reader.readLine()).length() > 0) {
			String[] parts = line.split(" ");
			int skill = Integer.parseInt(parts[0]);
			int level = Integer.parseInt(parts[1]);
			int exp = Integer.parseInt(parts[2]);
			level = level > 200 ? 200 : level;
			player.getSkills().setSkill(skill, level, exp);
		}
		player.getActionSender().sendSkills();
	}

}