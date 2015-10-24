package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.util.Time;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Gilles on 18/10/2015.
 */
public class AntiDupeEvent extends Event {

    private final static long CYCLETIME = Time.THIRTY_SECONDS;
    private static boolean enabled = false;
    //private final static long CYCLETIME = Time.ONE_SECOND * 5;

    public static void compareTotalValue() {
        Map<Player, Integer> currentAssumedValues = World.getWorld().getPlayers().stream().collect(Collectors.toMap(player -> player, player -> player.getExpectedValues().getExpectedValue()));
        Map<Player, Integer> currentValues = World.getWorld().getPlayers().stream().collect(Collectors.toMap(player -> player, player -> player.getAccountValue().getTotalValueWithoutGE()));

        currentAssumedValues.keySet().stream().filter(player -> player.getStartValue() == -1).forEach(player -> player.setStartValue(player.getAccountValue().getTotalValueWithoutPointsAndGE()));

        long assumedValue = currentAssumedValues.values().stream().mapToLong(i -> i.longValue()).sum();
        long currentValue = currentValues.values().stream().mapToLong(i -> i.longValue()).sum();

        if(assumedValue != currentValue) {
            currentValues.keySet().forEach(player -> compareTotalValueForPlayer(player));
        }
    }

    public static void compareTotalValueForPlayer(Player player) {
        if(player.getAccountValue().getTotalValueWithoutGE() != player.getExpectedValues().getExpectedValue()) {
            log(player, player.getAccountValue().getTotalValueWithoutGE() - player.getExpectedValues().getExpectedValue());
            player.getExpectedValues().changeDeltaOther("Correction from wrong value", player.getAccountValue().getTotalValueWithoutGE() - player.getExpectedValues().getExpectedValue());
        }
    }

    public static void log(Player player, int difference) {
        PlayerSaving.getSaving().saveLog("./logs/dupes/dupelogs.log", new Date() + " Suspected dupe for " + player.getSafeDisplayName() + ": " + difference);
    }

    /**
     * The event itself
     */

    public AntiDupeEvent() {
        super(CYCLETIME);
    }

    @Override
    public void execute() throws IOException {
        if(enabled)
            compareTotalValue();
    }

    static {
        CommandHandler.submit(new Command("disableantidupe") {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                AntiDupeEvent.enabled = false;
                player.sendMessage("Antidupe event is now disabled.");
                return true;
            }
        });
        CommandHandler.submit(new Command("enableantidupe") {
            @Override
            public boolean execute(Player player, String input) throws Exception {
                AntiDupeEvent.enabled = true;
                player.sendMessage("Antidupe event is now enabled.");
                return true;
            }
        });
    }
}
