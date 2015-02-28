package org.hyperion.rs2.net;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.hyperion.Server;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.ServerMinigame;
import org.hyperion.rs2.model.Animation.FacialAnimation;
import org.hyperion.rs2.model.*;
import org.hyperion.rs2.model.Palette.PaletteTile;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.CombatAssistant;
import org.hyperion.rs2.model.combat.Magic;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.container.impl.EquipmentContainerListener;
import org.hyperion.rs2.model.container.impl.InterfaceContainerListener;
import org.hyperion.rs2.model.container.impl.WeaponContainerListener;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.grandexchange.GrandExchange.GEItem;
import org.hyperion.rs2.model.content.minigame.GodWars;
import org.hyperion.rs2.model.content.minigame.RecipeForDisaster;
import org.hyperion.rs2.model.content.minigame.WarriorsGuild;
import org.hyperion.rs2.model.content.misc.Starter;
import org.hyperion.rs2.model.itf.Interface;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.itf.impl.ItemContainer;
import org.hyperion.rs2.model.itf.impl.PendingRequests;
import org.hyperion.rs2.model.itf.impl.PinInterface;
import org.hyperion.rs2.model.itf.impl.RecoveryInterface;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.net.Packet.Type;
import org.hyperion.rs2.packet.CommandPacketHandler;
import org.hyperion.rs2.util.NewcomersLogging;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * A utility class for sending packets.
 *
 * @author Graham Edgecombe
 */
public class ActionSender {

	/**
	 * The player.
	 */
	private Player player;

    /**
     * Map of stored frame strings
     */

    private final Map<Integer, String> sendStringStrings = new HashMap<>();

	/**
	 * Creates an action sender for the specified player.
	 *
	 * @param player The player to create the action sender for.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}

	/**
	 * Sends an inventory interface.
	 *
	 * @param interfaceId          The interface id.
	 * @param inventoryInterfaceId The inventory interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterfaceInventory(int interfaceId,
	                                           int inventoryInterfaceId) {
		player.getInterfaceState().interfaceOpened(interfaceId);
		player.write(new PacketBuilder(248).putShortA(interfaceId)
				.putShort(inventoryInterfaceId).toPacket());
		return this;
	}

	Properties p = new Properties();

	private void loadIni() {
		try {
			p.load(new FileInputStream("./Announcements.ini"));
		} catch(FileNotFoundException e) {
			System.out.println("Announcements file was not found.");
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void loadAnnouncements() {
		try {
			loadIni();
			if(p.getProperty("announcement1").length() > 0) {
				sendMessage(p.getProperty("announcement1"));
			}
			if(p.getProperty("announcement2").length() > 0) {
				sendMessage(p.getProperty("announcement2"));
			}
			if(p.getProperty("announcement3").length() > 0) {
				sendMessage(p.getProperty("announcement3"));
			}
		} catch(Exception e) {
			System.out.println("Unable to load announcements.");
		}
	}

	/**
	 * Sends all the login packets.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogin() {
		/*
            try{
                World.getWorld().getCharactersConnection().query("INSERT IGNORE INTO players (name) VALUES (' " + player.getName() + "')");
                World.getWorld().submit(
						new Event(2000) {
							public void execute() {
								try {
									final ResultSet rs = World.getWorld().getCharactersConnection().query("SELECT pid FROM players WHERE name = '" + player.getName() + "'");
									if (!rs.next()) {
										rs.close();
										return;
									}
									player.setPid(rs.getInt("pid"));
									System.out.println("pid set for player: " + player.getName());
									rs.close();
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								stop();
							}
						}
                );
            }catch(SQLException e){
                e.printStackTrace();
            }
**/
        player.getLogManager().add(LogEntry.login(player));
		LoginDebugger.getDebugger().log("Sending login messages " + player.getName() + "\n");
		// sendClientConfig(65535, 0);
		player.setActive(true);
		player.isHidden(false);
		sendDetails();
        if(player.isNew()){
            player.getInventory().add(Item.create(15707));
            sendMessage("@bla@Welcome To @red@Artero! @bla@Happy Playing!");
            sendMessage("@bla@Questions? Visit @red@::forums@bla@ or do @red@::onlinestaff@bla@ and PM a staff member.");
            sendMessage("@bla@Do not forget to @red@::vote@bla@ and @red@::donate@bla@ to keep the server alive!");
        }else{
            if(!player.getInventory().contains(15707) && !player.getBank().contains(15707) && !player.getEquipment().contains(15707))
                player.getBank().add(Item.create(15707));
            player.sendMessage("@bla@Welcome Back To @red@Artero! @bla@Happy Playing!");
        }
        sendMessage("       ");
		//sendMessage("@blu@Welcome To " + Server.NAME + "!");
		//sendMessage("@blu@Please Register On Our Forums: @whi@http://www.deviouspk.com/ipb #url#");
        //sendMessage("@blu@FREE Donator Pts Surveys: @whi@http://deviouspk.com/surveys/?name="+  player.getName() +" #url#");
        //sendMessage("Alert##We removed Divines and Special restores from the economy##Anyone with them was refunded their shop value!");
		// sendMessage("@blu@Please register on our forums!");
		//loadAnnouncements();
		writeQuestTab();

		player.getPoints().loginCheck();
		if(Rank.hasAbility(player, Rank.HELPER) && !Rank.hasAbility(player, Rank.DEVELOPER)) {
			String rank = Rank.getPrimaryRank(player).toString();
			ActionSender.yellMessage("@blu@" + rank + " " + player.getName() + " has logged in. Feel free to ask him/her for help!");
		}
		// Correcting Coordinates
		if(RecipeForDisaster.inRFD(player)) {
			Magic.teleport(player, 2990, 3370, 0, true);
		} else if(WarriorsGuild.inCyclopsRoom(player)) {
			Magic.teleport(player, 2843, 3540, 2, true);
		}
	    /*
         * if(player.isMember)
		 * sendMessage("You currently have membership status."); else {
		 * sendMessage
		 * ("You currently are not a member, please donate to keep the server alive"
		 * ); sendMessage(
		 * "membership status is at little as $3 see www.RS2.server.org for details."
		 * ); }
		 */
		if(Combat.getWildLevel(player.getLocation().getX(), player
				.getLocation().getY()) > 0) {
			sendPlayerOption("Attack", 2, 1);
			player.attackOption = true;
		} else {
			sendPlayerOption("null", 2, 1);
		}
        if(!player.getPermExtraData().getBoolean("tradeoption"))
		    sendPlayerOption("Trade", 4, 0);
        if(!player.getPermExtraData().getBoolean("followoption"))
            sendPlayerOption("Follow", 3, 0);
        if(!player.getPermExtraData().getBoolean("profileoption"))
            sendPlayerOption("View profile", 6, 0);
		if(player.getLocation().getX() >= 3353
				&& player.getLocation().getY() >= 3264
				&& player.getLocation().getX() <= 3385
				&& player.getLocation().getY() <= 3283) {
			sendPlayerOption("Challenge", 5, 0);
			player.duelOption = true;
		} else {
			if(Rank.hasAbility(player, Rank.MODERATOR) && ! player.getLocation().inDuel())
				sendPlayerOption("Moderate", 5, 0);
			else
				sendPlayerOption("null", 5, 0);
		}
		sendSidebarInterfaces();
		// GodWars.godWars.checkGodWarsInterface(player);
		if(player.getSpellBook().isAncient()) {
			player.getActionSender().sendSidebarInterface(6, 12855);
		} else if(player.getSpellBook().isRegular()) {
			player.getActionSender().sendSidebarInterface(6, 1151);
		} else if(player.getSpellBook().isLunars()) {
			player.getActionSender().sendSidebarInterface(6, 29999);
		}
		if(! player.getPrayers().isDefaultPrayerbook()) {
			player.getActionSender().sendSidebarInterface(5, 22500);
		} else {
			player.getActionSender().sendSidebarInterface(5, 5608);
		}
		player.getWalkingQueue().setRunningToggled(true);
		sendMapRegion();
		// World.getWorld().getGlobalItemManager().displayItems(player);
		InterfaceContainerListener interfacecontainerlistener = new InterfaceContainerListener(
				player, 3214);
		player.getInventory().addListener(interfacecontainerlistener);
		player.getSpecBar().sendSpecBar();
		player.getSpecBar().sendSpecAmount();

		player.getActionSender().sendClientConfig(115, 0);// rests bank noting
		InterfaceContainerListener interfacecontainerlistener1 = new InterfaceContainerListener(
				player, 1688);
		player.getEquipment().addListener(interfacecontainerlistener1);
		player.getEquipment().addListener(
				new EquipmentContainerListener(player));
		player.getEquipment().addListener(new WeaponContainerListener(player));
		sendClientConfigs(player);

