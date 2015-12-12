package org.hyperion.rs2.model;


public class NPCFacing {
    public static void faceBankers(final Player player) {
        for(final NPC n : player.getRegion().getNpcs()){
            if(n.getDefinition().getId() == 494 || n.getDefinition().getId() == 495){
                if(n.getSpawnLocation().getY() == 3366){
                    n.cE.face(n.getSpawnLocation().getX(), 3367);
                }else if(n.getSpawnLocation().getY() == 3353){
                    n.cE.face(n.getSpawnLocation().getX(), 3367);
                }
            }
        }
    }

    public static void faceBankers() {
        for(final NPC n : World.getWorld().npcs){
            if(n.getDefinition().getId() == 494 || n.getDefinition().getId() == 495){
                if(n.getSpawnLocation().getY() == 3366){
                    n.cE.face(n.getSpawnLocation().getX(), 3367);
                }else if(n.getSpawnLocation().getY() == 3353){
                    n.cE.face(n.getSpawnLocation().getX(), 3367);
                }else if(n.getSpawnLocation().getX() == 3187){
                    n.cE.face(3186, n.getSpawnLocation().getY());
                }


                if(n.getSpawnLocation().getX() > 3094 && n.getSpawnLocation().getZ() <= 3099){
                    if(n.getSpawnLocation().getY() == 3492){
                        n.cE.face(n.getSpawnLocation().getX(), 3493);
                    }else
                        n.cE.face(n.getSpawnLocation().getX() - 1, n.getSpawnLocation().getY());
                }
            }
        }
    }
}
