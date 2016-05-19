package org.hyperion.rs2.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.cache.Cache;
import org.hyperion.cache.InvalidCacheException;
import org.hyperion.cache.index.impl.StandardIndex;
import org.hyperion.cache.obj.ObjectDefinitionParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * Manages all of the in-game objects.
 *
 * @author Graham Edgecombe
 */
public class ObjectManager {

    private final static List<GameObject> list = new ArrayList<>();

    private static final File file = new File("./data/ObjectSpawns.json");
    private static int definitionCount = 0;
    private static int objectCount = 0;

    public static void addObject(GameObject gameObject) {
        addObject(gameObject, false);
    }

    private static void addObject(GameObject gameObject, boolean initializer) {
        if (!list.contains(gameObject)) {
            list.add(gameObject);
        }
        if (!initializer)
            update(gameObject);
    }

    public static void init() {
        try (Cache cache = new Cache(new File("./data/cache/"))) {
            StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
            ObjectDefinitionParser.parse(cache, defIndices);
            if (Configuration.getBoolean(Configuration.ConfigurationObject.DEBUG))
                Server.getLogger().log(Level.INFO, "Loaded " + definitionCount + " object definitions.");
        } catch (InvalidCacheException e) {
            Server.getLogger().log(Level.SEVERE, "The cache could not be found.", e);
        } catch (IOException ex) {
            Server.getLogger().log(Level.SEVERE, "Something went wrong while loading the cache.", ex);
        }
        JsonParser parser = new JsonParser();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            JsonArray array = (JsonArray) parser.parse(reader);
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()) {
                JsonObject object = iterator.next().getAsJsonObject();
                addObject(new GameObject(GameObjectDefinition.forId(object.get("Object").getAsInt()), Position.create(object.get("X").getAsInt(), object.get("Y").getAsInt(), object.get("Z").getAsInt()), object.get("Type").getAsInt(), object.get("Rotation").getAsInt()));
            }
            reader.close();
        } catch (IOException ex) {
            Server.getLogger().log(Level.SEVERE, String.format("Unable to parse Object Spawns."), ex);
        }
    }

    public static void objectParsed(GameObject obj) {
        if (obj == null)
            return;
        objectCount++;
    }

    public static void addMapObject(int x, int y, int z, int id) {
        //TODO ADD THIS METHOD
    }

    public static void objectDefinitionParsed(GameObjectDefinition def) {
        definitionCount++;
        GameObjectDefinition.addDefinition(def);
    }

    public static void removeObject(GameObject gameObject) {
        if (list.contains(gameObject)) {
            list.remove(gameObject);
        }
    }

    public static void update(GameObject obj) {
        World.getPlayers().stream().filter(p -> obj.isVisible(p.getPosition())).forEach(p -> p.getActionSender().sendReplaceObject(obj.getPosition(), obj.getDefinition().getId(), obj.getRotation(), obj.getType()));
    }

    public static void load(Player player) {
        list.stream().filter(value -> value.isVisible(player.getPosition())).forEach(value -> player.getActionSender().sendReplaceObject(value.getPosition(), value.getDefinition().getId(), value.getRotation(), value.getType()));
    }

    public static void replace(GameObject obj, GameObject obj2) {
        removeObject(obj);
        update(obj2);
    }

    public static boolean objectExist(final Position position) {
        return list.stream().anyMatch(value -> value.getPosition().equals(position));
    }

    public static GameObject getObjectAt(int x, int y, int z) {
        return getObjectAt(Position.create(x, y, z));
    }

    public static GameObject getObjectAt(final Position position) {
        for (GameObject array : list) {
            if (array.getPosition().equals(position)) {
                return array;
            }
        }
        return null;
    }
}
