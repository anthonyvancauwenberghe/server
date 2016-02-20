package org.hyperion.rs2.model.punishment.cmd;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CheckPunishmentCommand extends Command {

    public CheckPunishmentCommand() {
        super("checkpunish", Rank.HELPER);
    }

    public boolean execute(final Player player, final String input) {
        final String targetName = filterInput(input);
        final Player target = World.getPlayerByName(targetName);
        final List<Punishment> punishments = new ArrayList<>();
        for(final PunishmentHolder h : PunishmentManager.getInstance().getHolders()){
            for(final Punishment p : h.getPunishments()){
                if(p.getTime().isExpired())
                    continue;
                final String ip = TextUtils.shortIp(CommandPacketHandler.findCharString(targetName, "IP"));
                final int uid = Integer.parseInt(CommandPacketHandler.findCharString(targetName, "Mac"));
                if(p.getVictimName().equalsIgnoreCase(targetName)){
                    punishments.add(p);
                    continue;
                }
                if(target != null) {
                    if(Objects.equals(target.getShortIP(), p.getVictimIp())
                            || target.getUID() == p.getVictimMac()
                            || Arrays.equals(target.specialUid, p.getVictimSpecialUid()))
                        punishments.add(p);
                } else {
                    if(Objects.equals(ip, p.getVictimIp()) || uid == p.getVictimMac())
                        punishments.add(p);
                }
            }
        }
        if(punishments.isEmpty()){
            player.sendf("%s is not punished", Misc.ucFirst(targetName.toLowerCase()));
            return false;
        }
        for(final Punishment p : punishments)
            p.send(player, false);
        return true;
    }
}