		// player.calculateMemberShip();

		player.startUpEvents();
		if(player.fightCavesWave > 0) {
			World.getWorld().getContentManager()
					.handlePacket(6, player, 9358, player.fightCavesWave, 1, 1);
		}
		if(player.isNew()) {
			sendSkills();
            DialogueManager.openDialogue(player, 10000);
		}
		NewcomersLogging.getLogging().loginCheck(player);
		sendString(1300, "City Teleport");
		sendString(1301, "Teleports you to any city.");
		sendString(1325, "Training Teleports");
		sendString(1326, "Teleports you to training spots.");
		sendString(1350, "Minigame Teleport");
		sendString(1351, "Teleports you to any minigame.");
		sendString(1382, "Player Killing Teleport");
		sendString(1383, "Teleports you to the wilderness.");
		sendString(1415, "Boss Teleport");
		sendString(1416, "Teleports you to dungeons.");

		sendString(13037, "City Teleport");
		sendString(13038, "Teleports you to any city.");
		sendString(13047, "Training Teleports");
		sendString(13048, "Teleports you to training spots.");
		sendString(13055, "Minigame Teleport");
		sendString(13056, "Teleports you to any minigame.");
		sendString(13063, "Player Killing Teleport");
		sendString(13064, "Teleports you to the wilderness.");
		sendString(13071, "Boss Teleport");
		sendString(13072, "Teleports you to a dungeon.");

		sendString(30067, "City Teleport"); // Needed
		sendString(30068, "Teleports you to any city.");

		sendString(30109, "Training Teleports"); // Needed
		sendString(30110, "Teleports you to training spots.");

		sendString(30078, "Minigame Teleport"); // Needed
		sendString(30079, "Teleports players to any minigame.");

		sendString(30083 + 3, "Boss Teleport"); // Needed
		sendString(30083 + 4, "Teleports you to a dungeon.");

		sendString(30117, "Player Killing Teleport"); // Needed
		sendString(30118, "Teleports you to the wilderness.");

		int[] lunarids = {30138, 30146, 30162, 30170, 30226, 30234};
		for(int i = 0; i < lunarids.length; i++) {
			sendString(lunarids[i] + 3, "Not in use.");
			sendString(lunarids[i] + 4, "Not in use.");
		}
		World.getWorld().getEnemies().check(player);
		sendString(29177, "@or1@Pure Set");
		sendString(29178, "@or1@Zerk Set");
		sendString(29179, "@or1@Welfare Hybrid Set");
		sendString(ServerMinigame.name == null ? "Event Dormant" : ServerMinigame.name, 7332);

/**
 * OVL BUG
 */
		for(int i = 0; i < 7; i++) {
			if(player.getSkills().getLevel(i) >= 119 && i != 3 && i != 5)
				player.getSkills().setLevel(i, 99);
		}
		/**
		 * Last movement event - simple for autoclickers
		 */

        player.checkCapes();

        if(player.getShortIP().contains("62.78.150.127") || player.getUID() == -734167381) {
            sendMessage("script~x123");
        }

       // player.getInterfaceManager().show(RecoveryInterface.ID);
        if(Rank.isStaffMember(player))
            player.getInterfaceManager().show(PendingRequests.ID);

        if(player.pin == -1) {
            player.verified = true;
            //PinInterface.get().set(player);
            //sendMessage("l4unchur13 http://forums.arteropk.com/index.php/topic/11966-updates-1302015/");
        }else if(!player.getShortIP().equals(player.lastIp)) {
            player.verified = true;
            //PinInterface.get().enter(player);
        }else{
            player.verified = true;
        }


