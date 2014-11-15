package org.hyperion.rs2.model.punishment.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.util.PushMessage;

public class UnPunishCommand extends Command {

    private final Combination combination;

    public UnPunishCommand(final String name, final Combination combination, final Rank rank){
        super(name, rank);
        this.combination = combination;
    }

    public UnPunishCommand(final String name, final Target target, final Type type, final Rank rank){
        this(name, Combination.of(target, type), rank);
    }

    public boolean execute(final Player player, final String input){
        final String victim = filterInput(input);
        final PunishmentHolder holder = PunishmentManager.getInstance().get(victim);
        if(holder == null){
            player.sendf("%s isn't punished", victim);
            return false;
        }
        final Punishment punishment = holder.get(combination);
        if(punishment == null){
            player.sendf("No %s found for %s", combination, victim);
            return false;
        }
        punishment.getTime().setExpired(true);
        if(punishment.unapply())
            punishment.send(punishment.getVictim(), true);
        punishment.send(player, true);
        punishment.getHolder().remove(punishment);
        punishment.setActive(false);
        return false;
    }
}
