package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Entity;
import org.hyperion.rs2.model.Player;

/**
 * The death event handles player and npc deaths. Drops loot, does animation, teleportation, etc.
 *
 * @author Graham
 */

public class CutSceneEvent extends Event {

    private final Entity entity;
    public int timer = 99;

    /**
     * Creates the death event for the specified entity.
     *
     * @param entity The player or npc whose death has just happened.
     */
    public CutSceneEvent(final Entity entity) {
        super(500);
        this.entity = entity;
    }

    @Override
    public void execute() {
        if(entity instanceof Player){
            final Player player = (Player) entity;
            if(timer == 99){
                player.getActionSender().cameraMovement(3222, 3219, 3237, 3219, 200, 0, 1);
            }else if(timer == 1){
                //player.getActionSender().cameraReset();
                player.getActionSender().rotateCamera(3222, 3219, 3237, 3219, 200, 0, 1);
            }
            timer--;
            /*if(timer == 99){
                player.getActionSender().cameraMovement(3222, 3219,3237,3219,200,0,1);
			} else if(timer == 60){
				player.getActionSender().cameraReset();
				player.getActionSender().cameraMovement(3237,3219,3238,3201,200,0,1);
			} else if(timer == 20){
				player.getActionSender().cameraReset();
				player.getActionSender().cameraMovement(3238,3201,3250,3192,200,0,1);
			}
			timer--;*/
        }
    }

}