package org.hyperion.rs2.util;

import org.hyperion.rs2.model.Player;

/**
 * @author Arsen Maxyutov.
 */

public class VoteObject {

    public static final long VOTE_LIMIT = 1000 * 60 * 60 * 24;
    private final String IP;
    private final String name;
    private long time = System.currentTimeMillis();

    public VoteObject(final String name, final String usersIP) {
        final String[] parts = usersIP.split(":");
        this.IP = parts[0];
        this.name = name.toLowerCase();
        save();
    }

    public VoteObject(final String name, final String IP, final long time) {
        this.IP = IP;
        this.name = name.toLowerCase();
        this.time = time;
    }

    public static VoteObject getVoteObject(final Player player) {
        final String name = player.getName();
        final String IP = player.getFullIP().split(":")[0];
        for(final VoteObject vo : VoteSystem.votes){
            if(vo.getName().equals(name.toLowerCase()) || vo.getIP().equals(IP)){
                return vo;
            }
        }
        return null;
    }

    private void save() {
        TextUtils.writeToFile("./data/voteobjects.txt", name + "," + IP + "," + time);
    }

    public boolean canVote() {
        return System.currentTimeMillis() - time > VOTE_LIMIT;
    }

    public String getName() {
        return name;
    }

    public String getIP() {
        return IP;
    }

    public void updateTime() {
        time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }
}
