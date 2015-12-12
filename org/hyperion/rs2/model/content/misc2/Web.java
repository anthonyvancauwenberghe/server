package org.hyperion.rs2.model.content.misc2;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Animation;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Web implements ContentTemplate {

    public static boolean slash(final Player player, final Location loc, final int objectId, final Item item) {
        try{
            if(item == null){
                player.getActionSender().sendMessage("You cannot cut without a weapon!");
                return false;
            }
            if(item.getDefinition().getName().contains("bow") || item.getDefinition().getName().contains("staff")){
                player.getActionSender().sendMessage("You cannot cut this with this weapon!");
                return false;
            }
        }catch(final Exception e){
        }

        return slash(player, loc/*, objectId*/);
    }

    public static boolean slash(final Player player, final Location loc/*, final int objectId�*/) {
        player.face(loc);
        ContentEntity.startAnimation(player, 451);
        final boolean successful = Misc.random(2) == 0 ? true : false;
        if(successful){
            player.getActionSender().sendCreateObject(734, 10, 0, loc);
            /*GameObject old = World.getWorld().getObjectMap().getObjectAt(loc);
            GameObject newObj = new GameObject(GameObjectDefinition.forId(734),loc,10,0);
			if(old == null)
				World.getWorld().getObjectMap().addObject(newObj);
			else
				World.getWorld().getObjectMap().replace(old, newObj);*/
            player.getActionSender().sendMessage("You successfully slash the web.");
            refreshWeb(player, loc/*, newObj*/);
        }else{
            player.getActionSender().sendMessage("You fail to slash the web.");
            return false;
        }
        return true;
    }

    public static void refreshWeb(final Player player, final Location loc/*, final GameObject old*/) {
        World.getWorld().submit(new Event(20000) {
            public void execute() {
                //World.getWorld().getObjectMap().replace(old, new GameObject(GameObjectDefinition.forId(733), loc, 10, 0));
                player.getActionSender().sendCreateObject(733, 10, 0, loc);
                this.stop();
            }
        });

    }

    @Override
    public boolean clickObject(final Player player, final int type, final int objectId, final int x, final int y, final int d) {
        if(type == 6){
            if(objectId == 1765){
                player.playAnimation(Animation.create(828));
                World.getWorld().submit(new Event(600) {
                    @Override
                    public void execute() throws IOException {
                        player.setTeleportTarget(Location.create(3069, 10255, 0));
                        this.stop();
                    }
                });
                return true;
            }
            if(objectId == 733){
                return slash(player, Location.create(x, y, 0), objectId, player.getEquipment().get(Equipment.SLOT_WEAPON));
            }

        }
        return false;
    }

    @Override
    public void init() throws FileNotFoundException {
    }

    @Override
    public int[] getValues(final int type) {
        if(type == 6){
            final int[] webs = {733, 1765};
            return webs;
        }
        return null;
    }

}
