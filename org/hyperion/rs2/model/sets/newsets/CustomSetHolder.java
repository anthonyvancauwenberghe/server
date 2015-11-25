package org.hyperion.rs2.model.sets.newsets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperion.rs2.model.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Created by Gilles on 4/11/2015.
 */
public class CustomSetHolder {
    private final static String FILEPATH = "./data/sets/";

    private final static int AMOUNT_OF_SETS = 3;
    private CustomSet[] customSets = new CustomSet[AMOUNT_OF_SETS];
    private final Player player;

    public CustomSetHolder(Player player) {
        this.player = player;
    }

    public void addCustomSet(int slot) {
        loadAllSets();
        if (slot > 3 || slot <= 0)
            return;
        customSets[slot - 1] = CustomSet.fromCurrent(player);
        player.sendMessage("Saved current equipment and inventory in slot " + slot + ".");
        saveAllSets();
    }

    public void equipCustomSet(int slot) {
        loadAllSets();
        if(customSets[slot - 1] != null) {
            customSets[slot - 1].apply(player);
            player.sendMessage("Loaded equipment and inventory settings from slot " + slot + ".");
        } else {
            player.sendMessage("You have nothing saved in slot " + slot + ".");
        }
    }

    public void loadAllSets() {
        customSets = new CustomSet[AMOUNT_OF_SETS];
        File file = new File(FILEPATH + player.getSafeDisplayName() + ".json");
        if(!file.exists())
            return;

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);
            for (int i = 0; i < customSets.length; i++) {
                if (reader.has(String.format("set%d", i))) {
                    customSets[i] = CustomSet.fromJson(reader.get(String.format("set%d", i)).getAsJsonObject());
                }
            }
        } catch(Exception e) {
            player.sendImportantMessage("Something went wrong trying to load your sets, please try again.");
            e.printStackTrace();
        }
    }

    public void saveAllSets() {
        File fileToWrite = new File(FILEPATH + player.getSafeDisplayName() + ".json");
        fileToWrite.getParentFile().setWritable(true);

        if (!fileToWrite.getParentFile().exists()) {
            try {
                fileToWrite.getParentFile().mkdirs();
            } catch (SecurityException e) {
                System.out.println("Unable to create directory for set saving file!");
            }
        }

        try (FileWriter writer = new FileWriter(fileToWrite)) {
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject setObject = new JsonObject();
            for(int i = 0; i < customSets.length; i++) {
                if(customSets[i] == null)
                    continue;
                setObject.add(String.format("set%d", i), customSets[i].toJson());
            }
            writer.write(builder.toJson(setObject));
            writer.close();
        } catch (Exception e) {
            player.sendImportantMessage("Something went wrong trying to save your sets, please try again.");
            e.printStackTrace();
        }
    }
}
