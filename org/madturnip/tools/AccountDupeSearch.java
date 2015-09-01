package org.madturnip.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Allen Kinzalow on 4/11/2015.
 */
public class AccountDupeSearch {

    public static void main(String[] args) {
        try {

            /**
             * Map of all players & created long
             */
            HashMap<String,String> players = new HashMap<String,String>();

            /**
             * Map of found players to prevent duplicate prints
             */
            HashMap<String,ArrayList<String>> foundPlayers = new HashMap<String,ArrayList<String>>();
            /**
             * Load all longs, pair with username.
             */
            File dir = new File("./data/characters/mergedchars");
            for(File characterFile : dir.listFiles()) {
                if(characterFile.isDirectory())
                    continue;
                BufferedReader in = new BufferedReader(new FileReader(characterFile));
                String line;
                String name = "", createdLong = "";
                while((line = in.readLine()) != null) {
                    if(line.length() <= 1)
                        continue;
                    String[] keyValues = line.split("=");
                    if(keyValues == null)
                        continue;
                    if(keyValues[0].startsWith("Name"))
                        name = keyValues[1];
                    if(keyValues[0].startsWith("CreatedLong"))
                        createdLong = keyValues[1];
                    if(!name.equals("") && !createdLong.equals("")) {
                        players.put(name, createdLong);
                        break;
                    }
                }
            }

            /**
             * Compare createdlong's between players.
             */
            for(String key : players.keySet()) {
                String createdLong = players.get(key);
                for(String comparedKey : players.keySet()) {
                    if(comparedKey.equals(key))
                        continue;
                    String comparedCreatedLong = players.get(comparedKey);
                    if(createdLong.equalsIgnoreCase(comparedCreatedLong)) {
                        if((foundPlayers.containsKey(key) && foundPlayers.get(key).contains(comparedKey)) || (foundPlayers.containsKey(comparedKey) && foundPlayers.get(comparedKey).contains(key)))
                            continue;
                        ArrayList<String> keys = foundPlayers.get(key);
                        if(keys == null)
                            keys = new ArrayList<String>();
                        keys.add(comparedKey);
                        ArrayList compareds = foundPlayers.get(comparedKey);
                        if(compareds == null)
                            compareds = new ArrayList<String>();
                        compareds.add(key);
                        foundPlayers.put(key,keys);
                        foundPlayers.put(comparedKey,compareds);
                        System.out.println(key + " = " + comparedKey + " ; " + createdLong + " = " + comparedCreatedLong);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
