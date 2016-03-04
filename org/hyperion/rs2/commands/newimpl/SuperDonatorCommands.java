package org.hyperion.rs2.commands.newimpl;

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.SpellBook;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.content.skill.Prayer;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DrHales on 2/29/2016.
 */
public class SuperDonatorCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("openge", Rank.SUPER_DONATOR, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        player.getGrandExchangeTracker().openInterface();
                        return true;
                    }
                },
                new NewCommand("bank", Rank.SUPER_DONATOR, Time.FIVE_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        Bank.open(player, false);
                        return true;
                    }
                },
                new NewCommand("switchprayers", Rank.SUPER_DONATOR, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && player.isInCombat()) {
                            player.sendMessage("You cannot do this at this time.");
                            return true;
                        }
                        Prayer.changeCurses(player);
                        return true;
                    }
                },
                new NewCommand("lunars", Rank.SUPER_DONATOR, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && player.isInCombat()) {
                            player.sendMessage("You cannot do this at this time.");
                            return true;
                        }
                        player.getSpellBook().changeSpellBook(SpellBook.LUNAR_SPELLBOOK);
                        player.getActionSender().sendSidebarInterface(6, 29999);
                        return true;
                    }
                },
                new NewCommand("ancnients", Rank.SUPER_DONATOR, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && player.isInCombat()) {
                            player.sendMessage("You cannot do this at this time.");
                            return true;
                        }
                        player.getSpellBook().changeSpellBook(SpellBook.ANCIENT_SPELLBOOK);
                        player.getActionSender().sendSidebarInterface(6, 12855);
                        return true;
                    }
                },
                new NewCommand("moderns", Rank.SUPER_DONATOR, Time.FIFTEEN_SECONDS) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (player.getPosition().inPvPArea() && player.isInCombat()) {
                            player.sendMessage("You cannot do this at this time.");
                            return true;
                        }
                        player.getSpellBook().changeSpellBook(SpellBook.REGULAR_SPELLBOOK);
                        player.getActionSender().sendSidebarInterface(6, 1151);
                        return true;
                    }
                }
        );
    }

}
