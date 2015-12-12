package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class SaveLook extends SaveObject {

    public SaveLook(final String name) {
        super(name);
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        writer.write(getName());
        writer.newLine();
        for(int i = 0; i < 13; i++){
            writer.write(player.getAppearance().getLook()[i] + "");
            writer.newLine();
        }
        return true;
    }

    @Override
    public void load(final Player player, final String values, final BufferedReader reader) throws IOException {
        String line;
        final int[] look = new int[13];
        int idx = 0;
        while((line = reader.readLine()).length() > 0){
            final int value = Integer.parseInt(line);
            look[idx++] = value;
        }
        player.getAppearance().setLook(look);
    }

}
