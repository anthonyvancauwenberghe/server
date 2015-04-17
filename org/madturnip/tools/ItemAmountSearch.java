package org.madturnip.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Allen Kinzalow on 4/16/2015.
 */
public class ItemAmountSearch {

    public static void main(String[] args) {
        if(args.length < 2) {
            System.out.println("Used args: 'itemid amount' ex: '50 100000'");
            return;
        } else {
            try {
                int itemID = Integer.valueOf(args[0]);
                int itemAmount = Integer.valueOf(args[1]);
                File dir = new File("./data/characters");
                for(File characterFile : dir.listFiles()) {
                    if(characterFile.isDirectory())
                        continue;
                    BufferedReader in = new BufferedReader(new FileReader(characterFile));
                    String line;
                    String name = characterFile.getName();
                    String current = "";
                    while((line = in.readLine()) != null) {
                        if(line.length() <= 1) {
                            current = "";
                            continue;
                        }
                        if(current.equalsIgnoreCase("")) {
                            if(line.equalsIgnoreCase("Inventory") || line.equalsIgnoreCase("Bank") || line.equalsIgnoreCase("Equip")) {
                                current = line;
                                continue;
                            }
                        } else {
                            String[] items = line.split(" ");
                            if(items == null || items.length == 0) {
                                current = "";
                                continue;
                            }
                            int id = Integer.valueOf(items[0]);
                            int amount = Integer.valueOf(items[1]);
                            if(id == itemID && amount >= itemAmount)
                                System.out.println("[" + name + "] " + current + " Item: " + id + " Amount: " + amount);
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

}
