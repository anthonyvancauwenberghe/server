package org.hyperion.rs2.model.punishment.cmd;

import java.util.concurrent.TimeUnit;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Time;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.util.PlayerFiles;

public class PunishCommand extends Command{

    private final Combination combination;

    public PunishCommand(final String name, final Combination combination, final Rank rank){
        super(name, rank);
        this.combination = combination;
    }

    public PunishCommand(final String name, final Target target, final Type type, final Rank rank){
        this(name, Combination.of(target, type), rank);
    }

    public boolean execute(final Player player, final String input){
        final String[] parts = filterInput(input).split(",");
        if(parts.length != 3){
            player.sendf("Incorrect syntax. Usage: ::%s name,duration [minutes|hours|days],reason", getKey());
            return false;
        }
        final String victimName = parts[0].trim();
        final Player victim = World.getWorld().getPlayer(victimName);
        if(victimName.isEmpty() || (!PlayerFiles.exists(victimName) && victim == null)){
            player.sendf("Unable to find player: %s", victimName);
            return false;
        }
        if(victim != null && Rank.isStaffMember(victim)){
            player.sendf("You cannot punish other staff members");
            return false;
        }
        String ip = victim != null ? victim.getShortIP() : CommandPacketHandler.findCharString(victimName, "IP");
        if(ip.contains("="))//
            ip = ip.substring(ip.indexOf('/')+1, ip.indexOf(':'));//
        String macStr = victim != null ? Integer.toString(victim.getUID()) : CommandPacketHandler.findCharString(victimName, "Mac");
        if(macStr.contains("="))//
            macStr = macStr.substring(macStr.indexOf('=')+1).trim();//
        int mac;
        try{
            mac = Integer.parseInt(macStr);
        }catch(Exception ex){
            player.sendf("Unable to punish %s: No mac address found", victimName);
            return false;
        }
        final String[] durationParts = parts[1].split(" +");
        TimeUnit unit = TimeUnit.HOURS;
        long duration;
        try{
            duration = Long.parseLong(durationParts[0].trim());
            if(durationParts.length == 2){
                final String unitStr = durationParts[1].trim();
                if(unitStr.contains("minute"))
                    unit = TimeUnit.MINUTES;
                else if(unitStr.contains("hour"))
                    unit = TimeUnit.HOURS;
                else if(unitStr.contains("day"))
                    unit = TimeUnit.DAYS;
                else if(unitStr.contains("week")){
                    unit = TimeUnit.DAYS;
                    duration *= 7;
                }else if(unitStr.contains("month")){
                    unit = TimeUnit.DAYS;
                    duration *= 30;
                }else if(unitStr.contains("year")){
                    unit = TimeUnit.DAYS;
                    duration *= 365;
                }else if(unitStr.contains("decade")){
                    unit = TimeUnit.DAYS;
                    duration *= 3652;
                }else if(unitStr.contains("century")){
                    unit = TimeUnit.DAYS;
                    duration *= 36524;
                }
            }
        }catch(Exception ex){
            player.sendf("Error parsing duration. Syntax: duration [minute(s)|hour(s)|day(s)]");
            return false;
        }
        final Time time = Time.create(duration, unit);
        final String reason = parts[2].trim();
        if(reason.isEmpty()){
            player.sendf("Supply a reason for your doing");
            return false;
        }
        final PunishmentHolder holder = PunishmentManager.getInstance().get(victimName);
        final Punishment old = holder == null ? null : holder.get(combination);
        if(holder != null && old != null){
            old.setIssuer(player);
            old.getTime().setStartTime(System.currentTimeMillis());
            old.getTime().set(time);
            old.setReason(reason);
            if(victim != null)
                old.send(victim, true);
            old.send(player, true);
            old.update();
        }else{
            final Punishment punishment = Punishment.create(player, victimName, ip, mac, combination, time, reason);
            if(victim != null)
                punishment.send(victim, true);
            punishment.send(player, true);
            punishment.apply();
            PunishmentManager.getInstance().add(punishment);
            punishment.insert();
        }
        return true;
    }
}
