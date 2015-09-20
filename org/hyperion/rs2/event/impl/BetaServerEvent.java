package org.hyperion.rs2.event.impl;

import org.hyperion.Server;
import org.hyperion.rs2.event.Event;
import org.hyperion.util.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilles on 20/09/2015.
 */
public class BetaServerEvent extends Event {

    public static final List<String> whitelist = new ArrayList();
    public static final List<String> changes = new ArrayList();
    public static final List<String> toTest = new ArrayList();
    private static final File WHITELIST = new File("./data/beta/whitelist.txt");
    private static final File CHANGELOG = new File("./data/beta/changelog.txt");
    private static final File TOTEST = new File("./data/beta/totest.txt");


    static {
        if(Server.NAME.equalsIgnoreCase("ArteroBeta")) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(WHITELIST));
                for (String s = ""; (s = reader.readLine()) != null; ) {
                    if (!whitelist.contains(s))
                        whitelist.add(s);
                }
                reader = new BufferedReader(new FileReader(CHANGELOG));
                for (String s = ""; (s = reader.readLine()) != null; ) {
                    if (!changes.contains(s))
                        changes.add(s);
                }
                reader = new BufferedReader(new FileReader(TOTEST));
                for (String s = ""; (s = reader.readLine()) != null; ) {
                    if (!toTest.contains(s))
                        toTest.add(s);
                }
            } catch (Exception e) {

            }
        }
    }

    public BetaServerEvent(){
        super(Time.FIVE_MINUTES);
    }


    public void execute() {
        if(Server.NAME.equalsIgnoreCase("ArteroBeta")) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(WHITELIST));
                for (String s = ""; (s = reader.readLine()) != null; ) {
                    if (!whitelist.contains(s))
                        whitelist.add(s);
                }
            } catch (Exception e) {}
        }
    }
}
