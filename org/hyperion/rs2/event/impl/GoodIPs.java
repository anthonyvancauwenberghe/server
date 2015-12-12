package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.util.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 5/1/15
 * Time: 3:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class GoodIPs extends Event {

    public static final List<String> GOODS = new ArrayList<>();
    private static final File FILE = new File("./data/goodips.txt");


    static {
        try(final BufferedReader reader = new BufferedReader(new FileReader(FILE))){
            for(String s = ""; (s = reader.readLine()) != null; ){
                if(!GOODS.contains(s))
                    GOODS.add(s);
            }
        }catch(final Exception e){

        }
    }

    public GoodIPs() {
        super(Time.FIVE_MINUTES);
    }


    public void execute() {
        try(final BufferedReader reader = new BufferedReader(new FileReader(FILE))){
            for(String s = ""; (s = reader.readLine()) != null; ){
                if(!GOODS.contains(s))
                    GOODS.add(s);
            }
        }catch(final Exception e){

        }
    }
}
