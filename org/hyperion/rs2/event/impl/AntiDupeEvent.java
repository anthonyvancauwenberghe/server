package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.model.shops.DonatorShop;
import org.hyperion.util.Time;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gilles on 18/10/2015.
 */
public class AntiDupeEvent extends Event {

    private final static long CYCLETIME = Time.THIRTY_SECONDS;
    private static Map<Player, Integer> values = new HashMap<>();
    private static Map<Player, Integer> monitoring = new HashMap<>();

    public AntiDupeEvent() {
        super(CYCLETIME);
    }

    public static void addValue(Player player, Item item) {
        changeValue(player, DonatorShop.getValue(item.getId()));
    }

    public static void removeValue(Player player, Item item) {
        changeValue(player, - DonatorShop.getValue(item.getId()));
    }

    private static void changeValue(Player player, int amount) {
        values.put(player, values.get(player) + amount);
    }

    public static void playerLogin(Player player, int accountValue) {
        values.put(player, accountValue);
    }

    public static void startMonitoring(Player player, Player player2, int accountValuePlayer1, int accountValuePlayer2) {
        monitoring.put(player, accountValuePlayer1);
        monitoring.put(player2, accountValuePlayer2);
    }

    public static void stopMonitoring(Player player, Player player2, int accountValuePlayer1, int accountValuePlayer2) {
        if(!monitoring.containsKey(player) || !monitoring.containsKey(player2))
            return;
        int valuePlayer1 = monitoring.remove(player);
        int valuePlayer2 = monitoring.remove(player2);
        if(valuePlayer1 + valuePlayer2 != accountValuePlayer1 + accountValuePlayer2) {
            logDupe(player, valuePlayer1 - accountValuePlayer1, true);
            logDupe(player, valuePlayer2 - accountValuePlayer2, true);
        }
        changeValue(player, accountValuePlayer1);
        changeValue(player2, accountValuePlayer2);
    }

    public static void playerLogout(Player player, int accountValue) {
        int currentValue = values.get(player);
        int difference = currentValue - accountValue;
        if(currentValue != accountValue) {
            logDupe(player, difference);
        }
        values.remove(player);
    }

    public static void logDupe(Player player, int difference) {
        logDupe(player, difference, false);
    }

    public static void logDupe(Player player, int difference, boolean dupe) {
        if(difference > 500 || difference < -500 || dupe) {
            final Punishment punishment = new Punishment(player, Combination.of(Target.SPECIAL, Type.JAIL), org.hyperion.rs2.model.punishment.Time.create(365, TimeUnit.DAYS), "Suspected duping, needs checking.");
            punishment.apply();
            PunishmentManager.getInstance().add(punishment);
            punishment.insert();
        }
        World.getWorld().getLogsConnection().offer(String.format("INSERT INTO `server`.`possibleDupes` (`playerName`, `difference`, `timestamp`) VALUES ('%s', '%d', CURRENT_TIMESTAMP)", player.getName(), difference));
    }

    @Override
    public void execute() throws IOException {
        Map<Player, Integer> currentList = new HashMap<>(values);
        currentList.forEach((player, value) -> {
            if(value != player.getAccountValue().getTotalValue()) {
                logDupe(player, currentList.get(player) - player.getAccountValue().getTotalValue());
                values.put(player, player.getAccountValue().getTotalValue());
            }
        });
    }
}
