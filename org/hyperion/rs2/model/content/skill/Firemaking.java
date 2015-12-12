package org.hyperion.rs2.model.content.skill;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.GlobalItem;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.net.ActionSender;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Firemaking implements ContentTemplate {

    private static final int EXPMULTIPLIER = 5 * Constants.XPRATE;
    private final Map<Location, Fire> fires = new HashMap<Location, Fire>();
    private final Map<Integer, Log> logs = new HashMap<Integer, Log>();

    public void lightLogs(final Player player, final int logId) {
        if(player.isBusy()){
            return;
        }
        if(player.getRandomEvent().skillAction(8)){
            return;
        }
        if(!ContentEntity.isItemInBag(player, 590)){
            ContentEntity.sendMessage(player, "You need a tinderbox to light a fire.");
            return;
        }
        if(fires.get(player.getLocation()) != null)
            return;
        final Log log = logs.get(logId);
        if(log == null)
            return;
        if(ContentEntity.getLevelForXP(player, 11) < log.level){
            ContentEntity.sendMessage(player, "You need " + log.level + " firemaking to light this log.");
            return;
        }
        if(Jail.inJail(player)){
            ContentEntity.sendMessage(player, "You cannot do this while in jail.");
            return;
        }
        if(Duel.inDuelLocation(player) || player.duelAttackable > 0){
            ContentEntity.sendMessage(player, "You cannot do this in duel arena.");
            return;
        }
        ContentEntity.deleteItem(player, logId);
        final int timerLower = ContentEntity.getLevelForXP(player, 11) * 100;
        ContentEntity.startAnimation(player, 733);
        int timer = log.timer + ContentEntity.random(log.timer) - timerLower;
        if(timer < 2500)
            timer = 2500;
        player.setBusy(true);
        World.getWorld().submit(new Event(timer) {
            @Override
            public void execute() {
                if(!player.isBusy()){
                    this.stop();
                    return;
                }
                ContentEntity.startAnimation(player, -1);
                player.getAchievementTracker().itemSkilled(Skills.FIREMAKING, logId, 1);
                fires.put(player.getLocation(), new Fire(player.getLocation(), ContentEntity.random((log.timer))));
                int obj = 2732;
                if(logId == 1513){
                    final int r = ContentEntity.random(2);
                    if(r == 0)
                        obj = 11404;
                    else if(r == 2)
                        obj = 11405;
                    else if(r == 1)
                        obj = 11406;
                }
                ContentEntity.addSkillXP(player, log.xp * EXPMULTIPLIER, 11);
                createObject(player.getLocation(), obj);
                ContentEntity.sendMessage(player, "You light a fire.");
                try{
                    player.vacateSquare();
                }catch(final Exception e){
                    ActionSender.yellModMessage("Error with firemaking, player: " + player.getName());
                    this.stop();
                    return;
                }
                if(!player.getLocation().inDuel())
                    player.setBusy(false);
                this.stop();
            }
        });
    }

    private void process() {
        final ArrayList<Fire> outFires = new ArrayList<Fire>();
        for(final Map.Entry<Location, Fire> entry : fires.entrySet()){
            final Fire f = entry.getValue();
            if(f.timer == 0){
                removeObject(f.location);
                sendAshes(f.location);
                outFires.add(f);
            }else
                f.timer--;
        }
        for(final Fire f : outFires){
            fires.remove(f.location);
        }
        outFires.clear();
    }

    private void createObject(final Location loc, final int fire) {
        for(final Player player : World.getWorld().getPlayers()){
            player.getActionSender().sendCreateObject(fire, 10, 0, loc);
        }
    }

    private void removeObject(final Location loc) {
        for(final Player player : World.getWorld().getPlayers()){
            if(player != null)
                player.getActionSender().sendDestroyObject(10, 0, loc);
        }
    }

    private void sendAshes(final Location loc) {
        final GlobalItem globalItem = new GlobalItem(null, loc, new Item(592, 1));
        World.getWorld().getGlobalItemManager().globalItems.add(globalItem);
        World.getWorld().getGlobalItemManager().createItem(globalItem);
        globalItem.itemHidden = false;
    }

    @Override
    public void init() throws FileNotFoundException {
        /*
         * no template exists for: 1521 type: 4
			no template exists for: 1519 type: 4
			no template exists for: 1517 type: 4
			no template exists for: 1515 type: 4
			no template exists for: 1513 type: 4
		 */
        logs.put(1511, new Log(1511, 40, 1, 30));
        logs.put(1521, new Log(1521, 60, 15, 45));
        logs.put(1519, new Log(1519, 90, 30, 70));
        logs.put(1517, new Log(1517, 135, 45, 100));
        logs.put(1515, new Log(1515, 202, 60, 150));
        logs.put(1513, new Log(1513, 303, 70, 200));
        World.getWorld().submit(new Event(1000) {
            @Override
            public void execute() {
                process();
            }
        });
    }

    @Override
    public int[] getValues(final int type) {
        if(type == 13){
            final int[] j = {590, 1511, 1521, 1519, 1517, 1515, 1513,};
            return j;
        }
        return null;
    }

    @Override
    public boolean clickObject(final Player client, final int type, final int itemId, final int slot, final int objId, final int a) {
        if(type == 13){
            if(itemId == 590){
                lightLogs(client, objId);
            }else{
                lightLogs(client, itemId);
            }
        }
        return false;
    }

    public static class Log {
        public int logId;
        public int timer;
        public int level;
        public int xp;

        public Log(final int logId, final int xp, final int level, final int timer) {
            this.logId = logId;
            this.timer = timer;
            this.level = level;
            this.xp = xp;
        }
    }

    public static class Fire {
        public Location location;
        public int timer;

        public Fire(final Location loc, final int time) {
            location = loc;
            timer = time;
        }
    }

}
