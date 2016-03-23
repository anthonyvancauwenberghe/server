package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.content.authentication.PlayerAuthenticationGenerator;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.jge.entry.Entry;
import org.hyperion.rs2.model.content.misc.Ticket;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.misc2.Jail;
import org.hyperion.rs2.model.content.misc2.Zanaris;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
//</editor-fold>
/**
 * Created by DrHales on 2/29/2016.
 */
public class HelperCommands implements NewCommandExtension {
    //<editor-fold defaultstate="collapsed" desc="Rank">
    private final Rank rank = Rank.HELPER;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("authenticator", Rank.HELPER, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PlayerAuthenticationGenerator.startAuthenticationDialogue(player);
                        return true;
                    }
                },
                new NewCommand("gestats", rank, new CommandInput<Integer>(integer -> ItemDefinition.forId(integer) != null, "Integer", "An Item ID")) {
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
                new NewCommand("help", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
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
                new NewCommand("removejail", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player in Jail")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Jail.inJail(target)) {
                            target.setTeleportTarget(Edgeville.POSITION);
                        }
                        return true;
                    }
                },
                new NewCommand("jail", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
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
                new NewCommand("tojail", rank) {
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
                new NewCommand("unjail", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
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
                new NewCommand("tounjail", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.duelAttackable > 0) {
                            player.sendMessage("You cannot teleport away from a duel.");
                            return false;
                        }
                        player.setTeleportTarget(Zanaris.POSITION);
                        return true;
                    }
                },
                new NewCommand("authenticator", rank, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PlayerAuthenticationGenerator.startAuthenticationDialogue(player);
                        return true;
                    }
                },
                new NewCommand("checktickets", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        /*Ticket.checkTickets(player);
                        TicketManager.display(player);
                        player.write(Interface.createStatePacket(Interface.SHOW, 3));*/
                        player.sendMessage("'CheckTickets' is currently disabled.");
                        return true;
                    }
                },
                new NewCommand("syell", rank, new CommandInput<String>(string -> !string.trim().isEmpty(), "String", "Message")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        PushMessage.pushStaffMessage(input[0].trim(), player);
                        return true;
                    }
                },
                new NewCommand("assist", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (!player.canSpawnSet()) {
                            player.sendMessage("You are too busy to be assisting people.");
                            return true;
                        }
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (!target.canSpawnSet()) {
                            player.sendMessage("Cannot assist this player at this time.");
                            return true;
                        }
                        if (!Ticket.hasTicket(target)) {
                            player.sendMessage("This player has not asked for help.");
                            return true;
                        }
                        target.setTeleportTarget(player.getPosition().getCloseLocation());
                        Ticket.removeRequest(target);
                        return true;
                    }
                },
                new NewCommand("clearnulls", rank) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        World.getPlayers().stream().filter(target -> target == null).forEach(target -> {
                            World.getPlayers().remove(target);
                        });
                        player.sendMessage("Null Players Cleared.");
                        return true;
                    }
                },
                new NewCommand("display", rank, new CommandInput<String>(string -> !string.trim().isEmpty() && !string.toLowerCase().contains("arre") || !string.toLowerCase().contains("jet") || !string.toLowerCase().contains("ferry"), "String", "Display Name")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getName().toLowerCase().equals("knightmare")) {
                            String value = input[0].trim();
                            player.display = Character.toString(value.charAt(0)).toUpperCase() + value.substring(1);
                        }
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}
