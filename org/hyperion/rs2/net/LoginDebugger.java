package org.hyperion.rs2.net;

import debug.Debugger;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.util.Time;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class LoginDebugger extends Debugger {

    public static final File LOG_FILE = new File("./logs/logindump.log");

    public static final int MAX_LOGS_SIZE = 2000;

    public static final boolean DEFAULT_ENABLED = Boolean.FALSE;
    private static final Debugger singleton = new LoginDebugger(LOG_FILE);

    static {
        CommandHandler.submit(new Command("sendloginlogs", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) {
                int counter = 0;
                for(final String log : LoginDebugger.getDebugger().getLogs()){
                    if(counter++ > 100){
                        break;
                    }
                    player.getActionSender().sendMessage(log);
                }
                return true;
            }
        });
        CommandHandler.submit(new Command("enablelogindebugger", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) {
                getDebugger().setEnabled(true);
                player.getActionSender().sendMessage("Enabled");
                return true;
            }
        });
        CommandHandler.submit(new Command("disablelogindebugger", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) {
                getDebugger().setEnabled(false);
                player.getActionSender().sendMessage("Disabled");
                return true;
            }
        });
        CommandHandler.submit(new Command("dumploginlogs", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) {
                final boolean succesful = LoginDebugger.getDebugger().dumpLogs();
                if(succesful)
                    player.getActionSender().sendMessage("Succesfully dumped");
                else
                    player.getActionSender().sendMessage("Was not able to dump..");
                return true;
            }
        });

    }

    private final List<String> logs = new LinkedList<String>();

    public LoginDebugger(final File logFile) {
        super(logFile);
        setEnabled(DEFAULT_ENABLED);
    }

    public static Debugger getDebugger() {
        return singleton;
    }

    public void log(final String message) {
        //System.out.println(message);
        if(!isEnabled())
            return;
        if(message == null){
            System.out.println("Null being added into log: " + message);
            return;
        }
        if(message.toLowerCase().contains("graham"))
            System.out.println(message);
        final String line = Time.getGMTDate() + "\t" + message;
        synchronized(logs){
            if(logs.size() >= MAX_LOGS_SIZE){
                logs.remove(0);
            }
            logs.add(line);
        }
    }

    public List<String> getLogs() {
        System.out.println("Getting logs..");
        int counter = 0;
        for(final String log : logs){
            if(log != null)
                counter++;
        }
        System.out.println(counter + " logs collected!");
        return logs;
    }

}
