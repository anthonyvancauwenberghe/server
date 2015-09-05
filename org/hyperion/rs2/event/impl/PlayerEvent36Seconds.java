package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.GenericWorldLoader;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc2.Afk;
import sun.net.www.content.text.Generic;

/**
 * An event which increases ActivityPoints, refreshes Quest Tab , refreshes
 * Skills.
 */
public class PlayerEvent36Seconds extends Event {

	/**
	 * The delay in milliseconds between consecutive execution.
	 */
	public static final int CYCLETIME = 36000;

	/**
	 * Creates the event each 36 seconds.
	 */
	public PlayerEvent36Seconds() {
		super(CYCLETIME);
	}
	public static String old;
	public static void worldCupCycles() {
        //try {
            //String n = QuestTab.getWorldCupScores();
            //if(old == null || !old.equalsIgnoreCase(n)) {
            //	old = n;
            //	PushMessage.pushGlobalMessage("[WC NEWS]: "+n);
            //}
            //}catch(IOException e) {
            //	e.printStackTrace();
            //}
	}

	@Override
	public void execute() {
		int artero = 0;
		int instant = 0;
        for(Player player : World.getWorld().getPlayers()) {
        	if(player == null) {
        		continue;
        	}
			if(player.getInitialSource() == GenericWorldLoader.ARTERO)
				artero++;
			else if(player.getInitialSource() == GenericWorldLoader.INSTANT)
				instant++;

			if(player.getSource() == GenericWorldLoader.INSTANT) {
				player.sendServerMessage("Players from InstantPK had their items converted to Pk tickets");
				player.sendServerMessage("and donator points. They have been added to their bank.");
			}
			player.getSummBar().cycle();
        	player.getActionSender().sendString(4508, player.getSummBar().getAmount() + "");
        	player.getSpecBar().normalize();
        	player.getQuestTab().sendPlayerCount();
			player.getQuestTab().sendStaffCount();
        	player.getQuestTab().sendUptime();
            Afk.procesPlayer(player);
        }
		System.out.println("InstantPK players: " + instant + "/ArteroPK players: " + artero);
        ClanManager.save();
	}

}
