package org.hyperion.rs2.savingnew;

import com.google.gson.*;
import org.hyperion.Server;
import org.hyperion.rs2.model.Player;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

/**
 * Created by Gilles on 4/02/2016.
 */
public class PlayerLoading {
    public static boolean loadPlayer(Player player) {
        Path path = Paths.get(IOData.getCharFilePath(), player.getName().toLowerCase() + ".json");
        File file = path.toFile();

        if (!file.exists()) {
            return false;
        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);



            reader.entrySet().forEach(jsonEntry -> {
                IOData ioData = IOData.getBySaveName().get(jsonEntry.getKey());
                if(ioData != null) {
                    try {
                        ioData.loadValue(player, jsonEntry.getValue(), builder);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean playerExists(String playerName) {
        File file = new File(IOData.getCharFilePath(), playerName.toLowerCase() + ".json");
        return file.exists();
    }


    public static JsonElement getProperty(String playerName, IOData property) {
        if(playerName == null || property == null || playerName.trim().isEmpty() ||!playerExists(playerName))
            return null;

        File file = new File(IOData.getCharFilePath(), playerName.toLowerCase() + ".json");

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            JsonObject reader = (JsonObject)fileParser.parse(fileReader);
            if(reader.has(property.toString()))
                return reader.get(property.toString());
        } catch(Exception e) {
            Server.getLogger().log(Level.WARNING, String.format("Something went wrong getting the property '%s' from player '%s'.", property, playerName));
        }
        return null;
    }
}