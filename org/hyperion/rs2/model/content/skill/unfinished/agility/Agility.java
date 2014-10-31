package org.hyperion.rs2.model.content.skill.unfinished.agility;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.content.skill.GnomeStronghold;
import org.hyperion.util.Misc;

/**
 * 
 * @author High Detail
 * 
 */
public class Agility {

	/**
	 * the player is running a course
	 */
	private boolean running;

	/**
	 * The gnome course
	 */
	private final GnomeStronghold gnome = new GnomeStronghold();
	
	public static void appendFail(Player p) {
		p.getCombat().hit(Misc.random(10), p, false, Constants.DEFLECT);
		p.forceMessage("Ouch! I stubbed my toe...");
	}
	
	public static void walkAcross(final Player p, final Location pos, int anim, final int course, 
			int exp, final int courseType) {
		Agility.walkAcross(p, pos, anim, course, exp, courseType, 5, 0);
	}
	public static void walkAcross(final Player p, final Location pos, int anim, final int course, int exp, final int courseType, int ticks, int failrate) {
		/**
		 * The player is running the course
		 */
		if (p.getAgility().isRunning()) {
			return;
		}
		
		if(failrate > 0) {
			if(Misc.random(failrate) == 1)
				appendFail(p);
		}
		/**
		 * The movement
		 */
		p.debugMessage("Obstacle: "+course);
		p.getWalkingQueue().setRunningToggled(false);
		p.getAgility().setRunning(true);
		p.getActionSender().forceMovement(pos.getX(), pos.getY(), anim);
		/**
		 * Resetting
		 */
		reset(p, ticks, exp);
		if(courseType == GnomeStronghold.COURSE_TYPE)
			p.getAgility().getGnome().getCompleted()[course] = true;
	}

	/**
	 * Player interacting with obstacle
	 * 
	 * @param p
	 *            the player
	 * @param pos
	 *            the new position
	 * @param animation
	 *            the animation
	 * @param course
	 *            the course slot
	 * @param exp
	 *            the experience
	 */
	public static void obstacle(final Player p, final Location pos, int animation, final int course, int exp, final int courseType) {
		/**
		 * The player is running the course
		 */
		if (p.getAgility().isRunning()) {
			return;
		}
		/**
		 * The climbing
		 */
		p.debugMessage("Obstacle: "+course);
		p.getAgility().setRunning(true);
		p.playAnimation(Animation.create(animation));
		World.getWorld().submit(new Event(600) {

			@Override
			public void execute() {
				p.setTeleportTarget(Location.create(pos.getX(), pos.getY(), pos.getZ()));
				if(courseType == GnomeStronghold.COURSE_TYPE)
					p.getAgility().getGnome().getCompleted()[course] = true;
				this.stop();
			}
		});
		reset(p, 1, exp);
	}

	/**
	 * Resetting after a agility run
	 * 
	 * @param p
	 *            the player
	 * @param tick
	 *            the length of action
	 * @oaram exp the experience received for the run
	 */
	public static void reset(final Player p, final int tick, final double exp) {
		World.getWorld().submit(new Event(tick * 600) {

			@Override
			public void execute() {
				p.getAgility().setRunning(false);
				p.getWalkingQueue().setRunningToggled(true);
				p.getAppearance().setWalkAnim(0x337); //default walk animation
				p.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
				p.getSkills().addExperience(Skills.AGILITY, exp * Constants.XPRATE);
				this.stop();
			}
		});
	}

	/**
	 * @return the running
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * @param running
	 *            the running to set
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}

	/**
	 * @return the gnome
	 */
	public GnomeStronghold getGnome() {
		return gnome;
	}
}