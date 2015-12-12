package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.content.ContentTemplate;

import java.io.FileNotFoundException;

public class FightCaves implements ContentTemplate {

    public static final int failCoords[] = {2439, 5171};
    public static final int monsters[] = {2627, 2630, 2738, 2631, 2741, 2743, 2744, 2745};
    public static final int maxWaves = 10;
    public int waves[][] = {
            /*{2627},
             {2627},
    		 {2627, 2627},
    		 */{2630},
            // {2630, 2627},
            {2630, 2627, 2627},
            // {2630, 2630},
            // {2631},
            // {2631, 2627},
            // {2631, 2627, 2627},
            // {2631, 2630},
            // {2631, 2630, 2627},
            // {2631, 2630, 2627, 2627},
            {2631, 2630, 2630},
            // {2631, 2631},
            // {2741},
            // {2741, 2627},
            {2741, 2627, 2627},
            // {2741, 2630},
            // {2741, 2630, 2627},
            // {2741, 2630, 2627, 2627},
            {2741, 2630, 2630},
            // {2741, 2631},
            // {2741, 2631, 2627},
            // {2741, 2631, 2627, 2627},
            //{2741, 2631, 2630},
            // {2741, 2631, 2630, 2627},
            {2741, 2631, 2630, 2627, 2627},
            // {2741, 2631, 2630, 2630},
            //{2741, 2631, 2631},
            // {2741, 2741},
            //{2743},
            // {2743, 2627},
            //{2743, 2627, 2627},
            // {2743, 2630},
            {2743, 2630, 2627},
            // {2743, 2630, 2627, 2627},
            // {2743, 2630, 2630},
            // {2743, 2631},
            // {2743, 2631, 2627},
            // {2743, 2631, 2627, 2627},
            //{2743, 2631, 2630},
            // {2743, 2631, 2630, 2627},
            //{2743, 2631, 2630, 2627, 2627},
            // {2743, 2631, 2630, 2630},
            //{2743, 2631, 2631},
            // {2743, 2741},
            {2743, 2741, 2627},
            // {2743, 2741, 2627, 2627},
            //{2743, 2741, 2630},
            // {2743, 2741, 2630, 2627},
            {2743, 2741, 2630, 2627, 2627},
            // {2743, 2741, 2630, 2630},
            //{2743, 2741, 2631},
            // {2743, 2741, 2631, 2627},
            //{2743, 2741, 2631, 2627, 2627},
            // {2743, 2741, 2631, 2630},
            //{2743, 2741, 2631, 2630, 2627},
            // {2743, 2741, 2631, 2630, 2627, 2627},
            {2743, 2741, 2631, 2630, 2630},
            // {2743, 2741, 2631, 2631},
            //{2743, 2741, 2741},
            {2743, 2744}, {2745}};

    public FightCaves() {
    }

    public void init() throws FileNotFoundException {
    }

    public int[] getValues(final int i) {
        if(i == 6){
            final int[] ai = {9356, 9357, 9358};
            return ai;
        }
        if(i == 16){
            return monsters;
        }else{
            return null;
        }
    }

    public boolean clickObject(final Player player, final int i, final int j, final int k, final int l, final int i1) {
        if(i == 6){
            if(j == 9356){
                startCaves(player, 1);
            }else if(j == 9357){
                quitCaves(player);
            }else if(j == 9358 && l == 1 && i1 == 1){
                startCaves(player, k);
            }
        }
        if(i == 16){
            if(player.getDungeoneering().inDungeon())
                return false;
            if(player.fightCavesKills == -1){
                return false;
            }
            player.fightCavesKills--;
            if(j == 2630){
                //System.out.println("spawning extra");
                spawnNpc(2738, Location.create(k, l, player.getIndex() * 4), player);
                spawnNpc(2738, Location.create(k + 2, l, player.getIndex() * 4), player);
            }
            if(j != 2745 && player.fightCavesKills == 0){
                player.fightCavesWave++;
                if(player.fightCavesKills % 10 == 0){
                    player.getPoints().increasePkPoints(5);
                }
                spawnWave(player, player.fightCavesWave);
            }
        }
        return true;
    }

    public void startCaves(final Player player, final int i) {
        player.setTeleportTarget(Location.create(2400, 5093, player.getIndex() * 4));
        player.getActionSender().sendMessage("Prepare Yourself, the waves will start in 15 seconds.");
        player.fightCavesWave = i;
        player.fightCavesKills = 0;
        spawnWave(player, player.fightCavesWave);
        player.getActionSender().showInterfaceWalkable(4535);
        player.getActionSender().sendString(4536, (new StringBuilder()).append("Wave: ").append(player.fightCavesWave).toString());
    }

    public void quitCaves(final Player player) {
        player.getActionSender().showInterfaceWalkable(-1);
        player.setTeleportTarget(Location.create(2439, 5171, 0));
    }

    public void spawnWave(final Player player, int i) {
        int j = 0;
        if(i >= waves.length)
            i = waves.length - 1;
        for(int k = 0; k < waves[i].length; k++){
            spawnNpc(waves[i][k], getSpawnLoc(), player);
            if(waves[i][k] == 2630){
                j += 3;
            }else{
                j++;
            }
        }

        player.fightCavesKills = j;
        player.getActionSender().sendString(4536, (new StringBuilder()).append("Wave: ").append(i).toString());
    }

    public NPC spawnNpc(final int i, final Location location, final Player player) {
        final NPC npc = World.getWorld().getNPCManager().addNPC(location.getX(), location.getY(), player.getIndex() * 4, i, -1);
        npc.agressiveDis = 150;
        npc.ownerId = player.getIndex();
        return npc;
    }

    public Location getSpawnLoc() {
        final int[] ai = {2404, 2375, 2373, 2388, 2413};
        final int[] ai1 = {5101, 5098, 5064, 5068, 5076};
        final int[] ai2 = {16, 13, 14, 17, 8};
        final int[] ai3 = {12, 15, 15, 33, 10};
        final int i = Combat.random(ai.length - 1);
        return Location.create(ai[i] + Combat.random(ai2[i]), ai1[i] + Combat.random(ai3[i]), 0);
    }

}
