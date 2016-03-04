package org.hyperion.rs2.commands.newimpl;

import org.hyperion.Server;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DrHales on 2/29/2016.
 */
public class HeadModeratorCommands implements NewCommandExtension {

    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("resetdeaths", Rank.HEAD_MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setDeathCount(0);
                        return true;
                    }
                },
                new NewCommand("resetkills", Rank.HEAD_MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.setKillCount(0);
                        return true;
                    }
                },
                new NewCommand("resetelo", Rank.HEAD_MODERATOR, new CommandInput<String>(string -> World.getPlayerByName(string) != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        target.getPoints().setEloRating(1200);
                        return true;
                    }
                },
                new NewCommand("update", Rank.HEAD_MODERATOR) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        if (Rank.hasAbility(player.getPlayerRank(), Rank.ADMINISTRATOR)) {
                            int time;
                            try {
                                time = Integer.parseInt(input[0]);
                            } catch (ArrayIndexOutOfBoundsException e) {
                                time = 120;
                            }
                            Server.update(time, String.format("%sRestart Request", player.getName()));
                            return true;
                        }
                        Server.update(120, String.format("%sRestart Request", player.getName()));
                        return true;
                    }
                }
        );
    }

}
