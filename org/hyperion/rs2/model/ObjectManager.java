package org.hyperion.rs2.model;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.cache.Cache;
import org.hyperion.cache.InvalidCacheException;
import org.hyperion.cache.index.impl.StandardIndex;
import org.hyperion.cache.obj.ObjectDefinitionParser;
import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
    private final static List<GameObject> GAME_OBJECTS = new LinkedList<>();

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
                GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(Integer.parseInt(parts[0])), Position.create(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])), Integer.parseInt(parts[5]), Integer.parseInt(parts[4])));
            });
            if(Configuration.getBoolean(Configuration.ConfigurationObject.DEBUG))
                Server.getLogger().log(Level.INFO, "Successfully loaded all objects from the file.");
        } catch(IOException ex) {
            Server.getLogger().log(Level.SEVERE, "Something went wrong while loading the game objects from the file.", ex);
        }

        //TODO ADD THIS TO THE FILE
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(2213), create(3275, 2785, 0), 10, 1));
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(2213), create(3275, 2784, 0), 10, 1));

        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(7353), create(3203, 3422, 0), 10, 0));//slayer portal
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(61), create(3098, 3506, 0), 10, 2));//chaos altar
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(409), create(3094, 3506, 0), 10, 2));//Normal altar
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(6552), create(3096, 3500, 0), 10, 0));//ancient altar

        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(410), create(3106, 3507, 0), 10, 0));//guthix (curses) altar

        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(13192), create(2617, 3306, 0), 10, 2));
        //RFD Stuff.
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(12356), create(3207, 3225, 0), 10, 2));
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(2403), create(3207, 3220, 0), 10, 0));
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(2156), create(2975, 3392, 0), 10, 2));
        GAME_OBJECTS.add(new GameObject(GameObjectDefinition.forId(2157), create(2957, 3195, 0), 10, 0));
        GAME_OBJECTS.add(OSPK.loadObjects());
        OSPK.loadObjects();


        for (SpecialArea area : SpecialAreaHolder.getAreas()) {
            if (area instanceof NIGGERUZ)
                ((NIGGERUZ) area).initObjects(GAME_OBJECTS);
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

    public static void addObject(GameObject obj) {
        GAME_OBJECTS.add(obj);
        update(obj);
    }

    public static void removeObject(GameObject obj) {
        GAME_OBJECTS.remove(obj);
    }

    public static void update(GameObject obj) {
        for (Player p : World.getPlayers()) {
            if(p == null)
                continue;
                if (obj == null) {
                    System.out.println("Object is null!");
                    return;
                }
            if (obj.isVisible(p.getPosition())) {
                p.getActionSender().sendReplaceObject(obj.getPosition(), obj.getDefinition().getId(), obj.getRotation(), obj.getType());
            }
        }
    }

    public static void load(Player p) {
        GAME_OBJECTS.stream().filter(obj -> obj.isVisible(p.getPosition())).forEach(obj -> p.getActionSender().sendReplaceObject(obj.getPosition(), obj.getDefinition().getId(), obj.getRotation(), obj.getType()));
    }

    public static void replace(GameObject obj, GameObject obj2) {
        removeObject(obj);
        update(obj2);
    }

    public static GameObject getObjectAt(int x, int y, int z) {
        final Position loc = Position.create(x, y, z);
        return getObjectAt(loc);
    }

    public static GameObject getObjectAt(Position loc) {
        for (GameObject object : GAME_OBJECTS) {
            if (object.isAt(loc))
                return object;
        }
        return null;
    }

    public static boolean objectExist(Position loc, int id) {
        return true;
    }
}
