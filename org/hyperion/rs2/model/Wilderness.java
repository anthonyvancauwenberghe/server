package org.hyperion.rs2.model;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.combat.Combat;

import java.util.LinkedList;
import java.util.List;

public class Wilderness {
    /*Misc methods to do with wilderness, mage bank, obelisks etc*/

    public List<Obelisk> obelisks = new LinkedList<Obelisk>();

    public Wilderness() {
    }

    public void useObelisk(final Player player, final int x, final int y) {
        final Obelisk o = useObelisk(x, y);
        if(o == null)
            return;
        int maxX2 = 0;
        int maxY2 = 0;

        final int minX = o.x[0];
        final int minY = o.y[0];
        GameObject[] list = new GameObject[4];
        for(int i = 0; i < 4; i++){
            if(o.x[i] > minX)
                maxX2 = o.x[i];
            if(o.y[i] > minY)
                maxY2 = o.y[i];
            list[i] = replaceGlobalObject(o.x[i], o.y[i], 14825, -1, 10);
        }
        final GameObject[] list2 = list;
        final int maxX = maxX2;
        final int maxY = maxY2;
        final Obelisk o2 = randomObelisk();
        World.getWorld().submit(new Event(3000) {
            public int timer = 2;

            @Override
            public void execute() {
                if(timer == 2){
                    /*for(int i = 1; i < 4; i++){
                        for(int j = 1; j < 4; j++)
							createGfx(player,343,minX+i,minY+j);*/
                    //synchronized(player.getLocalPlayers()) {
                    for(final Player p : player.getLocalPlayers()){
                        createGfx(player, 343, p.getLocation().getX(), p.getLocation().getY());
                    }
                    //}
                    createGfx(player, 343, player.getLocation().getX(), player.getLocation().getY());
                }else if(timer == 1){
                    for(int j = 0; j < 4; j++){
                        World.getWorld().getObjectMap().removeObject(list2[j]);
                    }
                    for(final Player p : player.getLocalPlayers()){
                        tele(o2, p, minX, minY, maxX, maxY);
                    }

                    tele(o2, player, minX, minY, maxX, maxY);
                    //reset the oblisks
                    for(int i = 0; i < 4; i++){
                        World.getWorld().getObjectMap().removeObject(replaceGlobalObject(o.x[i], o.y[i], 14826, -1, 10));
                    }
                    this.stop();
                }
                timer--;
            }
        });
        list = null;
    }

    public void createGfx(final Player player, final int id, final int x, final int y) {
        for(final Player p : player.getLocalPlayers()){
            p.getActionSender().sendStillGraphics(id, 0, y, x, 50);
        }
        player.getActionSender().sendStillGraphics(id, 0, y, x, 50);
    }

    public void tele(final Obelisk o2, final Player p, final int minX, final int minY, final int maxX, final int maxY) {
        //System.out.println("x: "+minX+" y: "+minY +" x2: "+maxX+" y2: "+maxY);
        if(p.getLocation().getX() > minX && p.getLocation().getX() < maxX){
            if(p.getLocation().getY() > minY && p.getLocation().getY() < maxY){
                if(p.isTeleBlocked()){
                    p.getActionSender().sendMessage("The teleblock spell prevented you from teleporting..");
                    return;
                }
                //teleport and gfx
                final int addX = p.getLocation().getX() - minX;
                final int addY = p.getLocation().getY() - minY;
                //p.startAnimation(1979);
                if(p == null || o2 == null)
                    return;
                p.setTeleportTarget(Location.create(o2.x[0] + addX, o2.y[0] + addY, 0));
            }
        }
    }

    public GameObject replaceGlobalObject(final int x, final int y, final int id, final int face, final int type) {
        final GameObject gO = new GameObject(GameObjectDefinition.forId(id), Location.create(x, y, 0), type, face);
        World.getWorld().getObjectMap().addObject(gO);
        return gO;
    }

    public Obelisk useObelisk(final int x, final int y) {
        for(final Obelisk o : obelisks){
            for(int i = 0; i < 4; i++){
                if(o.x[i] == x && o.y[i] == y){
                    return o;
                }
            }
        }
        return null;
    }

    public Obelisk randomObelisk() {
        final int r = Combat.random(obelisks.size() - 1);
        int i = 0;
        for(final Obelisk o : obelisks){
            if(r == i)
                return o;
            i++;
        }
        return null;
    }

    public void init() {
        final int[] x = {3305, 3305, 3309, 3309,};
        final int[] y = {3914, 3918, 3918, 3914,};
        obelisks.add(new Obelisk(x, y));
        final int[] x2 = {3104, 3104, 3108, 3108,};
        final int[] y2 = {3792, 3796, 3792, 3796,};
        obelisks.add(new Obelisk(x2, y2));
        final int[] x3 = {3154, 3154, 3158, 3158,};
        final int[] y3 = {3618, 3622, 3618, 3622,};
        obelisks.add(new Obelisk(x3, y3));
        final int[] x4 = {3225, 3225, 3229, 3229,};
        final int[] y4 = {3665, 3669, 3665, 3669,};
        obelisks.add(new Obelisk(x4, y4));
        final int[] x5 = {2978, 2978, 2982, 2982,};
        final int[] y5 = {3864, 3868, 3864, 3868,};
        obelisks.add(new Obelisk(x5, y5));
        final int[] x6 = {3033, 3033, 3037, 3037,};
        final int[] y6 = {3730, 3734, 3730, 3734,};
        obelisks.add(new Obelisk(x6, y6));
    }

    public static class Obelisk {
        public int[] x = new int[4];
        public int[] y = new int[4];

        public Obelisk(final int[] x, final int[] y) {
            this.x = x;
            this.y = y;
        }
    }
}