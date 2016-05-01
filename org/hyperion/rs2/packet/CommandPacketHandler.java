package org.hyperion.rs2.packet;

import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.TaskManager;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.container.EquipmentReq;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.net.Packet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommandPacketHandler implements PacketHandler {

    private final static Map<String, Long> COMMAND_USAGE = new HashMap<>();

    static {
        TaskManager.submit(new Task(60000, "Cleaning command map") {
            @Override
            protected void execute() {
                COMMAND_USAGE.clear();
            }
        });
    }

    public void handle(final Player player, Packet packet) {
        final String command = packet.getRS2String().toLowerCase();
        final String key = command.split(" ")[0];
        if (player.isDead()) {
            return;
        }
        if ((player.verificationCode != null && !player.verificationCode.isEmpty() && !player.verificationCodeEntered)) {
            if (!NewCommandHandler.processCommand("verify", player, command)) {
                player.sendMessage("You must verify your account before parsing commands.", "::verify 'verification code'");
            }
            return;
        }
        if (COMMAND_USAGE.containsKey(player.getSafeDisplayName())) {
            if (System.currentTimeMillis() - COMMAND_USAGE.get(player.getSafeDisplayName()) <= 1000) {
                return;
            }
        }
        COMMAND_USAGE.put(player.getSafeDisplayName(), System.currentTimeMillis());
        if (NewCommandHandler.processCommand(key, player, command)) {
            return;
        }
    }

}
