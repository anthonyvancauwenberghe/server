package org.hyperion.rs2.model.possiblehacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperion.Server;
import org.hyperion.engine.GameEngine;
import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.util.TextUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * @author DrHales
 */
public final class PossibleHacksHolder {

    private final File folder = new File("./data/");

    private final File file = new File(folder, "possiblehacks.json");

    private static PossibleHacksHolder instance;

    public static PossibleHacksHolder getInstance() {
        return instance != null ? instance : (instance = new PossibleHacksHolder());
    }

    private PossibleHacksHolder() {
        loadPossibleHacks();
    }

    private final Map<String, List<String>> map = new HashMap();

    public Map<String, List<String>> getMap() {
        return map;
    }

    private void loadPossibleHacks() {
        final long initial = System.currentTimeMillis();
        JsonParser parser = new JsonParser();
        try (FileReader reader = new FileReader(file)) {
            final JsonArray array = (JsonArray) parser.parse(reader);
            final Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()) {
                final JsonObject object = iterator.next().getAsJsonObject();
                final String name = object.get("Username").getAsString().toLowerCase();
                final JsonArray protocols = object.get("Protocols").getAsJsonArray();
                if (!map.containsKey(name)) {
                    map.put(name, new ArrayList<>());
                }
                final Iterator<JsonElement> protocolsI = protocols.iterator();
                while (protocolsI.hasNext()) {
                    final String protocol = protocolsI.next().getAsString();
                    map.get(name).add(protocol);
                }
                final JsonArray passwords = object.get("Passwords").getAsJsonArray();
                final Iterator<JsonElement> passwordsI = passwords.iterator();
                while (passwordsI.hasNext()) {
                    final String password = passwordsI.next().getAsString();
                    map.get(name).add(password);
                }
            }
            reader.close();
        } catch (IOException ex) {
            Server.getLogger().log(Level.WARNING, "Error Parsing PossibleHacks.json", ex);
        }
        Server.getLogger().info(String.format("%,d Possible Hacks submitted in %,dms", map.size(), System.currentTimeMillis() - initial));
    }

    public void add(final String name, final String data) {
        if (!map.containsKey(name)) {
            map.put(name, new ArrayList<>());
        }
        map.get(name).add(data);
    }

    public void overwriteList(final boolean reload) {
        final Map<String, List<String>> map = getMap();
        try (FileWriter writer = new FileWriter(file)) {

        } catch (IOException ex) {
            Server.getLogger().log(Level.WARNING, "Error Overwriting PossibleHacks.json", ex);
        }
        if (reload) {
            loadPossibleHacks();
        }
    }

    public void check(final Player player, final String value) {
        final List<String> list = map.get(value);
        if (list == null || list.isEmpty()) {
            player.sendf("Player %s doesn't seem to have any account issues so far.", TextUtils.titleCase(value));
            return;
        }
        player.sendf("@dre@Hacks for player %s", TextUtils.titleCase(value));
        TaskManager.submit(new Task(500L, "Listing Possible Hacks Task") {
            @Override
            public void execute() {
                stop();
                map.get(value).stream().filter(string -> string != null).forEach(player::sendMessage);
            }
        });
    }
}
