package org.hyperion.rs2.savingnew;

import com.google.gson.*;
import org.hyperion.rs2.model.Player;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Created by Gilles on 4/02/2016.
 */
public class PlayerLoading {
    public static void loadPlayer(Player player) {
        Path path = Paths.get("./data/characters", player.getName().toLowerCase() + ".json");
        File file = path.toFile();

        if (!file.exists()) {
            return;
        }

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);

            Arrays.stream(IOData.VALUES).forEach(ioData -> {
                if(reader.has(ioData.toString())) {
                    JsonElement element = reader.get(ioData.toString());
                    if(element == null)
                        return;
                    ioData.loadValue(player, element, builder);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}