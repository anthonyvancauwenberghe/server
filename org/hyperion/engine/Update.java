package org.hyperion.engine;

import org.hyperion.Server;
import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.skill.dungoneering.Dungeon;
import org.hyperion.rs2.savingnew.PlayerSaving;
import org.hyperion.util.Time;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

/**
 * Created by Gilles on 11/02/2016.
 */
public class Update extends Task {

    private int updateTimer;
    private String reason;

    public Update(int updateTimer, String reason) {
        super(Time.ONE_SECOND, "update task");
        this.updateTimer = updateTimer;
        this.reason = reason;
        World.getPlayers().stream().filter(player -> player != null).forEach(player -> player.getActionSender().sendUpdate(updateTimer));
    }

    @Override
    protected void execute() {
        try {
            if (!Server.isUpdating() || updateTimer < 0) {
                Thread.interrupted();
                return;
            }
            if ((updateTimer % 5 == 0 || updateTimer <= 10) && updateTimer != 0)
                System.out.println("Time left before update: " + updateTimer);
            updateTimer--;
            if (updateTimer == 0) {
                World.getPlayers().forEach(Trade::declineTrade);
                Dungeon.activeDungeons.forEach(Dungeon::complete);
                World.getPlayers().stream().filter(player -> player != null).forEach(PlayerSaving::save);
                ClanManager.save();
                Server.getLogger().info("Update task finished! Reason for update: " + reason);
                try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./data/key.dat"))) {
                    out.writeObject(Server.getCharFileEncryption().getKey());
                }
                stop();
                Runtime.getRuntime().exec("cmd /c start run.bat");
                System.exit(1);
            }
        } catch (Exception e) {
            Server.getLogger().log(Level.SEVERE, "Exception during updating", e);
        }
    }
}
