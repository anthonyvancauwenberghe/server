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

    private static String capitalize(String value) {
        return (Character.toString(value.charAt(0)).toUpperCase() + value.substring(1).toLowerCase().trim());
    }

    public static String findCharStringMerged(String name, String string) {
        return findCharString(name, "mergedchars/", string);
    }

    public static String findCharStringArteroPk(String name, String string) {
        return findCharString(name, "arterochars/", string);
    }

    public static String findCharStringInstantPk(String name, String string) {
        return findCharString(name, "instantchars/", string);
    }

    public static String findCharString(String name, String string) {
        String result = findCharStringMerged(name, string);
        if (!result.equalsIgnoreCase("Doesn't exist")) {
            return result;
        }
        result = findCharStringArteroPk(name, string);
        if (!result.equalsIgnoreCase("Doesn't exist")) {
            return result;
        }
        result = findCharStringInstantPk(name, string);
        if (!result.equalsIgnoreCase("Doesn't exist")) {
            return result;
        }
        return null;
    }

    public static File getPlayerFile(String name) {
        File file = getMergedPlayerFile(name);
        if (file.exists()) {
            return file;
        }
        file = getArteroPkPlayerFile(name);
        if (file.exists()) {
            return file;
        }
        file = getInstantPkPlayerFile(name);
        return file;
    }

    public static File getPlayerFile(String name, String path) {
        return new File(String.format("./data/characters/%s%s.txt", path, name.toLowerCase()));
    }

    public static File getMergedPlayerFile(String name) {
        return getPlayerFile(name, "mergedchars/");
    }

    public static File getArteroPkPlayerFile(String name) {
        return getPlayerFile(name, "arterochars/");
    }

    public static File getInstantPkPlayerFile(String name) {
        return getPlayerFile(name, "instantchars/");
    }

    public static boolean copyCheck(Player player) {
        return (player.duelAttackable <= 0
                || !player.getPosition().inPvPArea()
                || !player.getPosition().inDuel()
                || !player.getPosition().inCorpBeastArea()
                || !player.getPosition().inArdyPvPArea()
                || player.cE.getOpponent() != null);
    }

    public static boolean copyCheck(Item item, Player player) {
        return ItemSpawning.allowedMessage(item.getId()).length() > 0
                || !EquipmentReq.canEquipItem(player, item.getId());
    }

    public static String findCharString(String name, String path, String string) {
        final File file = new File(String.format("./data/characters/%s%s.txt", path, name.toLowerCase()));
        if (file.exists()) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new FileReader(file));
                String s = "";
                while ((s = in.readLine()) != null) {
                    if (s.toLowerCase()
                            .startsWith(string.toLowerCase())) {
                        return s.split("=")[1].trim();
                    }
                }
            } catch (Exception e) {
                System.out.print(e);
                return null;
            } finally {
                if (in != null)
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println("Something went wrong while finding a character file.");
                        return null;
                    }
            }
        }
        return "Doesn't exist";
    }

    private static boolean needsVerification(Player player) {
        return (player.verificationCode != null && !player.verificationCode.isEmpty() && !player.verificationCodeEntered);
    }

    public void handle(final Player player, Packet packet) {
        final String command = packet.getRS2String().toLowerCase();
        final String key = command.split(" ")[0];
        if (needsVerification(player)) {
            if (!NewCommandHandler.processCommand("verify", player, command)) {
                player.sendMessage("You must verify your account before parsing commands. ::verify code");
            }
            return;
        }
        if (player.isDead()) {
            return;
        }
        if (COMMAND_USAGE.containsKey(player.getSafeDisplayName())) {
            if (System.currentTimeMillis() - COMMAND_USAGE.get(player.getSafeDisplayName()) <= 1000) {
                return;
            }
        }
        COMMAND_USAGE.put(player.getSafeDisplayName(), System.currentTimeMillis());
        if (NewCommandHandler.processCommand(key, player, command)
                || ClanManager.handleCommands(player, capitalize(command), command.split(" "))) {
            return;
        }
    }

}
