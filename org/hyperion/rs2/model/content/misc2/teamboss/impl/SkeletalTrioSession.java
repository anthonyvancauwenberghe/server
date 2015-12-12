package org.hyperion.rs2.model.content.misc2.teamboss.impl;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.NPCDrop;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.misc2.teamboss.TeamBossSession;
import org.hyperion.rs2.model.content.specialareas.SpecialArea;
import org.hyperion.util.Misc;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/20/14
 * Time: 1:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class SkeletalTrioSession extends TeamBossSession {

    public static final SpecialArea AREA = new SkeletalTrioArea();
    private static final int LOC_X = 0, LOC_Y = 0;

    public SkeletalTrioSession(final Player[] players) {
        super(LOC_X, LOC_Y, new NPCDrop[]{
                //rewards
        }, new NPC[]{
                World.getWorld().getNPCManager().addNPC(Location.create(LOC_X, LOC_Y, players[0].getIndex() * 4), 11255, -1),
                World.getWorld().getNPCManager().addNPC(Location.create(LOC_X, LOC_Y, players[0].getIndex() * 4), 11254, -1),
                World.getWorld().getNPCManager().addNPC(Location.create(LOC_X, LOC_Y, players[0].getIndex() * 4), 11253, -1)}, players);
    }


    @Override
    public void handleReward() {
        ;
        final int distribution = players.size();
        for(final Player p : players){
            for(final NPCDrop drop : rewards){
                if(Misc.random(100 * distribution) <= drop.getChance()){
                    //drop item here
                }
            }
        }
    }

    @Override
    public SpecialArea getArea() {
        return AREA;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private static class SkeletalTrioArea extends SpecialArea {

        @Override
        public boolean inArea(final int x, final int y, final int z) {
            return false;
        }

        @Override
        public String canEnter(final Player player) {
            return "";
        }

        @Override
        public boolean isPkArea() {
            return false;
        }

        @Override
        public Location getDefaultLocation() {
            return Edgeville.LOCATION;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean canSpawn() {
            return false;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

}
