package org.hyperion.rs2.model.content.grandexchange;

import org.hyperion.rs2.Constants;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.ItemsTradeable;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.util.NameUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class GrandExchange {

    public static final int GEInterfaceId = 5000;
    private static int ITEMID_INDEX = 1;
    @SuppressWarnings("unchecked")
    private final Map<Long, GEItem>[] items = new HashMap[20000];
    public Map<Long, Integer> moneyOwed = new HashMap<Long, Integer>();
    public Map<Long, Integer> amountItemsInGe = new HashMap<Long, Integer>();

    /**
     * The constructor
     */
    public GrandExchange() {
        //load all the data from GE
        if(true)
            return;
        try{
            if(ServerDatabase.isClosed())
                return;
        }catch(final SQLException e1){
            return;
        }
        try{
            final String query = "SELECT * FROM hyp_grandexchange";
            final ResultSet results = ServerDatabase.query(query);
            while(results.next()){
                final String username = results.getString("username");
                final long usernameLong = NameUtils.nameToLong(username);
                final int itemId = results.getInt("itemId");
                final int itemAm = results.getInt("itemAm");
                final int price = results.getInt("price");
                final int day = results.getInt("day");
                final int hour = results.getInt("hour");
                if(items[itemId] == null)
                    items[itemId] = new HashMap<Long, GEItem>();
                if(amountItemsInGe.get(usernameLong) != null){
                    int itemCount = amountItemsInGe.get(usernameLong);
                    amountItemsInGe.put(usernameLong, ++itemCount);
                }else
                    amountItemsInGe.put(usernameLong, 1);
                if(results.getInt("id") > ITEMID_INDEX)
                    ITEMID_INDEX = results.getInt("id") + 1;
                items[itemId].put(NameUtils.nameToLong(username), new GEItem(new Item(itemId, itemAm), price, username, hour, day));
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
        try{
            final String query = "SELECT * FROM hyp_grandmoney";
            final ResultSet results = (ResultSet) ServerDatabase.query(query);
            while(results.next()){
                final String username = results.getString("username");
                final int money = results.getInt("money");
                moneyOwed.put(NameUtils.nameToLong(username), money);
            }
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public boolean isGEAvaible() {
        try{
            return !ServerDatabase.isClosed();
        }catch(final SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public void addItem(final Player player, final int itemId, final int amount, final int slot) {
        //addItem(player,itemId,amount,slot,ItemDefinition.forId(itemId).getHighAlcValue());
        player.getExtraData().put("geamount", amount);
        player.getInterfaceState().openEnterAmountInterface(GEInterfaceId, slot, itemId);
    }

    public void addItem(final Player player, final int itemId, int amount, final int slot, final int price) {
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
            final String databaseUpdate = "UPDATE `hyp_grandexchange` SET itemAm = itemAm + " + amount + " WHERE username = '" + player.getName() + "' AND itemId = '" + item.getId() + "'";
            ServerDatabase.query(databaseUpdate);
        }else{
            geItem = new GEItem(new Item(itemId, amount), price, player.getName(), hour, day);
            items[item.getId()].put(player.getNameAsLong(), geItem);
            insertIntoDatabase(player, item, price, amount);
        }

        findPlayer(player, player.getName());
    }

    private void insertIntoDatabase(final Player player, final Item item, final int price, final int amount) {
        String databaseUpdate = "";
        final Calendar calendar = new GregorianCalendar();
        try{
            final String name = item.getDefinition().getName().replace("'", "");
            final int day = calendar.get(Calendar.DAY_OF_YEAR);
            final int hour = calendar.get(Calendar.HOUR);
            //INSERT INTO `darkstar`.`hyp_grandexchange` (`id`, `username`, `itemId`, `itemAm`, `price`, `day`, `hour`, `itemName`, `type`) VALUES (NULL, 'jack', '4675', '8', '5000', '2', '8', 'Ancient Staff', '0');
            databaseUpdate = "INSERT INTO `hyp_grandexchange` (`id`,`username`,`itemId`,`itemAm`,`price`,`day`,`hour`,`itemName`,`type`) VALUES (" + (ITEMID_INDEX++) + ",'" + player.getName() + "','" + item.getId() + "','" + amount + "','" + price + "','" + day + "','" + hour + "','" + name + "','0');";
            ServerDatabase.query(databaseUpdate);
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    public void buyItem(final Player player, final int itemId, int amount, final String playerName, final int slot) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        if(!ItemsTradeable.isTradeable(itemId)){
            player.getActionSender().sendMessage("You cannot buy this item.");
            return;
        }
        final GEItem item = items[itemId].get(NameUtils.nameToLong(playerName));
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
            databaseUpdate = "UPDATE `hyp_grandexchange` SET itemAm = itemAm - " + amount + " WHERE username = '" + item.getName() + "' AND itemId = '" + item.getItem().getId() + "'";
            item.setNewItem(new Item(itemId, item.getItem().getCount() - amount));
        }else{
            player.geItem[slot] = null;
            items[itemId].remove(NameUtils.nameToLong(playerName));
            databaseUpdate = "DELETE FROM `hyp_grandexchange` WHERE username = '" + item.getName() + "' AND itemId = '" + item.getItem().getId() + "'";
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
        //player.getActionSender().sendUpdateItems(3900, player.geItem);
        player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
    }

    public void claimMoney(final Player player) {
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

    public void getItems2(final Player player) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        final Item[] items2 = new Item[40];
        final int index = 0;
        for(int i = 11740; i > 0; i--){
            if(index >= 40)
                break;
            if(items[i] != null){
                for(final Map.Entry<Long, GEItem> entry : items[i].entrySet()){
                    if(index >= 40)
                        break;
                    items2[index] = (Item) entry.getValue().getItem();
                    //player.geItem[index++] = entry.getValue();
                }
            }
        }
        openGrandExchange(player, items2);
    }

    private void openGrandExchange(final Player player, final Item[] items2) {
        player.setShopId(-2);
        player.getActionSender().sendInterfaceInventory(3824, 3822);
        player.getActionSender().sendUpdateItems(3900, items2);
        player.getActionSender().sendUpdateItems(3823, player.getInventory().toArray());
        player.getActionSender().sendString(3901, "Custom Grand Exchange");
    }

    public void findItem(final Player player, final int item) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        final Item[] items2 = new Item[40];
        final int index = 0;
        if(items[item] != null){
            for(final Map.Entry<Long, GEItem> entry : items[item].entrySet()){
                if(index >= 39)
                    break;
                items2[index] = (Item) entry.getValue().getItem();
                //player.geItem[index++] = entry.getValue();
            }
        }
        openGrandExchange(player, items2);
    }

    public void findItemByName(final Player player, String name) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        name = name.toLowerCase();
        final Item[] items2 = new Item[40];
        final int index = 0;
        for(int i = 0; i < 11740; i++){
            if(index >= 39)
                break;
            if(items[i] != null && ItemDefinition.forId(i).getName().toLowerCase().contains(name)){
                for(final Map.Entry<Long, GEItem> entry : items[i].entrySet()){
                    if(index >= 39)
                        break;
                    items2[index] = (Item) entry.getValue().getItem();
                    //player.geItem[index++] = entry.getValue();
                }
            }
        }
        openGrandExchange(player, items2);
    }

    public void findPlayer(final Player player, final String username) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        final Item[] items2 = new Item[40];
        final int index = 0;
        final long user = NameUtils.nameToLong(username);
        for(int i = 0; i < 11740; i++){
            if(index >= 39)
                break;
            if(items[i] != null){
                if(items[i].get(user) != null){
                    items2[index] = items[i].get(user).getItem();
                    //player.geItem[index++] = items[i].get(user);
                }
            }
        }
        openGrandExchange(player, items2);
    }

    public void valueGeItem(final Player player, final int slot) {
        if(!isGEAvaible()){
            player.getActionSender().sendMessage("The grand exchange is currently unavaible, please wait.");
            return;
        }
        //GEItem item = player.geItem[slot];
        final DecimalFormat formatter = new DecimalFormat("#,###,###");
        //String priceString = formatter.format(item.getPrice());
        //player.getActionSender().sendMessage(item.getName()+"'s "+item.getItem().getDefinition().getName()+" costs "+ priceString +" coins to buy.");
    }

    public static class GEItem {
        private final int price;
        private final String name;
        @SuppressWarnings("unused")
        private final int hour;
        @SuppressWarnings("unused")
        private final int day;
        private Item item;

        public GEItem(final Item item, final int price, final String name, final int hour, final int day) {
            this.item = item;
            this.price = price;
            this.name = name;
            this.hour = hour;
            this.day = day;
        }

        public Item getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }

        public String getName() {
            return name;
        }

        public void setNewItem(final Item item) {
            this.item = item;
        }
    }


}
