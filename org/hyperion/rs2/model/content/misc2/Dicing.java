package org.hyperion.rs2.model.content.misc2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.content.ClickType;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.ContentTemplate;
import org.hyperion.rs2.model.content.clan.Clan;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.shops.DonatorShop;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.sql.requests.QueryRequest;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

/**
 * @author Arsen Maxyutov.
 */

public class Dicing implements ContentTemplate {

	public static HashMap<Entity, SecureRandom> dicingRandoms = new HashMap<Entity, SecureRandom>();
    private static Map<Integer, Integer> pkpValues = new HashMap<>();
	
	public static final HashMap<Integer, Integer> gambled = new HashMap<Integer, Integer>();

	public static final int DICE_ID = 15098;

	private static String badLuckDicer = "";

	public static void setBadLuckDicer(String name) {
		badLuckDicer = name;
	}

	private static void startRollingDice(Player player) {
		ContentEntity.startAnimation(player, 11900);
		ContentEntity.sendMessage(player, "Rolling...");
		player.playGraphics(Graphic.create(2075, 0));
	}

	public static void rollClanDice(final Player player, int value) {
		player.getActionSender().sendMessage("Clan dicing has been disabled.");
		if(true)
			return;
		if(player.getClanName().equals("")) {
			player.getActionSender().sendMessage("You must be in a clan chat channel to do that.");
			return;
		}
		if(player.getClanRank() <= 0) {
			player.getActionSender().sendMessage("You need to be a clan mod to do that, in a moderators channel.");
			return;
		}
		if(! player.getClanName().equals("monsterman") && ! player.getClanName().equals("z") && ! player.getClanName().equals("agent pk") && ! player.getClanName().equals("deranker")
				&& ! player.getClanName().equals("kaiba") && ! player.getClanName().equals("dj house")) {
			player.getActionSender().sendMessage("You need to be a clan mod to do that, in a moderators channel.");
			return;
		}
		final Clan clan = ClanManager.clans.get(player.getClanName());
		if(clan == null)
			return;
		if(player.getName().equalsIgnoreCase(badLuckDicer)) {
			int temp = getRandomNumber(player, 100);
			System.out.println(temp + "      " + value);
			if(temp > value)
				value = temp;
			badLuckDicer = "";
		}
		final int thrown = value;
		startRollingDice(player);
		World.getWorld().submit(new Event(3000) {
			public void execute() {
				ClanManager.sendDiceMessage(player, clan, thrown);
				this.stop();
			}
		});
	}
	public static synchronized void put(int k, int v) {
		gambled.put(k, v);
	}
	
	public static synchronized void remove(int k) {
		gambled.remove(k);
	}
	
	public static synchronized Integer get(int k) {
		return gambled.get(k);
	}
	
	public static synchronized Item[] getGambledItems() {
		List<Item> item = new LinkedList<Item>();
		for(int k : gambled.keySet()) {
			item.add(new Item(k, get(k)));
		}
		return item.toArray(new Item[item.size()]);
	}
	/*private static int applyCheats(int r, int id) {
	    switch(id){
		case 14484:
		case 1370:
			if(Math.random() > 0.5){
				r = Math.min(r, getRandomNumber(100));
			}
		break;
		case 19780:
			if(Math.random() > 0.90)
				r = Math.max(r, getRandomNumber(100));
			break;
		}
		return r;
	}*/


