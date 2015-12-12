package org.hyperion.rs2.saving;

import org.hyperion.rs2.GenericWorldLoader;
import org.hyperion.rs2.model.Password;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.saving.instant.InstantPlayerSaving;
import org.hyperion.util.Time;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MergedSaving {

    public static final String INSTANT_DIR = "./data/characters/instantchars/";
    public static final String ARTERO_DIR = "./data/characters/arterochars/";
    public static final String MERGED_DIR = "./data/characters/mergedchars/";

    public static final String BACKUP_DIR = "C:/Users/Administrator/Desktop/TEST/artero/";

    public static void load(final Player player) {
        final int source = player.getSource();
        if(source == GenericWorldLoader.ARTERO){
            PlayerSaving.getSaving().load(player, ARTERO_DIR);
        }else if(source == GenericWorldLoader.INSTANT){
            InstantPlayerSaving.getSaving().load(player);
        }else{
            PlayerSaving.getSaving().load(player, MERGED_DIR);
        }
    }

    public static boolean cleanArteroFile(String name) {
        name = name.toLowerCase();
        final File file = new File(ARTERO_DIR + name + ".txt");
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static boolean cleanInstantFile(String name) {
        name = name.toLowerCase();
        final File file = new File(INSTANT_DIR + name + ".txt");
        if(file.exists()){
            return file.delete();
        }
        return false;
    }

    public static void save(final Player player) {
        //System.out.println("Trying to save: " + player.doubleChar());
        PlayerSaving.getSaving().save(player);
    }

    public static boolean exists(final String name) {
        return existsMain(name) || existsArtero(name) || existsInstant(name);
    }

    public static Password getInstantPass(String playername) {
        final Password password = new Password();
        playername = playername.toLowerCase();
        if(existsInstant(playername)){
            try{
                final BufferedReader br = new BufferedReader(new FileReader(INSTANT_DIR + playername + ".txt"));
                String line = br.readLine();
                boolean passfound = false;
                while((line = br.readLine()) != null){
                    if(line.startsWith("Pass=")){
                        line = line.replace("Pass=", "");
                        password.setEncryptedPass(line);
                        password.setRealPassword(line);
                        passfound = true;
                    }else if(line.startsWith("Salt=")){
                        line = line.replace("Salt=", "");
                        password.setSalt(line);
                        if(passfound){
                            break;
                        }
                    }
                }
                br.close();
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
        return password;
    }

    public static Password getMainPass(String name) {
        final Password password = new Password();
        name = name.toLowerCase();
        if(existsMain(name)){
            try{
                final BufferedReader br = new BufferedReader(new FileReader(MERGED_DIR + name + ".txt"));
                String line = br.readLine();
                boolean passfound = false;
                while((line = br.readLine()) != null){
                    if(line.startsWith("Pass=")){
                        line = line.replace("Pass=", "");
                        password.setRealPassword(line);
                        passfound = true;
                    }else if(line.startsWith("Salt=")){
                        line = line.replace("Salt=", "");
                        password.setSalt(line);
                        if(passfound){
                            break;
                        }
                    }
                }
                br.close();
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
        return password;
    }

    public static Password getBackupPass(String name) {
        final Password password = new Password();
        name = name.toLowerCase();
        if(existsBackup(name)){
            try{
                final BufferedReader br = new BufferedReader(new FileReader(BACKUP_DIR + name + ".txt"));
                String line = br.readLine();
                boolean passfound = false;
                while((line = br.readLine()) != null){
                    if(line.startsWith("Pass=")){
                        line = line.replace("Pass=", "");
                        password.setRealPassword(line);
                        passfound = true;
                    }else if(line.startsWith("Salt=")){
                        line = line.replace("Salt=", "");
                        password.setSalt(line);
                        if(passfound){
                            break;
                        }
                    }
                }
                br.close();
            }catch(final Exception e){
                e.printStackTrace();
            }
        }
        return password;
    }


    public static String getArteroPass(String playerName) {
        playerName = playerName.toLowerCase();
        String pass = null;
        if(!existsArtero(playerName))
            return pass;
        try{
            final BufferedReader br = new BufferedReader(new FileReader(ARTERO_DIR + playerName + ".txt"));
            String line = br.readLine();
            while((line = br.readLine()) != null){
                if(line.startsWith("Pass=")){
                    pass = line.replace("Pass=", "");
                    break;
                }
            }
            br.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
        return pass;
    }

    public static boolean existsBackup(String name) {
        name = name.toLowerCase();
        final File file = new File(BACKUP_DIR + name + ".txt");
        return file.exists();
    }

    public static boolean existsMain(String name) {
        name = name.toLowerCase();
        final File file = new File(MERGED_DIR + name + ".txt");
        return file.exists();
    }

    public static boolean existsArtero(String name) {
        name = name.toLowerCase();
        final File file = new File(ARTERO_DIR + name + ".txt");
        return file.exists();
    }

    public static boolean existsInstant(String name) {
        name = name.toLowerCase();
        final File file = new File(INSTANT_DIR + name + ".txt");

        return file.exists();
    }

    public static int getArteroPriority(final String name) {
        final File file = new File(ARTERO_DIR + name + ".txt");
        if(!file.exists())
            return -1;
        return priority(file);
    }

    public static int getInstantPriority(final String name) {
        final File file = new File(INSTANT_DIR + name + ".txt");
        if(!file.exists())
            return -1;
        return priority(file);
    }


    public static int priority(final File file) {
        int multiplier = 1;
        int donated = 1;
        try{
            String line;
            final BufferedReader in = new BufferedReader(new FileReader(file));
            final long previous = file.lastModified();
            final long delta = System.currentTimeMillis() - previous;
            if(delta < Time.ONE_DAY * 2){
                multiplier = 50;
            }else if(delta < Time.ONE_WEEK){
                multiplier = 30;
            }else if(delta < Time.ONE_WEEK * 2){
                multiplier = 20;
            }else if(delta < Time.ONE_MONTH){
                multiplier = 10;
            }

            while((line = in.readLine()) != null){
                line = line.toLowerCase();
                if(line.contains("donatorsbought")){
                    final String[] parts = line.split("=");
                    final String donatorStr = parts[1].trim();
                    donated = Integer.parseInt(donatorStr);
                    break;
                }else if(line.contains("rights")){
                    donated = 1000000;
                }else if(line.startsWith("rank")){
                    final String[] parts = line.split("=");
                    final String longstr = parts[1].trim();
                    final long r = Long.parseLong(longstr);
                    if(Rank.isStaffMember(r)){
                        donated = 1000000;
                    }
                }
            }

            in.close();
        }catch(final IOException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return multiplier * donated;
    }

    /**
     *  else if(PlayerFiles.exists(pd.getName())) {
     newcharacter = false;
     try {
     String pass = PlayerFiles.getPassword(pd.getName());
     if(! pass.equalsIgnoreCase(pd.getPassword()))
     code = 3;
     } catch(Exception ex) {
     code = 11;
     ex.printStackTrace();
     }
     }
     */

    /**
     * @param oldName old username
     * @param newName new username
     */
    public static boolean renameArtero(final String oldName, final String newName) {
        final File file = new File(ARTERO_DIR + oldName + ".txt");
        if(file.exists()){
            return file.renameTo(new File(ARTERO_DIR + newName + ".txt"));
        }
        return false;
    }

    /**
     * @param oldName old username
     * @param newName new username
     */
    public static boolean renameInstant(final String oldName, final String newName) {
        final File file = new File(INSTANT_DIR + oldName + ".txt");
        if(file.exists())
            return file.renameTo(new File(INSTANT_DIR + newName + ".txt"));
        return false;
    }

}
