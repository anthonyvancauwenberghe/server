package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveSkills extends SaveObject {

    public static final int DEFAULT_EXP = 0;

    public SaveSkills(final String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        writer.newLine();
        writer.write(getName());
        writer.newLine();
        for(int i = 0; i < Skills.SKILL_COUNT; i++){
            final int exp = player.getSkills().getExperience(i);
            if(exp != DEFAULT_EXP){
                final int lvl = player.getSkills().getLevel(i);
                writer.write(i + " " + lvl + " " + exp);
                writer.newLine();
            }
        }
        return true;
    }

    @Override
    public void load(final Player player, final String values, final BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()).length() > 0){
            final String[] parts = line.split(" ");
            final int skill = Integer.parseInt(parts[0]);
            int level = Integer.parseInt(parts[1]);
            final int exp = Integer.parseInt(parts[2]);
            level = level > 200 ? 200 : level;
            player.getSkills().setSkill(skill, level, exp);

            if(skill == Skills.HITPOINTS){
                level = level > player.getSkills().calculateMaxLifePoints() ? player.getSkills().calculateMaxLifePoints() : level;
                player.getSkills().setSkill(skill, level, exp);
            }
        }
        player.getActionSender().sendSkills();
    }

}
