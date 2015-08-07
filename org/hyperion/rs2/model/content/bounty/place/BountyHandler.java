package org.hyperion.rs2.model.content.bounty.place;

import java.util.ArrayList;
import java.util.List;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.bounty.place.Bounty;
import org.hyperion.rs2.util.PushMessage;

public final class BountyHandler {
	private final List<Bounty> bounties = new ArrayList<>();
	
	public synchronized final boolean add(final String playerName, final String fromPlayer, int bounty) {
		bounty = (int)(bounty * .85);
		if(bounty < 400)
		    return false;
		Bounty old = this.getBountyByName(playerName);
		if(old != null && old.getBounty() > bounty)
			return false;
		else if(old != null) {
			if(remove(old)) {
                PushMessage.pushGlobalMessage(String.format("[@or2@Bounty@bla@]: %s has just placed a bounty of %d on %s's head!", fromPlayer, bounty, playerName));
                return bounties.add(Bounty.create(playerName, fromPlayer, bounty));
            } else return false;
		}
        PushMessage.pushGlobalMessage(String.format("[@or2@Bounty@bla@]: %s has just placed a bounty of %d on %s's head!", fromPlayer, bounty, playerName));
        return bounties.add(Bounty.create(playerName, fromPlayer, bounty));
	}
	
	public synchronized final boolean remove(Bounty bounty) {
		return bounties.remove(bounty);
	}
	
	public final boolean remove(String key) {
		for(Bounty bounty : bounties) {
			if(bounty.getName().equalsIgnoreCase(key))
				return remove(bounty);
		}
		return false;
	}
	
	public final Bounty getBountyByName(String name) {
		for(Bounty bounty : bounties) {
			if(bounty.getName().equalsIgnoreCase(name))
				return bounty;
		}
		return null;
	}
	
	public final void handle(final Player killer, String key) {
		final Bounty bounty = getBountyByName(key);
		if(bounty != null) {
            int pkpToGain = bounty.getBounty();
            if(ipCheck(killer, key))
                return;
			if(remove(bounty)) {
				PushMessage.pushGlobalMessage(String.format("[@or2@Bounty@bla@]: %s has just defeated %s for a %d Pk points bounty!", killer.getName(),key, pkpToGain));
				killer.getPoints().inceasePkPoints(pkpToGain);
			}
		}
	}

    public final boolean ipCheck(final Player killer, final String key) {
        Player keyPlayer = World.getWorld().getPlayer(key);
        if(keyPlayer != null)
            if(keyPlayer.getShortIP().equalsIgnoreCase(killer.getShortIP()))
                return true;
        return false;
    }
	
	public final void listBounties(Player player) {
		for(Bounty bounty : bounties) {
			player.sendf("From: @or2@%s@bla@ For: @red@%s@bla@ Amount: @blu@%d@bla@.", bounty.getBy(), bounty.getName(), bounty.getBounty());
		}
	}
}
