package org.madturnip.tools;

import org.hyperion.Server;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.saving.PlayerSaving;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 3/2/15
 * Time: 8:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class CharEditor {

    public static void main(final String[] args) throws Exception{
        //ItemDefinition.init();
        if(!Server.DEBUG_CLEAN)
            return;
        load();
        System.err.println("----------------LOADED----------------");
        int oldAccVal = 0;
        for(final Player player : players) {
            oldAccVal += player.getAccountValue().getTotalValue();
        }
        System.err.println("----------------"+oldAccVal+"----------------");
        edit(13740, 699);
        edit(13738, 199);
        edit(13736, 199);
        edit(13742, 199);
        edit(13744, 199);
        oldAccVal = 0;
        for(final Player player : players) {
            oldAccVal += player.getAccountValue().getTotalValue();
        }
        System.err.println("----------------"+oldAccVal+"----------------");
        save();
    }

    static final File[] users = PlayerSaving.SAVE_DIR.listFiles(new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("txt");
        }
    });

    static final List<Player> players = new CopyOnWriteArrayList<>();

    private static final ExecutorService service = Executors.newFixedThreadPool(20);

    public static final void load() throws InterruptedException {
        for(final File file : users) {
            service.submit(new Loader(file));
        }
        service.shutdown();
        service.awaitTermination(10000000L, TimeUnit.HOURS);
    }

    public static final void edit(final int id, final int price) {
        for(final Player player : players) {
            try {
            int remove = player.getBank().remove(Item.create(id, 1000000));
            remove += player.getInventory().remove(Item.create(id, 1000000));
            remove += player.getEquipment().remove(Item.create(id));
            if(remove > 0) {
                System.out.println("Removing "+remove+" "+ ItemDefinition.forId(id).getName()+ " from "+player.getName());
                int toAdd = remove * price;
                int old = player.getPoints().getDonatorPoints();
                System.out.println("\tAdding "+toAdd+ " honor points, old: "+old);
                player.getPoints().setDonatorPoints(toAdd + old);
                System.out.println("\tNew: "+player.getPoints().getDonatorPoints());
            }
            }catch(Exception ex) {

            }

        }
    }

    public static final void save() {
        for(final Player player : players) {
            PlayerSaving.getSaving().save(player);
        }
    }


    private static final class Loader implements Runnable {
        final File file;
        public Loader(final File file) {
            this.file = file;
        }

        public void run() {
            try {
            final String name = file.getName().substring(0, file.getName().lastIndexOf("."));
            final int uid = Integer.parseInt(CommandPacketHandler.findCharString(name, "Mac"));
            final String pass = CommandPacketHandler.findCharString(name, "Pass");
            final String ip = CommandPacketHandler.findCharString(name, "IP");
            final Player player = new Player(new PlayerDetails(null, name, pass, uid, null, null, ip, "yo"), false);
            PlayerSaving.getSaving().load(player);
            System.out.println("Loaded "+name);
            players.add(player);
            }catch(Exception ex) {
                ex.printStackTrace();
            }

        }
    }


}
