package org.hyperion.rs2.model;

import org.hyperion.engine.task.Task;
import org.hyperion.rs2.model.content.ContentEntity;

import java.util.LinkedList;
import java.util.List;

public class GlobalItemManager {

    public static void init() {
        World.submit(new Task(1000, "globalitems") {
            @Override
            public void execute() {
                process();
            }
        });
    }

    public static final List<GlobalItem> GLOBAL_ITEMS = new LinkedList<GlobalItem>();

    public static void displayItems(Player player) {
        for (GlobalItem globalItem : GLOBAL_ITEMS) {
            if ((!globalItem.itemHidden || globalItem.owner == player) && globalItem.getItem() != null && player.getLocation().isWithinDistance(globalItem.getLocation(), 64)) {
                //display the item
                if (!ItemsTradeable.isTradeable(globalItem.getItem().getId()) && globalItem.owner != player) {
                    continue;
                }
                //System.out.println("displaying items " + globalItem.getItem().getDefinition().getName());
                player.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
            }
        }
    }

    public static GlobalItem getItem(int id, Location loc) {
        for (GlobalItem g : GLOBAL_ITEMS) {
            //System.out.println(""+g.getItem().getId()+", "+g.getLocation().getX()+", "+g.getLocation().getY());
            if (g.getLocation().distance(loc) == 0) {
                if (g.getItem().getId() == id) {
                    return g;
                }
            }
        }
        return null;
    }

    public static void newDropItem(Player player, GlobalItem globalItem) {
        GlobalItem onGround = getItem(globalItem.getItem().getId(), globalItem.getLocation());
        if (onGround != null && onGround.getItem().getDefinition().isStackable()) {
            int count = onGround.getItem().getCount() + globalItem.getItem().getCount();
            onGround.setNewItem(new Item(onGround.getItem().getId(), count));
        } else {
            GLOBAL_ITEMS.add(globalItem);
            if (player.getLocation().isWithinDistance(globalItem.getLocation(), 64))
                player.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
        }
    }

    public static void addToItems(GlobalItem globalItem) {
        GLOBAL_ITEMS.add(globalItem);
    }

    public static void dropItem(Player player, int itemId, int slot) {
        Item item = player.getInventory().get(slot);
        if (item == null || itemId != item.getId()) {
            player.getActionSender().sendMessage("Client is out of sync, Please logout.");
            return;
        }
        GlobalItem globalItem = new GlobalItem(player, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ(), new Item(item.getId(), item.getCount()));
        GLOBAL_ITEMS.add(globalItem);
        player.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
        player.getInventory().remove(item);
    }

    public static void pickupItem(Player player, int item, int x, int y) {
        if (ContentEntity.freeSlots(player) <= 0) {
            ContentEntity.sendMessage(player, "You have not got enough space to pick up that item");
            return;
        }
        GlobalItem globalItem = null;
        for (GlobalItem g : GLOBAL_ITEMS) {
            if (g.getLocation().getX() == x && g.getLocation().getY() == y && g.getLocation().getZ() == player.getLocation().getZ()) {
                if (g.getItem().getId() == item) {
                    globalItem = g;
                    break;
                }
            }
        }
        if (globalItem != null) {
            if (globalItem.owner != null && globalItem.owner.getGameMode() != player.getGameMode()) {
                player.sendMessage("You cannot pick up this item as it belongs to another game mode");
                return;
            }
            player.getExpectedValues().pickupItem(globalItem.getItem());
            GLOBAL_ITEMS.remove(globalItem);
            if (item != 2422) {
                //player.getLogManager().add(LogEntry.pickupItem(globalItem));
                player.getInventory().add(globalItem.getItem());
            }
            if (globalItem.owner == null) {
                player.getAchievementTracker().pickupItemFromNpc(globalItem.getItem().getId(), globalItem.getItem().getCount());
            } else {
                player.getAchievementTracker().pickupItemFromPlayer(globalItem.getItem().getId(), globalItem.getItem().getCount());
            }
            removeItem(globalItem);
            globalItem.destroy();
        } else {
            //just remove it and pretend it was null
            player.getActionSender().removeGlobalItem(item, Location.create(x, y, 0));
            //player.getActionSender().sendMessage("That item doesn't exist.");
        }
    }

    public static void removeItem(GlobalItem globalItem) {
        for (Player p2 : World.getPlayers()) {
            if (p2.getLocation().isWithinDistance(globalItem.getLocation(), 64))
                p2.getActionSender().removeGlobalItem(globalItem.getItem(), globalItem.getLocation());
        }
    }

    public static void createItem(GlobalItem globalItem) {
        for (Player p2 : World.getPlayers()) {
            if (p2 == globalItem.owner)
                continue;
            if (p2.getLocation().isWithinDistance(globalItem.getLocation(), 64))
                p2.getActionSender().createGlobalItem(globalItem.getLocation(), globalItem.getItem());
        }
    }

    public static void process() {
        //TODO REWRITE THIS WITH AN ITERATOR
        for (Object object : GLOBAL_ITEMS.toArray()) {
            GlobalItem gItem = (GlobalItem) object;
            if (System.currentTimeMillis() - gItem.createdTime >= 52000 && gItem.itemHidden) {
                if (!ItemsTradeable.isTradeable(gItem.getItem().getId())) {
                    continue;
                }
                createItem(gItem);
                gItem.itemHidden = false;
            } else if (System.currentTimeMillis() - gItem.createdTime >= 120000) {
                removeItem(gItem);
                GLOBAL_ITEMS.remove(gItem);
                gItem.destroy();
            }
        }
    }
}