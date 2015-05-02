package org.hyperion.rs2.model.possiblehacks;

import org.hyperion.util.login.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 12/10/14
 * Time: 4:50 PM
 * To change this template use File | Settings | File Templates.
 */
public final class PossibleHacksHolder {

    public static void main(String[] args) {
        init();
    }

    public static final List<PossibleHack> list = new ArrayList<>();

    public static void init() {
        final long start = System.currentTimeMillis();
        final String file = "./data/possiblehacks.txt";
     //Player: Maul Votes Old IP: /188.33.54.251 New IP: /31.174.229.225 Date: Mon Sep 22 15:23:43 PDT 2014
        //Player: Wiz101 Old password: \.l./ New password: 77251t By IP: 173.20.91.184 Date: Sat Nov 15 02:40:34 GMT+01:00 2014
        try {
            List<String> lines = Files.readAllLines(Paths.get(file));
            for(final String s : lines) {
                try {
                    final String name = StringUtils.substring(s, "Player:", "Old").trim();
                    final String date = StringUtils.substring(s, "Date:", "TO_THE_END").trim();
                    final String ip;
                    if(s.toLowerCase().contains("password")) {
                        ip = StringUtils.substring(s, "By IP:", "Date:").trim();
                        final String oldPassword = StringUtils.substring(s, "Old password:", "New").trim();
                        final String newPassword = StringUtils.substring(s, "New password:", "By IP").trim();
                        list.add(new PasswordChange(name, ip, date, oldPassword, newPassword));
                    } else {
                        ip = StringUtils.substring(s, "Old IP:", "New").trim();
                        final String newIp = StringUtils.substring(s, "New IP:", "Date:").trim();
                        list.add(new IPChange(name, ip, date, newIp));
                    }
                }catch(Exception e) {

                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finished loading possible hacks in: "+(System.currentTimeMillis()-start)+"ms");


    }

    public static List<PossibleHack> getHacks(final String name) {
        final List<PossibleHack> hacks = new ArrayList<>();
        for(final PossibleHack hack : list) {
            if(hack != null)
                if(hack.name.equalsIgnoreCase(name))
                    hacks.add(hack);
        }
        return hacks;
    }

    public static synchronized void add(final PossibleHack hack) {
        list.add(hack);
    }

}
