package org.hyperion.rs2.model.log;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.NPC;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.log.util.LogUtils;

public class LogEntry implements Comparable<LogEntry>{

    public enum Category {

        DUEL("duel.log", "duel"),
        TRADE("trade.log", "trade"),
        PUBLIC_CHAT("public-chat.log", false, "public chat"),
        PRIVATE_CHAT("private-chat.log", "private chat", "private msg", "pm"),
        ACTIVITY("activity.log", "activity"),
        DEATH("death.log", "death"),
        KILL("kill.log", "kill"),
        PICKUP_ITEM("pickup-items.log", "pickup item"),
        COMMAND("command.log", "command"),
        GAMBLE("gamble.log", "gamble", "gambling", "stake"),
        YELL("yell.log", "yell"),
        BOB("bobdrops.log", "bob drops");

        public final String path;
        public final boolean save;
        public final List<String> names;

        private Category(final String path, final boolean save, final String... names){
            this.path = path;
            this.save = save;
            this.names = Arrays.asList(names);
        }

        private Category(final String path, final String... names){
            this(path, true, names);
        }

        public static Category byName(final String name){
            for(final Category c : values())
                for(final String n : c.names)
                    if(n.contains(name))
                        return c;
            return null;
        }
    }

    public final Date date;
    public final Category category;
    public final String info;

    public LogEntry(final Date date, final Category category, final String info){
        this.date = date;
        this.category = category;
        this.info = info;
    }

    public LogEntry(final Category category, final String info){
        this(new Date(), category, info);
    }

    public LogEntry(final Category category){
        this(category, "");
    }

    public String getDateStamp(){
        return LogUtils.format(date);
    }

    public void send(final Player player){
        player.sendf("@red@-------------------------------------------------------------------------------------");
        player.sendf("@blu@%s@bla@ At @blu@%s", category, getDateStamp());
        if(!info.isEmpty())
            LogUtils.send(player, info);
        player.sendf("@red@-------------------------------------------------------------------------------------");
    }

    public int compareTo(final LogEntry log){
        return (int)(date.getTime() - log.date.getTime());
    }

    public String toString(){
        return String.format("%s | %s", getDateStamp(), info);
    }

