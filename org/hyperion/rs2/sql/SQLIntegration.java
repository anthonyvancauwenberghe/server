package org.hyperion.rs2.sql;

import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.shops.DonatorShop;
import org.hyperion.rs2.model.shops.PkShop;
import org.hyperion.rs2.model.shops.VoteShop;
import org.hyperion.rs2.saving.MergedSaving;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Administrator on 6/18/2014.
 */
public class SQLIntegration implements Runnable {

    PkShop pkshop = new PkShop(-1, null, null);
    VoteShop voteshop = new VoteShop(-1, null, null);
    private Connection connection = null;

    public static void main(final String[] args) {
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }catch(final Exception e){
            e.printStackTrace();
        }
        new Thread(new SQLIntegration()).start();
    }

    private boolean createConnection(final String url, final String username, final String password) {
        try{
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected.");
            return true;
        }catch(final Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        final boolean connected = createConnection("jdbc:mysql://localhost/devious", "admin", "root");
        assert connected : "Could not connect to database";
        //executeQuery("DELETE FROM `devious`.`players`");
        //System.exit(0);
        try{
            while(!connection.isClosed()){
                final File[] characters = new File(MergedSaving.MERGED_DIR).listFiles();
                assert characters != null : "Character files are null";
                assert characters.length > 0 : "Character files cannot be found";

                for(final File character : characters){
                    final String query = getQuery(character);
                    try{
                        executeQuery(query);
                        Thread.sleep(30);
                    }catch(final InterruptedException ignore){
                    }catch(final Exception ignore){
                        System.out.println("Error executing query: " + query);
                    }
                }

                try{
                    Thread.sleep(1800000);
                }catch(final InterruptedException ignore){
                }
            }
        }catch(final SQLException e){
            e.printStackTrace();
        }
    }

    private String getQuery(final File character) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(character))){
            String line, username = null;
            int dpValue = 0, vpValue = 0, pkpValue = 0;
            long rank = -1;
            while((line = bufferedReader.readLine()) != null){
                if(line.startsWith("Name="))
                    username = line.substring("Name=".length()).toLowerCase();
                if(line.startsWith("DonatorPoints="))
                    dpValue += Integer.parseInt(line.substring("DonatorPoints=".length()));
                if(line.startsWith("VotePoints="))
                    vpValue += Integer.parseInt(line.substring("VotePoints=".length()));
                if(line.startsWith("PkPoints="))
                    pkpValue += Integer.parseInt(line.substring("PkPoints=".length()));
                if(line.startsWith("Rank"))
                    rank = Long.parseLong(line.replace(" ", "").trim().substring("Rank=".length()));
                if(line.startsWith("Inventory") || line.startsWith("Bank") || line.startsWith("Equip")){
                    while(!(line = bufferedReader.readLine()).isEmpty()){
                        final String[] data = line.split(" ");
                        final int amount = data.length == 2 ? Integer.parseInt(data[1]) : 1;
                        final int id = Integer.parseInt(data[0]);

                        int pts = DonatorShop.getPrice(id);//ShopManager.getPoints(63, id);
                        //pts = (pts > 0 && pts != 50000) ? pts : ShopManager.getPoints(64, id);
                        //pts = (pts > 0 && pts != 50000) ? pts : ShopManager.getPoints(65, id);
                        // if(pts > 0 && pts != 50000) {
                        //System.out.println(id+" - "+pts+" * "+amount);
                        if(pts > 0){
                            dpValue += pts * amount;
                            continue;
                        }
                        pts = pkshop.getPrice(id);
                        if(pts > 0){
                            pkpValue += pts * amount;
                            continue;
                        }
                        pts = pts = (pts > 0 && pts != 50000) ? pts : ShopManager.getPoints(71, id);
                        pts = voteshop.getPrice(id);
                        if(pts > 0){
                            vpValue += pts * amount;
                            continue;
                        }

                    }
                }
            }

            final boolean superDonor = Rank.hasAbility(rank, Rank.SUPER_DONATOR);
            final boolean donor = Rank.hasAbility(rank, Rank.DONATOR);

            assert (username == null || username.isEmpty()) : "Invalid Username: " + username;
            if(dpValue > 0 || vpValue > 0 || pkpValue > 0){
                /*return "UPDATE players " +
                        "SET dpvalue = "+dpValue+", vpvalue = "+vpValue+", pkpvalue = "+pkpValue+", " +
                        "donor = "+donor+", sdonor = "+superDonor+" " +
                        "WHERE username = '"+username+"'";*/
                return "INSERT INTO `devious`.`players` (`id`, `username`, `dpvalue`, `vpvalue`, `pkpvalue`, `donor`, `sdonor`) VALUES (NULL, '" + username + "', '" + dpValue + "', '" + vpValue + "', '" + pkpValue + "', '" + (donor ? 1 : 0) + "', '" + (superDonor ? 1 : 0) + "');";
            }
        }catch(final IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void executeQuery(final String query) {
        if(query != null && !query.isEmpty()){
            //System.out.println(query);
            try{
                final Statement statement = connection.createStatement();
                if(query.toLowerCase().startsWith("select")){
                    statement.executeQuery(query);
                }else
                    statement.executeUpdate(query);
            }catch(final Exception e){
            }
        }
    }
}
