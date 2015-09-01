package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ClickId;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc2.Afk;

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
        for(Player player : World.getWorld().getPlayers()) {
        	if(player == null) {
        		continue;
        	}
			player.getActionSender().sendMessage("We are currently in testing mode, testing the merge..");
			player.getActionSender().sendMessage("Your game progress of the past hour will possibly be lost.");
        	player.getSummBar().cycle();
        	player.getActionSender().sendString(4508, player.getSummBar().getAmount() + "");
        	player.getSpecBar().normalize();
        	player.getQuestTab().sendPlayerCount();
			player.getQuestTab().sendStaffCount();
        	player.getQuestTab().sendUptime();
            Afk.procesPlayer(player);
    		if(!World.getWorld().getContentManager().handlePacket(ClickType.OBJECT_CLICK1, player, ClickId.ATTACKABLE) && !player.getDungeoneering().inDungeon()) {
				try {
					FightPits.dissapate(player);
				}catch(StackOverflowError|Exception e) {
					e.printStackTrace();
				}
			}
        }

        ClanManager.save();
        //worldCupCycles();
	}

}
