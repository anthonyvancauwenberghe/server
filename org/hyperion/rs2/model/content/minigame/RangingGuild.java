package org.hyperion.rs2.model.content.minigame;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.DialogueManager;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.util.Misc;

import java.io.FileNotFoundException;

/**
 * @author SaosinHax/Linus/Vegas/Flux/Tinderbox/Jack Daniels/Arsen/Jolt <- All
 *         same person
 */

public class RangingGuild implements ContentTemplate {

    public static final int[] GAME_BOWS = {839, 845, 847, 851, 855, 859, 841, 843, 849, 853, 857, 861, 19143, 19146,
            19149};
    public static final int[] RANGE_MINI_FRAMES = {538, 557, 559, 560, 561, 562, 563, 564, 565, 566};

    public static void shootTarget(final Player p, final int xcoord, final int ycoord) {
        if(System.currentTimeMillis() - p.contentTimer < 2500){
            return;
        }
        if(p.rangeMiniShots <= 0){
            p.getActionSender().sendMessage("Start this minigame by talking to the Armour salesman.");
            return;
        }
        boolean usingBow = false;
        for(final int bowId : GAME_BOWS){
            if(p.getEquipment().get(Equipment.SLOT_WEAPON) == null)
                continue;
            if(p.getEquipment().get(Equipment.SLOT_WEAPON).getId() == bowId){
                usingBow = true;
                break;
            }
        }
        if(!usingBow){
            p.getActionSender().sendMessage("You need a bow equipped to shoot at the target.");
            return;
        }
        if(p.getEquipment().get(Equipment.SLOT_ARROWS) != null && p.getEquipment().get(Equipment.SLOT_WEAPON).getId() >= checkArrows(p.getEquipment().get(Equipment.SLOT_ARROWS).getId())){

            p.contentTimer = System.currentTimeMillis();
            ContentEntity.removeAllWindows(p);
            p.getActionSender().sendMessage("You shoot at the target.");
            ContentEntity.startAnimation(p, 426);
            p.cE.doGfx(getDrawBack(p.getEquipment().get(Equipment.SLOT_ARROWS).getId()));
            p.face(Location.create(xcoord, ycoord, 0));

            World.getWorld().submit(new Event(1000) {
                public void execute() {
                    final int offX = (p.getLocation().getX() - xcoord) * -1;
                    final int offY = (p.getLocation().getY() - ycoord) * -1;
                    p.getActionSender().createGlobalProjectile(p.getLocation().getX(), p.getLocation().getY(), offX, offY, 50, 40, getProjectile(p.getEquipment().get(Equipment.SLOT_ARROWS).getId()), 43, 31, 1, 5);
                    this.stop();
                }
            });
            World.getWorld().submit(new Event(2000) {
                public void execute() {
                    int hit = Misc.random(10) - Misc.random((int) p.getSkills().getLevel(4) / 12 + p.getBonus().get(4) / 15);
                    if(hit < 0){
                        hit = 0;
                    }
                    p.getActionSender().sendClientConfig(158, hit);
                    p.getActionSender().sendMessage("... and score " + getScore(hit) + " points.");
                    p.rangeMiniScore += getScore(hit);
                    p.getActionSender().sendString(hit == 0 ? "Bullseye" : (hit >= 9 ? "Missed!" : ""), 567);
                    p.getActionSender().sendString(Integer.toString(p.rangeMiniScore), 551);
                    p.rangeMiniShots--;
                    p.getActionSender().sendClientConfig(156, (10 - p.rangeMiniShots) + 1);
                    p.getActionSender().sendInterfaceModel(RANGE_MINI_FRAMES[p.rangeMiniShots], 100, -1);
                    p.getActionSender().showInterface(446);
                    this.stop();
                }
            });
        }else{
            if(p.getEquipment().get(Equipment.SLOT_ARROWS) == null)
                p.getActionSender().sendMessage("You have no arrows left in your quiver.");
            else
                p.getActionSender().sendMessage("You can not use this kind of ammo with this bow");
        }
    }

    public static boolean checkCompetition(final Player p) {
        if(p.rangeMiniShots > 0){
            p.getActionSender().sendMessage("You haven't finished your current game yet!");
            return true;
        }
        if(p.rangeMiniShots == 0){
            p.getActionSender().sendMessage("You scored " + p.rangeMiniScore + " points and recieved " + ((int) (p.rangeMiniScore / 10)) + " archery tickets.");
            ContentEntity.addItem(p, 1464, ((int) (p.rangeMiniScore / 10)));
            p.getSkills().addExperience(4, p.rangeMiniScore * 25);
            p.rangeMiniShots = -1;
            p.rangeMiniScore = 0;
            return true;
        }
        DialogueManager.openDialogue(p, 124);
        return false;
    }

    public static void buyShots(final Player p) {
        if(ContentEntity.getItemAmount(p, 995) >= 5000){
            p.getActionSender().sendMessage("You pay 5000 coins and enter the competition.");
            ContentEntity.deleteItemA(p, 995, 5000);
            p.rangeMiniShots = 10;
            ContentEntity.addItem(p, 882, 10);
        }else{
            p.getActionSender().sendMessage("You do not have enough coins to enter the competition.");
        }
    }

    public static int getScore(final int id) {
        switch(id){
            case 0:
                return 100;
            case 1:
                return 50;
            case 2:
            case 3:
            case 4:
                return 30;
            case 5:
            case 6:
            case 7:
            case 8:
                return 20;
            case 9:
            case 10:
                return 10;
        }
        return 0;
    }

    public static int getDrawBack(final int arrowId) {
        switch(arrowId){
            case 882:
                return 19;
            case 884:
                return 18;
            case 886:
                return 20;
            case 888:
                return 21;
            case 890:
                return 22;
            case 892:
                return 24;
        }
        return 0;
    }

    public static int getProjectile(final int arrowId) {
        switch(arrowId){
            case 882:
                return 10;
            case 884:
                return 9;
            case 886:
                return 11;
            case 888:
                return 12;
            case 890:
                return 13;
            case 892:
                return 15;
        }
        return 0;
    }

    public static int checkArrows(final int arrowId) {
        switch(arrowId){
            case 882:
            case 884:
                return 839;
            case 886:
                return 843;
            case 888:
                return 847;
            case 890:
                return 853;
            case 892:
                return 855;
        }
        return -1;
    }

    @Override
    public boolean clickObject(final Player player, final int type, final int a, final int xcoord, final int ycoord, final int d) {
        if(type == 6){
            if(a != 2514){
                player.cE.face(xcoord, ycoord);
                shootTarget(player, xcoord, ycoord);
            }else if(a == 2514){
                Magic.teleport(player, 2667, 3423, 0, true);
            }
            return true;
        }
        if(type == 10){
            if(a == 682){
                if(checkCompetition(player))
                    return true;
            }else if(a == 694){
                ShopManager.open(player, 74);
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
            final int[] objectids = {2513, 2514};
            return objectids;
        }
        if(type == 10){
            final int[] npcIds = {682, 694};
            return npcIds;
        }
        return null;
    }

}
