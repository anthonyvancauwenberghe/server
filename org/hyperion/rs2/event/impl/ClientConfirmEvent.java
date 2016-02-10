package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Gilles on 10/02/2016.
 */
public class ClientConfirmEvent extends Event {
    private final static Map<String, Integer> EXPECTED_RESULTS = new HashMap<>();
    private final static Map<String, Integer> RECEIVED_NUMBERS = new HashMap<>();

    public static void addResponse(String playerName, int number) {
        RECEIVED_NUMBERS.put(playerName, number);
    }

    public ClientConfirmEvent() {
        super(Time.ONE_MINUTE);
    }

    @Override
    public void execute() throws IOException {
        World.getWorld().getPlayers().stream().filter(player -> player != null).forEach(player -> {
            EXPECTED_RESULTS.clear();
            RECEIVED_NUMBERS.clear();
            int randomNumber = Misc.random(50000);
            EXPECTED_RESULTS.put(player.getName(), (((randomNumber + 40) / 3) * 7) / 8);
            player.getActionSender().sendClientConfirmation(randomNumber);

            World.getWorld().submit(new Event(Time.FIVE_SECONDS, player.getName()) {
                @Override
                public void execute() throws IOException {
                    if(!EXPECTED_RESULTS.containsKey(player.getName()))
                        return;
                    int expected = EXPECTED_RESULTS.get(player.getName());
                    if(!RECEIVED_NUMBERS.containsKey(player.getName()) || RECEIVED_NUMBERS.get(player.getName()) != expected) {
                        Punishment punishment = Punishment.create("Server", player, Combination.of(Target.SPECIAL, Type.BAN), org.hyperion.rs2.model.punishment.Time.create(1, TimeUnit.DAYS), "Invalid client");
                        punishment.apply();
                        punishment.insert();
                        PunishmentManager.getInstance().add(punishment);
                    }
                    stop();
                }
            });
        });
    }
}
