package org.hyperion.rs2.model.content.misc;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;

public class TriviaSettings {

    public static final boolean DEFAULT_ENABLED = false;

    private static final int TIMER = 3000;

    private boolean enabled = DEFAULT_ENABLED;

    private long lastTimeAnswered;

    public TriviaSettings(final long lastTimeAnswered, final boolean enabled) {
        this.enabled = enabled;
        this.lastTimeAnswered = 0;
    }

    public static void resetAllTimers() {
        for(final Player p : World.getWorld().getPlayers()){
            if(p != null){
                p.getTrivia().resetTimer();
            }
        }
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    public void change() {
        enabled = !enabled;
    }

    public void updateTimer() {
        lastTimeAnswered = System.currentTimeMillis();
    }

    public boolean canAnswer() {
        if(System.currentTimeMillis() - lastTimeAnswered > TIMER)
            return true;
        return false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean b) {
        enabled = b;
    }

    private void resetTimer() {
        lastTimeAnswered = 0;
    }
}
