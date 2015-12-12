package org.hyperion.rs2.net;

import java.io.BufferedReader;
import java.io.FileReader;

public class LoginDebugAnalyzer {

    private int counter = 0;

    private String lastName = null;

    private LoginDebugAnalyzer() {
        try{
            final BufferedReader in = new BufferedReader(new FileReader("C:/Users/SaosinHax/Dropbox/Reckless/logindebug.log"));
            String line;
            while((line = in.readLine()) != null){
                if(line.contains("Login Result")){
                    if(line.contains("unset"))
                        continue;
                    final String name = findLoginResultName(line);
                    //System.out.println(name);
                    if(name.equals(lastName))
                        counter++;
                    else
                        counter = 0;
                    if(counter >= 1)
                        System.out.println(line);
                    lastName = name;
                }
            }
            in.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(final String[] args) {
        new LoginDebugAnalyzer();
    }

    public static void printRectangle(final int h, final int l) {
        String blancoLine = "";
        for(int i = 0; i < l - 2; i++){
            blancoLine += " ";
        }
        for(int i = 0; i < l; i++){
            System.out.print("*");
        }
        for(int i = 0; i < h - 2; i++){
            //System.out.println();
            //System.out.print("*" + blancoLine + "*");
        }
        System.out.println();
        for(int i = 0; i < l; i++){
            //System.out.print("*");
        }
    }

    private String findLoginResultName(final String line) {
        final String name = line.split(":")[4].trim();
        return name.toLowerCase();
    }

}
