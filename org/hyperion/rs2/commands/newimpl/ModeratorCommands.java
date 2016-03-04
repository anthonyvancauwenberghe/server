package org.hyperion.rs2.commands.newimpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.hyperion.Server;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.SpawnCommand;
import org.hyperion.rs2.commands.impl.cmd.WikiCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.util.TextUtils;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by Drhales on 2/29/2016.
 */
public class ModeratorCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("getinfo", Rank.MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("Creation Date: " + new Date(target.getCreatedTime()));
                        player.sendf("Last HP Rewards: %s", new Date(target.getLastHonorPointsReward()));
                        return true;
                    }
                },
                new NewCommand("removeevent", Rank.MODERATOR) {
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
                new NewCommand("createevent", Rank.MODERATOR, new CommandInput<Integer>(integer -> integer > 0 && integer < 15000, "X", "An Amount between 0 & 15000"), new CommandInput<Integer>(integer -> integer > 0 && integer < 15000, "Y", "An Amount between 0 & 15000"), new CommandInput<Integer>(integer -> integer > -1 && integer < 100, "Z", "An Amount between -1 & 100"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Event", "A new Event name")) {
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
                new NewCommand("altsinwildy", Rank.MODERATOR) {
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
                new NewCommand("checkpkstats", Rank.MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("[%s] [Elo]: %,d - [K/D]: %d/%d - [KS]: %d", TextUtils.optimizeText(target.getName()),
                                target.getPoints().getEloRating(), target.getKillCount(), target.getDeathCount(), target.getKillStreak());
                        return true;
                    }
                },
                new NewCommand("checkpts", Rank.MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("trackdownnames", Rank.MODERATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Executing Command.");
                        World.getPlayers().stream().filter(target -> target.getPosition().equals(player.getPosition())).forEach(target -> {
                            player.sendf("[Name]: %s", target.getSafeDisplayName().replaceAll(" ", "_ "));
                        });
                        return true;
                    }
                },
                new NewCommand("rshu", Rank.MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.getExtraData().put("rshu", true);
                        return true;
                    }
                },
                new NewCommand("setkeyword", Rank.MODERATOR, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Item Keyword"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "An Item ID")) {
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
                new NewCommand("xteleto", Rank.MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("xteletome", Rank.MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && !Rank.hasAbility(player, Rank.DEVELOPER)
                                || player.duelAttackable > 0 && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendf("You are currently %s.", player.getPosition().inPvPArea() ? "in a PVP area" :
                            player.duelAttackable > 0 ? "in a duel" : "unable to perform this command");
                            return true;
                        }
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (target.duelAttackable > 0 && !Rank.hasAbility(player,  Rank.DEVELOPER)
                                || !Rank.hasAbility(player, Rank.getPrimaryRank(target)) && Rank.isStaffMember(target)) {
                            player.sendf("Player is currently %s.", target.duelAttackable > 0 ? "in a duel" : "unavailable");
                            return true;
                        }
                        target.setTeleportTarget(player.getPosition());
                        return true;
                    }
                }
        );
    }

}
