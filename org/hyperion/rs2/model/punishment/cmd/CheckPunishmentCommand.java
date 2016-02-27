package org.hyperion.rs2.model.punishment.cmd;

import com.google.gson.JsonElement;
import org.hyperion.Server;
import org.hyperion.engine.EngineTask;
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.saving.IOData;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class CheckPunishmentCommand extends NewCommand {

    public CheckPunishmentCommand(String key) {
        super(key, Rank.HELPER, Time.FIVE_SECONDS, new CommandInput<String>(PlayerLoading::playerExists, "player", "A player that exists in the system."));
    }

    public boolean execute(final Player player, final String[] input) {
        final String targetName = input[0];
        final Player target = World.getPlayerByName(targetName);
        final List<Punishment> punishments = new ArrayList<>();
        for(final PunishmentHolder h : PunishmentManager.getInstance().getHolders()){
            for(final Punishment p : h.getPunishments()){
                if(p.getTime().isExpired())
                    continue;
                final String ip = Server.getLoader().getEngine().submitIO(new EngineTask<String>("Loading IP for player " + targetName, 2, TimeUnit.SECONDS) {
                    @Override
                    public String call() throws Exception {
                        Optional<JsonElement> playerIp = PlayerLoading.getProperty(input[0], IOData.LAST_IP);
                        if(playerIp.isPresent())
                            return playerIp.get().getAsString();
                        return "";
                    }
                }).get();

                final int uid = Server.getLoader().getEngine().submitIO(new EngineTask<Integer>("Loading MAC for player " + targetName, 2, TimeUnit.SECONDS) {
                    @Override
                    public Integer call() throws Exception {
                        Optional<JsonElement> playerMac = PlayerLoading.getProperty(input[0], IOData.LAST_MAC);
                        if(playerMac.isPresent())
                            return playerMac.get().getAsInt();
                        return -1;
                    }
                }).get();

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
            return true;
        }
        for(final Punishment p : punishments)
            p.send(player, false);
        return true;
    }
}