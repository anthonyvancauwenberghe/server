package org.hyperion.rs2.model.recolor.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.recolor.Recolor;
import org.hyperion.rs2.saving.SaveObject;

public class SaveRecolorManager extends SaveObject{

    public SaveRecolorManager(){
        super("RecolorManager");
    }

    public boolean save(final Player player, final BufferedWriter writer) throws IOException{
        writer.write(getName());
        writer.newLine();
        final List<Recolor> recolors = player.getRecolorManager().getAll();
        writer.write(Integer.toString(recolors.size()));
        writer.newLine();
        for(final Recolor recolor : recolors){
            writer.write(recolor.toString());
            writer.newLine();
        }
        return true;
    }

    public void load(final Player player, final String values, final BufferedReader reader) throws IOException{
        final int size = Integer.parseInt(reader.readLine());
        for(int i = 0; i < size; i++)
            player.getRecolorManager().add(Recolor.parse(reader.readLine()));
    }
}
