package org.hyperion.rs2.model.combat;

import org.hyperion.util.Time;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class LastAttacker {

    private static final long MIN_TIME = Time.FIVE_MINUTES;

    //private final Queue<String> lastAttackers = new ArrayBlockingQueue<>(MAX_SIZE);

    private final Map<String, Long> lastAttackers = new HashMap<>();

    private String latest;
	private String clientName;
	private long lastAttack;
    private int lastNpcAttack;

	public LastAttacker(String clientName) {
        latest = "";
		this.clientName = clientName;
		lastAttack = System.currentTimeMillis() - 9000;
	}

	public void updateLastAttacker(String name) {
		updateLastAttacker(name, true);
	}

	public void updateLastAttacker(String name, boolean first) {
		if(name.equals(clientName))
			return;
		lastAttack = System.currentTimeMillis();
        latest = name;
        lastAttackers.put(name.toLowerCase(), lastAttack + MIN_TIME);
    }

    public void updateLastAttacker(int npcIndex) {
        lastAttack = System.currentTimeMillis();
        lastNpcAttack = npcIndex;
    }

	public boolean contains(final String name) {
        final long time = lastAttackers.getOrDefault(name.toLowerCase(), 0L) - System.currentTimeMillis();
        //System.out.println("Time: "+time);
        return time > 0;
    }

	public long timeSinceLastAttack() {
		return (System.currentTimeMillis() - lastAttack);
	}

    public String getName() {
        return latest;
    }

    public int getLastNpcAttack() {
        return lastNpcAttack;
    }

}
