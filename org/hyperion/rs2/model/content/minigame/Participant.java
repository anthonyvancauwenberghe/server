package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.model.Player;

/**
 * Created by Scott Perretta on 4/12/2015.
 */
public class Participant implements Comparable<Participant> {

    private final Player player;

    private int deaths;

    private int kills;

    private int bountyReward;

    public Participant(final Player player, final int deaths, final int kills) {
        this.player = player;
        this.deaths = deaths;
        this.kills = kills;
        bountyReward = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeaths(final int amount) {
        deaths += amount;
    }

    public int getKills() {
        return kills;
    }

    public void addKills(final int amount) {
        kills += amount;
    }

    public void increaseBountyReward(final int amount) {
        bountyReward += amount;
    }

    public int getBountyReward() {
        return bountyReward;
    }

    @Override
    public int compareTo(final Participant p) {
        return this.getKills() < p.getKills() ? -1 : this.getKills() > p.getKills() ? 1 : 0;
    }

}
