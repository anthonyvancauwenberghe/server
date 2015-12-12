package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveFriends extends SaveObject {

    public SaveFriends(final String name) {
        super(name);// TODO Auto-generated constructor stub
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        writer.write(getName());
        writer.newLine();
        for(final long friend : player.getFriends().toArray()){
            writer.write(friend + "");
            writer.newLine();
        }
        return true;
    }

    @Override
    public void load(final Player player, final String values, final BufferedReader reader) throws IOException {
        String line;
        while((line = reader.readLine()).length() > 0){
            final long friend = Long.parseLong(line);
            player.getFriends().add(friend);
        }
    }

}
