package org.hyperion.rs2.model;

import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.hyperion.Configuration;
import org.hyperion.map.BlockPoint;
import org.hyperion.map.DirectionCollection;
import org.hyperion.map.WorldMap;
import org.hyperion.map.pathfinding.PathTest;
import org.hyperion.rs2.*;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.EventManager;
import org.hyperion.rs2.event.impl.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.DoorManager;
import org.hyperion.rs2.model.content.bounty.BountyHunter;
import org.hyperion.rs2.model.content.bounty.BountyHunterEvent;
import org.hyperion.rs2.model.content.bounty.BountyHunterLogout;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.jge.JGrandExchange;
import org.hyperion.rs2.model.content.jge.event.PulseGrandExchangeEvent;
import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.minigame.LastManStanding;
import org.hyperion.rs2.model.content.misc.Lottery;
import org.hyperion.rs2.model.content.misc.TriviaBot;
import org.hyperion.rs2.model.content.skill.dungoneering.Dungeon;
import org.hyperion.rs2.model.joshyachievementsv2.Achievements;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.event.PunishmentExpirationEvent;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.region.RegionManager;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.net.PacketManager;
import org.hyperion.rs2.packet.PacketHandler;
import org.hyperion.rs2.sqlv2.DbHub;
import org.hyperion.rs2.task.Task;
import org.hyperion.rs2.util.ConfigurationParser;
import org.hyperion.rs2.util.EntityList;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.Restart;
import org.hyperion.util.Misc;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

//import org.hyperion.rs2.savingnew.SQLPlayerSaving;

//import org.hyperion.rs2.login.LoginServerWorldLoader;

//import org.hyperion.rs2.login.LoginServerWorldLoader;

/**
 * Holds data global to the game world.
 *
 * @author Graham Edgecombe
 */
public class World {

    private World() {}

    public static final double PLAYER_MULTI = 1.20;

    /**
     * Logging class.
     */
    private static final Logger logger = Logger.getLogger(World.class.getName());

    /**
     * The game engine.
     */
    private static GameEngine engine;

    /**
     * The event manager.
     */
    private static EventManager eventManager;

    /**
     * The current loader implementation.
     */
    private static WorldLoader loader;

    /**
     * A list of connected players.
     */
    private static EntityList<Player> players = new EntityList<>(Constants.MAX_PLAYERS);

    /**
     * A list of active NPCs.
     */
    public static EntityList<NPC> npcs = new EntityList<>(Constants.MAX_NPCS);

    public static LinkedList<NPC> npcsWaitingList = new LinkedList<>();

    private static final ConcurrentHashMap<String, Object> propertyMap = new ConcurrentHashMap<>();

    public static void putProperty(String key, Object value) {
        propertyMap.put(key, value);
    }

    public static <T> T getProperty(String key) {
        if (propertyMap.containsKey(key)) {
            return (T) propertyMap.get(key);
        }
        return null;
    }

    private final static Set<String> unlockedPlayers = new HashSet<>();
    private final static Set<String> unlockedRichPlayers = new HashSet<>();

    public static Set<String> getUnlockedPlayers() {
        return unlockedPlayers;
    }

    public static Set<String> getUnlockedRichPlayers() {
        return unlockedRichPlayers;
    }

