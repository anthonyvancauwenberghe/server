package org.hyperion.rs2.model.content.skill.fletching;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Skills;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.util.Misc;

/**
 * Created by Gilles on 9/09/2015.
 */
public class DartMaking {
    public static Dart getDart(final int id) {
        for(final Dart dart : Dart.values()){
            if(dart.getDartTipId() == id)
                return dart;
        }
        return null;
    }

    public static boolean createDarts(final Player client, final int item) {

        if(client.isBusy()){
            return true;
        }
        final Dart dart = getDart(item);
        if(dart == null)
            return false;

        int amount = ContentEntity.getItemAmount(client, item);

        if(ContentEntity.freeSlots(client) < 1){
            client.sendMessage("You have no space in your inventory");
            return false;
        }

        if(ContentEntity.returnSkillLevel(client, Skills.FLETCHING) < dart.getLevelReq()){
            ContentEntity.sendMessage(client, "You need a fletching level of " + dart.getLevelReq() + " to make these darts.");
            return false;
        }
        if(client.getRandomEvent().skillAction())
            return false;
        final int am2 = ContentEntity.getItemAmount(client, 314);
        if(am2 < amount)
            amount = am2;
        ContentEntity.deleteItemA(client, 314, amount > 15 ? 15 : amount);
        ContentEntity.deleteItemA(client, item, amount > 15 ? 15 : amount);
        client.getAchievementTracker().itemSkilled(Skills.FLETCHING, dart.getDartId(), amount > 15 ? 15 : amount);
        ContentEntity.addItem(client, dart.getDartId(), amount > 15 ? 15 : amount);
        ContentEntity.addSkillXP(client, dart.getExp(), Skills.FLETCHING);
        ContentEntity.sendMessage(client, "You make " + (amount == 1 ? Misc.aOrAn(dart.getName().toLowerCase()) : "some") + " " + dart.getName().toLowerCase() + (amount > 1 ? "s" : "") + ".");
        return true;
    }

    public enum Dart {
        BRONZE_DART(806, 819, 1, 2),
        IRON_DART(807, 820, 22, 4),
        STEEL_DART(808, 821, 37, 8),
        MITHRIL_DART(809, 822, 52, 12),
        ADAMANT_DART(810, 823, 67, 15),
        RUNE_DART(811, 824, 81, 19);

        public int dartId, dartTipId, levelReq, exp;

        Dart(final int dartId, final int dartTipId, final int levelReq, final int exp) {
            this.dartId = dartId;
            this.dartTipId = dartTipId;
            this.levelReq = levelReq;
            this.exp = exp;
        }

        public int getDartId() {
            return dartId;
        }

        public int getDartTipId() {
            return dartTipId;
        }

        public int getLevelReq() {
            return levelReq;
        }

        public int getExp() {
            return exp;
        }

        public String getName() {
            return Misc.ucFirst(this.toString().replaceAll("_", " ").toLowerCase());
        }
    }
}
