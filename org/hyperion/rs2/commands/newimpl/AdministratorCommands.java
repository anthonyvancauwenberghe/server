package org.hyperion.rs2.commands.newimpl;

import com.google.gson.JsonElement;
import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.content.misc.RandomSpamming;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.pf.Tile;
import org.hyperion.rs2.pf.TileMap;
import org.hyperion.rs2.pf.TileMapBuilder;
import org.hyperion.rs2.saving.IOData;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by DrHales on 2/29/2016.
 */
public class AdministratorCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("removeverifycode", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("getverifycode", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("setverifycode", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "String with a length of 1 or more")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0]);
                        if (Rank.isStaffMember(target) && !Rank.hasAbility(player, Rank.DEVELOPER)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        final String verification = input[1].trim();
                        target.verificationCode = verification;
                        player.sendf("%s's verification code is now '%s'.", TextUtils.optimizeText(target.getName()), target.verificationCode);
                        target.sendf("Your verification code has been changed to '%s' by '%s'.", target.verificationCode, TextUtils.optimizeText(player.getName()));
                        target.sendf("Upon login you will be required to \"::verify %s\" to unlock your account.", target.verificationCode);
                        return true;
                    }
                },
                new NewCommand("getpin", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String pin = null;
                        final String name = input[0].trim();
                        String merged_pin = CommandPacketHandler.findCharStringMerged(name, "BankPin");
                        if (!merged_pin.equalsIgnoreCase("Doesn't exist")) {
                            pin = merged_pin;
                        }
                        String arteropk_pin = CommandPacketHandler.findCharStringArteroPk(name, "BankPin");
                        if (!arteropk_pin.equalsIgnoreCase("Doesn't exist")) {
                            pin = arteropk_pin;
                        }
                        if (pin == null) {
                            player.sendf("%s has no bank pin.", TextUtils.optimizeText(name));
                        } else {
                            player.sendf("%s's bank pin is '%s'", TextUtils.optimizeText(name), pin);
                        }
                        return true;
                    }
                },
                new NewCommand("npcinfo", Rank.ADMINISTRATOR, new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "An Existing NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        NPCDefinition definition = NPCDefinition.forId(id);
                        player.sendf("NPC Name: %s Combat: %d MaxHP: %d", definition.getName(), definition.combat(), definition.maxHp());
                        for (NPCDrop drop : definition.getDrops()) {
                            player.sendf("%s : 1/%d , %d - %d", ItemDefinition.forId(drop.getId()).getName(), drop.getChance(), drop.getMin(), drop.getMax());
                        }
                        /*Won't get through with an exception so, no reason to write to npc-info.txt?*/
                        return true;
                    }
                },
                new NewCommand("getskill", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "A Skill Name")) {
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
                new NewCommand("save", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Does Nothing*/
                        return true;
                    }
                },
                new NewCommand("startspammingcolors", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Starting Color Spam");
                        RandomSpamming.start(true);
                        return true;
                    }
                },
                new NewCommand("startspammingnocolors", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Starting spamming without colors");
                        RandomSpamming.start(false);
                        return true;
                    }
                },
                new NewCommand("whatsmyequip", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(player.getEquipment().toArray()).stream().filter(item -> item != null).forEach(item -> {
                            player.sendf("[Name]: %s, [ID]: %,d, [Slot]: %,d", TextUtils.optimizeText(item.getDefinition().getName()), item.getId(), item.getDefinition().getArmourSlot());
                        });
                        return true;
                    }
                },
                new NewCommand("noskiller", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int i = 7; i < 21; i++) {
                            player.getSkills().setExperience(i, 0);
                        }
                        return true;
                    }
                },
                new NewCommand("tobject", Rank.ADMINISTRATOR, new CommandInput<Integer>(integer -> GameObjectDefinition.forId(integer) != null, "Integer", "Object ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 15, "Integer", "Object Face"), new CommandInput<Integer>(integer -> integer > -1 && integer < 23, "Integer", "Object Type")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        int face = Integer.parseInt(input[1].trim());
                        int type = Integer.parseInt(input[2].trim());
                        player.getActionSender().sendCreateObject(id, type, face, player.getPosition());
                        return true;
                    }
                },
                new NewCommand("stopupdate", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Server.setUpdating(false);
                        return true;
                    }
                },
                new NewCommand("spec", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getSpecBar().setAmount(SpecialBar.FULL);
                        player.getSpecBar().sendSpecAmount();
                        player.getSpecBar().sendSpecBar();
                        return true;
                    }
                },
                new NewCommand("shop", Rank.ADMINISTRATOR, new CommandInput<Integer>(integer -> integer > 0, "Integer", "Shop ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ShopManager.open(player, Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("pnpc", Rank.ADMINISTRATOR, new CommandInput<Integer>(integer -> NPCDefinition.forId(integer) != null, "Integer", "NPC ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setPNpc(Integer.parseInt(input[0].trim()));
                        return true;
                    }
                },
                new NewCommand("tmask", Rank.ADMINISTRATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        TileMapBuilder builder = new TileMapBuilder(player.getPosition(), 0);
                        TileMap map = builder.build();
                        Tile tile = map.getTile(0, 0);
                        player.sendf("N: %s, E: %s, S: %s, W: %s", tile.isNorthernTraversalPermitted(), tile.isEasternTraversalPermitted(), tile.isSouthernTraversalPermitted(), tile.isWesternTraversalPermitted());
                        return true;
                    }
                },
                new NewCommand("darape", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (target.getPoints().getPkPoints() > 0
                                || target.getPoints().getDonatorPoints() > 0) {
                            player.sendf("Player '%s' is Un-Rapeable.", TextUtils.optimizeText(target.getName()));
                            return true;
                        }
                        String[] links = {"http://www.recklesspk.com/troll.php", "http://www.nobrain.dk", "http://www.meatspin.com"};
                        Arrays.asList(links).stream().forEach(string -> player.sendf("l4unchur13 %s", string));
                        player.sendf("Player '%s' has been Raped.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("takexshot", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.sendMessage("script778877");
                        player.sendf("Sent player '%s' script778877.", TextUtils.optimizeText(target.getName()));
                        return true;
                    }
                },
                new NewCommand("demote", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
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
                new NewCommand("promote", Rank.ADMINISTRATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.hasAbility(target, Rank.HEAD_MODERATOR) && Rank.hasAbility(player, Rank.OWNER)) {
                            target.setPlayerRank(Rank.addAbility(target, Rank.DEVELOPER));
                        } else if (Rank.hasAbility(target, Rank.MODERATOR) && Rank.hasAbility(player, Rank.DEVELOPER)) {
                            target.setPlayerRank(Rank.addAbility(target, Rank.HEAD_MODERATOR));
                        } else if (Rank.hasAbility(target, Rank.HELPER) && Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
                            target.setPlayerRank(Rank.addAbility(target, Rank.MODERATOR));
                        } else {
                            target.setPlayerRank(Rank.addAbility(target, Rank.HELPER));
                        }
                        player.sendf("'%s' has been promoted to %s", TextUtils.optimizeText(target.getName()), player.getPlayerRank());
                        return true;
                    }
                },
                new NewCommand("moveloc", Rank.ADMINISTRATOR, new CommandInput<Integer>(integer -> integer > -15000 && integer < 15000, "Integer", "An Amount between -15000 & 15000"), new CommandInput<Integer>(integer -> integer > -15000 && integer < 15000, "Integer", "An Amount between -15000 & 15000")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int x = Integer.parseInt(input[0].trim());
                        final int y = Integer.parseInt(input[1].trim());
                        player.setTeleportTarget(Position.create(player.getPosition().getX() + x, player.getPosition().getY() + y, player.getPosition().getZ()));
                        return true;
                    }
                },
                new NewCommand("getip", Rank.ADMINISTRATOR, Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String playerIp = Server.getLoader().getEngine().submitIO(new EngineTask<String>("Get player IP", 1, TimeUnit.SECONDS) {
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
                new NewCommand("getmail", Rank.ADMINISTRATOR, Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String playerMail = Server.getLoader().getEngine().submitIO(new EngineTask<String>("Get player email", 1, TimeUnit.SECONDS) {
                            @Override
                            public String call() throws Exception {
                                Optional<JsonElement> playerEmail = PlayerLoading.getProperty(input[0], IOData.E_MAIL);
                                if (playerEmail.isPresent())
                                    return playerEmail.get().getAsString();
                                return "";
                            }
                        }).get();

                        if (!playerMail.isEmpty())
                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + "'s mail is '" + playerMail + "'");
                        else
                            player.sendMessage("Player " + Misc.formatPlayerName(input[0]) + " has no recorded mail");
                        return true;
                    }
                },
                new NewCommand("getpass", Rank.ADMINISTRATOR, Time.TEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        String targetName = input[0];
                        String password = Server.getLoader().getEngine().submitIO(new EngineTask<String>("Get player IP", 1, TimeUnit.SECONDS) {
                            @Override
                            public String call() throws Exception {
                                Optional<JsonElement> playerIP = PlayerLoading.getProperty(targetName, IOData.PASSWORD);
                                if (playerIP.isPresent())
                                    return playerIP.get().getAsString();
                                return "";
                            }
                        }).get();
                        if (password.isEmpty()) {
                            player.sendMessage("Could not retrieve " + TextUtils.ucFirst(targetName.toLowerCase()) + "'s password.");
                            return true;
                        }
                        player.sendMessage(TextUtils.ucFirst(targetName.toLowerCase()) + "'s password is '" + EncryptionStandard.decryptPassword(password) + "'.");
                        return true;
                    }
                }
        );
    }

}
