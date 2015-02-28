package org.hyperion.rs2.model;

import org.hyperion.Server;
import org.hyperion.cache.Cache;
import org.hyperion.cache.InvalidCacheException;
import org.hyperion.cache.index.impl.StandardIndex;
import org.hyperion.cache.map.LandscapeListener;
import org.hyperion.cache.obj.ObjectDefinitionListener;
import org.hyperion.cache.obj.ObjectDefinitionParser;
import org.hyperion.rs2.event.Event;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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
			while((s = br.readLine()) != null){
				try {
					String parts[] = s.replace("spawn = ","").split("\t");
					globalObjects.add(new GameObject(GameObjectDefinition.forId(Integer.parseInt(parts[0])), Location.create(Integer.parseInt(parts[1]),Integer.parseInt(parts[2]),Integer.parseInt(parts[3])), Integer.parseInt(parts[5]), Integer.parseInt(parts[4])));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			br.close();

            //globalObjects.add(new GameObject(GameObjectDefinition.forId(6552), Location.create(3256,3484,0), 10, 2));//ancient altar
            globalObjects.add(new GameObject(GameObjectDefinition.forId(7353), create(3203, 3422, 0), 10, 0));//slayer portal
            globalObjects.add(new GameObject(GameObjectDefinition.forId(61), create(3098, 3506, 0), 10, 2));//chaos altar
            globalObjects.add(new GameObject(GameObjectDefinition.forId(409), create(3094, 3506, 0), 10, 2));//Normal altar
            globalObjects.add(new GameObject(GameObjectDefinition.forId(6552), create(3096, 3500, 0), 10, 0));//ancient altar

            globalObjects.add(new GameObject(GameObjectDefinition.forId(410), create(3106, 3507, 0), 10, 0));//guthix (curses) altar

            //globalObjects.remove(new GameObject(GameObjectDefinition.forId(409), Location.create(2617, 3309, 0),10,0));

            globalObjects.add(new GameObject(GameObjectDefinition.forId(13192), create(2617, 3306, 0), 10, 2));
            //RFD Stuff.
            globalObjects.add(new GameObject(GameObjectDefinition.forId(12356), create(3207, 3225, 0), 10, 2));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2403), create(3207, 3220, 0), 10, 0));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2156), create(2975, 3392, 0), 10, 2));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2157), create(2957, 3195, 0), 10, 0));

            GameObjectDefinition object = GameObjectDefinition.forId(1278);
            globalObjects.add(new GameObject(object, create(3802, 2855, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3802, 2858, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3802, 2861, 0), 10, 0));

            object = GameObjectDefinition.forId(1308);
            globalObjects.add(new GameObject(object, create(3799, 2861, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3796, 2861, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3793, 2861, 0), 10, 0));

            object = GameObjectDefinition.forId(1307);
            globalObjects.add(new GameObject(object, create(3799, 2858, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3796, 2858, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3793, 2858, 0), 10, 0));

            object = GameObjectDefinition.forId(1306);
            globalObjects.add(new GameObject(object, create(3799, 2855, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3796, 2855, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3793, 2855, 0), 10, 0));

            object = GameObjectDefinition.forId(4172);
            globalObjects.add(new GameObject(object, create(3800, 2850, 0), 10, 2));
            globalObjects.add(new GameObject(object, create(3800, 2852, 0), 10, 2));

            object = GameObjectDefinition.forId(2782);
            globalObjects.add(new GameObject(object, create(3800, 2847, 0), 10, 0));
            globalObjects.add(new GameObject(object, create(3800, 2846, 0), 10, 0));

            globalObjects.add(new GameObject(GameObjectDefinition.forId(409), create(3795, 2839, 0), 10, 2));

            globalObjects.add(new GameObject(GameObjectDefinition.forId(2478), create(3786, 2843, 0), 10, 0));
            globalObjects.add(new GameObject(GameObjectDefinition.forId(2488), create(3790, 2843, 0), 10, 0));

            for(int i = 0; i < 4; i++) {
                globalObjects.add(new GameObject(GameObjectDefinition.forId(2090), create(3793-(i*2), 2838, 0), 10, 0));
                globalObjects.add(new GameObject(GameObjectDefinition.forId(2092), create(3793-(i*2), 2837, 0), 10, 0));
                globalObjects.add(new GameObject(GameObjectDefinition.forId(2096), create(3793-(i*2), 2836, 0), 10, 0));
                globalObjects.add(new GameObject(GameObjectDefinition.forId(2102), create(3793-(i*2), 2835, 0), 10, 0));
                globalObjects.add(new GameObject(GameObjectDefinition.forId(2104), create(3793-(i*2), 2834, 0), 10, 0));
                globalObjects.add(new GameObject(GameObjectDefinition.forId(2106), create(3793-(i*2), 2833, 0), 10, 0));
            }

            //globalObjects.add(new GameObject(GameObjectDefinition.forId(-1), create(3795, 2844, 0), 10, 0));

            globalObjects.add(OSPK.loadObjects());
            OSPK.loadObjects(); // portal
            if(! Server.SPAWN) {
                for(int i = 0; i < 4; i++) {
                    globalObjects.add(new GameObject(GameObjectDefinition.forId(4875 + i), create(3084, 3496 + i, 0), 10, 0));
                }
            }

			//logger.info("Loading map...");idk i tried to load on diff coords didnt work either
			/*MapIndex[] mapIndices = cache.getIndexTable().getMapIndices();
			for(MapIndex index : mapIndices) {
				new LandscapeParser(cache, index.getIdentifier(), this).parse();
			}*/
			//logger.info("Loaded " + objectCount + " objects.");
			//System.out.println("Loaded Object Definitions for " + objectCount + " objects.");
			
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
		objectCount++;
		/*buf.putShort((short) obj.getDefinition().getId());
		buf.putShort((short) obj.getLocation().getX());
		buf.putShort((short) obj.getLocation().getY());
		buf.put((byte) obj.getLocation().getZ());
		buf.put((byte) obj.getType());
		buf.put((byte) obj.getRotation());*/
		//World.getWorld().getRegionManager().getRegionByLocation(obj.getLocation()).getGameObjects().add(obj);
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
		for(Player p : World.getWorld().getPlayers()) {
			if(p.getLocation().distance(obj.getLocation()) < 64 && p.getLocation().getZ()%4 == obj.getLocation().getZ()%4) {
				p.getActionSender().sendReplaceObject(obj.getLocation(), obj.getDefinition().getId(), obj.getRotation(), obj.getType());
                if(obj.getDefinition().animation != -1)
                    p.getActionSender().createPlayersObjectAnim(obj.getLocation().getX(), obj.getLocation().getY(), obj.getDefinition().animation, obj.getType(), obj.getRotation());
			}
		}
	}

	public void load(Player p) {
		for(GameObject obj : globalObjects) {
			if(p.getLocation().distance(obj.getLocation()) < 64 && p.getLocation().getZ()%4 == obj.getLocation().getZ()%4) {
				p.getActionSender().sendReplaceObject(obj.getLocation(), obj.getDefinition().getId(), obj.getRotation(), obj.getType());
                if(obj.getDefinition().animation != -1)
                    p.getActionSender().createPlayersObjectAnim(obj.getLocation().getX(), obj.getLocation().getY(), obj.getDefinition().animation, obj.getType(), obj.getRotation());
            }
		}
	}

    public void submitEvent() {
        World.getWorld().submit(new Event(600) {
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
        });
    }

	public void replace(GameObject obj, GameObject obj2) {
		removeObject(obj);
		update(obj2);

	}

	public GameObject getObjectAt(int x, int y, int z) {
		for(GameObject object : globalObjects) {
			if(object.getLocation().getX() == x && object.getLocation().getY() == y)
				return object;
		}
		return null;
	}

	public GameObject getObjectAt(Location loc) {
		for(GameObject object : globalObjects) {
			if(object.getLocation().getX() == loc.getX() && object.getLocation().getY() == loc.getY())
				return object;
		}
		return null;
	}

}