    /**
     * Creates the world and begins background loading tasks.
     */
    public static void init() {
        try {
            ObjectManager.init();
            DoorManager.init();

            for (int i = 0; i < worldmapobjects; i++) {
                World_Objects[i] = null;
                World_Objects[i] = new Hashtable<>();
            }
            // worldMap = new WorldMap();
            WorldMap.loadWorldMap(true);


            // org.hyperion.map.Region.load();
            new Lottery();

        } catch (Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                for (Player player : players) {
                    loader.savePlayer(player);
                }
                System.out.println("Saved all players!");
            }
        });
    }

    public static int worldmapobjects = 10331; // 10331, 5116
    @SuppressWarnings("unchecked")
    public static Map<BlockPoint, DirectionCollection>[] World_Objects = new Hashtable[worldmapobjects];

    private static Wilderness wilderness = null;

    public static Wilderness getWilderness() {
        if (wilderness == null)
            wilderness = new Wilderness();
        return wilderness;
    }

    private static boolean updateInProgress = false;

    public static boolean updateInProgress() {
        return updateInProgress;
    }

    public static int updateTimer = -1;

    /**
     * Initialises the world: loading configuration and registering global
     * events.
     *
     * @param engine The engine processing this world's tasks.
     * @throws IOException            if an I/O error occurs loading configuration.
     * @throws ClassNotFoundException if a class loaded through reflection was not found.
     * @throws IllegalAccessException if a class could not be accessed.
     * @throws InstantiationException if a class could not be created.
     * @throws IllegalStateException  if the world is already initialised.
     */
    public static void init(GameEngine engine) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        if (World.engine != null) {
            throw new IllegalStateException("The world has already been initialised.");
        } else {
            World.engine = engine;
            eventManager = new EventManager(engine);
            NPCManager.init();
            NPCDefinition.init();
            ContentManager.init();
//            this.gui = new DebugGUI();
            getWilderness().init();
            GlobalItemManager.init();
            loadConfiguration();
            registerGlobalEvents();

            DbHub.initDefault();
            PunishmentManager.init();

            System.out.println("Initialized GE: " + JGrandExchange.init());
            submit(new PunishmentExpirationEvent());
            submit(new WildernessBossEvent(true));
            submit(new PulseGrandExchangeEvent());

            System.out.println("Loaded achievements: " + Achievements.load());
        }
    }

   /* private SQLPlayerSaving sqlSaving;

   public SQLPlayerSaving getSQLSaving() {
        return sqlSaving;
    }
	/*
	 * Writes an error to a file
	 */

    public static void writeError(String filename, Exception ex) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(filename,
                    true));
            bw.write(new Date().toString());
            bw.newLine();
            if (ex.getCause() != null) {
                bw.write("	cause: " + ex.getCause().toString());
                bw.newLine();
            }
            if (ex.getClass() != null) {
                bw.write("	class: " + ex.getClass().toString());
                bw.newLine();
            }
            if (ex.getMessage() != null) {
                bw.write("	message: " + ex.getMessage());
                bw.newLine();
            }
            if (ex.getStackTrace() == null)
                ex.fillInStackTrace();
            if (ex.getStackTrace() != null) {
                for (StackTraceElement s : ex.getStackTrace()) {
                    bw.write("	at " + s.getClassName() + "."
                            + s.getMethodName() + "(" + s.getFileName() + ":"
                            + s.getLineNumber() + ")");
                    bw.newLine();
                }
            }
            bw.newLine();
            bw.write("================================");
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (Exception ez) {
            ez.printStackTrace();
        }
    }

    /**
     * Loads server configuration.
     *
     * @throws IOException            if an I/O error occurs.
     * @throws ClassNotFoundException if a class loaded through reflection was not found.
     * @throws IllegalAccessException if a class could not be accessed.
     * @throws InstantiationException if a class could not be created.
     */
    public static void loadConfiguration() throws IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        FileInputStream fis = new FileInputStream("data/configuration.cfg");
        try {
            ConfigurationParser p = new ConfigurationParser(fis);
            Map<String, String> mappings = p.getMappings();
			/*
			 * Worldloader configuration.
			 */
            if (mappings.containsKey("worldLoader")) {
                String worldLoaderClass = mappings.get("worldLoader");
                Class<?> loader = Class.forName(worldLoaderClass);
                World.loader = (WorldLoader) loader.newInstance();
                System.out.println("WorldLoader set to : " + worldLoaderClass);
            } else {
                loader = new GenericWorldLoader();
                System.out.println("WorldLoader is set to default");
            }
            Map<String, Map<String, String>> complexMappings = p
                    .getComplexMappings();
			/*
			 * Packets configuration.
			 */
            if (complexMappings.containsKey("packetHandlers")) {
                Map<Class<?>, Object> loadedHandlers = new HashMap<Class<?>, Object>();
                for (Map.Entry<String, String> handler : complexMappings.get(
                        "packetHandlers").entrySet()) {
                    int id = Integer.parseInt(handler.getKey());
                    Class<?> handlerClass = Class.forName(handler.getValue());
                    Object handlerInstance;
                    if (loadedHandlers.containsKey(handlerClass)) {
                        handlerInstance = loadedHandlers.get(loadedHandlers
                                .get(handlerClass));
                    } else {
                        handlerInstance = handlerClass.newInstance();
                    }
                    PacketManager.getPacketManager().bind(id,
                            (PacketHandler) handlerInstance);
                    logger.fine("Bound " + handler.getValue() + " to opcode : "
                            + id);
                }
            }
			/*if (loader instanceof LoginServerWorldLoader) {
				connector = new LoginServerConnector(
						mappings.get("loginServer"));
				connector.connect(mappings.get("nodePassword"),
						Integer.parseInt(mappings.get("nodeId")));
			}*/
        } finally {
            fis.close();
        }
    }

    public static PathTest pathTest = new PathTest();

    public static boolean isWalkAble(int height, int absX, int absY, int toAbsX,
                              int toAbsY, int check) {
        // return WorldMap.isWalkAble(height, absX, absY, toAbsX, toAbsY,
        // check);
        return WorldMap.checkPos(height, absX, absY, toAbsX, toAbsY, check);
        // int dir = DirectionUtils.direction(absX-toAbsX,absY-toAbsY);
        // return WorldMap2.getSingleton().traversable(absX, absY, dir);
    }

    /**
     * Registers global events such as updating.
     */
    private static void registerGlobalEvents() {
        submit(new UpdateEvent());
        submit(new CleanupEvent());
        submit(new BankersFacing());
        submit(new PlayerEvent36Seconds());
        // submit(new PlayerEvent30Seconds());//unneeded
        submit(new PlayerEvent1Second());
        submit(new EPEvent());
        // submit(new SummoningEvent());
        submit(new HunterEvent());
       // submit(new RefreshNewsEvent());
        // abuse.start();
        submit(new DisconnectEvent());
        //submit(new EventDebuggingEvent());
        submit(new PlayerStatsEvent());
        submit(new PromoteEvent());
        submit(new PlayerCombatEvent());
        submit(new NpcCombatEvent());
        submit(new ServerMinigame());
        submit(new ServerMessages());
        submit(new BountyHunterEvent());
        submit(new BountyHunterLogout());
        submit(new GoodIPs());
        submit(new ClientConfirmEvent());
        TriviaBot.getBot().init();
        //FFARandom.initialize();
        // new SQL();
    }

    /**
     * Submits a new event.
     *
     * @param event The event to submit.
     */
    public static void submit(Event event) {
        if (eventManager == null || event == null)
            return;
        eventManager.submit(event);
    }

    /**
     * Submits a new task.
     *
     * @param task The task to submit.
     */
    public static void submit(Task task) {
        engine.pushTask(task);
    }

    /**
     * Gets the game engine.
     *
     * @return The game engine.
     */
    public static GameEngine getEngine() {
        return engine;
    }

	/*
	 * public ReportAbuse getAbuseHandler(){ return abuse; }
	 */

    /**
     * Loads a player's game in the work service.
     */
    public static void load(final PlayerDetails playerDetails) {
        engine.submitWork(() -> {
            Player player = new Player(playerDetails);
            LoginResponse loginResponse = loader.checkLogin(player, playerDetails);
            if(loginResponse != LoginResponse.NEW_PLAYER && loginResponse != LoginResponse.SUCCESSFUL_LOGIN) {
                playerDetails.getSession().write(new PacketBuilder().put((byte)loginResponse.getReturnCode()).toPacket()).addListener(future -> future.getSession().close(false));
                return;
            }
            player.getSession().setAttribute("player", player);
            register(player);
        });
    }

    public static void resetPlayersNpcs(Player player) {
        for (int i = 1; i <= npcs.size(); i++) {
            if (npcs.get(i) != null) {
                NPC npc = (NPC) npcs.get(i);
                if (npc.ownerId == player.getIndex()
                        && player.cE.summonedNpc != npc) {
                    npc.serverKilled = true;
                    if (!npc.isDead()) {
                        unregister(npc);
                    }
                    npc.setDead(true);
                    npc.health = 0;
                }
            }
        }

    }

    public static void resetSummoningNpcs(Player player) {
        NPC npc = player.cE.summonedNpc;
        if (npc == null)
            return;
        npc.serverKilled = true;
        if (!npc.isDead()) {
            submit(new NpcDeathEvent(npc));
        }
        npc.setDead(true);
        npc.health = 0;
        player.SummoningCounter = 0;
        player.getActionSender().sendCombatLevel();
        player.cE.summonedNpc = null;
    }

    public static void resetNpcs() {
        for (int i = 1; i <= npcs.size(); i++) {
            if (npcs.get(i) != null) {
                NPC npc = (NPC) npcs.get(i);
                if (!npc.isDead()) {
                    npc.serverKilled = true;
                    submit(new NpcDeathEvent(npc));
                }
                npc.setDead(true);
                npc.health = 0;
            }
        }

    }

    /**
     * Registers a new npc.
     *
     * @param n The npc to register.
     */
    public static void register(NPC n) {
        npcs.add(n);
    }

    public static void removeFromWaiting(NPC npc) {
        // TODO LOOK AT THIS CODE, IT MAY HAVE TO BE MODIFIED
        Region region = RegionManager.getRegionByLocation(npc.getLocation());
        region.addNpc(npc);
        npc.setLocation(npc.getSpawnLocation());
        register(npc);
    }

    /**
     * Unregisters an old npc.
     *
     * @param npc The npc to unregister.
     */
    public static void unregister(NPC npc) {
        // System.out.println("unregistering npc");
		/*
		 * Player b = null; try { b.activityPoints = 2; } catch(Exception e){
		 * e.printStackTrace(); }
		 */
        npcs.remove(npc);
        npc.destroy();
    }

    /**
     * Registers a new player.
     *
     * @param player The player to register.
     */
    public static void register(final Player player) {
        //player.getLogging().log("Logging in");
        // do final checks e.g. is player online? is world full?
        /*if(player.getPassword().getSalt() == null) {
            String salt = PasswordEncryption.generateSalt();
            player.getPassword().setSalt(salt);
            String enc = Password.encryptPassword(player.getPassword().getRealPassword(), salt);
            System.out.println("Real pass is: " + player.getPassword().getRealPassword() + " and enc is : " + enc);
            player.getPassword().setEncryptedPass(enc);
        }*/
        int returnCode = 2;
        if (returnCode == 2) {
            if (!players.add(player)) {
                returnCode = 7;
                LoginDebugger.getDebugger().log(
                        "Could not register player " + player.getName());
            }
        }
        //TODO REMOVE THIS AFTER ISSUES ARE OVER
        boolean has = false;
        for (String ipz : GoodIPs.GOODS) {
            if (player.getShortIP().startsWith(ipz) || ipz.equals(Integer.toString(player.getUID()))) {
                has = true;
                break;
            }
        }
        if (!has) {
            if(Configuration.getString(Configuration.ConfigurationObject.NAME).equalsIgnoreCase("ArteroPk") && !Rank.hasAbility(player, Rank.ADMINISTRATOR)) {
                if (player.getPermExtraData().getLong("passchange") < ActionSender.LAST_PASS_RESET.getTime() && !getUnlockedPlayers().contains(player.getName().toLowerCase()) && !player.isNew()) {
                    try {
                        String currentCutIp = player.getShortIP().substring(0, player.getShortIP().substring(0, player.getShortIP().lastIndexOf(".")).lastIndexOf("."));
                        String previousCutIp = player.lastIp.substring(0, player.lastIp.substring(0, player.lastIp.lastIndexOf(".")).lastIndexOf("."));
                        if (!currentCutIp.equals(previousCutIp)) {
                            returnCode = 12;
                        }
                    } catch (Exception e) {
                        returnCode = 12;
                    }
                }
                if (player.isNew())
                    player.getPermExtraData().put("passchange", System.currentTimeMillis());
            }
        }
        final PunishmentHolder holder = PunishmentManager.getInstance().get(player.getName()); //acc punishments
        if (holder != null) {
            for (final Punishment p : holder.getPunishments()) {
                p.getCombination().getType().apply(player);
                p.send(player, false);
            }
        } else {
            for (final PunishmentHolder h : PunishmentManager.getInstance().getHolders()) {
                if (player.getName().equalsIgnoreCase(h.getVictimName())) //skip acc punishments ^ wouldve been previously applied
                    continue;
                for (final Punishment p : h.getPunishments()) {
                    if ((p.getCombination().getTarget() == Target.IP && p.getVictimIp().equals(player.getShortIP()))
                            || (p.getCombination().getTarget() == Target.MAC && p.getVictimMac() == player.getUID())
                            || (p.getCombination().getTarget() == Target.SPECIAL && Arrays.equals(p.getVictimSpecialUid(), player.specialUid))) {
                        p.getCombination().getType().apply(player);
                        p.send(player, false);
                    }

                }
            }
        }
        final int fReturnCode = returnCode;
        PacketBuilder bldr = new PacketBuilder();
        bldr.put((byte) returnCode);
        bldr.put((byte) Rank.getPrimaryRankIndex(player));
        bldr.put((byte) 0);
        player.getSession().write(bldr.toPacket())
                .addListener(new IoFutureListener<IoFuture>() {
                    @Override
                    public void operationComplete(IoFuture future) {
                        if(fReturnCode != 2){
                            player.getSession().close(false);
                        }else{
                            player.getActionSender().sendLogin();
                            //PlayerFiles.saveGame(player);
                        }
                    }
                });
        if (returnCode == 2) {
            // logger.info("Registered player : " + player + " [online=" +
            // players.size() + "]");
            // System.out.println("Registered player : " + player.getName() +
            // " [online=" + players.size() + "]");
            HostGateway.enter(player.getShortIP());
        }

    }

    /**
     * Gets the player list.
     *
     * @return The player list.
     */
    public static EntityList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the npc list.
     *
     * @return The npc list.
     */
    public static EntityList<NPC> getNPCs() {
        return npcs;
    }

    /**
     * Checks if a player is online.
     *
     * @param name The player's name.
     * @return <code>true</code> if they are online, <code>false</code> if not.
     */
    public static boolean isPlayerOnline(String name) {
        LoginDebugger.getDebugger().log("Checking online players!");
        name = NameUtils.formatName(name);
        for (Player player : players) {
            if (player != null && player.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a player with the specified name is online.
     *
     * @param name
     * @return the player with the specified username, not case sensitive
     */
    public static Player getPlayer(String name) {
        name = NameUtils.formatName(name);
        for (Player player : players) {
            if (player.getName().equalsIgnoreCase(name) && !player.isHidden()) { //yes
                return player;
            }
        }
        return null;
    }

    /**
     * Attempts to gracefully close a session
     *
     * @param session The session that is about to be closed
     */
    public static boolean gracefullyExitSession(IoSession session) {
        if (session.containsAttribute("player")) {
            try {
                Player p = (Player) session.getAttribute("player");
                if (p != null) {
                    unregister(p);
                    return true;
                }
            } catch (ClassCastException e) {
                System.err.println("Session attribute \"player\" was not a player");
            }
        }
        return false;
    }

    /**
     * Unregisters a player, and saves their game.
     *
     * @param player The player to unregister.
     */
    public static void unregister(final Player player) {
		/*
		 * Combat.resetAttack(player.cE); final long xlog =
		 * System.currentTimeMillis();
		 */
        if (System.currentTimeMillis() - player.getExtraData().getLong("lastUnregister") < 1000)
            return;
        player.getExtraData().put("lastUnregister", System.currentTimeMillis());
        if (System.currentTimeMillis() - player.cE.lastHit >= 10000 && !player.isDead() && !player.isBusy() && player.duelAttackable < 1) {
            unregister2(player);
        } else {
            submit(new Event(20000) {
                @Override
                public void execute() {
                    //if(System.currentTimeMillis() - player.cE.lastHit >= 10000){
                    unregister2(player);
                    this.stop();
                    //}
                }
            });
        }
    }

    public static void unregister2(final Player player) {
        //auto save upon being called.
        if (player.getLogging() != null)
            //player.getLogging().log("Logging out");
            Combat.logoutReset(player.cE);
        player.getDungeoneering().fireOnLogout(player);
        player.setActive(false);
        LastManStanding.getLastManStanding().leaveGame(player, true);
        Bork.doDeath(player);
        // Combat.resetAttack(player.cE);
        resetPlayersNpcs(player);
        resetSummoningNpcs(player);
        player.getPermExtraData().put("logintime", player.getPermExtraData().getLong("logintime") + (System.currentTimeMillis() - player.getLogintime()));
        player.getTicketHolder().fireOnLogout();

        try {
            ClanManager.leaveChat(player, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // if(Server.dedi)
        // HighscoreConnection.updateHighscores(player.getName(),
        // ""+player.getRights().toInteger(), player.getSkills().getExps());
        if (player.duelAttackable <= 0) {
            Duel.declineTrade(player);
        } else {
            Duel.finishFullyDuel(player);
            player.setLocation(Location.create(3360 + Combat.random(17),
                    3274 + Combat.random(3), 0));
        }
        if (LastManStanding.getLastManStanding().gameStarted && LastManStanding.inLMSArea(player.cE.getAbsX(), player.cE.getAbsY())) {
            LastManStanding.getLastManStanding().leaveGame(player, true);
        }
        Trade.declineTrade(player);
        FightPits.removePlayerFromGame(player, false);
        BountyHunter.fireLogout(player);
        FriendsAssistant.refreshGlobalList(player, true);

        player.getActionQueue().cancelQueuedActions();
        player.getInterfaceState().resetContainers();
        player.isHidden(true);
        players.remove(player);
        HostGateway.exit(player.getShortIP());
        player.getSession().close(false);
        engine.submitWork(new Runnable() {
            public void run() {
                player.getLogManager().add(LogEntry.logout(player));
                player.getLogManager().clearExpiredLogs();
                player.getLogManager().save();
                if (player.verified)
                    loader.savePlayer(player);
                resetSummoningNpcs(player);
                player.destroy();
				/*
				 * player.getSkills().destroy();
				 * player.getActionSender().destroy();
				 * player.getInterfaceState().destroy();
				 */
                // player.getSession().removeAttribute("player");
            }
        });
    }

    /**
     * Handles an exception in any of the pools.
     *
     * @param t The exception.
     */
    public static void handleError(Throwable t) {
        logger.severe("An error occurred in an executor service! The server will be halted immediately.");
        t.printStackTrace();
        // System.exit(1);
    }

    public static void update(int time, final String reason) {
        //if(updateInProgress)
        //return;
        updateTimer = time; //modifies timer regardless
        updateInProgress = true;
        for (Player p : getPlayers()) {
            p.getActionSender().sendUpdate();
        }
        submit(new Event(1000) {
            @Override
            public void execute() {
                System.out.println("Seconds left: " + updateTimer);
                updateTimer--;
                if (!updateInProgress)
                    this.stop();
                if (updateTimer == 0) {
                    for (Player p : getPlayers()) {
                        Trade.declineTrade(p);
                    }
                    for (final Dungeon dungeon : Dungeon.activeDungeons) {
                        try {
                            dungeon.complete();
                        } catch (final Exception ex) {

                        }
                    }
                    ClanManager.save();
                    new Restart(reason).execute();
                }
            }
        });
    }

    public static void stopUpdate() {
        updateInProgress = false;
    }

    static {
        CommandHandler.submit(new Command("npcssize", Rank.PLAYER) {
            @Override
            public boolean execute(Player player, String input) {
                player.getActionSender().sendMessage(
                        "Npcs: " + getNPCs().size());
                player.getActionSender().sendMessage(
                        "Waiting list: " + npcsWaitingList.size());
                return true;
            }
        });
        CommandHandler.submit(new Command("unlock", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception{
                String playerName = filterInput(input);
                if(playerName == null)
                    throw new Exception();
                getUnlockedPlayers().add(playerName.toLowerCase().replaceAll("_", " "));
                player.getActionSender().sendMessage(Misc.formatPlayerName(playerName) + " has been unlocked and can now login.");
                return true;
            }
        });
        CommandHandler.submit(new Command("unlockrich", Rank.HEAD_MODERATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception{
                String playerName = filterInput(input);
                if(playerName == null)
                    throw new Exception();
                getUnlockedRichPlayers().add(playerName.toLowerCase().replaceAll("_", " "));
                player.getActionSender().sendMessage(Misc.formatPlayerName(playerName) + " has been unlocked and can now login.");
                return true;
            }
        });
        CommandHandler.submit(new Command("changeip", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(Player player, String input) throws Exception{
                String[] parts = filterInput(input).split(",");
                if(parts.length < 2)
                    throw new Exception();
                if(org.hyperion.rs2.savingnew.PlayerSaving.replaceProperty(parts[0], "IP", parts[1] + ":55222"))
                    player.getActionSender().sendMessage(Misc.formatPlayerName(parts[0]) + "'s IP has been changed to " + parts[1]);
                else
                    player.getActionSender().sendMessage("IP could not be changed.");
                return true;
            }
        });
    }
}