    public static LogEntry parse(final Category category, final String line){
        final String[] parts = line.split("\\|");
        try{
            final Date date = LogUtils.DATE_FORMAT.parse(parts[0].trim());
            final String info = parts[1].trim();
            return new LogEntry(date, category, info);
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static LogEntry publicChat(final String msg){
        return new LogEntry(Category.PUBLIC_CHAT, msg);
    }

    public static LogEntry yell(final String msg){
        return new LogEntry(Category.YELL, msg);
    }

    public static LogEntry privateChat(final String from, final String to, final String msg){
        return new LogEntry(Category.PRIVATE_CHAT,
                String.format("@red@%s@blu@ -> @red@%s@blu@: @bla@%s", from, to, msg)
        );
    }

    public static LogEntry duel(final Player player1, final Player player2){
        final Item player1Weapon = player1.getEquipment().get(Equipment.SLOT_WEAPON);
        final Item player2Weapon = player2.getEquipment().get(Equipment.SLOT_WEAPON);
        final Item[] player1Stake = player1.getDuel().toArray();
        final Item[] player2Stake = player2.getDuel().toArray();
        return new LogEntry(Category.DUEL,
                String.format(
                        "@red@%s (%s)@blu@ vs @red@%s (%s)@blu@%s@red@%s@blu@ Stake: @bla@%s@blu@%s@red@%s@blu@ Stake: @bla@%s",
                        player1.getName(),
                        player1Weapon != null ? player1Weapon.getDefinition().getName() : "UNARMED",
                        player2.getName(),
                        player2Weapon != null ? player2Weapon.getDefinition().getName() : "UNARMED",
                        LogUtils.NEW_LINE,
                        player1.getName(),
                        LogUtils.toString(player1Stake),
                        LogUtils.NEW_LINE,
                        player2.getName(),
                        LogUtils.toString(player2Stake)
                )
        );
    }

    public static LogEntry duelResult(final Player winner, final Player loser){
        final Item winnerWeapon = winner.getEquipment().get(Equipment.SLOT_WEAPON);
        final Item loserWeapon = loser.getEquipment().get(Equipment.SLOT_WEAPON);
        return new LogEntry(Category.DUEL,
                String.format(
                        "@red@%s (%s)@blu@ vs @red@%s (%s)@blu@: Winner: @red@%s",
                        winner.getName(),
                        winnerWeapon != null ? winnerWeapon.getDefinition().getName() : "UNARMED",
                        loser.getName(),
                        loserWeapon != null ? loserWeapon.getDefinition().getName() : "UNARMED",
                        winner.getName()
                )
        );
    }

    public static LogEntry trade(final String player1, final String player2, final Item[] player1Trade, final Item[] player2Trade){
        return new LogEntry(Category.TRADE,
                String.format(
                        "@red@%s@blu@ <> @red@%s@blu@%s@red@%s@blu@ Trade: @bla@%s%s@red@%s@blu@ Trade: @bla@%s",
                        player1,
                        player2,
                        LogUtils.NEW_LINE,
                        player1,
                        LogUtils.toString(player1Trade),
                        LogUtils.NEW_LINE,
                        player2,
                        LogUtils.toString(player2Trade)
                )
        );
    }

    public static LogEntry bob(final String player1, final Item[] bobItems){
        return new LogEntry(Category.BOB,
                String.format(
                        "@red@%s@blu@ Bob Drops: @bla@%s",
                        player1,
                        LogUtils.toString(bobItems)
                )
        );
    }

    public static LogEntry login(final Player player){
        return new LogEntry(Category.ACTIVITY, String.format("@blu@Login From @red@%s@bla@ - @blu@Account Value@bla@: @red@%,d", player.getShortIP(), player.getAccountValue().getTotalValue()));
    }

    public static LogEntry logout(final Player player){
        return new LogEntry(Category.ACTIVITY, String.format("@blu@Logout@bla@ - @blu@Account Value@bla@: @red@%,d", player.getAccountValue().getTotalValue()));
    }

    public static LogEntry death(final Player player, final Player killer, final Item[] items){
        return new LogEntry(Category.DEATH,
                String.format(
                        "@blu@Killed By: @red@%s (ip match: %s, mac match: %s)%s@blu@Lost:@bla@ %s",
                        (killer != null ? killer.getName() : "---"),
                        Objects.equals(player.getShortIP(), killer != null ? killer.getShortIP() : ""),
                        player.getUID() == (killer != null ? killer.getUID() : -1),
                        LogUtils.NEW_LINE,
                        LogUtils.toString(items)
                )
        );
    }

    public static LogEntry kill(final Player killer, final Player player, final Item[] items){
        return new LogEntry(Category.KILL,
                String.format(
                        "@blu@Killed: @red@%s (ip match: %s, mac match: %s)%s@blu@Lost:@bla@ %s",
                        (player != null ? player.getName() : "---"),
                        Objects.equals(killer.getShortIP(), player != null ? player.getShortIP() : ""),
                        killer.getUID() == (player != null ? player.getUID() : -1),
                        LogUtils.NEW_LINE,
                        LogUtils.toString(items)
                )
        );
    }

    public static LogEntry pickupItem(final Item item){
        return new LogEntry(Category.PICKUP_ITEM, LogUtils.toString(item));
    }

    public static LogEntry command(final String cmd){
        if(cmd.startsWith("yell "))
            return yell(cmd.substring(4).trim());
        else
            return new LogEntry(Category.COMMAND, cmd);
    }

    public static LogEntry gamble(final NPC npc, final Item item, final int n){
        return new LogEntry(Category.GAMBLE,
                String.format("%s - %s (%d) Rolled %d (%s)",
                        LogUtils.toString(item),
                        npc.getDefinition().getName(),
                        npc.getDefinition().getId(),
                        n, n >= 55 ? "Won" : "Lost")
        );
    }
}