    public static void diceNpc(final Player player, final NPC dicer, final Item item) {
        if(item == null)
            return;
        if(pkpValues.containsKey(item.getId())) {
            player.getActionSender().sendDialogue("Would you like", ActionSender.DialogueType.OPTION, 1, Animation.FacialAnimation.DEFAULT,
                    "Gamble for " + pkpValues.get(item.getId()) * 2 + " PKT", "Gamble for 2X the item");
            player.getInterfaceState().setNextDialogueId(0, 8500);
            player.getInterfaceState().setNextDialogueId(1, 8501);
            player.getExtraData().put("npcdiceitem", item);
            player.getExtraData().put("npcdice", dicer);
            return;
        }

        diceNpc(player, dicer, item, player.getExtraData().getBoolean("diceforpkp"));
    }
	/**
	 * Dices an item.
	 *
	 * @param player
	 * @param item
	 */
	public static void diceNpc(final Player player, final NPC dicer, final Item item, boolean toPkp) {
		if(item == null || dicer == null)
			return;
        player.getExtraData().put("diceforpkp", false);
		if(player.isDead()) {
			player.getActionSender().sendMessage("The gambler doesn't have a very strong stomache!");
		}
		if(DonatorShop.isVeblenGood(item.getId())) {
			player.getActionSender().sendMessage("Sorry, such exclusive items can not be played with..");
			return;
		}
		if(! (ItemSpawning.allowedMessage(item.getId()).length() > 1)) {
			player.getActionSender().sendMessage("Sorry, I only gamble with unspawnables.");
			return;
		}
        if((item.getId() >= 13195 && item.getId() <= 13205)) {
            player.sendMessage("The gambler doesn't know what to do with these...");
            return;
        }
		if(item.getCount() > 1000 && !Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
			player.getActionSender().sendMessage("You can't gamble more than 1000 of an item!");
			return;
		}

        if(item.getCount() > 20 && item.getId() == 13663) {
            player.getActionSender().sendMessage("Exhanging over 20 of these poses a security risk");
            return;
        }

		if(item.getCount() > 50 && item.getId() == 3062) {
			player.getActionSender().sendMessage("These boxes are simply too large to take in bulk!");
			return;
		}
        if(item.getCount() > 500 && item.getId() == 5020 && !Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
            player.getActionSender().sendMessage("These tickets are simply too large to take in bulk!");
            return;
        }
		if(! item.getDefinition().isStackable()) {
			if(player.getInventory().freeSlots() < 1) {
				player.getActionSender().sendMessage("You need some free spots before you can dice.");
				return;
			}
		}
		dicer.forceMessage("Rolling...");
		final int count = item.getCount();
		final int id = item.getId();
		player.getInventory().remove(new Item(item.getId(), item.getCount()));
		World.getWorld().submit(new Event(2000, "checked") {
			@Override
			public void execute() {
				int r = getRandomNumber(dicer, 100);
				if(id == 5020 || id == 3062) {
					if(r >= 55) {
						if(Misc.random(3) < 1) {
							r = new java.util.Random().nextInt(60);
						}
					}
				}

				//don't need since it'll remov before
				/*if(player.getInventory().getCount(item.getId()) < item.getCount()) {
					this.stop(); //Incase player would store item in BoB in these 2 seconds of wait.
					return;
				}*/
				int itemvalue = DonatorShop.getPrice(id) * count;

                if(itemvalue > 20_000)
                    r = Misc.random(54);
                if(itemvalue > 10_000)
                    r = Misc.random(75);

                player.getActionSender().sendMessage("The npc rolled @red@" + r + "@bla@ on the percentile dice.");
                dicer.forceMessage(r + "!");
				String query = null;
                player.getLogManager().add(LogEntry.gamble(dicer, item, r));
				if(r >= 55) {
					int amount = count;
					if(amount > 10 && Misc.random(2) == 0) {
						amount *= .9;
						player.getActionSender().sendMessage("@red@The gambler feels greedy and takes a 10% cut!");
					}
                    if(toPkp)
                        player.getInventory().add(Item.create(5020, pkpValues.get(id) * 2));
                    else
                        player.getInventory().add(new Item(id, amount*2));
					player.getActionSender().sendMessage("You have won the item!");
					player.setDiced(player.getDiced() + itemvalue);
					query = "INSERT INTO dicing(username,item_id,item_count,win_value) "
							+ "VALUES('" + player.getName().toLowerCase() + "'," + id + "," + count + "," + itemvalue + ")";

				} else {
					int previous = 0;
					if(get(id) != null) {
						previous = get(id);
						remove(id);
					}
					put(id, count + previous);
					player.getActionSender().sendMessage("You have lost your item.");
					//System.out.println("Count before: " + player.getInventory().getCount(item.getId()));
					//System.out.println("Count after: " + player.getInventory().getCount(item.getId()));
					player.setDiced(player.getDiced() - itemvalue);
					query = "INSERT INTO dicing(username,item_id,item_count,win_value) "
							+ "VALUES('" + player.getName().toLowerCase() + "'," + id + "," + count + "," + - 1 * itemvalue + ")";
				}
				if(itemvalue > 0) {
					World.getWorld().getLogsConnection().offer(new QueryRequest(query));
				}
                PlayerSaving.getSaving().save(player);
				//System.out.println("Wins: " + wins + ", loses: " + loses);
				this.stop();
			}
		});

	}

	public static void rollPrivateDice(final Player player) {
		startRollingDice(player);
		World.getWorld().submit(new Event(3000) {
			public void execute() {
				int thrown = getRandomNumber(player, 100);
				player.getActionSender().sendMessage("You roll @red@" + thrown + "@bla@ on the percentile dice.");
				this.stop();
			}
		});
	}

	public static int getRandomNumber(Entity entity, int n) {
		if(dicingRandoms.containsKey(entity)) {
			return dicingRandoms.get(entity).nextInt(100);
		} else {
			SecureRandom secureRandom = new SecureRandom();
			dicingRandoms.put(entity, secureRandom);
			return secureRandom.nextInt(100);
		}
	}

	@Override
	public boolean clickObject(Player player, int type, int a, int b, int c, int d) {
		//System.out.println("Type is " + type);
		if(type == 1) {
			rollPrivateDice(player);
			return false;
		}
		return false;
	}


	@Override
	public void init() throws FileNotFoundException {
        World.getWorld().submit(new Event(Time.FIVE_MINUTES) {
            public void execute() {
                try {
                    final List<String> lines = Files.readAllLines(new File("./data/dontopkp.txt").toPath());
                    for(String s : lines) {
                        final String[] split = s.split(":");
                        pkpValues.put(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                    }
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

	@Override
	public int[] getValues(int type) {
		// TODO Auto-generated method stub
		if(type == 1/* || type == 3*/) {
			int[] diceIds = {DICE_ID};
			return diceIds;
		}
        if(type == ClickType.DIALOGUE_MANAGER)
            return new int[]{8500, 8501};
		return null;
	}

    @Override
    public boolean dialogueAction(final Player player, int id) {
        System.out.println("HERE + "+((Item)player.getExtraData().get("npcdiceitem")).getId());
        switch(id) {
            case 8500:
                diceNpc(player, (NPC)player.getExtraData().get("npcdice"), (Item)player.getExtraData().get("npcdiceitem"), true);
                break;
            case 8501:
                diceNpc(player, (NPC)player.getExtraData().get("npcdice"), (Item)player.getExtraData().get("npcdiceitem"), false);
                break;
        }
        return true;
    }

}
