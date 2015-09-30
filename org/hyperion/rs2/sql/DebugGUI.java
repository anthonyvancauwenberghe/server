package org.hyperion.rs2.sql;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.net.LoginDebugger;

public class DebugGUI extends JFrame implements ActionListener {

    private JLabel status1 = new JLabel("Loading...");
    private JLabel status2 = new JLabel("Loading...");
    private JLabel status3 = new JLabel("Loading...");

    private JLabel lastQuery = new JLabel("Last query...");

    private JLabel lastStart = new JLabel("Last start...");

    private JLabel delta = new JLabel("Last delta...");

    private JLabel playersQueue = new JLabel("Players Queue...");
    private JLabel loadingQueue = new JLabel("Loading Queue...");
    private JLabel importantQueue = new JLabel("Important Queue...");
    private JLabel logsQueue = new JLabel("Logs Queue...");

    private JButton btnDumpLogins = new JButton("Dump logins");

    private JButton btnDumpThreads = new JButton("Dump threads");

    private boolean show = false;

    public void setShow(boolean b) {
        show = b;
    }

    public DebugGUI() {
        this.setMinimumSize(new Dimension(600,250));
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

    public void setStatus(String status) {
        //System.out.println("Updating status to: " + status);
        if(show) {
            String text3 = status3.getText();
            String text2 = status2.getText();
            status1.setText(text2);
            status2.setText(text3);
            status3.setText(status);
        }
    }

    public void setLastQuery(String query) {
        if(show)
            this.lastQuery.setText("Last query: " + query);
    }

    public void setStart(long start) {
        if(show)
            this.lastStart.setText("Start: " + start);
    }

    public void setDelta(long delta) {
        if(delta < 300)
            return;
        if(show)
            this.delta.setText("Delta: " + delta);
    }


    public void updateQueueSizes() {
        World world = World.getWorld();
        this.logsQueue.setText("Logs queue: " + World.getWorld().getLogsConnection().getQueueSize() + "  -- " + world.getLogsConnection().getLastQueryString());
        this.playersQueue.setText("Players Queue: " + World.getWorld().getPlayersConnection().getQueueSize() + " -- " + world.getPlayersConnection().getLastQueryString());
        this.importantQueue.setText("Important Queue: "  + World.getWorld().getImportantConnection().getQueueSize() + " -- " + world.getImportantConnection().getLastQueryString());
        this.loadingQueue.setText("Loading Queue: " + World.getWorld().getLoadingConnection().getQueueSize() + " -- " + world.getLoadingConnection().getLastQueryString());
    }



    static {
        CommandHandler.submit(new Command("showgui", Rank.ADMINISTRATOR) {

            @Override
            public boolean execute(Player player, String input)
                    throws Exception {
                World.getWorld().getGUI().setShow(true);
                return false;
            }

        });
        CommandHandler.submit(new Command("hidegui", Rank.ADMINISTRATOR) {

            @Override
            public boolean execute(Player player, String input)
                    throws Exception {
                World.getWorld().getGUI().setShow(false);
                return false;
            }

        });
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        //LoginDebugger.getLoadDebugger().dumpLogs();
        try {
            System.out.println(e.getActionCommand());
            if(e.getActionCommand().equals("threads")) {
                Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
                BufferedWriter out = new BufferedWriter(new FileWriter("./data/threaddump.log",true));
                for(Map.Entry<Thread, StackTraceElement[]> entry: allStackTraces.entrySet()) {
                    Thread thread = entry.getKey();
                    StackTraceElement[] traces = entry.getValue();
                    String name = thread.getName().toLowerCase();
                    if(name.contains("sql") || name.contains("singlethreadexecutor")) {
                        out.write("Thread: " + thread.getName());
                        out.newLine();
                        for(StackTraceElement trace: traces) {
                            out.write(trace.toString());
                            out.newLine();
                        }
                        out.write("==============================");
                        out.newLine();
                    }
                }
                out.close();
                System.out.println("Dumped thread stack!");
            } else if(e.getActionCommand().equals("logins")) {
               // LoginDebugger.getLoadDebugger().dumpLogs();
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }


}