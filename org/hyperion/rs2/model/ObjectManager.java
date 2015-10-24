package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.cache.Cache;
import org.hyperion.cache.InvalidCacheException;
import org.hyperion.cache.index.impl.MapIndex;
import org.hyperion.cache.index.impl.StandardIndex;
import org.hyperion.cache.map.LandscapeListener;
import org.hyperion.cache.map.LandscapeParser;
import org.hyperion.cache.obj.ObjectDefinitionListener;
import org.hyperion.cache.obj.ObjectDefinitionParser;
import org.hyperion.rs2.model.content.minigame.barrowsffa.BarrowsFFA;
import org.hyperion.rs2.model.content.specialareas.NIGGERUZ;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.packet.ObjectClickHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.hyperion.rs2.model.Location.create;

/**
 * Manages all of the in-game objects.
 *
 * @author Graham Edgecombe
 */
public class ObjectManager implements LandscapeListener, ObjectDefinitionListener {

    /**
     * Logger instance.
     */
    //private static final Logger logger = Logger.getLogger(ObjectManager.class.getName());

    /**
     * The number of definitions loaded.
     */
    private int definitionCount = 0;

    /**
     * The count of objects loaded.
     */
    private int objectCount = 0;

    public static Cache cache;

    /**
     * Loads the objects in the map.
     *
     * @throws IOException           if an I/O error occurs.
     * @throws InvalidCacheException if the cache is invalid.
     */
    public void load() throws IOException, InvalidCacheException {
        cache = new Cache(new File("./data/cache/"));
        try {
            /*OutputStream os = new FileOutputStream("data/itemdefnew.bin");
            buf = IoBuffer.allocate(1024);
			buf.setAutoExpand(true);*/
            //logger.info("Loading definitions...");
            StandardIndex[] defIndices = cache.getIndexTable().getObjectDefinitionIndices();
            new ObjectDefinitionParser(cache, defIndices, this).parse();
            System.out.println("Loaded " + definitionCount + " object definitions.");
            BufferedReader br = new BufferedReader(new FileReader("./data/objspawns.cfg"));
            String s;
            while ((s = br.readLine()) != null) {
                try {
                    String parts[] = s.replace("spawn = ", "").split("\t");
                    globalObjects.add(new GameObject(GameObjectDefinition.forId(Integer.parseInt(parts[0])), Location.create(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3])), Integer.parseInt(parts[5]), Integer.parseInt(parts[4])));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            br.close();

            globalObjects.add(new GameObject(GameObjectDefinition.forId(2213), create(3275, 2785, 0), 10, 1));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2213), create(3275, 2784, 0), 10, 1));

            globalObjects.add(new GameObject(GameObjectDefinition.forId(7353), create(3203, 3422, 0), 10, 0));//slayer portal
            globalObjects.add(new GameObject(GameObjectDefinition.forId(61), create(3098, 3506, 0), 10, 2));//chaos altar
            globalObjects.add(new GameObject(GameObjectDefinition.forId(409), create(3094, 3506, 0), 10, 2));//Normal altar
            globalObjects.add(new GameObject(GameObjectDefinition.forId(6552), create(3096, 3500, 0), 10, 0));//ancient altar

            globalObjects.add(new GameObject(GameObjectDefinition.forId(410), create(3106, 3507, 0), 10, 0));//guthix (curses) altar

            globalObjects.add(new GameObject(GameObjectDefinition.forId(13192), create(2617, 3306, 0), 10, 2));
            //RFD Stuff.
            globalObjects.add(new GameObject(GameObjectDefinition.forId(12356), create(3207, 3225, 0), 10, 2));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2403), create(3207, 3220, 0), 10, 0));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2156), create(2975, 3392, 0), 10, 2));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2157), create(2957, 3195, 0), 10, 0));
            globalObjects.add(OSPK.loadObjects());
            OSPK.loadObjects(); // portal
            if (!Server.SPAWN) {
                for (int i = 0; i < 4; i++) {
                    globalObjects.add(new GameObject(GameObjectDefinition.forId(4875 + i), create(3084, 3496 + i, 0), 10, 0));
                }
            }


            for (SpecialArea area : SpecialAreaHolder.getAreas()) {
                if (area instanceof NIGGERUZ)
                    ((NIGGERUZ) area).initObjects(globalObjects);
            }


            try {
                Class.forName("org.hyperion.rs2.model.World");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            //logger.info("Loading map...");idk i tried to load on diff coords didnt work either

            //logger.info("Loaded " + objectCount + " objects.");
            System.out.println("Loaded Objects for " + objectCount + " objects.");

			/*buf.flip();
            byte[] data = new byte[buf.limit()];
			buf.get(data);
			os.write(data);
			os.flush();
			os.close();*/
        } finally {
            //cache.close();
        }


    }
    //private IoBuffer buf = null;

    @Override
    public void objectParsed(GameObject obj) {
        if (obj == null)
            return;
        objectCount++;
        /*buf.putShort((short) obj.getDefinition().getId());
        buf.putShort((short) obj.getLocation().getX());
		buf.putShort((short) obj.getLocation().getY());
		buf.put((byte) obj.getLocation().getZ());
		buf.put((byte) obj.getType());
		buf.put((byte) obj.getRotation());*/

        /*World.getWorld().
                getRegionManager().
                getRegionByLocation(obj.getLocation()).
                getGameObjects().add(obj); */
    }

    @Override
    public void objectDefinitionParsed(GameObjectDefinition def) {
        definitionCount++;
        GameObjectDefinition.addDefinition(def);
    }

    private List<GameObject> globalObjects = new LinkedList<GameObject>();

    public void addObject(GameObject obj) {
        globalObjects.add(obj);
        update(obj);
    }

    public void removeObject(GameObject obj) {
        globalObjects.remove(obj);
    }

    public void update(GameObject obj) {
        for (Player p : World.getWorld().getPlayers()) {
                if (obj == null) {
                    System.out.println("Object is null!");
                    return;
                }
                if (p == null){
                    System.out.println("Player is null!");
                    continue;
                }
            if (obj.isVisible(p.getLocation())) {
                p.getActionSender().sendReplaceObject(obj.getLocation(), obj.getDefinition().getId(), obj.getRotation(), obj.getType());
            }
        }
    }

    public void load(Player p) {
        for (GameObject obj : globalObjects) {
            if (obj.isVisible(p.getLocation())) {
                p.getActionSender().sendReplaceObject(obj.getLocation(), obj.getDefinition().getId(), obj.getRotation(), obj.getType());

            }
        }
    }

    public void submitEvent() {
       /* World.getWorld().submit(new Event(3000) {
            @Override
            public void execute() throws IOException {
                for(GameObject obj : globalObjects) {
                    if(obj.getDefinition().animation == -1) continue;
                    for(final Player player : World.getWorld().getRegionManager().getRegionByLocation(obj.getLocation()).getPlayers()) {
                        if(player != null)
                            player.getActionSender().createPlayersObjectAnim(obj.getLocation().getX(), obj.getLocation().getY(), obj.getDefinition().animation, obj.getType(), obj.getRotation());
                    }
                }
            }
        }); */
    }

    public void replace(GameObject obj, GameObject obj2) {
        removeObject(obj);
        update(obj2);

    }

    public GameObject getObjectAt(int x, int y, int z) {
        final Location loc = Location.create(x, y, z);
        return getObjectAt(loc);
    }

    public GameObject getObjectAt(Location loc) {
        for (GameObject object : globalObjects) {
            if (object.isAt(loc))
                return object;
        }
        return null;
    }

    public void addMapObject(int x, int y, int z, int id) {

    }

    public boolean objectExist(Location loc, int id) {
       /* final GameObject obj;
        boolean object = ((obj = getObjectAt(loc)) != null && obj.getDefinition().getId() == id);
        return objects[loc.getX() % 5000][loc.getY() % 11000][loc.getZ() % 4] == id || object;  */
        return true;
    }

   /* public void toMap() {
        if (objects == null)
            throw new IllegalStateException("Outta here");
        int i = 0;
        for (int x = 0; x < objects.length; x++) {
            for (int y = 0; y < objects[x].length; y++) {
                for (int z = 0; z < objects[x][y].length; z++) {
                    i++;
                    objectMap.put(Location.create(x, y, z), (int) objects[x][y][z]);
                }
            }
        }
        objects = null;
        System.err.println("LOADED " + i + " OBJECTS TO THE SYSTEM");
        ObjectClickHandler.loaded = true;
        System.gc();
    }    */


}
