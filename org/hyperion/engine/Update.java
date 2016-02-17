package org.hyperion.engine;

import org.hyperion.Server;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.skill.dungoneering.Dungeon;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;

/**
 * Created by Gilles on 11/02/2016.
 */
public class Update implements Runnable {

    private int updateTimer;
    private String reason;

    public Update(int updateTimer, String reason) {
        this.updateTimer = updateTimer;
        this.reason = reason;
        World.getPlayers().stream().filter(player -> player != null).forEach(player -> player.getActionSender().sendUpdate(updateTimer));
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (!Server.isUpdating() || updateTimer < 0) {
                    Thread.interrupted();
                    return;
                }
                if ((updateTimer % 5 == 0 || updateTimer <= 10) && updateTimer != 0)
                    System.out.println("Time left before update: " + updateTimer);
                updateTimer--;
                if (updateTimer == 0) {
                    World.getPlayers().stream().filter(player -> player != null).forEach(World::unregister);
                    World.getPlayers().forEach(Trade::declineTrade);
                    Dungeon.activeDungeons.forEach(Dungeon::complete);
                    ClanManager.save();
                    Server.getLogger().info("Update task finished! Reason for update: " + reason);
                    try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("./data/key.dat"))) {
                        out.writeObject(Server.getCharFileEncryption().getKey());
                    }
                    Runtime.getRuntime().exec("cmd /c start run.bat");
                    System.exit(1);
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                Server.getLogger().log(Level.SEVERE, "Exception during updating", e);
            }
        }
    }
}
