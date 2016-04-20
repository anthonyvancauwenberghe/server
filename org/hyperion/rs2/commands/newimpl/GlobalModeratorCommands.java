package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.Yelling;

import java.util.Arrays;
import java.util.List;
//</editor-fold>
/**
 * Created by DrHales on 2/29/2016.
 */
public class GlobalModeratorCommands implements NewCommandExtension {
    //<editor-fold defaultstate="collapsed" desc="Rank">
    private final Rank rank = Rank.GLOBAL_MODERATOR;
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new NewCommand("setplayertag", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player"), new CommandInput<String>(string -> string != null, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.hasAbility(target, Rank.ADMINISTRATOR) && target != player) {
                            return true;
                        }
                        final String value = Yelling.isValidTitle(input[1].trim());
                        target.getYelling().setYellTitle(value);
                        return true;
                    }
                },
                new NewCommand("removeplayertag", rank, new CommandInput<String>(World::playerIsOnline, "Player", "An Online Player")) {
                    @Override
                    protected boolean execute(Player player, String[] input) {
                        final Player target = World.getPlayerByName(input[0].trim());
                        if (Rank.hasAbility(target, Rank.ADMINISTRATOR) && target != player) {
                            return true;
                        }
                        target.getYelling().setYellTitle("");
                        return true;
                    }
                }
        );
    }
    //</editor-fold>
}
