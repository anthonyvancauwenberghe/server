package org.hyperion.rs2.model.content.misc;

public class ScoreboardPlayer {

    private final String name;

    private final int bounty;

    public ScoreboardPlayer(final String name, final int bounty) {
        this.name = name;
        this.bounty = bounty;
    }

    public int getBounty() {
        return bounty;
    }

    public String getName() {
        return name;
    }

}
