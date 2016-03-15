package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">

import org.apache.http.client.fluent.Content;
import org.hyperion.Configuration;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.NewCommandHandler;
import org.hyperion.rs2.commands.impl.cmd.SkillSetCommand;
import org.hyperion.rs2.commands.impl.cmd.WikiCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.challenge.Challenge;
import org.hyperion.rs2.model.challenge.ChallengeManager;
import org.hyperion.rs2.model.color.Color;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatAssistant;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.bounty.place.BountyHandler;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.rs2.model.content.minigame.LastManStanding;
import org.hyperion.rs2.model.content.misc.*;
import org.hyperion.rs2.model.content.misc2.NewGameMode;
import org.hyperion.rs2.model.content.skill.HunterLooting;
import org.hyperion.rs2.model.content.skill.dungoneering.DungeoneeringManager;
import org.hyperion.rs2.model.content.specialareas.SpecialAreaHolder;
import org.hyperion.rs2.model.customtrivia.CustomTriviaManager;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.itf.impl.PlayerProfileInterface;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.security.EncryptionStandard;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.PlayerFiles;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.*;
import java.util.concurrent.TimeUnit;
//</editor-fold>

/**
 * Created by DrHales on 2/29/2016.
 */
public class PlayerCommands implements NewCommandExtension {
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    private final Rank rank = Rank.PLAYER;

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("yaks", rank, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Magic.teleport(player, 3051, 3515, 0, false);
                        ClanManager.joinClanChat(player, "Risk Fights", false);
                        return true;
                    }
                },
                new NewCommand("testbank", rank, Time.FIFTEEN_SECONDS) {
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
                new NewCommand("buyshards", rank, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("Spirit shard packs are available inside the emblem pt store");
                        return true;
                    }
                },
                new NewCommand("changecompcolors", rank, Time.FIFTEEN_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Primary Color", "A Color Name"), new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "Secondary Color", "A Color Name")) {
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
                new NewCommand("viewprofile", rank, Time.FIFTEEN_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An Existing Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String name = input[0].trim();
                        if (!InterfaceManager.<PlayerProfileInterface>get(PlayerProfileInterface.ID).view(player, name)) {
                            player.sendf("Error loading '%s' profile.", name);
                        }
                        return true;
                    }
                },
                new NewCommand("exchangeimps", rank, Time.TEN_SECONDS) {
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
                new NewCommand("buyrocktails", rank, Time.FIFTEEN_SECONDS, new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Amount", String.format("An Amount between 0 & %s", Integer.MAX_VALUE))) {
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
                new NewCommand("thread", rank, Time.THIRTY_SECONDS, new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Thread Number", "A Thread Number")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage(String.format("http://forums.arteropk.com/index.php?showtopic=%d", Integer.parseInt(input[0].trim())));
                        return true;
                    }
                },
                new NewCommand("acceptyellrules", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 198);
                        return true;
                    }
                },
                new NewCommand("forums", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage("http://forums.arteropk.com/portal/");
                        return true;
                    }
                },
                new NewCommand("moneymaking", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage("http://forums.arteropk.com/topic/23523-money-making-guide/");
                        return true;
                    }
                },
                new NewCommand("rules", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage("http://forums.arteropk.com/forum/28-in-game-rules/");
                        return true;
                    }
                },
                new NewCommand("skullmyself", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.setSkulled(true);
                        return true;
                    }
                },
                new NewCommand("support", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("l4unchur13 http://support.arteropk.com/helpdesk/");
                        return true;
                    }
                },
                new NewCommand("ospk", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        SpecialAreaHolder.get("ospk").ifPresent(s -> s.enter(player));
                        return true;
                    }
                },
                new NewCommand("dicing", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Magic.teleport(player, Position.create(3048, 4979, 1), false);
                        ClanManager.joinClanChat(player, "dicing", false);
                        return true;
                    }
                },
                new NewCommand("tutorial", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getTutorialProgress() == 0) {
                            player.setTutorialProgress(1);
                        }
                        Tutorial.getProgress(player);
                        return true;
                    }
                },
                new NewCommand("ks", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("You are on a '@red@%,d@bla@' killstreak!", player.getKillStreak());
                        return true;
                    }
                },
                new NewCommand("toggleprofile", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getPermExtraData().put("disableprofile", !player.getPermExtraData().getBoolean("disableprofile"));
                        player.sendf("your public profile is currently %s@bla@.", player.getPermExtraData().getBoolean("disableprofile") ? "@red@unviewable" : "@gre@viewable");
                        return true;
                    }
                },
                new NewCommand("top10", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        LastManStanding.getLastManStanding().loadTopTenInterface(player);
                        return true;
                    }
                },
                new NewCommand("combine", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PotionDecanting.decantPotions(player);
                        return true;
                    }
                },
                new NewCommand("wiki", rank, Time.THIRTY_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Wiki Shortcut")) {
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
                new NewCommand("graves", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 194);
                        return true;
                    }
                },
                new NewCommand("wests", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DialogueManager.openDialogue(player, 196);
                        return true;
                    }
                },
                new NewCommand("answertrivia", rank, Time.FIVE_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Custom Trivia Answer")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        CustomTriviaManager.processAnswer(player, input[0].trim());
                        return true;
                    }
                },
                new NewCommand("viewtrivia", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        CustomTriviaManager.send(player);
                        return true;
                    }
                },
                new NewCommand("vote", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().sendWebpage(String.format("http://vote.arteropk.com/index.php?toplist_id=0&username=%s", player.getName()));
                        return true;
                    }
                },
                new NewCommand("viewchallenges", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        ChallengeManager.send(player, false);
                        return true;
                    }
                },
                new NewCommand("changepass", rank, new CommandInput<String>(string -> string.matches("[a-zA-Z0-9]+") && string.length() > 5, "password", "The new password to use.")) {
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
                },
                new NewCommand("prayers", rank, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        StringBuilder builder = new StringBuilder();
                        for (int array = 0; array < Prayers.SIZE; array++) {
                            if (player.getPrayers().isEnabled(array)) {
                                builder.append(String.format("%d,", array));
                            }
                        }
                        player.sendf("[Active Prayers]: %s", builder.toString());
                        return true;
                    }
                },
                new NewCommand("onlinestaff", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        List<Player> online = StaffManager.getOnlineStaff();
                        online.stream().forEach(other -> {
                            final Rank rank = Rank.getPrimaryRank(other);
                            player.sendf("[%s%s@bla@] - %s%s",
                                    rank.getYellColor(), other.display == null || other.display.isEmpty() ? other.getName() : other.display,
                                    rank.getYellColor(), rank);
                        });
                        return true;
                    }
                },
                new NewCommand("clearwalkinterface", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().showInterfaceWalkable(-1);
                        return true;
                    }
                },
                new NewCommand("bork", rank, Time.FIVE_MINUTES) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        long delay;
                        if ((delay = System.currentTimeMillis() - player.getPermExtraData().getLong(Bork.getTimeKey())) < Bork.getDelay()) {
                            player.sendf("You must wait %d more minutes to kill Bork", TimeUnit.MINUTES.convert(Bork.getDelay() - delay, TimeUnit.MILLISECONDS));
                            return true;
                        } else if (player.getTotalOnlineTime() < Time.ONE_HOUR * 6) {
                            player.sendf("You need at least 6 hours of online time to attempt Bork");
                            return true;
                        }
                        if (!ItemSpawning.canSpawn(player)) {
                            player.sendMessage("You can't start bork here");
                            return false;

                        }
                        final int height = player.getIndex() * 4;
                        Magic.teleport(player, Bork.getTeleportPosition().transform(0, 0, height), false);
                        World.submit(new Bork.BorkEvent(player));
                        return true;
                    }
                },
                new NewCommand("testcolors", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(Yelling.COLOUR_SUFFICES).stream().forEach(string -> {
                            player.sendf("@%s@[Owner][Arre]:Testing Message: %s", string, string);
                        });
                        return true;
                    }
                },
                new NewCommand("guessnumber", rank, Time.TEN_SECONDS, new CommandInput<Integer>(integer -> integer > Lottery.MIN_GUESS && integer < Lottery.MAX_GUESS, "Integer", String.format("An amount between %,d & %,d", Lottery.MIN_GUESS, Lottery.MAX_GUESS))) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int value = Integer.parseInt(input[0].trim());
                        Lottery.checkGuess(player, value);
                        return true;
                    }
                },
                new NewCommand("lotteryinfo", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().openLotteryInformation();
                        return true;
                    }
                },
                new NewCommand("getmail", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Mail]: %d", player.getMail());
                        return true;
                    }
                },
                new NewCommand("setmail", rank, Time.ONE_MINUTE, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "e-Mail")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getMail().setTempmail(input[0].trim());
                        return true;
                    }
                },
                new NewCommand("answer", rank, Time.FIVE_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "Trivia Answer")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        TriviaBot.sayAnswer(player, input[0].trim());
                        return true;
                    }
                },
                new NewCommand("getprice", rank, Time.TEN_SECONDS, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "Item ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        player.sendf("Price of %s costs %,df coins, it sells for %,d coins.", ItemDefinition.forId(id).getName(), NewGameMode.getUnitPrice(id), (int) NewGameMode.getUnitPrice(id) * NewGameMode.SELL_REDUCTION);
                        player.sendMessage("Incorrect? Please contact an admin.");
                        return true;
                    }
                },
                new NewCommand("sellitem", rank, Time.FIVE_SECONDS, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "", ""), new CommandInput<Integer>(integer -> integer > 0 && integer < 1000, "Integer", "An amount between 0 & 1,000")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int id = Integer.parseInt(input[0].trim());
                        if (!ItemSpawning.canSpawn(id)) {
                            player.sendf("You cannot sell item '%s'.", ItemDefinition.forId(id).getName());
                            return true;
                        }
                        int amount = Integer.parseInt(input[1].trim());
                        int price = (int) ((NewGameMode.getUnitPrice(id)) * NewGameMode.SELL_REDUCTION);
                        int sold = player.getInventory().remove(Item.create(id, amount));
                        long value = price * sold;
                        if (value > Integer.MAX_VALUE) {
                            player.sendMessage("You have sold too much of this item.");
                            return true;
                        }
                        if (sold > 0) {
                            return player.getInventory().add(Item.create(995, price * sold));
                        }
                        return true;
                    }
                },
                new NewCommand("resetparse", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        DungeoneeringManager.setItems(DungeoneeringManager.parse());
                        return true;
                    }
                },
                new NewCommand("reqhelp", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        //player.write(Interface.createStatePacket(SHOW, ID));
                        /*Disabled due to obfuscated client*/
                        player.sendMessage("'ReqHelp' is currently disabled.");
                        return true;
                    }
                },
                new NewCommand("accountvalue", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Account Value]: %,d", player.getAccountValue().getTotalValue());
                        return true;
                    }
                },
                new NewCommand("commands", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final List<Rank> list = new ArrayList<>(NewCommandHandler.getCommandsList().keySet());
                        final List<String> commands = new ArrayList<>();
                        list.stream().filter(rank -> Rank.hasAbility(player, rank)).forEach(rank -> NewCommandHandler.getCommandsList().get(rank).stream().forEach(command -> commands.add(command.getKey())));
                        return true;
                    }
                },
                new NewCommand("findcommand", rank, Time.FIFTEEN_SECONDS, new CommandInput<String>(string -> string.trim() != null && !string.trim().isEmpty(), "String", "A Command Phrase to Search for")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].toLowerCase().trim();
                        final List<Rank> list = new ArrayList<>(NewCommandHandler.getCommandsList().keySet());
                        Collections.sort(list, (one, two) -> two.ordinal() - one.ordinal());
                        list.stream().filter(rank -> Rank.hasAbility(player, rank)).forEach(rank -> NewCommandHandler.getCommandsList().get(rank).stream().filter(command -> command.getKey().toLowerCase().contains(value)).forEach(command -> player.sendf("[%s]:%s", rank, command.getKey())));
                        return true;
                    }
                },
                new NewCommand("printcmds", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final List<Rank> list = new ArrayList<>(NewCommandHandler.getCommandsList().keySet());
                        Collections.sort(list, (one, two) -> two.ordinal() - one.ordinal());
                        list.stream().filter(rank -> Rank.hasAbility(player, rank)).forEach(rank -> NewCommandHandler.getCommandsList().get(rank).stream().forEach(command -> player.sendf("[%s]:%s", rank, command.getKey())));
                        return true;
                    }
                },
                new NewCommand("settag", rank, Time.THIRTY_SECONDS, new CommandInput<String>(string -> !string.trim().isEmpty() && !(string.length() > 14) && !(Yelling.isValidTitle(string).length() > 1), "String", "Yell Tag")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPoints().getDonatorPointsBought() < 25000) {
                            player.sendMessage("You need to donate at least $250 to be able to set your tag.");
                        } else {
                            final String value = input[0].trim();
                            player.getYelling().setYellTitle(TextUtils.ucFirst(value.toLowerCase()));
                            player.sendf("Your yell tag has been set to %s.", player.getYelling().getTag());
                        }
                        return true;
                    }
                },
                new NewCommand("challenge", rank, Time.TEN_SECONDS, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Challenge Answer")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        final Challenge challenge = ChallengeManager.getChallenge(value);
                        if (challenge == null) {
                            player.sendf("No challenge found for '@dre@%s@bla@'.", value);
                            return true;
                        }
                        ChallengeManager.remove(challenge);
                        player.getBank().add(challenge.getPrize());
                        player.sendImportantMessage("%s x%,d has been added to your bank!", challenge.getPrize().getDefinition().getName(), challenge.getPrize().getCount());
                        final String message = String.format("@blu@[Challenge] %s has beaten %s's challenge for %s x%,d!", player.getSafeDisplayName(), challenge.getName(), challenge.getPrize().getDefinition().getName(), challenge.getPrize().getCount());
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> target.sendMessage(message));
                        return true;
                    }
                },
                new NewCommand("maxhit", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Melee]: %d, [Range]: %d", CombatAssistant.calculateMaxHit(player), CombatAssistant.calculateRangeMaxHit(player));
                        return true;
                    }
                },
                new NewCommand("zombies", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (!player.getExtraData().getBoolean("zombietele")) {
                            player.sendMessage("@red@This zone is in deep wilderness and leads into multi combat",
                                    "@blu@Type ::zombies again if you wish to proceed");
                            player.getExtraData().put("zombietele", true);
                        } else {
                            Magic.teleport(player, Position.create(3028, 3851, 0), false, false);
                        }
                        return true;
                    }
                },
                new NewCommand("placebounty", rank, Time.ONE_MINUTE, new CommandInput<String>(PlayerLoading::playerExists, "Player", "An Existing Player"), new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", "PKP Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String other = input[0].trim();
                        final int amount = Integer.parseInt(input[1].trim());
                        if (player.getPoints().getPkPoints() < amount) {
                            player.sendMessage("You don't have enough PK points to do this.");
                            return true;
                        }
                        if (BountyHandler.add(other, player.getName(), amount)) {
                            player.getPoints().setPkPoints(player.getPoints().getPkPoints() - amount);
                            player.sendf("You have successfully placed a bounty of %d on %s", amount, other);
                        } else {
                            player.sendMessage("Minimum bounty is 500pkp, or player's bounty is greater than yours!");
                        }
                        return true;
                    }
                },
                new NewCommand("checkbounties", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        BountyHandler.listBounties(player);
                        return true;
                    }
                },
                new NewCommand("selectitem", rank, Time.FIFTEEN_SECONDS, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "Item ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getGrandExchangeTracker().selectItem(Integer.parseInt(input[0].trim()), Entry.Type.BUYING);
                        return true;
                    }
                },
                new NewCommand("setlvl", rank, Time.FIVE_SECONDS, new CommandInput<Integer>(integer -> integer > -1 && integer < 7, "Integer", "Skill ID"), new CommandInput<Integer>(integer -> integer > 0 && integer < 100, "Integer", "Level")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (!SkillSetCommand.canChangeLevel(player)
                                || player.isInCombat()) {
                            player.sendMessage("You cannot do this at this time.");
                            return true;
                        }
                        final int skill = Integer.parseInt(input[0].trim());
                        final int level = Integer.parseInt(input[1].trim());
                        player.getSkills().setLevel(skill, level);
                        player.getSkills().setExperience(skill, Skills.getXPForLevel(level) + 5);
                        return true;
                    }
                },
                new NewCommand("rest", rank, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.playAnimation(Animation.create(11786));
                        return true;
                    }
                },
                new NewCommand("dismiss", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.SummoningCounter = 0;
                        player.getActionSender().sendMessage("You dismiss your familiar.");
                        return true;
                    }
                },
                new NewCommand("resetmyappearance", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getAppearance().resetAppearance();
                        player.sendMessage("Looks reset.");
                        PlayerFiles.saveGame(player);
                        return true;
                    }
                },
                new NewCommand("switchmode", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getExtraData().getBoolean("switchmode")) {
                            player.setGameMode(0);
                            player.sendMessage("Successfully switched to normal game mode");
                        } else {
                            player.getExtraData().put("switchmode", true);
                            player.sendMessage("Type ::switchmode again to switch to normal game mode");
                        }
                        return true;
                    }
                },
                new NewCommand("upcount", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Player Count]: %,d", World.getPlayers().size());
                        return true;
                    }
                },
                new NewCommand("13s", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Magic.goTo13s(player);
                        return true;
                    }
                },
                new NewCommand("nextbonus", rank, Time.FIVE_MINUTES) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int day = (Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + 4);
                        player.sendMessage("The next 5 bonus skills will be;");
                        for (int array = 0; array < 5; array++) {
                            int skill = ((day + array) % (Skills.SKILL_COUNT - 8)) + 7;
                            if (skill == 21) {
                                player.sendMessage("Random Skill");
                            } else {
                                player.sendMessage(Misc.getSkillName(skill).trim());
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("nameitem", rank, Time.FIVE_SECONDS, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Item Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].toLowerCase().trim();
                        final int max = Rank.hasAbility(player, Rank.DEVELOPER) ? 20000 : ItemSpawning.MAX_ID;
                        int count = 0;
                        List<Item> list = new ArrayList<>();
                        for (int array = max; array > 0; array++) {
                            if (ItemDefinition.forId(array) != null
                                    && ItemDefinition.forId(array).getName().toLowerCase().contains(value)) {
                                list.add(new Item(array));
                                count++;
                                if (count == 30) {
                                    break;
                                }
                            }
                        }
                        if (list.isEmpty()) {
                            player.sendf("No items found with the name '%s'", value);
                            return true;
                        }
                        Item items[] = new Item[list.size()];
                        for (Item array : list) {
                            items[--count] = array;
                        }
                        player.getActionSender().displayItems(items);
                        return true;
                    }
                },
                new NewCommand("vengrunes", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (ItemSpawning.canSpawn(player, false)
                                && !player.hardMode()) {
                            ContentEntity.addItem(player, 557, 1000);
                            ContentEntity.addItem(player, 560, 1000);
                            ContentEntity.addItem(player, 9075, 1000);
                        }
                        return true;
                    }
                },
                new NewCommand("barragerunes", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (ItemSpawning.canSpawn(player, false)
                                && !player.hardMode()) {
                            ContentEntity.addItem(player, 560, 1000);
                            ContentEntity.addItem(player, 565, 1000);
                            ContentEntity.addItem(player, 555, 1000);
                        }
                        return true;
                    }
                },
                new NewCommand("copy", rank, Time.THIRTY_SECONDS, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (ItemSpawning.canSpawn(player, false)
                                && !player.hardMode()
                                && CommandPacketHandler.copyCheck(player)) {
                            if (ContentEntity.getTotalAmountOfEquipmentItems(player) > 0) {
                                player.sendMessage("You need to take off your armour before copying!");
                            } else {
                                final Player target = World.getPlayerByName(input[0].trim());
                                if (!Rank.hasAbility(target, Rank.ADMINISTRATOR)) {
                                    Arrays.asList(target.getEquipment().toArray()).stream().filter(item -> item != null && CommandPacketHandler.copyCheck(item, player)).forEach(item -> player.getEquipment().set(Equipment.getType(item).getSlot(), item));
                                }
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("copyinv", rank, Time.THIRTY_SECONDS, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (ItemSpawning.canSpawn(player, false)
                                && !player.hardMode()
                                && CommandPacketHandler.copyCheck(player)) {
                            if (ContentEntity.getTotalAmountOfItems(player) > 0) {
                                player.sendMessage("You need to remove items from your inventory before copying!");
                            } else {
                                final Player target = World.getPlayerByName(input[0].trim());
                                if (!Rank.hasAbility(target, Rank.ADMINISTRATOR)) {
                                    Arrays.asList(target.getEquipment().toArray()).stream().filter(item -> item != null && CommandPacketHandler.copyCheck(item, player)).forEach(item -> player.getInventory().add(item));
                                }
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("copylvl", rank, Time.THIRTY_SECONDS, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (ItemSpawning.canSpawn(player, false)
                                && !player.hardMode()
                                && CommandPacketHandler.copyCheck(player)) {
                            player.resetPrayers();
                            if (ContentEntity.getTotalAmountOfEquipmentItems(player) > 0) {
                                player.sendMessage("You need to take off your armour before copying!");
                            } else {
                                final Player target = World.getPlayerByName(input[0].trim());
                                if (!Rank.hasAbility(target, Rank.ADMINISTRATOR)) {
                                    for (int array = 0; array < 6; array++) {
                                        player.getSkills().setLevel(array, target.getSkills().getRealLevels()[array]);
                                        player.getSkills().setExperience(array, target.getSkills().getXps()[array]);
                                    }
                                }
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("resetrfd", rank, Time.TEN_MINUTES) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.RFDLevel = 0;
                        player.sendMessage("RFD reset.");
                        return true;
                    }
                },
                new NewCommand("findids", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(player.getInventory().toArray()).stream().filter(item -> item != null).forEach(item -> player.sendf("[Name]: %s, [ID]: %d", item.getDefinition().getName(), item.getDefinition().getId()));
                        return true;
                    }
                },
                new NewCommand("showwildinterface", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.showEP = false;
                        player.getActionSender().sendWildLevel(player.wildernessLevel);
                        player.sendMessage("Now showing wilderness level interface.");
                        return true;
                    }
                },
                new NewCommand("clearfriendslist", rank, Time.TEN_MINUTES) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getFriends().clear();
                        player.sendMessage("Done clearing friends list; Relog.");
                        return true;
                    }
                },
                new NewCommand("wildlvl", rank, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Wilderness Level]: %d", player.wildernessLevel);
                        return true;
                    }
                },
                new NewCommand("myep", rank, Time.ONE_MINUTE) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[EP Level]: %d", player.EP);
                        return true;
                    }
                },
                new NewCommand("givemetabsplz", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int[] value = new int[]{8007, 8008, 8009, 8010, 8011, 8012};
                        for (int array : value) {
                            ContentEntity.addItem(player, array, 1000);
                        }
                        return true;
                    }
                },
                new NewCommand("myopp", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("[Oponent]: %s", player.cE.getOpponent());
                        return true;
                    }
                },
                new NewCommand("buytickets", rank, Time.FIFTEEN_SECONDS, new CommandInput<Integer>(integer -> integer > 0 && integer < 100000, "Integer", "Pk Tickets Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        int amount = Integer.parseInt(input[0].trim());
                        if (player.getPoints().getPkPoints() > (amount * 10)) {
                            player.getPoints().setPkPoints(player.getPoints().getPkPoints() - (amount * 10));
                            final Item item = Item.create(5020, amount);
                            if (!player.getInventory().add(item)) {
                                player.getBank().add(item);
                                player.sendf("%,d Pk Tickets have been added to your bank.");
                                return true;
                            } else {
                                player.getInventory().add(item);
                                player.sendf("%,d Pk Tickets have been added to your inventory.");
                                return true;
                            }
                        } else {
                            player.sendMessage("You don't have enough Pkp for this.");
                        }
                        return true;
                    }
                },
                new NewCommand("selltickets", rank, Time.FIFTEEN_SECONDS, new CommandInput<Integer>(integer -> integer > 0 && integer < Integer.MAX_VALUE, "Integer", "Pk Tickets Amount")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (!Position.inAttackableArea(player)) {
                            int amount = Integer.parseInt(input[0].trim());
                            if (player.getPoints().getPkPoints() + (amount * 10) < Integer.MAX_VALUE) {
                                int removed;
                                if ((removed = player.getInventory().remove(new Item(5020, amount))) > 0) {
                                    player.getPoints().increasePkPoints(removed * 10);
                                    player.sendf("You sold %,d Pk Tickets.", removed);
                                }
                            }
                        }
                        return true;
                    }
                },
                new NewCommand("empty", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (!player.getPosition().inPvPArea()) {
                            DialogueManager.openDialogue(player, 143);
                        }
                        return true;
                    }
                },
                new NewCommand("players", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendServerMessage(String.format("There are currently %,d players online.", (int) World.getPlayers().size() * Configuration.getDouble(Configuration.ConfigurationObject.PLAYER_MULTIPLIER)));
                        player.getActionSender().openPlayersInterface();
                        return true;
                    }
                },
                new NewCommand("kdr", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        double kdr = (double) player.getKillCount() / (double) player.getDeathCount();
                        kdr = Misc.round(kdr, 3);
                        player.forceMessage(String.format("[KDR]: %,d, %,d / %,d", kdr, player.getKillCount(), player.getDeathCount()));
                        return true;
                    }
                },
                new NewCommand("switchoption", rank, Time.FIVE_SECONDS, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Player Option")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String option = input[0].toLowerCase().trim();
                        boolean old = player.getPermExtraData().getBoolean(String.format("%soption", option));
                        switch (option) {
                            case "moderate":
                                player.getActionSender().sendPlayerOption(old ? TextUtils.titleCase(option) : "null", 5, 0);
                                return true;
                            case "trade":
                                player.getActionSender().sendPlayerOption(old ? TextUtils.titleCase(option) : "null", 4, 0);
                                break;
                            case "follow":
                                player.getActionSender().sendPlayerOption(old ? TextUtils.titleCase(option) : "null", 3, 0);
                                break;
                            case "profile":
                                player.getActionSender().sendPlayerOption(old ? TextUtils.titleCase(option) : "null", 6, 0);
                                break;
                            default:
                                return true;
                        }
                        player.getPermExtraData().put(option + "option", !player.getPermExtraData().getBoolean(option + "option"));
                        player.sendf("You have %s your %s option", old ? "enabled" : "disabled", option);
                        return true;
                    }
                },
                new NewCommand("mypos", rank, Time.TEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final int x = player.getPosition().getX();
                        final int y = player.getPosition().getY();
                        final int z = player.getPosition().getZ();
                        final int rx = x >> 6;
                        final int ry = y >> 6;
                        player.sendf("[X]: %d, [Y]: %d, [Z]: %d, [Region]: %d, [RX]: %d, [RY]: %d", x, y, z, (rx << 8) + ry, rx, ry);
                        return true;
                    }
                },
                new NewCommand("reqhelp", rank, Time.ONE_MINUTE, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Help Reason")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (System.currentTimeMillis() - player.lastTickReq() < 60000) {
                            player.sendMessage("You need to wait 60 seconds to request another ticket.");
                            return true;
                        }
                        final String reason = input[0].trim();
                        if (Ticket.hasTicket(player)) {
                            Ticket.removeRequest(player);
                        }
                        Ticket.putRequest(player, reason);
                        PushMessage.pushHelpMessage(String.format("%s has just requested help for '%s'.", TextUtils.optimizeText(player.getName()), reason));
                        player.sendMessage("Your ticket was submitted. Remember to use ::help for most questions.");
                        player.refreshTickReq();
                        return true;
                    }
                },
                new NewCommand("npclogs", rank, Time.THIRTY_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getActionSender().openQuestInterface("NPC Logs", player.getNPCLogs().getDisplay());
                        return true;
                    }
                },
                new NewCommand("clearjunk", rank, Time.FIVE_MINUTES) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Arrays.asList(player.getBank().toArray()).stream().filter(item -> item.getCount() < 10 && ItemSpawning.canSpawn(item.getId())).forEach(item -> player.getBank().remove(item));
                        player.sendMessage("Done cleaning bank.");
                        return true;
                    }
                },
                new NewCommand("listcolors", rank, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Color[] colors = Color.values();
                        final String[] strings = new String[colors.length];
                        for (int array = 0; array < colors.length; array++) {
                            strings[array] = Character.toString(colors[array].toString().charAt(0)).toUpperCase() + colors[array].toString().substring(1).toLowerCase().trim();
                        }
                        player.getActionSender().openQuestInterface("Color List", strings);
                        return true;
                    }
                },
                new NewCommand("verify", rank, 0, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Verification Code")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final String value = input[0].trim();
                        if (!player.verificationCode.equals(value)) {
                            if (--player.verificationCodeAttemptsLeft == 0) {
                                Arrays.asList(new Target[]{Target.IP, Target.MAC, Target.SPECIAL}).stream().forEach(target -> {
                                    final Punishment punishment = Punishment.create("Server", player, Combination.of(target, Type.BAN), org.hyperion.rs2.model.punishment.Time.create(1, TimeUnit.DAYS), "Too many failed verification attempts.");
                                    PunishmentManager.getInstance().add(punishment);
                                    punishment.insert();
                                });
                                EntityHandler.deregister(player);
                                return false;
                            } else {
                                player.sendf("You have %,d attempts left to verify", player.verificationCodeAttemptsLeft);
                            }
                            return false;
                        }
                        player.verificationCodeEntered = true;
                        player.sendMessage("Successfully verified.");
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}
