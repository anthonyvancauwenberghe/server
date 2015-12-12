package org.hyperion.rs2.model.content;

import org.hyperion.data.PersistenceManager;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.GameObjectDefinition;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.Door.DoorType;
import org.hyperion.rs2.model.region.Region;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DoorManager {

    private static final Logger logger = Logger.getLogger(DoorManager.class.getName());
    private static final Map<Location, Door> doors = new HashMap<Location, Door>();

    @SuppressWarnings("unchecked")
    public static void init() {
        try{
            final List<Door> list = (List<Door>) PersistenceManager.load(new FileInputStream("./data/doors.xml"));
            for(final Door d : list){
                if(d.getType() == DoorType.NORMAL || d.getType() == DoorType.NORMALFORCE){
                    doors.put(d.getOpenLocation(), d);
                    doors.put(d.getClosedLocation(), d);
                }else{ //For now theres only double doors left.
                    doors.put(d.getOpenLocation(), d);
                    doors.put(d.getClosedLocation(), d);
                    doors.put(d.getSecondOpenLocation(), d);
                    doors.put(d.getSecondClosedLocation(), d);
                }
            }
            logger.info("Loaded " + list.size() + " doors.");
        }catch(final Exception e){
            logger.warning("Failed to load the doors for some reason, check if doors.xml is in the data folder.");
            System.out.println(e);
        }
    }

    /**
     * Checks if we have the current door in our HashMap.
     * If thats the case, we open or close it, based on its open/close state.
     *
     * @param player The player clicking objects.
     * @param loc    The location of the object clicked.
     * @return True if the map contains the location, false if not.
     */
    public static boolean handleDoor(final Player player, final Location loc, final int objectId) {
        final Door door = doors.get(Location.create(loc.getX(), loc.getY(), loc.getZ() % 4));
        if(door == null){
            final GameObjectDefinition def = GameObjectDefinition.forId(objectId);
            if(def.getName() != null){
                //System.out.println(""+def.getName());
                if(def.getName().toLowerCase().contains("gate") || objectId == 1553 || objectId == 1551){
                    player.getActionSender().sendDestroyObject(0, 0, loc);
                    return true;
                }
            }
            return false;
        }
        if(door.isOpen()){
            switch(door.getType()){
                case NORMAL:
                    for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                        for(final Player p : reg.getPlayers()){
                            if(p.getLocation().distance(door.getOpenLocation()) < 15){
                                p.getActionSender().sendDestroyObject(door.getOpenType(), door.getOpenFace(), Location.create(door.getOpenLocation().getX(), door.getOpenLocation().getY(), p.getLocation().getZ()));
                            }
                            if(p.getLocation().distance(door.getClosedLocation()) < 15){
                                p.getActionSender().sendCreateObject(door.getClosedId(), door.getClosedType(), door.getClosedFace(), Location.create(door.getClosedLocation().getX(), door.getClosedLocation().getY(), p.getLocation().getZ()));
                            }
                        }
                    }
                    door.redoOpenState();
                    break;
                case NORMALFORCE:
                    //TODO: Nothing.
                    break;
                case DOUBLE:
                    for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                        for(final Player p : reg.getPlayers()){
                            if(p.getLocation().distance(door.getOpenLocation()) < 15){
                                p.getActionSender().sendDestroyObject(door.getOpenType(), door.getOpenFace(), Location.create(door.getOpenLocation().getX(), door.getOpenLocation().getY(), p.getLocation().getZ()));
                                p.getActionSender().sendDestroyObject(door.getSecondaryOpenType(), door.getSecondaryOpenFace(), Location.create(door.getSecondOpenLocation().getX(), door.getSecondOpenLocation().getY(), p.getLocation().getZ()));
                            }
                            if(p.getLocation().distance(door.getClosedLocation()) < 15){
                                p.getActionSender().sendCreateObject(door.getClosedId(), door.getClosedType(), door.getClosedFace(), Location.create(door.getClosedLocation().getX(), door.getClosedLocation().getY(), p.getLocation().getZ()));
                                p.getActionSender().sendCreateObject(door.getSecondaryClosedId(), door.getSecondaryClosedType(), door.getSecondaryClosedFace(), Location.create(door.getSecondClosedLocation().getX(), door.getSecondClosedLocation().getY(), p.getLocation().getZ()));
                            }
                        }
                    }
                    door.redoOpenState();
                    break;
                case DOUBLEFORCE:
                    //TODO: Nothing.
                    break;
            }
        }else{
            switch(door.getType()){

                case NORMAL:
                    for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                        for(final Player p : reg.getPlayers()){
                            if(p.getLocation().isWithinDistance(door.getClosedLocation())){
                                p.getActionSender().sendDestroyObject(door.getClosedType(), door.getClosedFace(), door.getClosedLocation());
                            }
                            if(p.getLocation().isWithinDistance(door.getOpenLocation())){
                                p.getActionSender().sendCreateObject(door.getOpenId(), door.getOpenType(), door.getOpenFace(), door.getOpenLocation());
                            }
                        }
                    }
                    door.redoOpenState();
                    break;
                case NORMALFORCE:
                    final double distance1 = player.getLocation().distance(door.getWalkTo());
                    final double distance2 = player.getLocation().distance(door.getSecondaryWalkTo());
                    player.getWalkingQueue().reset();
                /*
                 * We want to walk to the location which is farthest away.
				 */
                    if(distance1 < distance2){
                        if(!player.getLocation().equals(door.getWalkTo())){
                            player.getWalkingQueue().addStep(door.getWalkTo().getX(), door.getWalkTo().getY());
                        }
                        player.getWalkingQueue().addStep(door.getSecondaryWalkTo().getX(), door.getSecondaryWalkTo().getY());
                    }else{
                        if(!player.getLocation().equals(door.getSecondaryWalkTo())){
                            player.getWalkingQueue().addStep(door.getSecondaryWalkTo().getX(), door.getSecondaryWalkTo().getY());
                        }
                        player.getWalkingQueue().addStep(door.getWalkTo().getX(), door.getWalkTo().getY());
                    }
                    player.getWalkingQueue().finish();
                    for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                        for(final Player p : reg.getPlayers()){
                            if(p.getLocation().isWithinDistance(door.getClosedLocation())){
                                p.getActionSender().sendDestroyObject(door.getClosedType(), door.getClosedFace(), door.getClosedLocation());
                                p.getActionSender().sendCreateObject(door.getOpenId(), door.getOpenType(), door.getOpenFace(), door.getOpenLocation());
                            }
                        }
                    }
                    World.getWorld().submit(new Event(1200) {
                        @Override
                        public void execute() {
                            for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                                for(final Player p : reg.getPlayers()){
                                    if(p.getLocation().distance(door.getOpenLocation()) < 15){
                                        p.getActionSender().sendDestroyObject(door.getClosedType(), door.getClosedFace(), door.getOpenLocation());
                                        p.getActionSender().sendCreateObject(door.getClosedId(), door.getClosedType(), door.getClosedFace(), door.getClosedLocation());
                                    }
                                }
                            }
                            this.stop();
                        }
                    });
                    break;
                case DOUBLE:
                    for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                        for(final Player p : reg.getPlayers()){
                            if(p.getLocation().distance(door.getOpenLocation()) < 15){
                                p.getActionSender().sendCreateObject(door.getClosedId(), door.getClosedType(), door.getClosedFace(), Location.create(door.getClosedLocation().getX(), door.getClosedLocation().getY(), player.getLocation().getZ()));
                                p.getActionSender().sendCreateObject(door.getSecondaryClosedId(), door.getSecondaryClosedType(), door.getSecondaryClosedFace(), Location.create(door.getSecondClosedLocation().getX(), door.getSecondClosedLocation().getY(), player.getLocation().getZ()));
                            }
                            if(p.getLocation().distance(door.getClosedLocation()) < 15){
                                p.getActionSender().sendDestroyObject(door.getOpenType(), door.getOpenFace(), Location.create(door.getOpenLocation().getX(), door.getOpenLocation().getY(), player.getLocation().getZ()));
                                p.getActionSender().sendDestroyObject(door.getSecondaryOpenType(), door.getSecondaryOpenFace(), Location.create(door.getSecondOpenLocation().getX(), door.getSecondOpenLocation().getY(), player.getLocation().getZ()));
                            }
                        }
                    }
                    door.redoOpenState();
                    break;
                case DOUBLEFORCE:
                    final Location[] locations = new Location[4];
                    locations[0] = door.getLocations()[4];
                    locations[1] = door.getLocations()[5];
                    locations[2] = door.getLocations()[6];
                    locations[3] = door.getLocations()[7];
                    double smallestDistance = player.getLocation().distance(locations[0]);
                    int smallestIndex = 0;
                    for(int index = 1; index < locations.length; index++){
                        final double dist = player.getLocation().distance(locations[index]);
                        if(dist < smallestDistance){
                            smallestDistance = dist;
                            smallestIndex = index;
                        }
                    }
                    player.getWalkingQueue().reset();
                    if(smallestDistance != 0){ //We're not standing at the distance that is closest to us.
                        player.getWalkingQueue().addStep(locations[smallestIndex].getX(), locations[smallestIndex].getY());
                    }

				/*
				 * We want to walk to the closest of the two locations which is farthest away.
				 */
                    if(smallestIndex == 0 || smallestIndex == 1){
                        if(player.getLocation().distance(locations[2]) > player.getLocation().distance(locations[3])){
                            player.getWalkingQueue().addStep(locations[3].getX(), locations[3].getY());
                        }else{
                            player.getWalkingQueue().addStep(locations[2].getX(), locations[2].getY());
                        }

                    }else{//Index is 2 or 3.
                        if(player.getLocation().distance(locations[0]) > player.getLocation().distance(locations[1])){
                            player.getWalkingQueue().addStep(locations[1].getX(), locations[1].getY());
                        }else{
                            player.getWalkingQueue().addStep(locations[0].getX(), locations[0].getY());
                        }
                    }
                    player.getWalkingQueue().finish();
                    for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                        for(final Player p : reg.getPlayers()){
                            if(p.getLocation().isWithinDistance(door.getClosedLocation())){
                                p.getActionSender().sendDestroyObject(door.getClosedType(), door.getClosedFace(), door.getClosedLocation());
                                p.getActionSender().sendDestroyObject(door.getSecondaryClosedType(), door.getSecondaryClosedFace(), door.getSecondClosedLocation());
                                p.getActionSender().sendCreateObject(door.getOpenId(), door.getOpenType(), door.getOpenFace(), door.getOpenLocation());
                                p.getActionSender().sendCreateObject(door.getSecondaryOpenId(), door.getSecondaryOpenType(), door.getSecondaryOpenFace(), door.getSecondOpenLocation());
                            }
                        }
                    }
                    World.getWorld().submit(new Event(1200) {
                        @Override
                        public void execute() {
                            for(final Region reg : World.getWorld().getRegionManager().getSurroundingRegions(loc)){
                                for(final Player p : reg.getPlayers()){
                                    if(p.getLocation().isWithinDistance(door.getOpenLocation())){
                                        p.getActionSender().sendDestroyObject(door.getOpenType(), door.getOpenFace(), door.getOpenLocation());
                                        p.getActionSender().sendDestroyObject(door.getSecondaryOpenType(), door.getSecondaryOpenFace(), door.getSecondOpenLocation());
                                        p.getActionSender().sendCreateObject(door.getClosedId(), door.getClosedType(), door.getClosedFace(), door.getClosedLocation());
                                        p.getActionSender().sendCreateObject(door.getSecondaryClosedId(), door.getSecondaryClosedType(), door.getSecondaryClosedFace(), door.getSecondClosedLocation());
                                    }
                                }
                            }
                            this.stop();
                        }
                    });
                    break;
            }

        }
        return true;
    }

    /**
     * Sends all the correct doors(open/closed) within location of the player.
     * Use this when we're reloading the map region.
     */
    public static void refresh(final Player player) {
        for(final Door door : doors.values()){
            if(player.getLocation().isWithinDistance(door.getClosedLocation())){
                if(door.isOpen())
                    player.getActionSender().sendCreateObject(door.getOpenId(), 0, door.getOpenFace(), door.getOpenLocation());
                else
                    player.getActionSender().sendCreateObject(door.getClosedId(), 0, door.getClosedFace(), door.getClosedLocation());
            }
        }
    }
    //public static Door[] spawns;

    public static int getOpenRot(final int rot, final Location loc) {
        System.out.println("Open: " + rot);
        switch(rot){
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 3;
            case 3:
                return 0;
            default:
                System.out.println("Missing rotation configs for: " + rot + " GET OPEN" + loc);
        }
        return -1;
    }

    public static int getClosedRot(final int rot, final Location loc) {
        System.out.println("Closed: " + rot);
        switch(rot){
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 0:
                return 3;
            default:
                System.out.println("Missing rotation configs for: " + rot + " GET CLOSED" + loc);
        }
        return -1;
    }

    public static int getOpenXOffset(final int rot, final Location loc) {
        switch(rot){
            case 0:
                return -1;
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 0;
            default:
                System.out.println("Missing open x offset for: " + rot + " " + loc);
        }
        return 0;
    }

    public static int getOpenYOffset(final int rot, final Location loc) {
        switch(rot){
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 0;
            case 3:
                return -1;
            default:
                System.out.println("Missing open y offset for: " + rot + " " + loc);
        }
        return 0;
    }

    public static int getClosedXOffset(final int rot, final Location loc) {
        switch(rot){
            case 0:
                return 0;
            case 1:
                return 1;
            case 2:
                return 0;
            case 3:
                return -1;
            default:
                System.out.println("Missing closed x offset for: " + rot + " " + loc);
        }
        return 0;
    }

    public static int getClosedYOffset(final int rot, final Location loc) {
        switch(rot){
            case 0:
                return 1;
            case 1:
                return 0;
            case 2:
                return -1;
            case 3:
                return 0;
            default:
                System.out.println("Missing closed y offset for: " + rot + " " + loc);
        }
        return 0;
    }

    public static int getOpenDoor(final int id) {
        switch(id){
            case 15644:
            case 15645:
                return 15645;
            default:
                return -1;
        }
    }

    public static int getClosedDoor(final int id) {
        switch(id){
            case 15644:
            case 15645:
                return 15644;
            default:
                return -1;
        }
    }

	/*public static void handleConstructionDoor(Player player, ConstructionObject obj, Room room, Location location, int id) {
		int openDoor = getOpenDoor(id);
		int closedDoor = getClosedDoor(id);
		boolean open = id == openDoor;
		if(openDoor != -1 && closedDoor != -1) {
			//Location loc = null;
			int rotation = -1;		
			//north
			System.out.println("Locations: " + location + " " + room.getY() + " " + room.getX());
			if(location.getY() == room.getY() + 7) {
				System.out.println("North");
				Location one = Location.create(room.getX() + 3, room.getY() + 7, room.getHeight());
				Location two = Location.create(room.getX() + 4, room.getY() + 7, room.getHeight());
				System.out.println("One: " + one + " two: " + two);
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), one);
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), two);
				if(open) {
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 0, one);
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 2, two);
				} else {
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 1, one);
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 1, two);
				}
				//south
			} else if(location.getY() == room.getY()) {
				System.out.println("south");
				Location one = Location.create(room.getX() + 3, room.getY(), room.getHeight());
				Location two = Location.create(room.getX() + 4, room.getY(), room.getHeight());
				System.out.println("One: " + one + " two: " + two);
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), one);
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), two);
				if(open) {
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 0, one);
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 2, two);
				} else {
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 3, one);
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 3, two);
				}
				//loc = Location.create((x / 8), (y / 8) - 1, z);
				//east
			} else if(location.getX() == room.getX() + 7) {
				Location one = Location.create(room.getX() + 7, room.getY() + 3, room.getHeight());
				Location two = Location.create(room.getX() + 7, room.getY() + 4, room.getHeight());
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), one);
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), two);
				if(open) {
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 2, one);
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 2, two);
				} else {
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 3, one);
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 1, two);
				}
				System.out.println("east");
				//loc = Location.create((x / 8) + 1, (y / 8), z);
				//west
			} else if(location.getX() == room.getX()) {
				Location one = Location.create(room.getX(), room.getY() + 3, room.getHeight());
				Location two = Location.create(room.getX(), room.getY() + 4, room.getHeight());
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), one);
				player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), two);
				if(open) {
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 0, one);
					player.getActionSender().sendCreateLocalObject(closedDoor, obj.getType(), 0, two);
				} else {
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 3, one);
					player.getActionSender().sendCreateLocalObject(openDoor, obj.getType(), 1, two);
				}
				System.out.println("west");
				//loc = Location.create((x / 8) - 1, (y / 8), z);
			}
			System.out.println("win stuff");
			obj.setRotation(rotation);
			room.saveOtherObject(obj);
			//player.getActionSender().sendDestroyLocalObject(obj.getType(), obj.getRotation(), loc);
			//player.getActionSender().sendCreateLocalObject(open ? closedDoor : openDoor, obj.getType(), rotation, location);
		}

	}*/
}