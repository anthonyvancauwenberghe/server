package org.hyperion.rs2.model.content.grandexchange;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.ItemsTradeable;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Container.Type;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.Misc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@SuppressWarnings("unchecked")
public class GrandExchangeV2 {

    private static final Map<Long, GEItem>[] items = new HashMap[20000];
    public static Map<Long, Integer> moneyOwed = new HashMap<Long, Integer>();
    public static Map<Long, Integer> amountItemsInGe = new HashMap<Long, Integer>();
    public static LinkedList<GEItem> playerNameList = new LinkedList<GEItem>();

    public GrandExchangeV2() {
        //ServerDatabase.query("DROP TABLE `hyp_grandexchange2`");
        //ServerDatabase.query("CREATE TABLE `hyp_grandexchange2` (`username` varchar(30) NOT NULL,`itemId` int(6) NOT NULL,`itemAm` int(20) NOT NULL,`price` int(20) NOT NULL,`time` long(9) NOT NULL,`itemName` varchar(30) NOT NULL,`type` int(10) NOT NULL)");

        try{
            final String query = "SELECT * FROM hyp_grandexchange2";
            final ResultSet results = ServerDatabase.query(query);
            if(results == null)
                return;
            /*while(results.next()){
                String username = results.getString("username");
				long usernameLong = NameUtils.nameToLong(username);
				int itemId = results.getInt("itemId");
				int itemAm = results.getInt("itemAm");
				int price = results.getInt("price");
				long time = results.getLong("time");
				if(items[itemId] == null)
					items[itemId] = new HashMap<Long,GEItem>();
				//System.out.println(username+" : "+itemId);
				if(amountItemsInGe.get(usernameLong) != null){
					int itemCount = amountItemsInGe.get(usernameLong);
					amountItemsInGe.put(usernameLong, ++itemCount);
				} else
					amountItemsInGe.put(usernameLong, 1);
				GEItem item = new GEItem(new Item(itemId,itemAm),price,username,time);
				playerNameList.add(item);
				items[itemId].put(NameUtils.nameToLong(username), item);
			}*/
        }catch(final Exception e){

            e.printStackTrace();
        }

		/*items[4151] = new HashMap<Long,GEItem>();
		for(int i = 0; i < 120; i++){
			GEItem item = new GEItem(new Item(4151,i),10000+i,"martin"+i,System.currentTimeMillis());
			items[4151].put(NameUtils.nameToLong("martin"+i), item);
			playerNameList.add(item);
		}*/

        //ServerDatabase.query("CREATE TABLE `hyp_grandmoney` (`username` varchar(32) NOT NULL,`money` int(255) NOT NULL,PRIMARY KEY (`username`))");
        try{
            final String query = "SELECT * FROM hyp_grandmoney";
            final ResultSet results = (ResultSet) ServerDatabase.query(query);
            if(results != null)
                while(results.next()){
                    final String username = results.getString("username");
                    final int money = results.getInt("money");
                    moneyOwed.put(NameUtils.nameToLong(username), money);
                }
        }catch(final Exception e){
            e.printStackTrace();
        }
        final char c = 'j';
        final byte b = (byte) c;
    }

    public static void openGE(final Player player) {
        if(true)
            return;
        if(player.getName().toLowerCase().equals("faggot"))
            return;
        player.geItems.clear();
        int itemCount = 0;
        for(final GEItem item : playerNameList){
            if(NameUtils.nameToLong(item.getName()) == player.getNameAsLong()){
                player.geItems.add(item);
                itemCount++;
            }
        }
        player.getActionSender().sendString(28508, "Total Listed Items: " + itemCount);
        player.getActionSender().sendString(28509, "Your Listed Items: " + itemCount);
        player.getActionSender().sendString(29011, "Your Listed Items: " + itemCount);
        int moneyCount = 0;
        if(moneyOwed.get(player.getNameAsLong()) != null)
            moneyCount = (Integer) moneyOwed.get(player.getNameAsLong());
        player.getActionSender().sendString(28510, "Money in Collection Box: " + Misc.getFormattedValue(moneyCount));
        player.getExtraData().put("gepage", 1);
        clickPage(player, 1);
        GrandExchangeV2.resetSellInterface(player);
        player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
        player.getActionSender().sendInterfaceInventory(28000, 29500);
    }

