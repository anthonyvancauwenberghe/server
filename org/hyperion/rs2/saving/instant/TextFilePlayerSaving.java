package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.MergedSaving;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class TextFilePlayerSaving extends InstantPlayerSaving {

    /**
     * The saving directory.
     */
    public static final File SAVE_DIR = new File(MergedSaving.INSTANT_DIR);

    /**
     * The buffer size used for saving and loading.
     */
    public static final int BUFFER_SIZE = 1024;

    static {
        if(!SAVE_DIR.exists()){
            SAVE_DIR.mkdir();
        }
    }

    /**
     * Gets the filename of character file for the player.
     *
     * @param name
     * @returns The players save file.
     */
    public static String getFileName(final String name) {
        return SAVE_DIR + "/" + name.toLowerCase() + ".txt";
    }

    /**
     * Gets the filename of character file for the player.
     *
     * @param player
     * @returns The players save file.
     */
    public static String getFileName(final Player player) {
        return getFileName(player.getName());
    }

    /**
     * @param name
     */
    public static void copyFile(final String name) {
        copyFile(name, "./data/bugchars/");
    }

    /**
     * @param name
     * @param directory
     */
    public static boolean copyFile(final String name, final String directory) {
        try{
            final File file = new File(getFileName(name));
            if(!file.exists())
                return false;
            final BufferedReader in = new BufferedReader(new FileReader(file));
            final BufferedWriter out = new BufferedWriter(new FileWriter(directory + name + ".txt"));
            String line;
            while((line = in.readLine()) != null){
                out.write(line);
                out.newLine();
            }
            in.close();
            out.close();
            return true;
        }catch(final Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Saves the player's data to his character file.
     *
     * @param player
     * @return true if successful, false if not
     */
    public boolean save(final Player player) {
        try{
            final BufferedWriter file = new BufferedWriter(new FileWriter(getFileName(player)), BUFFER_SIZE);
            for(final SaveObject so : saveList){
                if(so.getName().contains("custom-inv") || so.getName().contains("custom-equip"))
                    continue;
                final boolean saved = so.save(player, file);
                if(saved){
                    file.newLine();
                }
            }
            file.close();
            //World.getWorld().getSQLSaving().save(player);
            return true;
        }catch(final IOException e){
            System.out.println("Player's name: " + player.getName());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Loads the player's data from his character file.
     *
     * @param player
     */
    public boolean load(final Player player) {
        //loadSQL(player);

        try{
            final BufferedReader in = new BufferedReader(new FileReader(getFileName(player)), BUFFER_SIZE);
            String line;
            while((line = in.readLine()) != null){
                if(line.length() <= 1)
                    continue;
                final String[] parts = line.split("=");
                final String name = parts[0].trim();
                String values = null;
                if(parts.length > 1)
                    values = parts[1].trim();
                final SaveObject so = saveData.get(name);
                if(so != null){
                    so.load(player, values, in);
                }else{
                    //System.out.println("Nulled saveobject for " + player.getName() + " line: " + line);
                }
            }
            in.close();

        }catch(final IOException e){
            e.printStackTrace();
        }
        player.init();
        return true;
    }
}
