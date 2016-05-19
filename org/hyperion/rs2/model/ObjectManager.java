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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import static org.hyperion.rs2.model.Position.create;

/**
 * Manages all of the in-game objects.
 *
 * @author Graham Edgecombe
 */
public class ObjectManager {

    private static final File file = new File("./data/ObjectSpawns.json");
    private static int definitionCount = 0;
    private static int objectCount = 0;
    /**
     * There can only be one object on one position. To make this easy we'll just divide it in sections.
     * First we have the Z, which is the first map.
     * Then we have the X, which the the second map.
     * Then we have the Y, which is the third map.
     * Finally, if through that maze we find an object, it is located exactly there.
     */
    private final static Map<Integer, Map<Integer, Map<Integer, GameObject>>> GAME_OBJECTS = new HashMap<>();

    public static void addObject(GameObject gameObject) {
        addObject(gameObject, false);
    }

    private static void addObject(GameObject gameObject, boolean initializer) {
        final int objectX = gameObject.getPosition().getX();
        final int objectY = gameObject.getPosition().getY();
        final int objectZ = gameObject.getPosition().getZ();
        if(!GAME_OBJECTS.containsKey(objectZ))
            GAME_OBJECTS.put(objectZ, new HashMap<>());
        Map<Integer, Map<Integer, GameObject>> mapWithX = GAME_OBJECTS.get(objectZ);
        if(!mapWithX.containsKey(objectX))
            mapWithX.put(objectX, new HashMap<>());
        mapWithX.get(objectX).put(objectY, gameObject);
        if(!initializer)
            update(gameObject);
    }

    /**
     * Loads the objects in the map.
     *
     * @throws IOException           if an I/O error occurs.
     * @throws InvalidCacheException if the cache is invalid.
     */
    public static void init() {
        try(Cache cache = new Cache(new File("./data/cache/"))) {
            StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
            ObjectDefinitionParser.parse(cache, defIndices);
            if(Configuration.getBoolean(Configuration.ConfigurationObject.DEBUG))
                Server.getLogger().log(Level.INFO, "Loaded " + definitionCount + " object definitions.");
        } catch(InvalidCacheException e) {
            Server.getLogger().log(Level.SEVERE, "The cache could not be found.", e);
        } catch(IOException ex) {
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
        Map<Integer, Map<Integer, GameObject>> objectsOnZ = GAME_OBJECTS.get(gameObject.getPosition().getZ());
        if(objectsOnZ == null || objectsOnZ.isEmpty())
            return;
        Map<Integer, GameObject> objectsOnX = objectsOnZ.get(gameObject.getPosition().getX());
        if(objectsOnX == null || objectsOnX.isEmpty())
            return;
        objectsOnX.remove(gameObject.getPosition().getY());
    }

    public static void update(GameObject obj) {
        World.getPlayers().stream().filter(p -> obj.isVisible(p.getPosition())).forEach(p -> p.getActionSender().sendReplaceObject(obj.getPosition(), obj.getDefinition().getId(), obj.getRotation(), obj.getType()));
    }

    public static void load(Player player) {
        //Basically, a player can see 64 tiles in each direction, so we have to spawn objects 64 tiles in every direction, so we'll check for each of
        //those to see if there is an object there; if so we'll spawn it.
        final int coordX = player.getPosition().getX();
        final int coordY = player.getPosition().getY();
        final int coordZ = player.getPosition().getZ();
        Map<Integer, Map<Integer, GameObject>> objectOnX = GAME_OBJECTS.get(coordZ);
        if(objectOnX == null || objectOnX.isEmpty())
            return;
        for(int i = -64; i < 64; i++) {
            Map<Integer, GameObject> objectOnY = objectOnX.get(coordX + i);
            if(objectOnY == null || objectOnY.isEmpty())
                continue;
            for(int j = -64; j < 64; j++) {
                GameObject gameObject = objectOnY.get(coordY + j);
                if(gameObject == null)
                    continue;
                player.getActionSender().sendReplaceObject(gameObject.getPosition(), gameObject.getDefinition().getId(), gameObject.getRotation(), gameObject.getType());
            }
        }
        if ((player.getLocation().equals(Locations.Location.DUNGEONEERING_START)
                || player.getLocation().equals(Locations.Location.DUNGEONEERING_PVM))
                && player.getDungeoneering().inDungeon()) {
            loadDungeoneering(player);
        }
    }

    private static void loadDungeoneering(final Player player) {
        final int coordX = player.getPosition().getX();
        final int coordY = player.getPosition().getY();
        Map<Integer, Map<Integer, GameObject>> objectOnX = GAME_OBJECTS.get(0);
        if(objectOnX == null || objectOnX.isEmpty())
            return;
        for(int i = -64; i < 64; i++) {
            Map<Integer, GameObject> objectOnY = objectOnX.get(coordX + i);
            if(objectOnY == null || objectOnY.isEmpty())
                continue;
            for(int j = -64; j < 64; j++) {
                GameObject gameObject = objectOnY.get(coordY + j);
                if(gameObject == null)
                    continue;
                player.getActionSender().sendReplaceObject(gameObject.getPosition(), gameObject.getDefinition().getId(), gameObject.getRotation(), gameObject.getType());
            }
        }
    }

    public static void replace(GameObject obj, GameObject obj2) {
        removeObject(obj);
        update(obj2);
    }

    public static GameObject getObjectAt(int x, int y, int z) {
        final Position loc = Position.create(x, y, z);
        return getObjectAt(loc);
    }

    public static GameObject getObjectAt(Position position) {
        Map<Integer, Map<Integer, GameObject>> objectsOnZ = GAME_OBJECTS.get(position.getZ());
        if(objectsOnZ == null || objectsOnZ.isEmpty())
            return null;
        Map<Integer, GameObject> objectsOnX = objectsOnZ.get(position.getX());
        if(objectsOnX == null || objectsOnX.isEmpty())
            return null;
        return objectsOnX.get(position.getY());
    }

    public static boolean objectExist(Position loc) {
        return getObjectAt(loc.getX(), loc.getY(), loc.getZ()) != null;
    }
}
