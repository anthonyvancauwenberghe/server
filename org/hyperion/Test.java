package org.hyperion;

import java.io.BufferedReader;
import java.io.FileReader;

public class Test {
    public static void main(final String[] args) {
        try{
            final BufferedReader r = new BufferedReader(new FileReader("./data/capitals.txt"));
            String s;
            while((s = r.readLine()) != null){
                final int index = s.indexOf("- ");
                String country = s.substring(0, index);
                country = country.replaceAll(" ", "");
                final String capital = s.substring(index + 2).toLowerCase();
                System.out.println("<question>What is the capital of " + country + "?");
                System.out.println("<answer>" + capital);
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
    }
}
