package org.hyperion.rs2.sql;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

public class DebugGUI extends JFrame implements ActionListener {

    static {
        CommandHandler.submit(new Command("showgui", Rank.ADMINISTRATOR) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                World.getWorld().getGUI().setShow(true);
                return false;
            }

        });
        CommandHandler.submit(new Command("hidegui", Rank.ADMINISTRATOR) {

            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                World.getWorld().getGUI().setShow(false);
                return false;
            }

        });
    }

    private final JLabel status1 = new JLabel("Loading...");
    private final JLabel status2 = new JLabel("Loading...");
    private final JLabel status3 = new JLabel("Loading...");
    private final JLabel lastQuery = new JLabel("Last query...");
    private final JLabel lastStart = new JLabel("Last start...");
    private final JLabel delta = new JLabel("Last delta...");
    private final JLabel playersQueue = new JLabel("Players Queue...");
    private final JLabel loadingQueue = new JLabel("Loading Queue...");
    private final JLabel importantQueue = new JLabel("Important Queue...");
    private final JLabel logsQueue = new JLabel("Logs Queue...");
    private final JButton btnDumpLogins = new JButton("Dump logins");
    private final JButton btnDumpThreads = new JButton("Dump threads");
    private boolean show = false;

    public DebugGUI() {
        this.setMinimumSize(new Dimension(600, 250));
        this.add(status1);
        this.add(status2);
        this.add(status3);
        this.add(lastQuery);
        this.add(lastStart);
        this.add(delta);
        this.add(playersQueue);
        this.add(loadingQueue);
        this.add(importantQueue);
        this.add(logsQueue);
        this.add(btnDumpLogins);
        this.add(btnDumpThreads);
        btnDumpLogins.addActionListener(this);
        btnDumpLogins.setActionCommand("logins");
        btnDumpThreads.addActionListener(this);
        btnDumpThreads.setActionCommand("threads");
        this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public void setShow(final boolean b) {
        show = b;
    }

    public void setStatus(final String status) {
        //System.out.println("Updating status to: " + status);
        if(show){
            final String text3 = status3.getText();
            final String text2 = status2.getText();
            status1.setText(text2);
            status2.setText(text3);
            status3.setText(status);
        }
    }

    public void setLastQuery(final String query) {
        if(show)
            this.lastQuery.setText("Last query: " + query);
    }

    public void setStart(final long start) {
        if(show)
            this.lastStart.setText("Start: " + start);
    }

    public void setDelta(final long delta) {
        if(delta < 300)
            return;
        if(show)
            this.delta.setText("Delta: " + delta);
    }

    public void updateQueueSizes() {
        final World world = World.getWorld();
        this.logsQueue.setText("Logs queue: " + World.getWorld().getLogsConnection().getQueueSize() + "  -- " + world.getLogsConnection().getLastQueryString());
        this.playersQueue.setText("Players Queue: " + World.getWorld().getPlayersConnection().getQueueSize() + " -- " + world.getPlayersConnection().getLastQueryString());
        this.importantQueue.setText("Important Queue: " + World.getWorld().getImportantConnection().getQueueSize() + " -- " + world.getImportantConnection().getLastQueryString());
        this.loadingQueue.setText("Loading Queue: " + World.getWorld().getLoadingConnection().getQueueSize() + " -- " + world.getLoadingConnection().getLastQueryString());
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        //LoginDebugger.getLoadDebugger().dumpLogs();
        try{
            System.out.println(e.getActionCommand());
            if(e.getActionCommand().equals("threads")){
                final Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
                final BufferedWriter out = new BufferedWriter(new FileWriter("./data/threaddump.log", true));
                for(final Map.Entry<Thread, StackTraceElement[]> entry : allStackTraces.entrySet()){
                    final Thread thread = entry.getKey();
                    final StackTraceElement[] traces = entry.getValue();
                    final String name = thread.getName().toLowerCase();
                    if(name.contains("sql") || name.contains("singlethreadexecutor")){
                        out.write("Thread: " + thread.getName());
                        out.newLine();
                        for(final StackTraceElement trace : traces){
                            out.write(trace.toString());
                            out.newLine();
                        }
                        out.write("==============================");
                        out.newLine();
                    }
                }
                out.close();
                System.out.println("Dumped thread stack!");
            }else if(e.getActionCommand().equals("logins")){
                // LoginDebugger.getLoadDebugger().dumpLogs();
            }
        }catch(final Exception ex){
            ex.printStackTrace();
        }
    }


}