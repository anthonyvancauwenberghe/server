package org.hyperion.rs2.commands.newimpl;

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.authentication.PlayerAuthenticationGenerator;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.misc2.Zanaris;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;

/**
 * Created by DrHales on 2/29/2016.
 */
public class HelperCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("gestats", Rank.HELPER, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "An Item ID")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendf("Grand Exchange is current %s@bla@.", JGrandExchange.enabled ? "@gre@Enabled" : "@red@Disabled");
                        if (input[0].matches("\\d{1,5}")) {
                            final int item = Integer.parseInt(input[0]);
                            final ItemDefinition definition = ItemDefinition.forId(item);
                            final IntSummaryStatistics buyStats = JGrandExchange.getInstance().itemUnitPriceStats(item, Entry.Type.BUYING, Entry.Currency.PK_TICKETS);
                            final IntSummaryStatistics sellStats = JGrandExchange.getInstance().itemUnitPriceStats(item, Entry.Type.SELLING, Entry.Currency.PK_TICKETS);
                            player.sendf("Grand Exchange Stats for %s (%d)", definition.getProperName(), definition.getId());
                            player.sendf("%,d players Buying: Min %,d PKT | Avg %1.2f PKT | Max %,d PKT", buyStats.getCount(), buyStats.getMin(), buyStats.getAverage(), buyStats.getMax());
                            player.sendf("%,d players Selling: Min %,d PKT | Avg %1.2f PKT | Max %,d PKT", sellStats.getCount(), sellStats.getMin(), sellStats.getAverage(), sellStats.getMax());
                            return true;
                        } else {
                            player.sendf("Number of buying entries: %,d", JGrandExchange.getInstance().get(Entry.Type.BUYING).size());
                            player.sendf("Number of selling entries: %,d", JGrandExchange.getInstance().get(Entry.Type.SELLING).size());
                        }
                        return true;
                    }
                },
                new NewCommand("help", Rank.HELPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.isStaffMember(target)) {
                            player.sendMessage("You cannot parse this command on staff members.");
                            return true;
                        }
                        Magic.teleport(target, Position.create(2607, 9672, 0), false);
                        return true;
                    }
                },
                new NewCommand("players2", Rank.HELPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.sendMessage("--Players Start--");
                        World.getPlayers().stream().filter(target -> target != null).forEach(target -> {
                            player.sendf(String.format("[Player]:%d,%s,%d,%d,%d,%d", Rank.getPrimaryRank(target).ordinal(), TextUtils.optimizeText(target.getName()), target.getSkills().getCombatLevel(), target.getPosition().getX(), target.getPosition().getX(), target.getPosition().getZ()));
                        });
                        player.sendMessage("--Players End----");
                        return true;
                    }
                },
                new NewCommand("removejail", Rank.HELPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null && Jail.inJail(World.getPlayerByName(string)), "Player", "An Online Player in Jail")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setTeleportTarget(Edgeville.POSITION);
                        return true;
                    }
                },
                new NewCommand("jail", Rank.HELPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (target.getPosition().inPvPArea()
                                || target.duelAttackable > 0
                                || Rank.isStaffMember(target)
                                || Jail.inJail(target)) {
                            player.sendf("This player is currently %s.", target.getPosition().inPvPArea() ? "in a PVP area"
                                    : target.duelAttackable > 0 ? "in a Duel" :
                                    Rank.isStaffMember(target) ? "unavailable for teleport" :
                                            Jail.inJail(target) ? "in Jail" : "unavailable");
                            return true;
                        }
                        target.setTeleportTarget(Jail.POSITION);
                        return true;
                    }
                },
                new NewCommand("tojail", Rank.HELPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.duelAttackable > 0) {
                            player.sendMessage("You cannot teleport away from a duel.");
                            return true;
                        }
                        player.setTeleportTarget(Jail.POSITION);
                        return true;
                    }
                },
                new NewCommand("unjail", Rank.HELPER, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.isStaffMember(target)
                                || !Jail.inJail(target)) {
                            player.sendf("Player is currently %s.", Rank.isStaffMember(target) ? "unavailable for teleport" :
                                    !Jail.inJail(target) ? "not in jail" : "unavailable");
                            return true;
                        }
                        target.setTeleportTarget(Zanaris.POSITION);
                        return true;
                    }
                },
                new NewCommand("tounjail", Rank.HELPER) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if(player.duelAttackable > 0) {
                            player.sendMessage("You cannot teleport away from a duel.");
                            return false;
                        }
                        player.setTeleportTarget(Zanaris.POSITION);
                        return true;
                    }
                },
                new NewCommand("authenticator", Rank.HELPER, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PlayerAuthenticationGenerator.startAuthenticationDialogue(player);
                        return true;
                    }
                }
        );
    }

}
