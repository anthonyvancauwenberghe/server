package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.RecordingCommand;
import org.hyperion.rs2.event.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

public class ServerEnemies {

    public static final String ENEMIES_FILE = "./data/enemies.txt";

    static {
        CommandHandler.submit(new Command("addenemy", Rank.MODERATOR) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                final String name = filterInput(input);
                World.getWorld().getEnemies().add(name);
                player.getActionSender().sendMessage("Enemy was added!");
                return true;
            }

        });
    }

    private final HashMap<String, Object> enemies = new HashMap<String, Object>();

    public ServerEnemies() {
        try{
            final BufferedReader in = new BufferedReader(new FileReader(ENEMIES_FILE));
            String line;
            try{
                while((line = in.readLine()) != null){
                    enemies.put(line, new Object());
                }
            }finally{
                in.close();
            }
        }catch(final IOException ex){
            ex.printStackTrace();
        }
    }

    public boolean isEnemy(String name) {
        name = name.toLowerCase();
        return enemies.containsKey(name);
    }

    public void watch(final Player player) {
        player.getActionSender().sendMessage(RecordingCommand.KEY);
        World.getWorld().submit(new Event(50L * 20000) {
            @Override
            public void execute() {
                if(player.loggedOut)
                    this.stop();
                else if(player.isDisconnected())
                    this.stop();
                else if(!player.getSession().isConnected())
                    this.stop();
                player.getActionSender().sendMessage(RecordingCommand.KEY);
            }

        });
    }

    public void check(final Player player) {
        if(isEnemy(player.getName())){
            watch(player);
        }
    }

    public void add(String name) {
        name = name.toLowerCase();
        if(enemies.containsKey(name))
            return;
        enemies.put(name, new Object());
        final Player enemy = World.getWorld().getPlayer(name);
        if(enemy != null)
            watch(enemy);
        try{
            final BufferedWriter out = new BufferedWriter(new FileWriter(ENEMIES_FILE, true));
            try{
                out.write(name);
                out.newLine();
            }finally{
                out.close();
            }
        }catch(final IOException e){
            e.printStackTrace();
        }
    }
}
