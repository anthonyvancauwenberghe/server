package org.hyperion.rs2.model.content.skill;

import java.io.FileNotFoundException;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.skill.unfinished.agility.Agility;

/**
 * 
 * @author High Detail
 * @author Alsk Jet
 * 
 */
public class GnomeStronghold implements ContentTemplate{
	public static final int COURSE_TYPE = 0;
	private final static int EXPMULTIPLIER = Constants.XPRATE;

	public final static Location GNOMELOCATION = Location.create(2479, 3437, 0);
	
	
	/**
	 * The completed state of the course
	 */
	private boolean[] completed = new boolean[6];
	

	/**
	 * Object clicking for Gnome Stronghold
	 * 
	 * @param p
	 *            the player
	 * @param objectId
	 *            The Object ID
	 */
	public static boolean objectClicking(Player p, int objectId) {
		switch(objectId) {
		case 2295://log
			Agility.walkAcross(p, Location.create(2474, 3429, 0), 762, 0, 20, COURSE_TYPE, 7, 15);
		return true;
		case 2313://branch up
			Agility.obstacle(p, Location.create(2473, 3419, 2), 828, 2, 30, COURSE_TYPE);
		return true;
		case 2285://net up
				Agility.obstacle(p, Location.create(2474, 3424, 1), 828, 1, 20, COURSE_TYPE);
		break;
		case 2312://rope
			if(p.getLocation().getX() <= 2478)
			Agility.walkAcross(p, Location.create(2483, 3420, 2), 762, 3, 20, COURSE_TYPE, 6, 0);
		return true;
		case 2286://net up
				if(p.getLocation().getY() <= 3425)
					Agility.obstacle(p, Location.create(p.getLocation().getX(), 3427, 0), 828, 5, 20, COURSE_TYPE);
		return true;
		case 2314:
		case 2315:
			Agility.obstacle(p, Location.create(p.getLocation().getX(), p.getLocation().getY(), 0), 828, 4, 30, COURSE_TYPE);
			return true;
		case 154:
		case 4058:
			pipeCrawl(p);
			return true;
		}
		return false;
	}

	/**
	 * Player is crawling pipe
	 * 
	 * @param p
	 *            the player
	 */
	public static void pipeCrawl(final Player p) {
		/**
		 * The player is running the course
		 */
		if (p.getAgility().isRunning()) {
			return;
		}
		/**
		 * The experience
		 */
		int exp = 1290;
		for (int i = 0; i < p.getAgility().getGnome().getCompleted().length; i++) {
			if (!p.getAgility().getGnome().getCompleted()[i]) {
				exp = 70;
			}
		}
		final int xp = exp;
		/**
		 * The crawling
		 */
		if (p.getLocation().getY() == 3430) {
			p.getAgility().setRunning(true);
			p.getWalkingQueue().setRunningToggled(false);
			World.getWorld().submit(new Event(600) {

				@Override
				public void execute() {
					p.getActionSender().forceMovement(p.getLocation().getX(), 3437, 844);
					this.stop();
				}
			});
			Agility.reset(p, 6, xp);
			for (int i = 0; i < p.getAgility().getGnome().getCompleted().length; i++) {
				p.getAgility().getGnome().getCompleted()[i] = false;
			}
		}
	}

	/**
	 * @return the completed
	 */
	public boolean[] getCompleted() {
		return completed;
	}

	/**
	 * @param completed
	 *            the completed to set
	 */
	public void setCompleted(boolean[] completed) {
		this.completed = completed;
	}


	@Override
	public void init() throws FileNotFoundException {
		// TODO Auto-generated method stub
		
	}
	
	public boolean clickObject(Player player, int type, int objId, int x, int y,
			int d) {
		return objectClicking(player,objId);
	}


	@Override
	public int[] getValues(int type) {
		if(type == ContentManager.OBJECT_CLICK1){
			//System.out.println("INITALIZING AGAILITY STUFF");
			return new int[]{2295,2285,2314,2312,2313,154,104, 4058, 2286, 2315};
		} else if(type == ContentManager.OBJECT_CLICK2){
			//return new int[]{111,112,113,114};
		}
		return null;
	}
}