package org.hyperion.rs2.sql;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.shops.*;
import org.hyperion.rs2.saving.PlayerSaving;

import java.io.*;
import java.sql.*;

/**
 * Created by Administrator on 6/18/2014.
 */
public class SQLIntegration implements Runnable {

    private Connection connection = null;

    PkShop pkshop = new PkShop(-1,null,null);

    VoteShop voteshop = new VoteShop(-1,null,null);

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new SQLIntegration()).start();
    }

    private boolean createConnection(String url, String username, String password) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected.");
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        boolean connected = createConnection("jdbc:mysql://localhost/devious", "admin", "root");
        assert connected : "Could not connect to database";
        //executeQuery("DELETE FROM `devious`.`players`");
        //System.exit(0);
        try {
            while (!connection.isClosed()) {
                File[] characters = PlayerSaving.SAVE_DIR.listFiles();
                assert characters != null : "Character files are null";
                assert characters.length > 0 : "Character files cannot be found";

                for(File character : characters) {
                    String query = getQuery(character);
                    try {
                        executeQuery(query);
                        Thread.sleep(30);
                    } catch (InterruptedException ignore){
                    } catch (Exception ignore){
                        System.out.println("Error executing query: "+query);
                    }
                }

                try {
                    Thread.sleep(1800000);
                } catch (InterruptedException ignore){}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getQuery(File character) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(character))) {
            String line, username = null;
            int dpValue = 0, vpValue = 0, pkpValue = 0;
            long rank = -1;
            while ((line = bufferedReader.readLine()) != null) {
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
                if(line.startsWith("Inventory") || line.startsWith("Bank") || line.startsWith("Equip")) {
                    while (!(line = bufferedReader.readLine()).isEmpty()) {
                        String[] data = line.split(" ");
                        int amount = data.length == 2 ? Integer.parseInt(data[1]) : 1;
                        int id = Integer.parseInt(data[0]);

                        int pts = DonatorShop.getPrice(id);//ShopManager.getPoints(63, id);
                        //pts = (pts > 0 && pts != 50000) ? pts : ShopManager.getPoints(64, id);
                        //pts = (pts > 0 && pts != 50000) ? pts : ShopManager.getPoints(65, id);
                       // if(pts > 0 && pts != 50000) {
                            //System.out.println(id+" - "+pts+" * "+amount);
                        if(pts > 0) {
                            dpValue += pts * amount;
                            continue;
                        }
                        pts = pkshop.getPrice(id);
                        if(pts > 0) {
                            pkpValue += pts * amount;
                            continue;
                        }
                        pts =
                        pts = (pts > 0 && pts != 50000) ? pts : ShopManager.getPoints(71, id);
                        pts = voteshop.getPrice(id);
                        if(pts > 0) {
                            vpValue += pts * amount;
                            continue;
                        }

                    }
                }
            }

            boolean superDonor = Rank.hasAbility(rank, Rank.SUPER_DONATOR);
            boolean donor = Rank.hasAbility(rank, Rank.DONATOR);

            assert (username == null || username.isEmpty()) : "Invalid Username: "+username;
            if(dpValue > 0 || vpValue > 0 || pkpValue > 0) {
                /*return "UPDATE players " +
                        "SET dpvalue = "+dpValue+", vpvalue = "+vpValue+", pkpvalue = "+pkpValue+", " +
                        "donor = "+donor+", sdonor = "+superDonor+" " +
                        "WHERE username = '"+username+"'";*/
                return "INSERT INTO `devious`.`players` (`id`, `username`, `dpvalue`, `vpvalue`, `pkpvalue`, `donor`, `sdonor`) VALUES (NULL, '"+username+"', '"+dpValue+"', '"+vpValue+"', '"+pkpValue+"', '"+(donor?1:0)+"', '"+(superDonor?1:0)+"');";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void executeQuery(String query) {
        if(query != null && !query.isEmpty()) {
            //System.out.println(query);
            try {
                Statement statement = connection.createStatement();
                if(query.toLowerCase().startsWith("select")) {
                    statement.executeQuery(query);
                } else
                    statement.executeUpdate(query);
            } catch (Exception e) {}
        }
    }
}
