package org.hyperion.rs2.event.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

public class ServerMessages extends Event {

    private static final File FILE = new File("./data/server_messages.txt");

    private static final List<String> MESSAGES = new ArrayList<>();

    private static int currentIndex;

    static{
        load();

        CommandHandler.submit(new Command("reloadmessages", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input) {
                player.getActionSender().sendMessage("Reloading server messages...");
                MESSAGES.clear();
                if(!load()){
                    player.getActionSender().sendMessage("Error reloading messages");
                    return false;
                }
                player.getActionSender().sendMessage("Successfully reloaded messages");
                return true;
            }
        });
        CommandHandler.submit(new Command("savemessages", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                player.getActionSender().sendMessage("Saving server messages...");
                if(!save()){
                    player.getActionSender().sendMessage("Error saving messages");
                    return false;
                }
                player.getActionSender().sendMessage("Successfully saved messages");
                return true;
            }
        });
        CommandHandler.submit(new Command("removemessage", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
               try{
                   final int index = Integer.parseInt(input.split(" +")[1].trim());
                   if(index < 0 || index > size()-1){
                       player.getActionSender().sendMessage(String.format("Index out of bounds: [0, %d]", size()-1));
                       return false;
                   }
                   final String message = get(index);
                   if(!remove(message)){
                       player.getActionSender().sendMessage("Error removing message at index: " + index);
                       return false;
                   }
                   player.getActionSender().sendMessage(String.format("[Removed Message] Index %d: %s", index, message));
                   return true;
               }catch(Exception ex){
                   player.getActionSender().sendMessage("Syntax: ::removemessage index");
                   return false;
               }
            }
        });
        CommandHandler.submit(new Command("listmessages", Rank.ADMINISTRATOR){
            public boolean execute(final Player player, final String input){
                player.getActionSender().sendMessage(String.format("@blu@%d@bla@ Server Messages", size()));
                for(int i = 0; i < size(); i++)
                    player.getActionSender().sendMessage(String.format("[Index @blu@%s@bla@] @whi@%s", i, get(i)));
                return true;
            }
        });
    }
	
	public ServerMessages() {
		super(250000);
	}

    public static boolean load(){
        if(!FILE.exists())
            return false;
        Scanner input = null;
        try{
            input = new Scanner(FILE, "UTF-8");
            while(input.hasNextLine()){
                final String line = input.nextLine().trim();
                if(line.isEmpty())
                    continue;
                MESSAGES.add(line);
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally{
            if(input != null)
                input.close();
        }
    }

    public static boolean save(){
        BufferedWriter writer = null;
        try{
            if(!FILE.exists())
                FILE.createNewFile();
            writer = new BufferedWriter(new FileWriter(FILE));
            for(final String message : MESSAGES){
                writer.write(message);
                writer.newLine();
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }finally{
            if(writer != null){
                try{
                    writer.flush();
                    writer.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        }
    }
    
    public static int size(){
        return MESSAGES.size();
    }

    public static int indexOf(final String message){
        for(int i = 0; i < size(); i++)
            if(MESSAGES.get(i).equalsIgnoreCase(message))
                return i;
        return -1;
    }

    public static boolean contains(final String message){
        return indexOf(message) != -1;
    }

    public static List<String> get(){
        return MESSAGES;
    }
    
    public static String get(final int i){
        return MESSAGES.get(i);
    }

    public static boolean remove(final String message){
        return MESSAGES.remove(message) && save();
    }

    public static boolean add(final String message){
        return MESSAGES.add(message) && save();
    }
	
	public void execute() {
        if(MESSAGES.isEmpty())
            return;
		try {
			if(currentIndex == size())
                currentIndex = 0;
			final String message = MESSAGES.get(currentIndex++);
			for(Player p : World.getWorld().getPlayers())
				if(p != null)
                    if(!p.getName().equalsIgnoreCase("Ferry"))
					    p.sendServerMessage(message);
		} catch(final Exception e) {
			e.printStackTrace();
			this.stop();
		}
	}
	
	
}
