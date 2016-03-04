package org.hyperion.rs2.model;

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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import static org.hyperion.rs2.model.Position.create;

/**
 * Manages all of the in-game objects.
 *
 * @author Graham Edgecombe
 */
public class ObjectManager {

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

        try(BufferedReader reader = new BufferedReader(new FileReader("./data/objspawns.cfg"))) {
            reader.lines().forEach(line -> {
                String parts[] = line.replace("spawn = ", "").split("\t");
                addObject(new GameObject(GameObjectDefinition.forId(Integer.parseInt(parts[0])), Position.create(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])), Integer.parseInt(parts[5]), Integer.parseInt(parts[4])));
            });
            if(Configuration.getBoolean(Configuration.ConfigurationObject.DEBUG))
                Server.getLogger().log(Level.INFO, "Successfully loaded all objects from the file.");
        } catch(IOException ex) {
            Server.getLogger().log(Level.SEVERE, "Something went wrong while loading the game objects from the file.", ex);
        }

        //TODO ADD THIS TO THE FILE
        addObject(new GameObject(GameObjectDefinition.forId(2213), create(3275, 2785, 0), 10, 1));
        addObject(new GameObject(GameObjectDefinition.forId(2213), create(3275, 2784, 0), 10, 1));

        addObject(new GameObject(GameObjectDefinition.forId(7353), create(3203, 3422, 0), 10, 0));//slayer portal
        addObject(new GameObject(GameObjectDefinition.forId(61), create(3098, 3506, 0), 10, 2));//chaos altar
        addObject(new GameObject(GameObjectDefinition.forId(409), create(3094, 3506, 0), 10, 2));//Normal altar
        addObject(new GameObject(GameObjectDefinition.forId(6552), create(3096, 3500, 0), 10, 0));//ancient altar

        addObject(new GameObject(GameObjectDefinition.forId(410), create(3106, 3507, 0), 10, 0));//guthix (curses) altar

        addObject(new GameObject(GameObjectDefinition.forId(13192), create(2617, 3306, 0), 10, 2));
        //RFD Stuff.
        addObject(new GameObject(GameObjectDefinition.forId(12356), create(3207, 3225, 0), 10, 2));
        addObject(new GameObject(GameObjectDefinition.forId(2403), create(3207, 3220, 0), 10, 0));
        addObject(new GameObject(GameObjectDefinition.forId(2156), create(2975, 3392, 0), 10, 2));
        addObject(new GameObject(GameObjectDefinition.forId(2157), create(2957, 3195, 0), 10, 0));
        addObject(OSPK.loadObjects());
        OSPK.loadObjects();
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

    public static boolean objectExist(Position loc, int id) {
        return true;
    }
}
