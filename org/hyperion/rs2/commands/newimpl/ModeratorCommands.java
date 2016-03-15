package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">
import org.hyperion.Server;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.SpawnCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.BoB;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.model.content.misc.TriviaBot;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.AccountLogger;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
//</editor-fold>
/**
 * Created by Drhales on 2/29/2016.
 */
public class ModeratorCommands implements NewCommandExtension {
    //<editor-fold defaultstate="collapsed" desc="Rank">
    private final Rank rank = Rank.MODERATOR;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("getinfo", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("Creation Date: " + new Date(target.getCreatedTime()));
                        player.sendf("Last HP Rewards: %s", new Date(target.getLastHonorPointsReward()));
                        return true;
                    }
                },
                new NewCommand("removeevent", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String event = Events.eventName;
                        Events.resetEvent();
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            target.sendServerMessage(String.format("%s has ended the event '%s'", player.getSafeDisplayName(), event));
                        });
                        return true;
                    }
                },
                new NewCommand("createevent", rank, new CommandInput<Integer>(integer -> integer > 0 && integer < 15000, "X", "An Amount between 0 & 15000"), new CommandInput<Integer>(integer -> integer > 0 && integer < 15000, "Y", "An Amount between 0 & 15000"), new CommandInput<Integer>(integer -> integer > -1 && integer < 100, "Z", "An Amount between -1 & 100"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Event", "A new Event name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (Events.eventName != null
                                && !Events.eventName.isEmpty()) {
                            player.sendf("%s event is currently active; Remove it via ::removeevent command", Events.eventName);
                            return true;
                        }
                        final int x = Integer.parseInt(input[0].trim());
                        final int y = Integer.parseInt(input[1].trim());
                        final int z = Integer.parseInt(input[3].trim());
                        if (Combat.getWildLevel(x, y) > 0) {
                            player.sendMessage("Events cannot take place inside the wilderness.");
                            return true;
                        }
                        final String event = input[4].trim();
                        Events.fireNewEvent(TextUtils.ucFirst(event.toLowerCase()), true, 0, Position.create(x, y, z));
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            target.sendServerMessage(String.format("%s has just created the event '%s'.", player.getSafeDisplayName(), Events.eventName));
                            target.sendServerMessage("Click it int the questtab to join in!");
                        });
                        return true;
                    }
                },
                new NewCommand("altsinwildy", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (final Player primary : World.getPlayers()) {
                            for (final Player secondary : World.getPlayers()) {
                                if (primary.equals(secondary)
                                        || (!primary.getPosition().inPvPArea() || !secondary.getPosition().inPvPArea())
                                        || (!Objects.equals(primary.getShortIP(), secondary.getShortIP()) && primary.getUID() != secondary.getUID())) {
                                    continue;
                                }
                                final int x = Math.abs(primary.getPosition().getX() - secondary.getPosition().getX());
                                final int y = Math.abs(primary.getPosition().getY() - secondary.getPosition().getY());
                                if (x > 10 && y > 10) {
                                    continue;
                                }
                                player.sendf("%s (%d,%d,%d) & %s (%d,%d,%d)",
                                        TextUtils.optimizeText(primary.getName()), TextUtils.optimizeText(secondary.getName()),
                                        primary.getPosition().getX(), primary.getPosition().getY(), primary.getPosition().getZ(),
                                        secondary.getPosition().getX(), secondary.getPosition().getY(), secondary.getPosition().getZ());
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("checkpkstats", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("[%s] [Elo]: %,d - [K/D]: %d/%d - [KS]: %d", TextUtils.optimizeText(target.getName()),
                                target.getPoints().getEloRating(), target.getKillCount(), target.getDeathCount(), target.getKillStreak());
                        return true;
                    }
                },
                new NewCommand("checkpts", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final PlayerPoints points = target.getPoints();
                        player.getActionSender().openQuestInterface(
                                TextUtils.optimizeText(target.getName()),
                                new String[]{String.format("[PK Points]: %,d", points.getPkPoints()),
                                        String.format("[Honor Points]: %,d", points.getHonorPoints()),
                                        String.format("[Voting Points]: %,d", points.getVotingPoints()),
                                        String.format("[Donator Points]: %,d", points.getDonatorPoints()),
                                        String.format("[Bounty Hunter Points]: %,d", target.getBountyHunter().getKills()),
                                        String.format("[Emblem Points]: %,d", target.getBountyHunter().getEmblemPoints()),}
                        );
                        return true;
                    }
                },
                new NewCommand("trackdownnames", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Executing Command.");
                        World.getPlayers().stream().filter(target -> target.getPosition().equals(player.getPosition())).forEach(target -> {
                            player.sendf("[Name]: %s", target.getSafeDisplayName().replaceAll(" ", "_ "));
                        });
                        return true;
                    }
                },
                new NewCommand("rshu", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.getExtraData().put("rshu", true);
                        return true;
                    }
                },
                new NewCommand("setkeyword", rank, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Item Keyword"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "An Item ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String keyword = input[0].trim();
                        int id = Integer.parseInt(input[1].trim());
                        if (SpawnCommand.getId(keyword) != null) {
                            player.sendf("Keyword '%s' was already set before.", keyword);
                            if (Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
                                int old = SpawnCommand.getId(keyword);
                                if (id == old) {
                                    player.sendf("ID '%,d' was alread set.", id);
                                    return true;
                                }
                                SpawnCommand.setKeyword(keyword, id);
                                return true;
                            }
                        }
                        SpawnCommand.setKeyword(keyword, id);
                        return true;
                    }
                },
                new NewCommand("xteleto", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && !Rank.hasAbility(player, Rank.ADMINISTRATOR)
                                || player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendf("You are currently %s.", player.getPosition().inPvPArea() ? "in a PVP area" :
                                    player.duelAttackable > 0 ? "in a Duel" : "unable to perform this command.");
                            return true;
                        }
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (target.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendf("Player is currently %s.", target.duelAttackable > 0 ? "in a duel" : "unavailable");
                            return true;
                        }
                        player.setTeleportTarget(target.getPosition());
                        return true;
                    }
                },
                new NewCommand("xteletome", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && !Rank.hasAbility(player, Rank.DEVELOPER)
                                || player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendf("You are currently %s.", player.getPosition().inPvPArea() ? "in a PVP area" :
                                    player.duelAttackable > 0 ? "in a duel" : "unable to perform this command");
                            return true;
                        }
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (target.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER)
                                || !Rank.hasAbility(player, Rank.getPrimaryRank(target)) && Rank.isStaffMember(target)) {
                            player.sendf("Player is currently %s.", target.duelAttackable > 0 ? "in a duel" : "unavailable");
                            return true;
                        }
                        target.setTeleportTarget(player.getPosition());
                        return true;
                    }
                },
                new NewCommand("xtele", rank, 0, new CommandInput<Integer>(integer -> integer > 0, "Integer", "X"), new CommandInput<Integer>(integer -> integer > 0, "Integer", "Y"), new CommandInput<Integer>(integer -> integer > -1, "Integer", "Z")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && !Rank.hasAbility(player, Rank.ADMINISTRATOR)
                                || player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendf("You are currently %s.", player.getPosition().inPvPArea() ? "in a PVP area" :
                                    player.duelAttackable > 0 ? "in a Duel" : "unable to perform this command.");
                            return true;
                        }
                        player.setTeleportTarget(Position.create(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim()), Integer.parseInt(input[2].trim())));
                        return true;
                    }
                },
                new NewCommand("npcids", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getNpcs().stream().filter(npc -> npc != null && (player.getPosition().distance(npc.getPosition()) < 5)).forEach(npc -> {
                            player.sendf("[NPC]: %d, %d", npc.getDefinition().getId(), npc.getDefinition().combat());
                        });
                        return true;
                    }
                },
                new NewCommand("dumpservtimes", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ServerTimeManager.getSingleton().dumpValues();
                        player.sendMessage("Dumped all values.");
                        return true;
                    }
                },
                new NewCommand("teletospammer", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        LinkedList<Spam> players = new LinkedList<Spam>();
                        World.getPlayers().stream().filter(target -> target != null && target.getSpam().isSpamming()).forEach(target -> {
                            players.add(target.getSpam());
                        });
                        if (players.size() > 0) {
                            Spam spam = players.get(Misc.random(players.size() - 1));
                        } else {
                            player.sendMessage("No Spammers Found.");
                        }
                        return true;
                    }
                },
                new NewCommand("huntspammers", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getSpam().setHunting(true);
                        return true;
                    }
                },
                new NewCommand("banallspammers", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null && target.getSpam().isSpamming()).forEach(target -> {
                            target.getSpam().punish();
                        });
                        return true;
                    }
                },
                new NewCommand("howmanytrivia", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("There are currently %,d people playing trivia.", TriviaBot.getPlayersAmount());
                        return true;
                    }
                },
                new NewCommand("setwatched", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String value = input[0].trim();
                        Player target = World.getPlayerByName(value);
                        AccountLogger.getDupers().put(value, new Object());
                        target.getLogging().setWatched(true);
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(AccountLogger.DUPERS_FILE, true))) {
                            writer.write(value);
                            writer.newLine();
                            writer.flush();
                            writer.close();
                        } catch (IOException ex) {
                            Server.getLogger().log(Level.WARNING, "Error Writing to DUPERS_FILE", ex);
                        }
                        player.sendf("Now watching '%s'.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("sendhome", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.hasAbility(target, Rank.ADMINISTRATOR)
                                || Rank.isStaffMember(target)) {
                            player.sendf("Cannot send this player home.");
                            return true;
                        }
                        Magic.teleport(target, Edgeville.POSITION, false);
                        return true;
                    }
                },
                new NewCommand("viewbank", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getChecking().getBankListener() != null) {
                            player.getChecking().getBank().removeListener(player.getChecking().getBankListener());
                            player.getChecking().setBank(null);
                            player.getChecking().setBankListener(null);
                        }
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.getChecking().setBankListener(new InterfaceContainerListener(player, 5382));
                        player.getChecking().setBank(target.getBank());
                        player.getActionSender().sendInterfaceInventory(5292, 5063);
                        player.getInterfaceState().addListener(player.getChecking().getBank(), player.getChecking().getBankListener());
                        int tab = 0;
                        for (; tab < target.getBankField().getTabAmount(); tab++) {
                            int from = target.getBankField().getOffset(tab);
                            int to = from + target.getBankField().getTabAmounts()[tab];
                            Item[] items = Arrays.copyOf(Arrays.copyOfRange(target.getBank().toArray(), from, to), Bank.SIZE);
                            player.getActionSender().sendUpdateItems(Bank.BANK_INVENTORY_INTERFACE + tab, items);
                        }
                        for (; tab < 9; tab++) {
                            player.getActionSender().sendUpdateItems(Bank.BANK_INVENTORY_INTERFACE + tab, new Item[Bank.SIZE]);
                        }
                        return true;
                    }
                },
                new NewCommand("viewinv", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getChecking().getInvListener() != null) {
                            player.getChecking().getInv().removeListener(player.getChecking().getInvListener());
                            player.getChecking().setInv(null);
                            player.getChecking().setInvListener(null);
                        }
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.getChecking().setInvListener(new InterfaceContainerListener(player, 5064));
                        player.getChecking().setInv(target.getInventory());
                        player.getActionSender().sendInterfaceInventory(5292, 5063);
                        player.getInterfaceState().addListener(player.getChecking().getInv(), player.getChecking().getInvListener());
                        player.getActionSender().sendUpdateItems(5064, player.getChecking().getInv().toArray());
                        return true;
                    }
                },
                new NewCommand("tracepkp", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        TreeMap<Long, Player> map = new TreeMap<>();
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            long amount = target.getInventory().getCount(5020);
                            amount += target.getBank().getCount(5020);
                            amount += target.getPoints().getPkPoints() / 10;
                            map.put(amount, target);
                        });
                        map.descendingKeySet().stream().forEach(value -> {
                            player.sendf("%s - %s", map.get(value).getName(), value);
                        });
                        return true;
                    }
                },
                new NewCommand("richest", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        TreeMap<Integer, Player> map = new TreeMap<Integer, Player>();
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> map.put(target.getAccountValue().getTotalValue(), target));
                        map.descendingKeySet().stream().forEach(value -> {
                            player.sendf("%s - %s", map.get(value).getName(), value);
                        });
                        return true;
                    }
                },
                new NewCommand("resetviewed", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getChecking().getBank() != null) {
                            player.getChecking().getBank().removeListener(player.getChecking().getBankListener());
                            player.getChecking().setBank(null);
                            player.getChecking().setBankListener(null);
                        }
                        if (player.getChecking().getInv() != null) {
                            player.getChecking().getInv().removeListener(player.getChecking().getInvListener());
                            player.getChecking().setInv(null);
                            player.getChecking().setInvListener(null);
                        }
                        return true;
                    }
                },
                new NewCommand("accvalue", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("[%s]: %,d", TextUtils.optimizeText(target.getName()), target.getAccountValue().getTotalValue());
                        return true;
                    }
                },
                new NewCommand("resetks", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setKillStreak(0);
                        return true;
                    }
                },
                new NewCommand("tele", rank, new CommandInput<Integer>(integer -> integer > -1 && integer < 20001, "Integer", "X Coordinate"), new CommandInput<Integer>(integer -> integer > -1 && integer < 20001, "Integer", "Y Coordinate"), new CommandInput<Integer>(integer -> integer > -1 && integer < 1001, "Integer", "Z Coordinate")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.duelAttackable > 0) {
                            player.sendf("You cannot teleport out of a duel.");
                            return true;
                        }
                        final int x = Integer.parseInt(input[0].trim());
                        final int y = Integer.parseInt(input[1].trim());
                        final int z = Integer.parseInt(input[2].trim());
                        player.setTeleportTarget(Position.create(x, y, z));
                        return true;
                    }
                },
                new NewCommand("staff", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if ((player.getPosition().getX() >= 2934 && player.getPosition().getY() <= 3392
                                && player.getPosition().getX() < 3061 && player.getPosition().getY() >= 3326)
                                || Rank.hasAbility(player, Rank.ADMINISTRATOR)
                                || player.getName().equalsIgnoreCase("charmed")) {
                            final Player target = World.getPlayerByName(input[0].trim());
                            target.setTeleportTarget(Position.create(3165, 9635, 0));
                        }
                        return true;
                    }
                },
                new NewCommand("brightness", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendClientConfig(166, 4);
                        return true;
                    }
                },
                new NewCommand("bob", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        BoB.openInventory(player);
                        return true;
                    }
                },
                new NewCommand("giles", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getNpcs().stream().filter(npc -> npc.getDefinition().getId() == 2538).forEach(npc -> {
                            player.sendf("[Giles]: %d, %d, %d, %sDead, %sServerKilled, %sTeleporting", npc.getPosition().getX(), npc.getPosition().getY(), npc.getPosition().getZ(), npc.isDead() ? "=" : "!", npc.serverKilled ? "=" : "!", npc.isTeleporting() ? "=" : "!");
                            npc.vacateSquare();
                        });
                        return true;
                    }
                },
                new NewCommand("namenpc", rank, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "NPC Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        for (int array = 0; array < 6693; array++) {
                            if (NPCDefinition.forId(array).name().toLowerCase().contains(value.toLowerCase())) {
                                player.sendf("[Name]: %s, [ID]: %,d", NPCDefinition.forId(array).getName(), array);
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("resetslayertask", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.getSlayer().setPoints(target.getSlayer().getSlayerPoints() + 20);
                        target.getSlayer().resetTask();
                        player.sendf("You have succesfully reset '%s'; Their slayer task.", TextUtils.optimizeText(target.getName()));
                        player.sendf("Your slayer task has been reset by '%s'.", TextUtils.optimizeText(player.getName()));
                        return true;
                    }
                },
                new NewCommand("alts", rank, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An Existing Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {

                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}