    public static void claimMoney(final Player player) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        if(moneyOwed.get(player.getNameAsLong()) != null){
            final String databaseUpdate = "DELETE FROM `hyp_grandmoney` WHERE username = '" + player.getName() + "'";
            ServerDatabase.query(databaseUpdate);
            int getMoney = moneyOwed.get(player.getNameAsLong());
            if(getMoney + ContentEntity.getItemAmount(player, 995) >= Constants.MAX_ITEMS)
                getMoney = getMoney - ContentEntity.getItemAmount(player, 995);

            ContentEntity.addItem(player, 995, getMoney);
            ContentEntity.sendMessage(player, "You claim your money back from the Grand Exchange.");
            moneyOwed.remove(player.getNameAsLong());
        }else
            ContentEntity.sendMessage(player, "You have no money to claim back.");
    }

    public static void upPage(final Player player) {
        final int maxPages = 1 + (player.geItems.size() / 50);
        int currentPage = (Integer) player.getExtraData().get("gepage");
        if(currentPage == maxPages)
            return;
        else{
            player.getExtraData().put("gepage", ++currentPage);
            clickPage(player, currentPage);
        }
    }

    public static void downPage(final Player player) {
        int currentPage = (Integer) player.getExtraData().get("gepage");
        if(currentPage == 1)
            return;
        else{
            player.getExtraData().put("gepage", --currentPage);
            clickPage(player, currentPage);
        }
    }

    public static void clickPage(final Player player, final int page) {
        final int startAt = (page - 1) * 50;
        int index = -1;
        int stringIndex = 0;
        final int pages = 1 + (player.geItems.size() / 50);
        for(final GEItem item : player.geItems){
            index++;
            if(index >= startAt + 50)
                break;
            if(index < startAt)
                continue;
            player.getActionSender().sendString(28015 + 18 + stringIndex, item.getItem().getDefinition().getName() + " : " + item.getName());
            player.getActionSender().sendString(28015 + 168 + stringIndex, item.getItem().getDefinition().isStackable() + "");
            player.getActionSender().sendString(28015 + 218 + stringIndex, item.getItem().getCount() + "");
            player.getActionSender().sendString(28015 + 268 + stringIndex, Misc.getFormattedValue(item.getPrice()) + " GP");
            stringIndex++;
        }
        for(int i = stringIndex; i < 50; i++){
            player.getActionSender().sendString(28015 + 18 + i, "");
            player.getActionSender().sendString(28015 + 168 + i, "");
            player.getActionSender().sendString(28015 + 218 + i, "");
            player.getActionSender().sendString(28015 + 268 + i, "");
        }
        player.getActionSender().sendString(28024, "Page " + page + "/" + pages);
        player.getActionSender().sendString(28025, "Page " + page + "/" + pages);
    }

    public static void buyItem(final Player player, int slot) {
        slot -= 28133;//gets it down to index 0 - 50;

        slot += 50 * ((Integer) player.getExtraData().get("gepage") - 1);
        if(slot >= player.geItems.size())
            return;
        player.getExtraData().put("geitemslot", slot);
        player.getInterfaceState().openEnterAmountInterface(28000, slot, player.geItems.get(slot).getItem().getId());
    }

    public static void setAmount(final Player player) {
        player.getInterfaceState().openEnterAmountInterface(29000, 0, 0);
    }

    public static void setPrice(final Player player) {
        player.getInterfaceState().openEnterAmountInterface(29000, 1, 1);
    }

    public static void setAmount(final Player player, final int am) {
        player.getExtraData().put("geitemam", am);
        player.getActionSender().sendString(29019, "" + am);
        player.getActionSender().sendString(29021, "" + (am * (Integer) player.getExtraData().get("geitemprice")));
    }


    public static void setPrice(final Player player, final int value) {
        player.getExtraData().put("geitemprice", value);
        player.getActionSender().sendString(29020, "" + value);
        player.getActionSender().sendString(29021, "" + (value * (Integer) player.getExtraData().get("geitemam")));
    }

    public static void buyItem(final Player player, final int itemId, final int slot, int amount) {
        if(slot >= player.geItems.size())
            return;
        final GEItem item = player.geItems.get(slot);
        final String playerName = item.getName();
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        if(!ItemsTradeable.isTradeable(itemId)){
            player.getActionSender().sendMessage("You cannot buy this item.");
            return;
        }

        if(item == null)
            return;
        if(item.getItem().getCount() < amount)
            amount = item.getItem().getCount();
        if(ContentEntity.getItemAmount(player, 995) <= item.getPrice() * amount && !player.getName().toLowerCase().equals(playerName.toLowerCase())){
            player.getActionSender().sendMessage("You have not got enough money to buy this item.");
            return;
        }
        if(ContentEntity.freeSlots(player) <= amount && item.getItem().getDefinition().isStackable()){
            player.getActionSender().sendMessage("You do not have enough free slots to buy these.");
            return;
        }
        if(moneyOwed.get(NameUtils.nameToLong(playerName)) != null){
            final long val = moneyOwed.get(NameUtils.nameToLong(playerName)) + (item.getPrice() * amount);
            if(val > Integer.MAX_VALUE){
                player.getActionSender().sendMessage("This player has too much money in there account.");
                return;
            }
        }
        String databaseUpdate = "";

        if(item.getItem().getCount() > 1 && item.getItem().getCount() != amount){
            databaseUpdate = "UPDATE `hyp_grandexchange2` SET itemAm = itemAm - " + amount + " WHERE username = '" + item.getName() + "' AND itemId = '" + item.getItem().getId() + "'";
            item.setNewItem(new Item(itemId, item.getItem().getCount() - amount));
        }else{
            player.geItems.remove(slot);
            items[itemId].remove(NameUtils.nameToLong(playerName));
            playerNameList.remove(item);
            databaseUpdate = "DELETE FROM `hyp_grandexchange2` WHERE username = '" + item.getName() + "' AND itemId = '" + item.getItem().getId() + "'";
        }

        if(!player.getName().toLowerCase().equals(playerName.toLowerCase())){
            ContentEntity.deleteItemA(player, 995, item.getPrice() * amount);
            ContentEntity.sendMessage(player, "You buy a " + ItemDefinition.forId(itemId).getName() + " from " + playerName + ".");
        }else{
            ContentEntity.sendMessage(player, "You remove your " + ItemDefinition.forId(itemId).getName() + " from GrandExchange.");
        }
        if(amount == 1 || item.getItem().getDefinition().isStackable())
            ContentEntity.addItem(player, item.getItem().getId(), amount);
        else{
            for(int i = 0; i < amount; i++){
                ContentEntity.addItem(player, item.getItem().getId(), 1);
            }
        }
        ServerDatabase.query(databaseUpdate);
        final Player seller = World.getWorld().getPlayer(item.getName());
        if(seller != null){
            if(!player.getName().toLowerCase().equals(playerName.toLowerCase()))
                seller.getActionSender().sendMessage("Your " + ItemDefinition.forId(itemId).getName() + " has been sold in the grand exchange.");
        }
        if(!player.getName().toLowerCase().equals(playerName.toLowerCase())){
            if(moneyOwed.get(NameUtils.nameToLong(item.getName())) != null){
                moneyOwed.put(NameUtils.nameToLong(item.getName()), ((item.getPrice() * amount) + moneyOwed.get(NameUtils.nameToLong(item.getName()))));
                ServerDatabase.query("UPDATE `hyp_grandmoney` SET money = " + ((item.getPrice() * amount) + moneyOwed.get(NameUtils.nameToLong(item.getName()))) + " WHERE username = '" + item.getName() + "'");
            }else{
                moneyOwed.put(NameUtils.nameToLong(item.getName()), item.getPrice() * amount);
                databaseUpdate = "INSERT INTO `hyp_grandmoney` (`username`,`money`) VALUES ('" + item.getName() + "','" + (item.getPrice() * amount) + "');";
                ServerDatabase.query(databaseUpdate);
            }
        }
        player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
        clickPage(player, (Integer) player.getExtraData().get("gepage"));
    }

    public static void addItem(final Player player, final int itemId, int amount, final int slot, final int price) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        if(itemId <= 0 || amount <= 0 || price <= 0 || itemId == 995)
            return;
        if(!ItemsTradeable.isTradeable(itemId)){
            player.getActionSender().sendMessage("You cannot put this item in your shop.");
            return;
        }
        if(amountItemsInGe.get(NameUtils.nameToLong(player.getName())) != null){
            if(amountItemsInGe.get(NameUtils.nameToLong(player.getName())) >= 5){
                player.getActionSender().sendMessage("You may only have 5 items in the grand exchange at one time.");
            }
        }
        final Item item = player.getInventory().get(slot);
        if(item == null)
            return;
        final int itemCount = ContentEntity.getItemAmount(player, itemId);
        if(amount > itemCount)
            amount = itemCount;
        if(items[item.getId()] == null)
            items[item.getId()] = new HashMap<Long, GEItem>();
        final Calendar calendar = new GregorianCalendar();
        final int day = calendar.get(Calendar.DAY_OF_YEAR);
        final int hour = calendar.get(Calendar.HOUR);

        if(amount == 1 || item.getDefinition().isStackable())
            ContentEntity.deleteItemA(player, itemId, amount);
        else{
            for(int i = 0; i < amount; i++){
                ContentEntity.deleteItemA(player, itemId, 1);
            }
        }

        GEItem geItem = items[item.getId()].get(NameUtils.nameToLong(player.getName()));
        if(geItem != null){
            geItem.setNewItem(new Item(item.getId(), geItem.getItem().getCount() + amount));
            geItem.setPrice(price);
            final String databaseUpdate = "UPDATE `hyp_grandexchange2` SET itemAm = itemAm + " + amount + " WHERE username = '" + player.getName() + "' AND itemId = '" + item.getId() + "'";
            ServerDatabase.query(databaseUpdate);
        }else{
            geItem = new GEItem(new Item(itemId, amount), price, player.getName(), System.currentTimeMillis());
            items[item.getId()].put(player.getNameAsLong(), geItem);
            playerNameList.add(geItem);
            insertIntoDatabase(player, item, price, amount);
        }
        player.getActionSender().sendMessage("Your " + item.getDefinition().getName() + " has been added to the Grand Exchange.");
        resetSellInterface(player);
    }

    public static void resetSellInterface(final Player player) {
        player.getActionSender().sendString(29019, "0");
        player.getActionSender().sendString(29020, "0");
        player.getActionSender().sendString(29021, "0");
        player.getExtraData().put("geitemprice", 0);
        player.getExtraData().put("geitemam", 0);
        player.getActionSender().sendInterfaceModel(29044, 250, -1);
    }


    private static void insertIntoDatabase(final Player player, final Item item, final int price, final int amount) {
        String databaseUpdate = "";
        final Calendar calendar = new GregorianCalendar();
        try{
            final String name = item.getDefinition().getName().replace("'", "");
            final int day = calendar.get(Calendar.DAY_OF_YEAR);
            final int hour = calendar.get(Calendar.HOUR);
            //INSERT INTO `darkstar`.`hyp_grandexchange` (`id`, `username`, `itemId`, `itemAm`, `price`, `day`, `hour`, `itemName`, `type`) VALUES (NULL, 'jack', '4675', '8', '5000', '2', '8', 'Ancient Staff', '0');
            databaseUpdate = "INSERT INTO `hyp_grandexchange2` (`username`,`itemId`,`itemAm`,`price`,`time`,`itemName`,`type`) VALUES ('" + player.getName() + "','" + item.getId() + "','" + amount + "','" + price + "','" + System.currentTimeMillis() + "','" + name + "','0');";
            ServerDatabase.query(databaseUpdate);
        }catch(final Exception e){
            e.printStackTrace();
        }
    }


    public static void searchGE(final Player player, String itemName) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        itemName = itemName.toLowerCase();
        final int index = 0;
        player.geItems.clear();
        for(int i = 0; i < 18000; i++){
            if(index >= 39)
                break;
            if(items[i] != null && ItemDefinition.forId(i).getName().toLowerCase().contains(itemName)){
                for(final Map.Entry<Long, GEItem> entry : items[i].entrySet()){
                    player.geItems.add(entry.getValue());
                }
            }
        }
        player.getActionSender().sendString(28508, "Total Listed Items: " + player.geItems.size());
        int moneyCount = 0;
        if(moneyOwed.get(player.getNameAsLong()) != null)
            moneyCount = (Integer) moneyOwed.get(player.getNameAsLong());
        player.getActionSender().sendString(28510, "Money in Collection Box: " + moneyCount);
        player.getExtraData().put("gepage", 1);
        clickPage(player, 1);
    }

    public static boolean isGEAvaible() {
        if(true)
            return false;
        try{
            return !ServerDatabase.isClosed();
        }catch(final SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void setItem(final Player player, final int id, final int slot) {
        if(player.getInventory().get(slot).getId() != id)
            return;
        player.getExtraData().put("geitemid", id);
        player.getExtraData().put("geitemslot", slot);
        player.getActionSender().sendInterfaceModel(29044, 250, id);
        player.getActionSender().sendUpdateItems(3823, new Container(Type.STANDARD, 28).toArray());
    }

    public static class GEItem {
        private final String name;
        private final long time;
        private Item item;
        private int price;

        public GEItem(final Item item, final int price, final String name, final long time) {
            this.item = item;
            this.price = price;
            this.name = name;
            this.time = time;
        }

        public Item getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice(final int price) {
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public void setNewItem(final Item item) {
            this.item = item;
        }

        public long getTime() {
            return time;
        }
    }


}