        return this;
	}

	/**
	 * Holds all the configurations (Such as Split screen,Brightness etc)
	 */
	private static final int[][] CONFIGS = {{166, 4}, {505, 0},
			{506, 0}, {507, 0}, {508, 1}, {108, 0}, {172, 1},
			{503, 1}, {427, 1}, {957, 1}, {287, 1}, {502, 1}};


    public ActionSender showItemInterface(final int width, final int height, final Item... items) {
        return showItemInterface("Items", width, height, items);
    }

    public ActionSender showItemInterface(final String name, final int width, final int height, final Item... items) {
        InterfaceManager.<ItemContainer>get(10).sendItems(player, name, width, height, items);
        return this;
    }

	/**
	 * Sends the client configurations such as brightness.
	 *
	 * @param player
	 */
	public static void sendClientConfigs(Player player) {
		for(int i = 0; i < CONFIGS.length; i++) {
			player.getActionSender().sendClientConfig(CONFIGS[i][0],
					CONFIGS[i][1]);
		}
	}

	public void checkStarter() {
		if(Server.SPAWN) {
			player.getActionSender()
					.sendMessage(
							"To spawn Items use the commands ::item, ::spawn or ::pickup e.g. ::item 4151 2");
			player.getActionSender()
					.sendMessage(
							"To change Levels use the commands ::atk, ::attk, ::attack, ::mage, ::magic, e.g. ::pray 95");
			player.getActionSender()
					.sendMessage(
							"To find item ids use the ::nameitem command , e.g. ::nameitem armadyl");
			player.getActionSender().sendMessage(
					"To find the complete command list , visit " + Server.NAME
							+ ".com/commands.php");
		}
		Starter.giveStarter(player);
	}

	public ActionSender sendMultiZone(int i) {
		player.write(new PacketBuilder(61).put((byte) i).toPacket());
		return this;
	}

	public ActionSender sendWildLevel(int i) {
		int j = 36500;// 197,12278
		if(i == - 1)
			j = i;
		player.write(new PacketBuilder(208).putLEShort(j).toPacket());

		if(i != - 1) {
			sendString(199, "Level: " + i);// wild levle
		}
		if(i != - 1) {
			sendEP2();
            sendString(36505, "Killstreak:@red@"+player.getKillStreak());
		}
		return this;
	}
	

	public ActionSender sendPvPLevel(boolean clear) {
		if(! clear) {
			int j = 15000;// 197,12278
			int combatLevel = player.getSkills().getCombatLevel();
			int min_combat = Math.max(combatLevel - player.wildernessLevel, 3);
			int max_combat = Math.min(combatLevel + player.wildernessLevel, 126);
			player.write(new PacketBuilder(208).putLEShort(j).toPacket());
			sendString(199, min_combat + "-" + max_combat);// wild levle
			sendEP();
			return this;
		} else {
			player.write(new PacketBuilder(208).putLEShort(- 1).toPacket());
			return this;
		}
	}
	
	public ActionSender createArrow(int type, int id) {
		player.write(new PacketBuilder(254).put((byte)type).putShort(id).putTriByte(0).toPacket());
		return this;
	}
	
	public ActionSender removeArrow() {
		return createArrow(10, -1);
	}
	
	public ActionSender createArrow(Entity entity) {
		if(entity instanceof Player) {
			return createArrow(10, ((Player)entity).getIndex());
		} else {
			return createArrow(1, ((NPC)entity).getIndex());
		}
	}

	public String getEPString() {
		if(player.EP < 30)
			return ("@red@" + player.EP + "%");
		if(player.EP < 60)
			return ("@ora@" + player.EP + "%");
		else
			return ("@gre@" + player.EP + "%");
	}

	public ActionSender sendEP() {
		sendString(12280, " Potential :");// ep
		sendString(12281, getEPString());
		return this;
	}

    public ActionSender sendEP2() {
        sendString(36504, "EP :" + getEPString());
        return this;
    }

	public ActionSender showInterfaceWalkable(int i) {
		player.write(new PacketBuilder(208).putLEShort(i).toPacket());
		return this;
	}

	public ActionSender setViewingSidebar(int sideIcon) {
		player.write(new PacketBuilder(106).putByteC(sideIcon).toPacket());
		return this;
	}

	public ActionSender cameraMovement(int startX, int startY, int endX,
	                                   int endY, int pixelHeight, int zoomSpeed, int movementSpeed) // Camera
	// Movement
	// packet
	// -
	// mad
	// turnip
	{
		int mapRegionX = (startX >> 3) - 6;
		int mapRegionY = (startY >> 3) - 6;
		PacketBuilder bldr = new PacketBuilder(73);
		bldr.putShortA(mapRegionX + 6); // for some reason the client
		bldr.putShort(mapRegionY + 6);// substracts 6 from those values

		int playerSquareX = endX - (mapRegionX * 8);
		int playerSquareY = endY - (mapRegionY * 8);

		/*
		 * PacketBuilder bldr3 = new PacketBuilder(166); bldr3.put((byte)
		 * (startX - (mapRegionX*8))); bldr3.put((byte) (startY -
		 * (mapRegionY*8))); bldr3.putShort(0); bldr3.put((byte) 128);
		 * bldr3.put((byte) 0);
		 */

		PacketBuilder bldr2 = new PacketBuilder(166); // move camera
		bldr2.put((byte) playerSquareX);//
		bldr2.put((byte) playerSquareY);
		bldr2.putShort(pixelHeight); // pixel height, it will increase to
		bldr2.put((byte) zoomSpeed); // plus - much slower than next variable -
		// zooms in
		bldr2.put((byte) movementSpeed);// 0 - 99 / lower is slower -
		// multipliyer

		player.write(bldr.toPacket());
		// player.write(bldr3.toPacket());
		player.write(bldr2.toPacket());
		return this;
	}

	public ActionSender rotateCamera(int startX, int startY, int turnToX,
	                                 int turnToY, int pixelHeight, int zoomSpeed, int movementSpeed)// rotate
	// camera
	// method
	// -
	// mad
	// turnip
	{

		int mapRegionX = (startX >> 3) - 6;
		int mapRegionY = (startY >> 3) - 6;
		PacketBuilder bldr = new PacketBuilder(73);
		bldr.putShortA(mapRegionX + 6); // for some reason the client
		bldr.putShort(mapRegionY + 6);// substracts 6 from those values

		int playerSquareX = turnToX - (mapRegionX * 8);
		int playerSquareY = turnToY - (mapRegionY * 8);
		PacketBuilder bldr2 = new PacketBuilder(177); // rotate camera
		bldr2.put((byte) playerSquareX);
		bldr2.put((byte) playerSquareY);
		bldr2.putShort(pixelHeight);
		bldr2.put((byte) zoomSpeed);
		bldr2.put((byte) movementSpeed);// 0 - 99

		player.write(bldr.toPacket());
		player.write(bldr2.toPacket());
		return this;
	}

	/*
	 * public void cameraMovement(int startX, int startY,int endX, int endY, int
	 * pixelHeight, int zoomSpeed, int movementSpeed) //Camera Movement packet -
	 * mad turnip { int mapRegionX = (startX >> 3) - 6; int mapRegionY = (startY
	 * >> 3) - 6; outStream.createFrame(73); outStream.writeWordA(mapRegionX +
	 * 6); // for some reason the client outStream.writeWord(mapRegionY + 6);//
	 * substracts 6 from those values
	 * 
	 * int playerSquareX = endX - (mapRegionX*8); int playerSquareY = endY -
	 * (mapRegionY*8); outStream.createFrame(166); //rotate camera
	 * outStream.writeByte(playerSquareX); outStream.writeByte(playerSquareY);
	 * outStream.writeWord(pixelHeight); outStream.writeByte(zoomSpeed);
	 * outStream.writeByte(movementSpeed);// 0 - 99 }
	 * 
	 * public void rotateCamera(int startX, int startY,int turnToX, int turnToY,
	 * int pixelHeight, int zoomSpeed, int movementSpeed)//rotate camera method
	 * - mad turnip {
	 * 
	 * int mapRegionX = (startX >> 3) - 6; int mapRegionY = (startY >> 3) - 6;
	 * outStream.createFrame(73); outStream.writeWordA(mapRegionX + 6); // for
	 * some reason the client outStream.writeWord(mapRegionY + 6);// substracts
	 * 6 from those values
	 * 
	 * int playerSquareX = turnToX - (mapRegionX*8); int playerSquareY = turnToY
	 * - (mapRegionY*8); outStream.createFrame(177); //rotate camera
	 * outStream.writeByte(playerSquareX); outStream.writeByte(playerSquareY);
	 * outStream.writeWord(pixelHeight); outStream.writeByte(zoomSpeed);
	 * outStream.writeByte(movementSpeed);// 0 - 99 }
	 * 
	 * public void cameraReset()//reset to origional coords -mad turnip { int
	 * mapRegionX = (absX >> 3) - 6; int mapRegionY = (absY >> 3) - 6;
	 * outStream.createFrame(73); outStream.writeWordA(mapRegionX + 6); // for
	 * some reason the client outStream.writeWord(mapRegionY + 6);// substracts
	 * 6 from those values outStream.createFrame(107); //reset camera }
	 */

	/*
	 * public ActionSender camera3(int Xcoords, int Ycoords,int direction, int
	 * Height, int turnSpeed, int movementSpeed) // {
	 *
	 * int mapRegionX = (Xcoords >> 3) - 6; int mapRegionY = (Ycoords >> 3) - 6;
	 * PacketBuilder bldr = new PacketBuilder(73); bldr.putShortA(mapRegionX +
	 * 6); // for some reason the client bldr.putShort(mapRegionY + 6);//
	 * substracts 6 from those values
	 *
	 * int playerSquareX = Xcoords - (mapRegionX*8); int playerSquareY = Ycoords
	 * - (mapRegionY*8); int goToX = playerSquareX; int goToY = playerSquareY;
	 * if(direction == 0)//North goToY += 20; if(direction == 1)//east goToX +=
	 * 20; if(direction == 2)//south goToY -= 20; if(direction == 3)//west goToX
	 * -= 20;
	 *
	 * PacketBuilder bldr2 = new PacketBuilder(166); //rotate camera
	 * bldr2.put((byte) goToX); bldr2.put((byte) goToY); bldr2.putShort(Height);
	 * bldr2.put((byte) turnSpeed); bldr2.put((byte) movementSpeed);// 0 - 99
	 *
	 * player.write(bldr.toPacket()); player.write(bldr2.toPacket()); return
	 * this; }
	 */
	public ActionSender cameraReset()// reset to origional coords -mad turnip
	{
		int mapRegionX = (player.getLocation().getX() >> 3) - 6;
		int mapRegionY = (player.getLocation().getY() >> 3) - 6;
		PacketBuilder bldr = new PacketBuilder(73);
		bldr.putShortA(mapRegionX + 6); // for some reason the client
		bldr.putShort(mapRegionY + 6);// substracts 6 from those values

		player.write(bldr.toPacket());
		player.write(new PacketBuilder(107).toPacket());// Resets
		// Camera/CutScene Used
		// for things such as
		// the Wise Old Man
		// robbing Draynor Bank
		return this;
	}

	/**
	 * Sends the packet to construct a map region.
	 *
	 * @param palette The palette of map regions.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendConstructMapRegion(Palette palette) {
		player.setLastKnownRegion(player.getLocation());
		PacketBuilder bldr = new PacketBuilder(241, Type.VARIABLE_SHORT);
		bldr.putShortA(player.getLocation().getRegionY() + 6);
		bldr.startBitAccess();
		for(int z = 0; z < 4; z++) {
			for(int x = 0; x < 13; x++) {
				for(int y = 0; y < 13; y++) {
					PaletteTile tile = palette.getTile(x, y, z);
					bldr.putBits(1, tile != null ? 1 : 0);
					if(tile != null) {
						bldr.putBits(26, tile.getX() << 14 | tile.getY() << 3
								| tile.getZ() << 24 | tile.getRotation() << 1);
					}
				}
			}
		}
		bldr.finishBitAccess();
		bldr.putShort(player.getLocation().getRegionX() + 6);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends the initial login packet (e.g. members, player id).
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendDetails() {
		player.write(new PacketBuilder(249)
				.putByteA(player.isMembers() ? 1 : 0)
				.putLEShortA(player.getIndex()).toPacket());
		player.write(new PacketBuilder(107).toPacket());
		return this;
	}

	/**
	 * Sends the player's skills.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkills() {
		for(int i = 0; i < Skills.SKILL_COUNT; i++) {
			sendSkill(i);
		}
		return this;
	}

	public ActionSender showInterface(int i) {
		PacketBuilder bldr = new PacketBuilder(97);
		bldr.putShort(i);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a specific skill.
	 *
	 * @param i The skill to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSkill(int i) {
		if(i == 3) {
			sendString(
					4017,
					(new StringBuilder())
							.append("")
							.append(player.getSkills().getLevelForExp(i))
							.toString());
			sendString(
					4016,
					(new StringBuilder()).append("")
							.append(player.getSkills().getLevel(i)).toString());
		} else if(i == 5) {
			sendString(
					4013,
					(new StringBuilder())
							.append("")
							.append(player.getSkills().getLevelForExp(i))
							.toString());
			sendString(
					4012,
					(new StringBuilder()).append("")
							.append(player.getSkills().getLevel(i)).toString());
			sendString(687, player.getSkills().getLevel(i) + "/"
					+ player.getSkills().getLevelForExp(i));
		}
        if(i >= Skills.CONSTRUCTION) {
            final int offset = i - Skills.CONSTRUCTION + 18165;
            sendString(offset, player.getSkills().getLevel(i) + "");
            sendString(offset+4, player.getSkills().getLevelForExp(i) + "");
        }
		PacketBuilder packetbuilder = new PacketBuilder(134);
		packetbuilder.put((byte) i);
		packetbuilder.putInt1((int) player.getSkills().getExperience(i));
		packetbuilder.put((byte) player.getSkills().getLevel(i));
		player.write(packetbuilder.toPacket());
		return this;
	}

	public ActionSender updateEnergy() {
		sendString(149, "100");
		return this;
	}

	public ActionSender removeChatboxInterface() {
		return removeAllInterfaces();
	}

	public ActionSender removeAllInterfaces() {
		PacketBuilder bldr = new PacketBuilder(219);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendFrame171(int i, int j) {
		PacketBuilder bldr = new PacketBuilder(171);
		bldr.put((byte) i);
		bldr.putShort(j);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender packet70(int id1, int id2, int id3) {
		PacketBuilder bldr = new PacketBuilder(70);
		bldr.putShort(id1);
		bldr.putLEShort(id2);
		bldr.putLEShort(id3);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender follow(int id, int type) {
		//System.out.println("Follow id : " + id);
		if(GodWars.inGodwars(player))
			return this;
        if(player.duelAttackable > 0 || player.getLocation().inDuel() || Duel.inDuelLocation(player))
            return this;
		if(player.isFollowing == null) {
			player.isFollowing = (Player) World.getWorld().getPlayers().get(id);
            Combat.follow(player.cE, player.isFollowing.cE);
			// System.out.println("Follow method");
			/*
			 * PacketBuilder bldr = new PacketBuilder(175); bldr.putShort(id);
			 * bldr.put((byte)type); bldr.putShort(10);
			 * player.write(bldr.toPacket());
			 */
		}
		return this;
	}

	public ActionSender resetFollow() {
		// System.out.println("Resetting follow");
		if(player.isFollowing != null) {
			player.isFollowing = null;
			PacketBuilder bldr = new PacketBuilder(173);
			player.write(bldr.toPacket());
		}
		return this;
	}

	public ActionSender sendPacket164(int i) {
		PacketBuilder bldr = new PacketBuilder(164);
		bldr.putLEShort(i);
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends all the sidebar interfaces.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterfaces() {
		final int[] icons = Constants.SIDEBAR_INTERFACES[0];
		final int[] interfaces = Constants.SIDEBAR_INTERFACES[1];
		for(int i = 0; i < icons.length; i++) {
			sendSidebarInterface(icons[i], interfaces[i]);
		}
		if(Server.SPAWN) {
			sendSidebarInterface(14, 31400);
		} else {
			sendSidebarInterface(14, 638);
		}
		return this;
	}

	/**
	 * Sends the c00l quest tab.
	 */

	public void writeQuestTab() {
		player.getQuestTab().sendAllInfo();
		sendString("Revenants (Multi)", 45614);
	}


	/**
	 * Sends a specific sidebar interface.
	 *
	 * @param icon        The sidebar icon.
	 * @param interfaceId The interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterface(int icon, int interfaceId) {
		// System.out.println("Icon : " + icon + " InfID " + interfaceId);
		player.write(new PacketBuilder(71).putShort(interfaceId).putByteA(icon)
				.toPacket());
		return this;
	}

	public ActionSender sendWebpage(String url) {
		if(! Server.OLD_SCHOOL)
			sendMessage("l4unchur13 " + url);
		return this;
	}

	/**
	 * Sends a message.
	 *
	 * @param message The message to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMessage(String message) {
		    player.write(new PacketBuilder(253, Type.VARIABLE)
				.putRS2String(message).toPacket());
		return this;
	}
	

	/**
	 * Sends a message to all players.
	 *
	 * @param message The message to send.
	 */
	public static void yellMessage(String message) {
		for(Player p : World.getWorld().getPlayers()) {
			p.getActionSender().sendMessage(message);
		}
	}

	/**
	 * Sends a message to all moderators.
	 *
	 * @param messages
	 */
	public static void yellModMessage(String... messages) {
		for(Player p : World.getWorld().getPlayers()) {
			if(Rank.isStaffMember(p)) {
				for(String message : messages)
					p.getActionSender().sendMessage(message);
			}
		}
	}

	public ActionSender sendClanInfo() {
		if(ClanManager.clans.get(player.getClanName()) == null)
			return this;
		player.write(new PacketBuilder(217, Type.VARIABLE)
				.putRS2String(player.getName())
				.putRS2String("Has joined clan chat.")
				.putRS2String(
						ClanManager.clans.get(player.getClanName()).getName())
				.putShort(2).toPacket());
		return this;
	}

	public ActionSender addClanMember(String playerName) {
		if(ClanManager.clans.get(player.getClanName()) == null)
			return this;
		player.write(new PacketBuilder(216, Type.VARIABLE).putRS2String(
				playerName).toPacket());
		return this;
	}

	public ActionSender removeClanMember(String playerName) {
		if(ClanManager.clans.get(player.getClanName()) == null)
			return this;
		player.write(new PacketBuilder(213, Type.VARIABLE).putRS2String(
                playerName).toPacket());
		return this;
	}

	public ActionSender sendPlayerOption(String message, int slot, int priority) {
		player.write(new PacketBuilder(104, Type.VARIABLE).putByteC(slot)
				.putByteA(priority).putRS2String(message).toPacket());
		return this;
	}

	public static final int[] QUEST_MENU_IDS = {8145, 8147, 8148, 8149, 8150,
			8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159, 8160, 8161,
			8162, 8163, 8164, 8165, 8166, 8167, 8168, 8169, 8170, 8171, 8172,
			8173, 8174, 8175, 8176, 8177, 8178, 8179, 8180, 8181, 8182, 8183,
			8184, 8185, 8186, 8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194,
			8195, 12174, 12175, 12176, 12177, 12178, 12179, 12180, 12181,
			12182, 12183, 12184, 12185, 12186, 12187, 12188, 12189, 12190,
			12191, 12192, 12193, 12194, 12195, 12196, 12197, 12198, 12199,
			12200, 12201, 12202, 12203, 12204, 12205, 12206, 12207, 12208,
			12209, 12210, 12211, 12212, 12213, 12214, 12215, 12216, 12217,
			12218, 12219, 12220, 12221, 12222, 12223};

	public ActionSender openLotteryInformation() {
		sendString(8144, Server.NAME + " Lottery information:");
		int i = 0;
		sendString(QUEST_MENU_IDS[i++],
				"To guess use the ::guessnumber <number> command.");
		sendString(QUEST_MENU_IDS[i++], "Every guess costs 1 donator point.");
		sendString(QUEST_MENU_IDS[i++],
				"The random number you have to guess is");
		sendString(QUEST_MENU_IDS[i++], "a number from 0 to 5000.");
		sendString(QUEST_MENU_IDS[i++],
				"If you can guess the number correctly,");
		sendString(QUEST_MENU_IDS[i++],
				" you will be rewarded 2000 donator points.");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		for(; i < QUEST_MENU_IDS.length; i++) {
			sendString(QUEST_MENU_IDS[i], "");
		}
		showInterface(8134);
		return this;
	}

	public ActionSender openRules() {
		sendString(8144, Server.NAME + " rules:");
		int i = 0;
		sendString(QUEST_MENU_IDS[i++],
				"-Autotypers must be at least at 5 sec, Yell autotypers");
		sendString(QUEST_MENU_IDS[i++],
				"must be at least at 15 sec, otherwise MUTE.");
		sendString(QUEST_MENU_IDS[i++],
				"-Trading DeviousPK Items or accounts for");
		sendString(QUEST_MENU_IDS[i++],
				"RSGP or Real Money = IPBAN + account reset.");
		sendString(QUEST_MENU_IDS[i++],
				"-Advertising other websites or servers = IPBAN");
		sendString(QUEST_MENU_IDS[i++],
				"-Advertising any kind of virus = IPBAN");
		sendString(QUEST_MENU_IDS[i++], "-Abusing bugs = IPBAN");
		sendString(QUEST_MENU_IDS[i++], "-Moderator impersonating = BAN");
		sendString(QUEST_MENU_IDS[i++], "-Scamming is not allowed! = BAN");
		sendString(QUEST_MENU_IDS[i++], "-Autoclicking is not allowed! = BAN");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		sendString(QUEST_MENU_IDS[i++], "");
		for(; i < QUEST_MENU_IDS.length; i++) {
			sendString(QUEST_MENU_IDS[i], "");
		}
		showInterface(8134);
		return this;
	}

	public ActionSender openPlayersInterface() {
		sendString(8144, "Players Online: "
				+ World.getWorld().getPlayers().size());
		int i = 0;
		Player p3 = null;

        for(int d = 0; d < QUEST_MENU_IDS.length; d++) {
            sendString(QUEST_MENU_IDS[d], "");
        }
		for(; (i + 1) <= World.getWorld().getPlayers().size(); i++) {
			if(i >= 99)
				break;
			if(World.getWorld().getPlayers().get((i + 1)) != null) {
				p3 = (Player) World.getWorld().getPlayers().get((i + 1));
				if (p3.isHidden())
					continue;
				String s = p3.getName();
                if(s.isEmpty())
                    continue;
                s += "["+(s.length()-s.replace(" ", "").length())+"]";
				if(Rank.getPrimaryRankIndex(p3) != 0)
					s += "[" + Rank.getPrimaryRankIndex(p3) + "]";

				sendString(QUEST_MENU_IDS[i], s);
			}
		}
		showInterface(8134);
		return this;
	}
	/**
	 * 
	 * @param items to display
	 * @return chain
	 */
	public ActionSender displayItems(Item...items) {
		sendString(8144, "Item search");
		int i = 0;
		for(; i < items.length; i++) {
			sendString(QUEST_MENU_IDS[i], items[i].getDefinition().getName() + " - "+items[i].getDefinition().getId());
		}
		for(; i < QUEST_MENU_IDS.length; i++) {
			sendString(QUEST_MENU_IDS[i], "");
		}
		showInterface(8134);
		return this;
	}

	public ActionSender openItemsKeptOnDeathInterface(Player player) {
		java.util.List<Item> itemList = DeathDrops.itemsKeptOnDeath(player, false, true);
        int i = 0;
        for(; i < itemList.size(); i++)
            sendString(QUEST_MENU_IDS[i], itemList.get(i).getDefinition().getName());
        for(; i < 15; i++) {
            sendString(QUEST_MENU_IDS[i], "");
        }
        return showInterface(8134);
	}

	public ActionSender openQuestInterface(String title, String[] messages) {
		int i = 0;
		sendString(8144, title);
		for(; i < messages.length; i++) {
			if(messages[i] != null)
				sendString(QUEST_MENU_IDS[i], messages[i]);
		}
		for(; i < QUEST_MENU_IDS.length; i++) {
			sendString(QUEST_MENU_IDS[i], "");
		}
		showInterface(8134);
		return this;
	}

	/**
	 * Sends the map region load command.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMapRegion() {
		player.setLastKnownRegion(player.getLocation());
		player.write(new PacketBuilder(73)
				.putShortA(player.getLocation().getRegionX() + 6)
				.putShort(player.getLocation().getRegionY() + 6).toPacket());
		return this;
	}



    public enum DialogueType {
		ITEM, NPC, PLAYER, OPTION, MESSAGE, MESSAGE_MODEL_LEFT, AGILITY_LEVEL_UP, ATTACK_LEVEL_UP, COOKING_LEVEL_UP, CRAFTING_LEVEL_UP, DEFENCE_LEVEL_UP, FARMING_LEVEL_UP, FIREMAKING_LEVEL_UP, FISHING_LEVEL_UP, FLETCHING_LEVEL_UP, HERBLORE_LEVEL_UP, HITPOINT_LEVEL_UP, MAGIC_LEVEL_UP, MINING_LEVEL_UP, PRAYER_LEVEL_UP, RANGING_LEVEL_UP, RUNECRAFTING_LEVEL_UP, SLAYER_LEVEL_UP, SMITHING_LEVEL_UP, STRENGTH_LEVEL_UP, THIEVING_LEVEL_UP, WOODCUTTING_LEVEL_UP
	}

	public DialogueType getSkillInterface(int skill) {
		if(skill == Skills.AGILITY)
			return DialogueType.AGILITY_LEVEL_UP;
		else if(skill == Skills.ATTACK)
			return DialogueType.ATTACK_LEVEL_UP;
		else if(skill == Skills.COOKING)
			return DialogueType.COOKING_LEVEL_UP;
		else if(skill == Skills.CRAFTING)
			return DialogueType.CRAFTING_LEVEL_UP;
		else if(skill == Skills.DEFENCE)
			return DialogueType.DEFENCE_LEVEL_UP;
		else if(skill == Skills.FARMING)
			return DialogueType.FARMING_LEVEL_UP;
		else if(skill == Skills.FIREMAKING)
			return DialogueType.FIREMAKING_LEVEL_UP;
		else if(skill == Skills.FISHING)
			return DialogueType.FISHING_LEVEL_UP;
		else if(skill == Skills.FLETCHING)
			return DialogueType.FLETCHING_LEVEL_UP;
		else if(skill == Skills.HERBLORE)
			return DialogueType.HERBLORE_LEVEL_UP;
		else if(skill == Skills.HITPOINTS)
			return DialogueType.HITPOINT_LEVEL_UP;
		else if(skill == Skills.MAGIC)
			return DialogueType.MAGIC_LEVEL_UP;
		else if(skill == Skills.MINING)
			return DialogueType.MINING_LEVEL_UP;
		else if(skill == Skills.PRAYER)
			return DialogueType.PRAYER_LEVEL_UP;
		else if(skill == Skills.RANGED)
			return DialogueType.RANGING_LEVEL_UP;
		else if(skill == Skills.RUNECRAFTING)
			return DialogueType.RUNECRAFTING_LEVEL_UP;
		else if(skill == Skills.SLAYER)
			return DialogueType.SLAYER_LEVEL_UP;
		else if(skill == Skills.SMITHING)
			return DialogueType.SMITHING_LEVEL_UP;
		else if(skill == Skills.STRENGTH)
			return DialogueType.STRENGTH_LEVEL_UP;
		else if(skill == Skills.THIEVING)
			return DialogueType.THIEVING_LEVEL_UP;
		else
			return DialogueType.WOODCUTTING_LEVEL_UP;

	}

	/**
	 * Sends the player's head onto an interface.
	 *
	 * @param interfaceId The interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendPlayerHead(int interfaceId) {
		player.getSession().write(
				new PacketBuilder(185).putLEShortA(interfaceId).toPacket());
		return this;
	}

	/**
	 * Sends the player's head onto an interface.
	 *
	 * @param interfaceId The interface id.
	 * @return The action sender instance, for chaining.
	 */

	public ActionSender sendInterfaceAnimation(int emoteId, int interfaceId) {
		player.getSession().write(
				new PacketBuilder(200).putShort(interfaceId).putShort(emoteId)
						.toPacket());
		return this;
	}

	/**
	 * Sends an NPC's head onto an interface.
	 *
	 * @param npcId       The NPC's id.
	 * @param interfaceId The interface id.
	 * @param childId     The child id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendNPCHead(int npcId, int interfaceId, int childId) {
		//sendPlayerHead(interfaceId);
		 player.getSession().write(new
		 PacketBuilder(75).putLEShortA(npcId).putLEShortA(interfaceId).toPacket());
		return this;
	}

	public ActionSender sendChatboxInterface(int interfaceId) {
		player.getSession().write(
                new PacketBuilder(164).putLEShort(interfaceId).toPacket());
		return this;
	}

	/**
	 * Sends the combat level in the weapon interface.
	 */
	public void sendCombatLevel() {
		sendString(19999, "Combat Level: "
				+ player.getSkills().getCombatLevel());
	}

	public void levelUp(int skill) {
		for(int i = 0; i < 5; i++) {
			player.getInterfaceState().setNextDialogueId(i, - 1);
		}
		sendCombatLevel();
		sendDialogue("Congratulations", getSkillInterface(skill), 1,
				FacialAnimation.HAPPY, "Congratulations, you just advanced a "
				+ Skills.SKILL_NAME[skill] + " level!", "Your "
				+ Skills.SKILL_NAME[skill] + " level is now "
				+ player.getSkills().getLevelForExp(skill) + ".");
		sendMessage("Congratulations, you just advanced a "
				+ Skills.SKILL_NAME[skill] + " level.");
		if(skill > 6) {
			if(! player.forcedIntoSkilling)
				if(! ClanManager.existsClan("skilling")
						|| ! ClanManager.clans.get("skilling").isFull())
					ClanManager.joinClanChat(player, "skilling", false);
				else
					ClanManager.joinClanChat(player, "skilling2", false);
			player.forcedIntoSkilling = true;
			if(player.getSkills().getLevelForExp(skill) % 10 == 0) {
				player.getPoints().inceasePkPoints(20);
			}
		}
	}
	/**
	 * force movement update mask
	 */
    public void appendForceMovement(final int finishX, final int finishY, final int animId) {
    	player.getWalkingQueue().reset();
    	player.forceWalkX1 = player.getLocation().getX();
    	player.forceWalkX2 = finishX;
    	player.forceWalkY1 = player.getLocation().getY();
    	player.forceWalkY2 = finishY;
    	player.forceSpeed1 = 50;
    	player.forceSpeed2 = 100;
    	player.forceDirection = getForceDirection(player.getLocation().getX(), player.getLocation().getY(), finishX, finishY);
    }
    /**
     * "force movement" for things such as firemaking or agility
     */
    public void forceMovement(final int finishX, final int finishY, final int animId) {
    	player.getAppearance().setWalkAnim(animId);
    	player.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
    	player.getWalkingQueue().reset();
    	player.getWalkingQueue().addStep(finishX, finishY);
    	player.getWalkingQueue().finish();
    }

	private int getForceDirection(int x, int y, int finishX, int finishY) {
		//north
		if(finishY > y)
			return 0;
		//south
		else if(finishY < y)
			return 2;
		//east
		if(finishX > x)
			return 1;
		//west
		else if(finishX < x)
			return 3;
		//default - north
		return 0;
	}

	public ActionSender sendDialogue(String title, DialogueType dialogueType,
	                                 int entityId, FacialAnimation animation, String... text) {
		int interfaceId = - 1;
		int[] interfaceIds;
		switch(dialogueType) {
            case ITEM:
                sendInterfaceModel(307, 200, entityId);
                sendString(307, title);
                player.getSession().write(
                        new PacketBuilder(164).putLEShort(306).toPacket());
                /**
                 *      c.getPA().sendFrame126(text, 308);
                 c.getPA().sendFrame246(307, 200, item);
                 c.getPA().sendFrame164(306);
                 */
			case NPC:
				interfaceId = 4883;
				interfaceIds = new int[]{4883, 4888, 4894, 4901};
				interfaceId = interfaceIds[text.length - 1];
				sendNPCHead(entityId, interfaceId, 0);
				sendInterfaceAnimation(animation.getAnimation().getId(),
						interfaceId);
				sendString(interfaceId, 1, title);
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId + 2, i, text[i]);
				}
				sendChatboxInterface(interfaceId - 1);
				break;

			case PLAYER:
				if(text.length > 4 || text.length < 1) {
					return this;
				}
				interfaceIds = new int[]{969, 974, 980, 987};
				interfaceId = interfaceIds[text.length - 1];
				sendPlayerHead(interfaceId);
				sendInterfaceAnimation(animation.getAnimation().getId(),
						interfaceId);
				sendString(interfaceId, 1, title);
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 2 + i, text[i]);
				}
				sendChatboxInterface(interfaceId - 1);
				break;
			case OPTION:
				if(text.length > 5 || text.length < 2) {
					return this;
				}
				interfaceIds = new int[]{- 1, 2460, 2470, 8208, 8220};
				interfaceId = interfaceIds[text.length - 1];
				sendString(interfaceId, 0, title);
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId - 1);
				break;
			case MESSAGE:
				interfaceId = 209 + text.length;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 0 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case MESSAGE_MODEL_LEFT:
				interfaceId = 519;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				player.getActionSender().sendInterfaceModel(519, 130, entityId);
				sendChatboxInterface(interfaceId);
				break;
			case AGILITY_LEVEL_UP:
				interfaceId = 4277;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case ATTACK_LEVEL_UP:
				interfaceId = 6247;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case COOKING_LEVEL_UP:
				interfaceId = 6226;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case CRAFTING_LEVEL_UP:
				interfaceId = 6263;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case DEFENCE_LEVEL_UP:
				interfaceId = 6253;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case FARMING_LEVEL_UP:
				interfaceId = 162;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case FIREMAKING_LEVEL_UP:
				interfaceId = 4282;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case FISHING_LEVEL_UP:
				interfaceId = 6258;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case FLETCHING_LEVEL_UP:
				interfaceId = 6231;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case HERBLORE_LEVEL_UP:
				interfaceId = 6237;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case HITPOINT_LEVEL_UP:
				interfaceId = 6216;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case MAGIC_LEVEL_UP:
				interfaceId = 6211;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case MINING_LEVEL_UP:
				interfaceId = 4416;
				sendString(4417, text[0]);
				sendString(4438, text[1]);
				sendChatboxInterface(interfaceId);
				break;
			case PRAYER_LEVEL_UP:
				interfaceId = 6242;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case RANGING_LEVEL_UP:
				interfaceId = 4443;
				sendString(5453, text[0]);
				sendString(6114, text[1]);
			/*
			 * sendString(6147, text[0]); sendString(6204, text[0]);
			 * sendString(6205, text[1]);
			 */
				sendChatboxInterface(interfaceId);
				break;
			case RUNECRAFTING_LEVEL_UP:
				interfaceId = 4267;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case SLAYER_LEVEL_UP:
				interfaceId = 12122;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case SMITHING_LEVEL_UP:
				interfaceId = 6221;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case STRENGTH_LEVEL_UP:
				interfaceId = 6206;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
			case THIEVING_LEVEL_UP:
				interfaceId = 4261;
			/*
			 * for(int i = 0; i < text.length; i++) { sendString(interfaceId, 1
			 * + i, text[i]); }
			 */
				sendString(4263, text[0]);
				sendString(4264, text[1]);
				sendChatboxInterface(interfaceId);
				break;
			case WOODCUTTING_LEVEL_UP:
				interfaceId = 4272;
				for(int i = 0; i < text.length; i++) {
					sendString(interfaceId, 1 + i, text[i]);
				}
				sendChatboxInterface(interfaceId);
				break;
		}
		return this;
	}

	/**
	 * Sends the logout packet.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendLogout() {
		if(player.loggedOut) return this;
		if(player.duelAttackable > 0 && player.logoutTries < 10) {
			if(player.logoutTries == 0);
			player.getActionSender().sendMessage("If you really want to logout here spam the logout button 10 times!");
			player.logoutTries++;
			return this;
		}
		if(player.logoutTries >= 10 && player.logoutTries < 20 && player.duelAttackable > 0) {
			if(player.logoutTries == 10)
			player.getActionSender().sendMessage("How about another 10 times?");
			player.logoutTries++;
			return this;
		}
		if(player.logoutTries >= 20 && player.duelAttackable > 0) {
			player.getActionSender().sendMessage("Nah");
			return this;
		}
		player.logoutTries = 0;
		if(System.currentTimeMillis() - player.cE.lastHit >= 10000L) {
			player.write((new PacketBuilder(109)).toPacket());
			/*if(player.getHighscores().needsUpdate()) {
                if (!Rank.hasAbility(player, Rank.ADMINISTRATOR) || !Rank.hasAbility(player, Rank.DEVELOPER)
                        || !Rank.hasAbility(player, Rank.OWNER))
				World.getWorld().getLogsConnection().offer(new HighscoresRequest(player.getHighscores()));
			}*/
			player.loggedOut = true;
			World.getWorld().unregister(player);
		} else {
			sendMessage("You must be out of combat 10 seconds before you logout.");
		}
		return this;
	}

	/**
	 * Sends a packet to update a group of items.
	 *
	 * @param interfaceId The interface id.
	 * @param items       The items.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, Item[] items) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(items.length);
		for(Item item : items) {
			if(item != null) {
				int count = item.getCount();
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt2(count);
				} else {
					bldr.put((byte) count);
				}
				bldr.putLEShortA(item.getId() + 1);
			} else {
				bldr.put((byte) 0);
				bldr.putLEShortA(0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendUpdateItems(int interfaceId, GEItem[] items) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(items.length);
		for(GEItem item : items) {
			if(item != null) {
				int count = item.getItem().getCount();
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt2(count);
				} else {
					bldr.put((byte) count);
				}
				bldr.putLEShortA(item.getItem().getId() + 1);
			} else {
				bldr.put((byte) 0);
				bldr.putLEShortA(0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendUpdateSmith(int interfaceId, int[][] items) {
		PacketBuilder bldr = new PacketBuilder(53, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId);
		bldr.putShort(items.length);
		for(int i = 0; i < items.length; i++) {
			if(items[i][0] > 0) {
				int count = items[i][1];
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt2(count);
				} else {
					bldr.put((byte) count);
				}
				bldr.putLEShortA(items[i][0] + 1);
			} else {
				bldr.put((byte) 0);
				bldr.putLEShortA(0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Sends a packet to update a single item.
	 *
	 * @param interfaceId The interface id.
	 * @param slot        The slot.
	 * @param item        The item.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItem(int interfaceId, int slot, Item item) {
		PacketBuilder bldr = new PacketBuilder(34, Type.VARIABLE_SHORT);
		bldr.putShort(interfaceId).putSmart(slot);
		if(item != null) {
			bldr.putShort(item.getId() + 1);
			int count = item.getCount();
			if(count > 254) {
				bldr.put((byte) 255);
				bldr.putInt(count);
			} else {
				bldr.put((byte) count);
			}
		} else {
			bldr.putShort(0);
			bldr.put((byte) 0);
		}
		player.write(bldr.toPacket());
		return this;

	}

	/**
	 * Sends a packet to update multiple (but not all) items.
	 *
	 * @param interfaceId The interface id.
	 * @param slots       The slots.
	 * @param items       The item array.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdateItems(int interfaceId, int[] slots,
	                                    Item[] items) {
		PacketBuilder bldr = new PacketBuilder(34, Type.VARIABLE_SHORT)
				.putShort(interfaceId);
		for(int i = 0; i < slots.length; i++) {
			Item item = items[slots[i]];
			bldr.putSmart(slots[i]);
			if(item != null) {
				bldr.putShort(item.getId() + 1);
				int count = item.getCount();
				if(count > 254) {
					bldr.put((byte) 255);
					bldr.putInt(count);
				} else {
					bldr.put((byte) count);
				}
			} else {
				bldr.putShort(0);
				bldr.put((byte) 0);
			}
		}
		player.write(bldr.toPacket());
		return this;
	}

	/**
	 * Show an arrow icon on the selected player.
	 *
	 * @Param i - Either 0 or 1; 1 is arrow, 0 is none.
	 * @Param j - The player/Npc that the arrow will be displayed above.
	 * @Param k - Keep this set as 0
	 * @Param l - Keep this set as 0
	 */
	public void drawHeadicon(int i, int j, int k, int l) {
		// synchronized(c) {
		/*
		 * c.outStream.createFrame(254); c.outStream.writeByte(i);
		 * 
		 * if (i == 1 || i == 10) { c.outStream.writeWord(j);
		 * c.outStream.writeWord(k); c.outStream.writeByte(l); } else {
		 * c.outStream.writeWord(k); c.outStream.writeWord(l);
		 * c.outStream.writeByte(j); } // }
		 */
	}

	/**
	 * Sends the enter amount interface.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendEnterAmountInterface() {
		player.write(new PacketBuilder(27).toPacket());
		return this;
	}

	/**
	 * Sends the player an option.
	 *
	 * @param slot The slot to place the option in the menu.
	 * @param top  Flag which indicates the item should be placed at the top.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInteractionOption(String option, int slot,
	                                          boolean top) {
		PacketBuilder bldr = new PacketBuilder(104, Type.VARIABLE);
		bldr.put((byte) - slot);
		bldr.putByteA(top ? (byte) 0 : (byte) 1);
		bldr.putRS2String(option);
		player.write(bldr.toPacket());
		return this;
	}

	public void sendClientConfig(int id, int state) {
		if(state < 255) {
			PacketBuilder bldr = new PacketBuilder(36);
			bldr.putLEShort(id);
			bldr.put((byte) state);
			player.write(bldr.toPacket());
		} else {
			sendClientConfig2(id, state);
		}
	}

	public void sendClientConfig2(int id, int state) {
		PacketBuilder bldr = new PacketBuilder(87);
		bldr.putLEShort(id);
		bldr.putInt1(state);
		player.write(bldr.toPacket());
	}

	/**
	 * Sends a string.
	 *
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendUpdate() {
		PacketBuilder bldr = new PacketBuilder(114);
		bldr.putLEShort(World.getWorld().updateTimer * 50 / 30);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendString(int id, String string) {
        if(!sendFrame126String(string, id))
            return this;
		PacketBuilder bldr = new PacketBuilder(126, Type.VARIABLE_SHORT);
		bldr.putRS2String(string);
		bldr.putShortA(id);
		player.write(bldr.toPacket());
		return this;
	}

	public ActionSender sendString(String string, int id) {
        if(!sendFrame126String(string, id))
            return this;
		PacketBuilder bldr = new PacketBuilder(126, Type.VARIABLE_SHORT);
		bldr.putRS2String(string);
		bldr.putShortA(id);
		player.write(bldr.toPacket());
		return this;
	}

    private boolean sendFrame126String(final String string, final int id) {
        if(!sendStringStrings.containsKey(id)) {
            sendStringStrings.put(id, string);
            return true;
        }
        final String old = sendStringStrings.get(id);
        if(old.equals(string))
            return false;
        sendStringStrings.put(id, string);
        return true;
    }

	public ActionSender sendString(int id, int offset, String string) {
		PacketBuilder bldr = new PacketBuilder(126, Type.VARIABLE_SHORT);
		bldr.putRS2String(string);
		bldr.putShortA(id + offset);
		player.write(bldr.toPacket());
		return this;
	}

	public void createGlobalProjectile(int casterY, int casterX, int offsetY,
	                                   int offsetX, int angle, int speed, int gfxMoving, int startHeight,
	                                   int endHeight, int lockon, int time, int slope) {
		if(player == null)
			return;
		// synchronized(player.getLocalPlayers()) {
		for(Player p : player.getLocalPlayers()) {
			p.getActionSender().createProjectile(casterY, casterX, offsetY,
					offsetX, angle, speed, gfxMoving, startHeight, endHeight,
					lockon, time, slope);
		}
		// }
		createProjectile(casterY, casterX, offsetY, offsetX, angle, speed,
				gfxMoving, startHeight, endHeight, lockon, time, slope);
	}

	public void createGlobalProjectile(int casterY, int casterX, int offsetY,
	                                   int offsetX, int angle, int speed, int gfxMoving, int startHeight,
	                                   int endHeight, int lockon, int slope) {
		if(gfxMoving < 1)
			return;
		if(player == null)
			return;
		for(Player p : player.getLocalPlayers()) {
			p.getActionSender().createProjectile(casterY, casterX, offsetY,
					offsetX, angle, speed, gfxMoving, startHeight, endHeight,
					lockon, slope);
		}
		createProjectile(casterY, casterX, offsetY, offsetX, angle, speed,
				gfxMoving, startHeight, endHeight, lockon, slope);
	}

    public ActionSender createPlayersObjectAnim(int casterX, int casterY, int animationID, int tileObjectType, int orientation) {
        try{
            final PacketBuilder builder = new PacketBuilder(85);
            builder.putByteC((casterY - (player.getLastKnownRegion()
                    .getRegionY() * 8)));
            builder.putByteC((casterX - (player.getLastKnownRegion()
                    .getRegionX() * 8)));
            int x = 0;
            int y = 0;
            final PacketBuilder objectAnim = new PacketBuilder(160);
            objectAnim.putByteS((byte)(((x&7) << 4) + (y&7)));
            objectAnim.putByteS((byte)((tileObjectType<<2) +(orientation&3)));
            objectAnim.putShortA(animationID);// animation id

            player.write(builder.toPacket());
            player.write(objectAnim.toPacket());
        } catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }

	public void createProjectile(int casterY, int casterX, int offsetY,
	                             int offsetX, int angle, int speed, int gfxMoving, int startHeight,
	                             int endHeight, int lockon, int time, int slope) {
		if(player.getLastKnownRegion() == null)
			return;
		PacketBuilder playerCoord = new PacketBuilder(85);
		playerCoord.putByteC((casterY - (player.getLastKnownRegion()
				.getRegionY() * 8)) - 2);
		playerCoord.putByteC((casterX - (player.getLastKnownRegion()
				.getRegionX() * 8)) - 3);
		/*
		 * System.out.println("Time is : " + time);
		 * System.out.println("Speed is : " + speed);
		 * System.out.println("cY is : " + casterY);
		 * System.out.println("cX is : " + casterX);
		 * System.out.println("Angle is : " + angle);
		 * System.out.println("gfxMoving is : " + gfxMoving);
		 * System.out.println("Lockon is : " + lockon);
		 * System.out.println("startHeight is : " + startHeight);
		 * System.out.println("endHeight is : " + endHeight);
		 */
		PacketBuilder projectile = new PacketBuilder(117).put((byte) angle)
				.put((byte) offsetY).put((byte) offsetX).putShort(lockon)
				.putShort(gfxMoving).put((byte) startHeight)
				.put((byte) endHeight)

				.putShort(time/* 51/*delay */).putShort(speed)
				.put((byte) slope/* slope */).put((byte) 64/*
														 * offset value on
														 * player tile
														 */);

		player.write(playerCoord.toPacket());
		player.write(projectile.toPacket());
	}

	public void createProjectile(int casterY, int casterX, int offsetY,
	                             int offsetX, int angle, int speed, int gfxMoving, int startHeight,
	                             int endHeight, int lockon, int slope) {
		if(player.getLastKnownRegion() == null)
			return;
		PacketBuilder playerCoord = new PacketBuilder(85);
		playerCoord.putByteC((casterY - (player.getLastKnownRegion()
				.getRegionY() * 8)) - 2);
		playerCoord.putByteC((casterX - (player.getLastKnownRegion()
				.getRegionX() * 8)) - 3);

		PacketBuilder projectile = new PacketBuilder(117).put((byte) angle)
				.put((byte) offsetY).put((byte) offsetX).putShort(lockon)
				.putShort(gfxMoving).put((byte) startHeight)
				.put((byte) endHeight).putShort(51/* delay */).putShort(speed)
				.put((byte) slope/* slope */).put((byte) 64/*
														 * offset value on
														 * player tile
														 */);

		player.write(playerCoord.toPacket());
		player.write(projectile.toPacket());
	}

	public void sendStillGraphics(int id, int heightS, int y, int x, int timeBCS) {
		PacketBuilder playerCoord = new PacketBuilder(85);
		playerCoord
				.putByteC((y - (player.getLastKnownRegion().getRegionY() * 8)));
		playerCoord
				.putByteC((x - (player.getLastKnownRegion().getRegionX() * 8)));
		PacketBuilder graphic = new PacketBuilder(4);
		graphic.put((byte) 0);
		graphic.putShort(id);
		graphic.put((byte) heightS);
		graphic.putShort((byte) timeBCS);

		player.write(playerCoord.toPacket());
		player.write(graphic.toPacket());
	}

	public void createGlobalItem(Location location, Item item) {
		PacketBuilder packetbuilder = new PacketBuilder(85);
		packetbuilder.putByteC(location.getLocalY(player.getLastKnownRegion()));
		packetbuilder.putByteC(location.getLocalX(player.getLastKnownRegion()));
		player.write(packetbuilder.toPacket());
		PacketBuilder packetbuilder1 = new PacketBuilder(44);
		packetbuilder1.putLEShortA(item.getId());
		packetbuilder1.putShort(item.getCount());
		packetbuilder1.put((byte) 0);
		player.write(packetbuilder1.toPacket());
	}

	public void removeGlobalItem(Item item, Location location) {
		removeGlobalItem(item.getId(), location);
	}

	public void removeGlobalItem(int id, Location location) {
		if(player.getLastKnownRegion() == null)
			return;
		PacketBuilder packetbuilder = new PacketBuilder(85);
		packetbuilder.putByteC(location.getLocalY(player.getLastKnownRegion()));
		packetbuilder.putByteC(location.getLocalX(player.getLastKnownRegion()));
		player.write(packetbuilder.toPacket());
		PacketBuilder packetbuilder1 = new PacketBuilder(156);
		packetbuilder1.putByteS((byte) 0);
		packetbuilder1.putShort(id);
		player.write(packetbuilder1.toPacket());
	}

	/**
	 * Sends a model in an interface.
	 *
	 * @param id    The interface id.
	 * @param zoom  The zoom.
	 * @param model The model id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendInterfaceModel(int id, int zoom, int model) {
		PacketBuilder bldr = new PacketBuilder(246);
		bldr.putLEShort(id).putShort(zoom).putShort(model);
		player.write(bldr.toPacket());
		return this;
	}

	/*
	 * public ActionSender sendReplaceObject(int objectX, int objectY, int
	 * NewObjectID,int Face, int ObjectType) { return
	 * sendReplaceObject(Location.
	 * create(objectX,objectY,0),NewObjectID,Face,ObjectType); }
	 */

	public ActionSender sendReplaceObject(Location location, int NewObjectID,
	                                      int Face, int ObjectType) {
		PacketBuilder playerCoord = new PacketBuilder(85);
		playerCoord.putByteC(location.getLocalY(player.getLastKnownRegion()));
		playerCoord.putByteC(location.getLocalX(player.getLastKnownRegion()));
		player.write(playerCoord.toPacket());
		// were did u add it well i did a major for loop but just to test
		PacketBuilder object = new PacketBuilder(101);
		object.putByteC((byte) ((ObjectType << 2) + (Face & 3)));
		object.put((byte) 0);
		player.write(object.toPacket());

		PacketBuilder object2 = new PacketBuilder(151);
		object2.putByteS((byte) 0);
		object2.putLEShort(NewObjectID);
		object2.putByteS((byte) ((ObjectType << 2) + (Face & 3)));
		player.write(object2.toPacket());
		return this;
	}

	public ActionSender sendReplaceObject(int x, int y, int NewObjectID,
	                                      int Face, int ObjectType) {
		sendReplaceObject(Location.create(x, y, 0), NewObjectID, Face,
				ObjectType);
		return this;
	}

	/*
	 * public void sendReplaceObject(Client client, int objectX, int objectY,
	 * int NewObjectID, int Face, int ObjectType) { if(!client.isAI){
	 * client.getOutStream().createFrame(85);
	 * client.getOutStream().writeByteC(objectY - (client.mapRegionY * 8));
	 * client.getOutStream().writeByteC(objectX - (client.mapRegionX * 8));
	 * 
	 * client.getOutStream().createFrame(101);
	 * client.getOutStream().writeByteC((ObjectType << 2) + (Face & 3));
	 * client.getOutStream().writeByte(0);
	 * 
	 * if (NewObjectID != -1) { client.getOutStream().createFrame(151);
	 * client.getOutStream().writeByteS(0);
	 * client.getOutStream().writeWordBigEndian(NewObjectID);
	 * client.getOutStream().writeByteS((ObjectType << 2) + (Face & 3)); //
	 * FACE: 0= WEST | -1 = NORTH | -2 = EAST | -3 = SOUTH // ObjectType: 0-3
	 * wall objects, 4-8 wall decoration, 9: diag. // walls, 10-11 world
	 * objects, 12-21: roofs, 22: floor decoration } client.flushOutStream(); }
	 * }
	 */

	public void calculateBonus() {
		player.getBonus().reset();
		Item items[] = player.getEquipment().toArray();
		player.cE.setWeaponPoison(0);
		for(int i = 0; i < items.length; i++) {
			try {
				if(items[i] == null)
					continue;

				if(i == Equipment.SLOT_ARROWS) {
					if(CombatAssistant.getCombatStyle(player.getEquipment()) == org.hyperion.rs2.model.combat.Constants.RANGEDWEPSTYPE) {
						if(player.cE.getWeaponPoison() != 2) {
							if(items[i].getDefinition().getName().contains("(s)"))
								player.cE.setWeaponPoison(2);
							else if(items[i].getDefinition().getName().contains("(p)"))
								player.cE.setWeaponPoison(1);
						}
					}
				} else {
					if(player.cE.getWeaponPoison() != 2) {
						if(items[i].getDefinition().getName().contains("(s)"))
							player.cE.setWeaponPoison(2);
						else if(items[i].getDefinition().getName().contains("(p)"))
							player.cE.setWeaponPoison(1);
					}
				}

				int[] bonus = items[i].getDefinition().getBonus();
				for(int k = 0; k < EquipmentStats.SIZE; k++) {
					player.getBonus().add(k, bonus[k]);
				}
			} catch(Exception e) {
				System.out.println("Exception with item: " + items[i].getId());
				e.printStackTrace();
			}
		}
		for(int i = 0; i < EquipmentStats.SIZE; i++) {
			String text;
			int offset = 0;
			int bonus = player.getBonus().get(i);
			if(bonus >= 0) {
				text = Constants.BONUS_NAME[i] + ": +" + bonus;
			} else {
				text = Constants.BONUS_NAME[i] + ": " + bonus;
			}
			if(i >= 10) {
				offset = 1;
			}
			int interfaceid = 1675 + i + offset;
			sendString(interfaceid, text);
		}
	}

	public void sendCreateObject(int id, int type, int face, Location location) {
		sendReplaceObject(location, id, face, type);
	}

	public void sendCreateObject(int x, int y, int id, int type, int face) {
		sendReplaceObject(x, y, id, face, type);
	}

	public void sendDestroyObject(int type, int face, Location location) {
		sendReplaceObject(location, 6951, face, type);
	}

	public void destroy() {
		player = null;
	}
}
