package org.hyperion.rs2.model.punishment.cmd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;

public class CheckPunishmentCommand extends Command {

    public CheckPunishmentCommand(){
        super("checkpunish", Rank.HELPER);
    }

    public boolean execute(final Player player, final String input){
        final String targetName = filterInput(input);
        final Player target = World.getWorld().getPlayer(targetName);
        final List<Punishment> punishments = new ArrayList<>();
        for(final PunishmentHolder h : PunishmentManager.getInstance().getHolders()){
            for(final Punishment p : h.getPunishments()){
                if(p.getVictimName().equalsIgnoreCase(targetName)){
                    punishments.add(p);
                    continue;
                }
                if(target == null)
                    continue;
                if(Objects.equals(target.getShortIP(), p.getVictimIp()) || target.getUID() == p.getVictimMac())
                    punishments.add(p);
            }
        }
        if(punishments.isEmpty()){
            player.sendf("%s is not punished", targetName);
            return false;
        }
        for(final Punishment p : punishments)
            p.send(player, false);
        return true;
    }
}
