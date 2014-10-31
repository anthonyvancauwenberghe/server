package org.hyperion.rs2.model.punishment.cmd;

import java.util.ArrayList;
import java.util.List;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;

public class RemovePunishmentCommand extends Command {

    private static final String IP_REGEX = "\\d+\\.\\d+\\.\\d+\\.\\d+";

    public RemovePunishmentCommand(){
        super("removepunishment", Rank.ADMINISTRATOR);
    }

    public boolean execute(final Player player, final String input){
        final String target = filterInput(input).trim();
        final List<Punishment> punishments = new ArrayList<>();
        if(target.matches(IP_REGEX)){
            punishments.addAll(PunishmentManager.getInstance().getByIp(target));
        }else{
            final PunishmentHolder holder = PunishmentManager.getInstance().get(target);
            if(holder != null)
                punishments.addAll(holder.getPunishments());
        }
        if(punishments.isEmpty()){
            player.sendf("No punishments found for: %s", target);
            return false;
        }
        for(final Punishment p : punishments){
            p.getTime().setExpired(true);
            if(p.unapply())
                p.send(p.getVictim(), false);
            p.send(player, false);
            p.setActive(false);
            p.getHolder().remove(p);
        }
        return true;
    }
}
