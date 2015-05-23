package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.model.Player;

import java.util.Comparator;

/**
 * Created by Scott Perretta on 4/12/2015.
 */
public class Participant implements Comparator<Object> {

    private Player player;

    private int deaths;

    private int kills;

    private int bountyReward;

    public Participant(Player player, int deaths, int kills) {
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

    public void addDeaths(int amount) {
        deaths += amount;
    }

    public int getKills() {
        return kills;
    }

    public void addKills(int amount) {
        kills += amount;
    }

    public void increaseBountyReward(int amount) {
        bountyReward += amount;
    }

    public int getBountyReward() {
        return bountyReward;
    }

    @Override
    public int compare(Object a, Object b) {
        Participant p1 = (Participant) a;
        Participant p2 = (Participant) b;
        return p1.getKills() < p2.getKills() ? -1 : p1.getKills() > p2.getKills() ? 1 : 0;
    }

}
