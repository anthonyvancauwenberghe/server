package org.hyperion.rs2.model;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.log.LogEntry;

import java.util.LinkedList;
import java.util.List;

public class GlobalItemManager {

    public List<GlobalItem> globalItems = new LinkedList<GlobalItem>();

    public GlobalItemManager() {
        World.getWorld().submit(new Event(1000, "globalitems") {
            @Override
            public void execute() {
                process();
            }
        });
    }

    public void displayItems(final Player player) {
        synchronized(globalItems){
            for(final GlobalItem globalItem : globalItems){
                if((!globalItem.itemHidden || globalItem.owner == player) && globalItem.getItem() != null && player.getLocation().isWithinDistance(globalItem.getLocation(), 64)){
                    //display the item
                    if(!ItemsTradeable.isTradeable(globalItem.getItem().getId()) && globalItem.owner != player){
                        continue;
                    }
                    //System.out.println("displaying items " + globalItem.getItem().getDefinition().getName());
                    player.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
                }
            }
        }
    }

    public GlobalItem getItem(final int id, final Location loc) {
        synchronized(globalItems){
            for(final GlobalItem g : globalItems){
                //System.out.println(""+g.getItem().getId()+", "+g.getLocation().getX()+", "+g.getLocation().getY());
                if(g.getLocation().distance(loc) == 0){
                    if(g.getItem().getId() == id){
                        return g;
                    }
                }
            }
        }
        return null;
    }

    public void newDropItem(final Player player, final GlobalItem globalItem) {
        final GlobalItem onGround = getItem(globalItem.getItem().getId(), globalItem.getLocation());
        if(onGround != null && onGround.getItem().getDefinition().isStackable()){
            final int count = onGround.getItem().getCount() + globalItem.getItem().getCount();
            onGround.setNewItem(new Item(onGround.getItem().getId(), count));
        }else{
            synchronized(globalItems){
                globalItems.add(globalItem);
            }
            if(player.getLocation().isWithinDistance(globalItem.getLocation(), 64))
                player.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
        }
    }

    public void addToItems(final GlobalItem globalItem) {
        synchronized(globalItems){
            globalItems.add(globalItem);
        }
    }

    public void dropItem(final Player player, final int itemId, final int slot) {
        final Item item = player.getInventory().get(slot);
        if(item == null || itemId != item.getId()){
            player.getActionSender().sendMessage("Client is out of sync, Please logout.");
            return;
        }
        final GlobalItem globalItem = new GlobalItem(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), new Item(item.getId(), item.getCount()));
        synchronized(globalItems){
            globalItems.add(globalItem);
        }
        player.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
        player.getInventory().remove(item);
    }

    public void pickupItem(final Player player, final int item, final int x, final int y) {
        if(ContentEntity.freeSlots(player) <= 0){
            ContentEntity.sendMessage(player, "You have not got enough space to pick up that item");
            return;
        }
        GlobalItem globalItem = null;
        synchronized(globalItems){
            for(final GlobalItem g : globalItems){
                //System.out.println(""+g.getItem().getId()+", "+g.getLocation().getX()+", "+g.getLocation().getY());
                if(g.getLocation().getX() == x && g.getLocation().getY() == y && g.getLocation().getZ() == player.getLocation().getZ()){
                    if(g.getItem().getId() == item){
                        globalItem = g;
                        break;
                    }
                }
            }
        }
        if(globalItem != null){
            synchronized(globalItems){
                if(globalItem.owner != null && globalItem.owner.getGameMode() != player.getGameMode()){
                    player.sendMessage("You cannot pick up this item as it belongs to another game mode");
                    return;
                }
                player.getExpectedValues().pickupItem(globalItem.getItem());
                globalItems.remove(globalItem);
                if(item != 2422){
                    player.getLogManager().add(LogEntry.pickupItem(globalItem));
                    player.getInventory().add(globalItem.getItem());
                }
                if(globalItem.owner == null){
                    player.getAchievementTracker().pickupItemFromNpc(globalItem.getItem().getId(), globalItem.getItem().getCount());
                }else{
                    player.getAchievementTracker().pickupItemFromPlayer(globalItem.getItem().getId(), globalItem.getItem().getCount());
                }
                removeItem(globalItem);
                globalItem.destroy();
            }
        }else{
            //just remove it and pretend it was null
            player.getActionSender().removeGlobalItem(item, Location.create(x, y, 0));
            //player.getActionSender().sendMessage("That item doesn't exist.");
        }
    }

    public void removeItem(final GlobalItem globalItem) {
        for(final Player p2 : World.getWorld().getPlayers()){
            if(p2.getLocation().isWithinDistance(globalItem.getLocation(), 64))
                p2.getActionSender().removeGlobalItem(globalItem.getItem(), globalItem.getLocation());
        }
    }

    public void createItem(final GlobalItem globalItem) {
        for(final Player p2 : World.getWorld().getPlayers()){
            if(p2 == globalItem.owner)
                continue;
            if(p2.getLocation().isWithinDistance(globalItem.getLocation(), 64))
                p2.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
        }
    }

    public void process() {
        Object[] items = null;
        synchronized(globalItems){
            items = globalItems.toArray();
        }
        for(final Object object : items){
            GlobalItem gItem = (GlobalItem) object;
            if(System.currentTimeMillis() - gItem.createdTime >= 52000 && gItem.itemHidden){
                if(!ItemsTradeable.isTradeable(gItem.getItem().getId())){
                    continue;
                }
                createItem(gItem);
                gItem.itemHidden = false;
            }else if(System.currentTimeMillis() - gItem.createdTime >= 120000){
                removeItem(gItem);
                synchronized(globalItems){
                    globalItems.remove(gItem);
                }
                gItem.destroy();
                gItem = null;
            }
        }
    }


}