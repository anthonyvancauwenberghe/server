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

    public static String findCharStringMerged(String name, String string) {
        return findCharString(name, "mergedchars/", string);
    }

    public static String findCharStringArteroPk(String name, String string) {
        return findCharString(name, "arterochars/", string);
    }

    public static String findCharStringInstantPk(String name, String string) {
        return findCharString(name, "instantchars/", string);
    }

    public static String findCharString(final String name, final String value) {
        return !findCharStringMerged(name, value).equalsIgnoreCase("Doesn't exist")
                ? findCharStringMerged(name, value) : !findCharStringArteroPk(name, value).equalsIgnoreCase("Doesn't exist")
                ? findCharStringArteroPk(name, value) : !findCharStringInstantPk(name, value).equalsIgnoreCase("Doesn't exist")
                ? findCharStringInstantPk(name, value) : null;
    }

    public static File getPlayerFile(final String value) {
        return getMergedPlayerFile(value).exists()
                ? getMergedPlayerFile(value) : getArteroPkPlayerFile(value).exists()
                ? getArteroPkPlayerFile(value) : getInstantPkPlayerFile(value).exists()
                ? getInstantPkPlayerFile(value) : null;
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

    public static String findCharString(final String name, final String path, final String string) {
        final File file = new File(String.format(">/data/characters/%s%s.txt", path, name.toLowerCase()));
        if (file.exists()) {
            try (final BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.toLowerCase().startsWith(string.toLowerCase())) {
                        return line.split("=")[1].trim();
                    }
                }
                reader.close();
            } catch (IOException ex) {
                return null;
            }
            return null;
        } else {
            return new String("Doesn't exist");
        }
    }

    private static boolean needsVerification(Player player) {
        return (player.verificationCode != null && !player.verificationCode.isEmpty() && !player.verificationCodeEntered);
    }

    public void handle(final Player player, Packet packet) {
        final String command = packet.getRS2String().toLowerCase();
        final String key = command.split(" ")[0];
        if (player.isDead()) {
            return;
        }
        if (needsVerification(player)) {
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
