package org.hyperion.rs2.commands.newimpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.hyperion.Server;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.commands.impl.cmd.SpawnCommand;
import org.hyperion.rs2.commands.impl.cmd.WikiCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.challenge.ChallengeManager;
import org.hyperion.rs2.model.color.Color;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.minigame.LastManStanding;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc.PotionDecanting;
import org.hyperion.rs2.model.content.misc.Tutorial;
import org.hyperion.rs2.model.content.skill.HunterLooting;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.model.customtrivia.CustomTriviaManager;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.itf.impl.PlayerProfileInterface;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * Created by DrHales on 2/29/2016.
 */
public class PlayerCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("yaks", Rank.PLAYER, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Magic.teleport(player, 3051, 3515, 0, false);
                        ClanManager.joinClanChat(player, "Risk Fights", false);
                        return true;
                    }
                },
                new NewCommand("testbank", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (int array = 0; array < player.getBank().size(); array++) {
                            BankItem item = (BankItem) player.getBank().get(array);
                            System.out.println(String.format("Tab Index: %s \tTab Item: %s \tTab Count: %s", item.getTabIndex(), item.getId(), item.getCount()));
                        }
                        for (int array = 0; array < 9; array++) {
                            System.out.println(String.format("Tab Amount: %s", player.getBankField().getTabAmounts()[array]));
                        }
                        return true;
                    }
                },
                new NewCommand("buyshards", Rank.PLAYER, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Spirit shard packs are available inside the emblem pt store");
                        return true;
                    }
                },
                new NewCommand("changecompcolors", Rank.PLAYER, Time.FIFTEEN_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Primary Color", "A Color Name"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Secondary Color", "A Color Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String primary_color = input[0].trim();
                        final String secondary_color = input[1].trim();
                        Color primary = null;
                        Color secondary = null;
                        for (final Color color : Color.values()) {
                            if (primary != null && secondary != null) {
                                break;
                            }
                            final String current = color.toString();
                            if (primary_color.equalsIgnoreCase(current)) {
                                primary = color;
                            }
                            if (secondary_color.equalsIgnoreCase(current)) {
                                secondary = color;
                            }
                        }
                        if (primary == null || secondary == null) {
                            player.sendf("%s is not a valid color.", primary == null ? primary_color : secondary_color);
                            return true;
                        }
                        if (!Rank.hasAbility(player, Rank.ADMINISTRATOR) && primary == Color.WHITE && primary == secondary) {
                            player.sendMessage("Ferry bitch slapped you from making both colors white");
                            return true;
                        }
                        player.compCapePrimaryColor = primary.color;
                        player.compCapeSecondaryColor = secondary.color;
                        player.getUpdateFlags().set(UpdateFlags.UpdateFlag.APPEARANCE, true);
                        player.sendf("Changed Completionist Cape colors: Primary -> %s | Secondary -> %s", primary, secondary);
                        return true;
                    }
                },
                new NewCommand("viewprofile", Rank.PLAYER, Time.FIFTEEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An Existing Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String name = input[0].trim();
                        if (!InterfaceManager.<PlayerProfileInterface>get(PlayerProfileInterface.ID).view(player, name)) {
                            player.sendf("Error loading '%s' profile.", name);
                        }
                        return true;
                    }
                },
                new NewCommand("exchangeimps", Rank.PLAYER, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        for (final Item array : player.getInventory().toArray()) {
                            if (array != null) {
                                HunterLooting.giveLoot(player, array.getId());
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("buyrocktails", Rank.PLAYER, Time.FIFTEEN_SECONDS, new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount between 0 & %s", Integer.MAX_VALUE))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int amount = Math.min(Integer.parseInt(input[0].trim()), player.getPoints().getPkPoints());
                        if (player.getPoints().getPkPoints() < amount) {
                            amount = player.getPoints().getPkPoints();
                        }
                        player.getPoints().setPkPoints(player.getPoints().getPkPoints() - amount);
                        player.getBank().add(new BankItem(0, 15272, amount));
                        player.sendf("%d rocktails have been added to your bank.", amount);
                        return true;
                    }
                },
                new NewCommand("thread", Rank.PLAYER, Time.THIRTY_SECONDS, new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Thread Number", "A Thread Number")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage(String.format("http://forums.arteropk.com/index.php?showtopic=%d", Integer.parseInt(input[0].trim())));
                        return true;
                    }
                },
                new NewCommand("acceptyellrules", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 198);
                        return true;
                    }
                },
                new NewCommand("forums", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage("http://forums.arteropk.com/portal/");
                        return true;
                    }
                },
                new NewCommand("moneymaking", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage("http://forums.arteropk.com/topic/23523-money-making-guide/");
                        return true;
                    }
                },
                new NewCommand("rules", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage("http://forums.arteropk.com/forum/28-in-game-rules/");
                        return true;
                    }
                },
                new NewCommand("skullmyself", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setSkulled(true);
                        return true;
                    }
                },
                new NewCommand("support", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("l4unchur13 http://support.arteropk.com/helpdesk/");
                        return true;
                    }
                },
                new NewCommand("ospk", Rank.PLAYER, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        SpecialAreaHolder.get("ospk").ifPresent(s -> s.enter(player));
                        return true;
                    }
                },
                new NewCommand("dicing", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Magic.teleport(player, Location.create(3048, 4979, 1), false);
                        ClanManager.joinClanChat(player, "dicing", false);
                        return true;
                    }
                },
                new NewCommand("tutorial", Rank.PLAYER, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getTutorialProgress() == 0) {
                            player.setTutorialProgress(1);
                        }
                        Tutorial.getProgress(player);
                        return true;
                    }
                },
                new NewCommand("ks", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("You are on a '@red@%,d@bla@' killstreak!", player.getKillStreak());
                        return true;
                    }
                },
                new NewCommand("toggleprofile", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getPermExtraData().put("disableprofile", !player.getPermExtraData().getBoolean("disableprofile"));
                        player.sendf("your public profile is currently %s@bla@.", player.getPermExtraData().getBoolean("disableprofile") ? "@red@unviewable" : "@gre@viewable");
                        return true;
                    }
                },
                new NewCommand("top10", Rank.PLAYER, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        LastManStanding.getLastManStanding().loadTopTenInterface(player);
                        return true;
                    }
                },
                new NewCommand("combine", Rank.PLAYER, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PotionDecanting.decantPotions(player);
                        return true;
                    }
                },
                new NewCommand("wiki", Rank.PLAYER, Time.THIRTY_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Wiki Shortcut")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String shortcut = input[0].trim();
                        if (!WikiCommand.KEY_TO_URL.containsKey(shortcut)) {
                            player.sendf("No such link '%s'.", shortcut);
                            return true;
                        }
                        player.sendf("l4unchur13 http://www.arteropk.wikia.com/wiki/%s", TextUtils.titleCase(WikiCommand.KEY_TO_URL.get(shortcut)).replace(" ", "%20"));
                        return true;
                    }
                },
                new NewCommand("graves", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 194);
                        return true;
                    }
                },
                new NewCommand("wests", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 196);
                        return true;
                    }
                },
                new NewCommand("answertrivia", Rank.PLAYER, Time.FIVE_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Custom Trivia Answer")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        CustomTriviaManager.processAnswer(player, input[0].trim());
                        return true;
                    }
                },
                new NewCommand("viewtrivia", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        CustomTriviaManager.send(player);
                        return true;
                    }
                },
                new NewCommand("vote", Rank.PLAYER, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage(String.format("http://vote.arteropk.com/index.php?toplist_id=0&username=%s", player.getName()));
                        return true;
                    }
                },
                new NewCommand("viewchallenges", Rank.PLAYER, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ChallengeManager.send(player, false);
                        return true;
                    }
                },
                new NewCommand("changepass", Rank.PLAYER, new CommandInput<String>(string -> string.matches("[a-zA-Z0-9]+") && string.length() > 5, "password", "The new password to use.")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPassword().equalsIgnoreCase(EncryptionStandard.encryptPassword(input[0]))) {
                            player.sendMessage("Don't use the same password again!");
                            return true;
                        }
                        TextUtils.writeToFile("./data/possiblehacks.txt", String.format("Player: %s Old password: %s New password: %s By IP: %s Date: %s", player.getName(), player.getPassword(), input[0], player.getShortIP(), new Date().toString()));
                        player.setPassword(EncryptionStandard.encryptPassword(input[0].toLowerCase()));
                        player.sendImportantMessage("Your password is now " + input[0].toLowerCase());
                        player.getPermExtraData().put("passchange", System.currentTimeMillis());
                        player.getExtraData().put("needpasschange", false);
                        return true;
                    }
                }
        );
    }

}
