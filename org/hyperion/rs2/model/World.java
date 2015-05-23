package org.hyperion.rs2.model;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.hyperion.Server;
import org.hyperion.map.BlockPoint;
import org.hyperion.map.DirectionCollection;
import org.hyperion.map.WorldMap;
import org.hyperion.map.pathfinding.PathTest;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.GameEngine;
import org.hyperion.rs2.GenericWorldLoader;
import org.hyperion.rs2.HostGateway;
import org.hyperion.rs2.WorldLoader;
import org.hyperion.rs2.WorldLoader.LoginResult;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.SpawnCommand;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.EventManager;
import org.hyperion.rs2.event.impl.*;
import org.hyperion.rs2.login.LoginServerConnector;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.ContentManager;
import org.hyperion.rs2.model.content.DoorManager;
import org.hyperion.rs2.model.content.bounty.BountyHunter;
import org.hyperion.rs2.model.content.bounty.BountyHunterEvent;
import org.hyperion.rs2.model.content.bounty.place.BountyHandler;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.minigame.LastManStanding;
import org.hyperion.rs2.model.content.minigame.barrowsffa.BarrowsFFA;
import org.hyperion.rs2.model.content.misc.Lottery;
import org.hyperion.rs2.model.content.misc.TriviaBot;
import org.hyperion.rs2.model.content.skill.dungoneering.Dungeon;
import org.hyperion.rs2.model.content.ticket.TicketManager;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.event.PunishmentExpirationEvent;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.region.RegionManager;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.net.PacketManager;
import org.hyperion.rs2.packet.PacketHandler;
import org.hyperion.rs2.sql.*;
import org.hyperion.rs2.sql.requests.AccountValuesRequest;
import org.hyperion.rs2.sql.requests.HighscoresRequest;
import org.hyperion.rs2.task.Task;
import org.hyperion.rs2.task.impl.SessionLoginTask;
import org.hyperion.rs2.util.ConfigurationParser;
import org.hyperion.rs2.util.EntityList;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.NewcomersLogging;
import org.hyperion.rs2.util.Restart;
import org.hyperion.util.BlockingExecutorService;

//import org.hyperion.rs2.login.LoginServerWorldLoader;

//import org.hyperion.rs2.login.LoginServerWorldLoader;

/**
 * Holds data global to the game world.
 *
 * @author Graham Edgecombe
 */
public class World {

    public static final double PLAYER_MULTI = 1.11;

    /**
     * Ticket Manager - no fuckin shit
     */

    private final TicketManager ticketManager = new TicketManager();

    public final TicketManager getTicketManager() {
        return ticketManager;
    }

    /**
     * Logging class.
     */
    private static final Logger logger = Logger
            .getLogger(World.class.getName());

    /**
     * World instance.
     */
    private static final World world = new World();

    /**
     * Gets the world instance.
     *
     * @return The world instance.
     */
    public static World getWorld() {
        return world;
    }

    /**
     * An executor service which handles background loading tasks.
     */
    private BlockingExecutorService backgroundLoader = new BlockingExecutorService(
            Executors.newSingleThreadExecutor());


    /**
     * The game engine.
     */
    private GameEngine engine;

    /**
     * The event manager.
     */
    private EventManager eventManager;

    /**
     * The current loader implementation.
     */
    private WorldLoader loader;

    /**
     * A list of connected players.
     */
    private EntityList<Player> players = new EntityList<Player>(
            Constants.MAX_PLAYERS);

    /**
     * A list of active NPCs.
     */
    public EntityList<NPC> npcs = new EntityList<NPC>(Constants.MAX_NPCS);

    public LinkedList<NPC> npcsWaitingList = new LinkedList<NPC>();

    /**
     * The game object manager.
     */
    private ObjectManager objectManager;

    /**
     * The login server connector.
     */
    private LoginServerConnector connector;

    /**
     * The region manager.
     */
    private RegionManager regionManager = new RegionManager();

    /**
     * Global Item Manager, for drops
     */
    private GlobalItemManager globalItemManager;

    private ContentManager contentManager = new ContentManager();

    /**
     * The NPC Manager
     */
    private NPCManager npcManager;

    /**
     * The Staff Manager
     */
    private StaffManager staffManager;

    private MySQLConnection donationsSQL;

    private MySQLConnection logsSQL;

    private MySQLConnection charsSQL;
    /**
     * The Ban Manager
     */
    private BanManager banManager;

    private final BountyHandler bountyHandler = new BountyHandler();

    private ServerEnemies enemies;

    private final ConcurrentHashMap<String, Object> propertyMap = new ConcurrentHashMap<>();

    public void putProperty(String key, Object value) {
        propertyMap.put(key, value);
    }

    public <T> T getProperty(String key) {
        if(propertyMap.containsKey(key)) {
            return (T) propertyMap.get(key);
        }
        return null;
    }


    /**
     * Creates the world and begins background loading tasks.
     */
    public World() {
        try {
	        /*
			 * backgroundLoader.submit(new Callable<Object>() {
			 *
			 * @Override public Object call() throws Exception { objectManager =
			 * new ObjectManager(); objectManager.load(); DoorManager.init();
			 * return null; } });
			 */
			/*
			 * backgroundLoader.submit(new Callable<Object>() {
			 *
			 * @Override public Object call() throws Exception {
			 * ItemDefinition.init(); //NPCDefinition.init(); for(int i = 0; i <
			 * worldmapobjects; i++) { World_Objects[i] = null; World_Objects[i]
			 * = new Hashtable<BlockPoint, DirectionCollection>(); }
			 * WorldMap.loadWorldMap(true);
			 * //WorldMap2.getSingleton().initialize(); return null; } });
			 */
            objectManager = new ObjectManager();
            objectManager.load();
            DoorManager.init();

            for(int i = 0; i < worldmapobjects; i++) {
                World_Objects[i] = null;
                World_Objects[i] = new Hashtable<BlockPoint, DirectionCollection>();
            }
            // worldMap = new WorldMap();
            WorldMap.loadWorldMap(true, this);

            // org.hyperion.map.Region.load();
            new Lottery();

        } catch(Exception e) {
            e.printStackTrace();
        }
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                for(Player player : players) {
                    loader.savePlayer(player, "allsave");
                }
                System.out.println("Saved all players!");
            }
        });
    }

    public int worldmapobjects = 10331; // 10331, 5116
    @SuppressWarnings("unchecked")
    public Map<BlockPoint, DirectionCollection>[] World_Objects = new Hashtable[worldmapobjects];

    /**
     * Gets the login server connector.
     *
     * @return The login server connector.
     */
    public LoginServerConnector getLoginServerConnector() {
        return connector;
    }

    /**
     * Gets the background loader.
     *
     * @return The background loader.
     */
    public BlockingExecutorService getBackgroundLoader() {
        return backgroundLoader;
    }

    /**
     * Gets the region manager.
     *
     * @return The region manager.
     */
    public RegionManager getRegionManager() {
        return regionManager;
    }

    public GlobalItemManager getGlobalItemManager() {
        return globalItemManager;
    }

    public StaffManager getStaffManager() {
        return staffManager;
    }

    private Wilderness wilderness = null;

    public Wilderness getWilderness() {
        if(wilderness == null)
            wilderness = new Wilderness();
        return wilderness;
    }


    public MySQLConnection getDonationsConnection() {
        return donationsSQL;
    }

    public MySQLConnection getLogsConnection() {
        return logsSQL;
    }

    public MySQLConnection getCharactersConnection(){
        return charsSQL;
    }

	/*public PlayersSQLConnection getPlayersConnection() {
		return playersSQL;
	}*/

    public ServerEnemies getEnemies() {
        return enemies;
    }

    private boolean updateInProgress = false;

    public boolean updateInProgress() {
        return updateInProgress;
    }

    public int updateTimer = - 1;

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
    public void init(GameEngine engine) throws IOException,
            ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        if(this.engine != null) {
            throw new IllegalStateException(
                    "The world has already been initialised.");
        } else {
            this.engine = engine;
            this.eventManager = new EventManager(engine);
            this.npcManager = new NPCManager();
            this.contentManager.init();
            getWilderness().init();
            this.globalItemManager = new GlobalItemManager();
            this.staffManager = new StaffManager();
            this.loadConfiguration();
            this.registerGlobalEvents();
            if(Server.getConfig().getBoolean("sql")) {
                logsSQL = new LogsSQLConnection(Server.getConfig());
                donationsSQL = new DonationsSQLConnection(Server.getConfig());
                charsSQL = new CharactersSQLConnection(Server.getConfig());
            } else {
                logsSQL = new DummyConnection();
                donationsSQL = new DummyConnection();
                charsSQL = new DummyConnection();
            }
            donationsSQL.init();
            logsSQL.init();
            charsSQL.init();
            //LocalServerSQLConnection.init();
            //playersSQL.init();
            //banManager = new BanManager(logsSQL);
            PunishmentManager.init(logsSQL);
            //this.banManager.init();
            this.enemies = new ServerEnemies();
            SpawnCommand.init();
            NewcomersLogging.getLogging().init();
            submit(new PunishmentExpirationEvent());
        }
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
            if(ex.getCause() != null) {
                bw.write("	cause: " + ex.getCause().toString());
                bw.newLine();
            }
            if(ex.getClass() != null) {
                bw.write("	class: " + ex.getClass().toString());
                bw.newLine();
            }
            if(ex.getMessage() != null) {
                bw.write("	message: " + ex.getMessage());
                bw.newLine();
            }
            if(ex.getStackTrace() == null)
                ex.fillInStackTrace();
            if(ex.getStackTrace() != null) {
                for(StackTraceElement s : ex.getStackTrace()) {
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
        } catch(Exception ez) {
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
    public void loadConfiguration() throws IOException, ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        FileInputStream fis = new FileInputStream("data/configuration.cfg");
        try {
            ConfigurationParser p = new ConfigurationParser(fis);
            Map<String, String> mappings = p.getMappings();
			/*
			 * Worldloader configuration.
			 */
            if(mappings.containsKey("worldLoader")) {
                String worldLoaderClass = mappings.get("worldLoader");
                Class<?> loader = Class.forName(worldLoaderClass);
                this.loader = (WorldLoader) loader.newInstance();
                System.out.println("WorldLoader set to : " + worldLoaderClass);
            } else {
                this.loader = new GenericWorldLoader();
                System.out.println("WorldLoader is set to default");
            }
            Map<String, Map<String, String>> complexMappings = p
                    .getComplexMappings();
			/*
			 * Packets configuration.
			 */
            if(complexMappings.containsKey("packetHandlers")) {
                Map<Class<?>, Object> loadedHandlers = new HashMap<Class<?>, Object>();
                for(Map.Entry<String, String> handler : complexMappings.get(
                        "packetHandlers").entrySet()) {
                    int id = Integer.parseInt(handler.getKey());
                    Class<?> handlerClass = Class.forName(handler.getValue());
                    Object handlerInstance;
                    if(loadedHandlers.containsKey(handlerClass)) {
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

    public PathTest pathTest = new PathTest();

    public boolean isWalkAble(int height, int absX, int absY, int toAbsX,
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
    private void registerGlobalEvents() {
        submit(new UpdateEvent());
        submit(new CleanupEvent());
        submit(new BankersFacing());
        submit(new PlayerEvent36Seconds());
        // submit(new PlayerEvent30Seconds());//unneeded
        submit(new PlayerEvent1Second());
        submit(new EPEvent());
        // submit(new SummoningEvent());
        submit(new HunterEvent());
        // abuse.start();
        submit(new DisconnectEvent());
        //submit(new EventDebuggingEvent());
        submit(new PlayerStatsEvent());
        submit(new PromoteVotingEvent());
        submit(new PlayerCombatEvent());
        submit(new NpcCombatEvent());
        submit(new ServerMinigame());
        submit(new ServerMessages());
        submit(new BountyHunterEvent());
        submit(new GoodIPs());
        TriviaBot.getBot().init();
        objectManager.submitEvent();
        //FFARandom.initialize();
        // new SQL();
    }

    /**
     * Submits a new event.
     *
     * @param event The event to submit.
     */
    public void submit(Event event) {
        if(eventManager == null || event == null)
            return;
        this.eventManager.submit(event);
    }

    /**
     * Submits a new task.
     *
     * @param task The task to submit.
     */
    public void submit(Task task) {
        this.engine.pushTask(task);
    }

    /**
     * Gets the object map.
     *
     * @return The object map.
     */
    public ObjectManager getObjectMap() {
        return objectManager;
    }

    /**
     * Gets the world loader.
     *
     * @return The world loader.
     */
    public WorldLoader getWorldLoader() {
        return loader;
    }

    /**
     * Gets the game engine.
     *
     * @return The game engine.
     */
    public GameEngine getEngine() {
        return engine;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

	/*
	 * public ReportAbuse getAbuseHandler(){ return abuse; }
	 */

    /**
     * Loads a player's game in the work service.
     *
     * @param pd The player's details.
     */
    public void load(final PlayerDetails pd, final int code2) {
        // System.out.println("Load method in World");
        engine.submitWork(new Runnable() {
            public void run() {
                LoginDebugger.getDebugger().log("4. Playerdetails received: " + pd.getName());
                int code = code2;
                LoginDebugger.getDebugger().log("Code : " + code);
                LoginResult lr = null;
                if(isPlayerOnline(pd.getName())) {
                    LoginDebugger.getDebugger().log("About to code 5");
                    code = 5;
                } else {
                    LoginDebugger.getDebugger().log("Pre checking");
                    lr = loader.checkLogin(pd);
                    LoginDebugger.getDebugger().log("Checked login");
                    if(code == 0) {
                        code = lr.getReturnCode();
                        LoginDebugger.getDebugger().log("Code is 0 so..");
                    }
                    if(code == 2 || code == 8) {
                        lr.getPlayer().getSession().setAttribute("player", lr.getPlayer());
                        LoginDebugger.getDebugger().log("Code is 2 or 8 so..");
                    }
                    LoginDebugger.getDebugger().log("4. Checking loader login");
                }
                if(! NameUtils.isValidName(pd.getName())) {
                    code = 11;
                }
                LoginDebugger.getDebugger().log(pd.getName() + " code is : " + code);
                if(code != 2 && code != 8) {
                    LoginDebugger.getDebugger().log("Packetbuilder code");
                    PacketBuilder bldr = new PacketBuilder();
                    bldr.put((byte) code);
                    pd.getSession().write(bldr.toPacket())
                            .addListener(new IoFutureListener<IoFuture>() {
                                @Override
                                public void operationComplete(IoFuture future) {
                                    future.getSession().close(false);
                                }
                            });
                } else {

                    loader.loadPlayer(lr.getPlayer());
                    // lr.getPlayer().getActionSender().sendLogin();
                    LoginDebugger.getDebugger().log("7. Loaded Player in World");
                    engine.pushTask(new SessionLoginTask(lr.getPlayer()));
                }
            }
        });
    }

    public void resetPlayersNpcs(Player player) {
        for(int i = 1; i <= npcs.size(); i++) {
            if(npcs.get(i) != null) {
                NPC npc = (NPC) npcs.get(i);
                if(npc.ownerId == player.getIndex()
                        && player.cE.summonedNpc != npc) {
                    npc.serverKilled = true;
                    if(! npc.isDead()) {
                        submit(new NpcDeathEvent(npc));
                    }
                    npc.setDead(true);
                    npc.health = 0;
                }
            }
        }

    }

    public void resetSummoningNpcs(Player player) {
        NPC npc = player.cE.summonedNpc;
        if(npc == null)
            return;
        npc.serverKilled = true;
        if(! npc.isDead()) {
            submit(new NpcDeathEvent(npc));
        }
        npc.setDead(true);
        npc.health = 0;
        player.SummoningCounter = 0;
        player.getActionSender().sendCombatLevel();
        player.cE.summonedNpc = null;
    }

    public void resetNpcs() {
        for(int i = 1; i <= npcs.size(); i++) {
            if(npcs.get(i) != null) {
                NPC npc = (NPC) npcs.get(i);
                if(! npc.isDead()) {
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
    public void register(NPC n) {
        npcs.add(n);
    }

    public void removeFromWaiting(NPC npc) {
        // TODO LOOK AT THIS CODE, IT MAY HAVE TO BE MODIFIED
        Region region = World.getWorld().getRegionManager()
                .getRegionByLocation(npc.getLocation());
        region.addNpc(npc);
        npc.setLocation(npc.getSpawnLocation());
        register(npc);
    }

    /**
     * Unregisters an old npc.
     *
     * @param npc The npc to unregister.
     */
    public void unregister(NPC npc) {
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
    public void register(final Player player) {
        //player.getLogging().log("Logging in");
        // do final checks e.g. is player online? is world full?
        int returnCode = 2;
        if(returnCode == 2) {
            if(! players.add(player)) {
                returnCode = 7;
                LoginDebugger.getDebugger().log(
                        "Could not register player " + player.getName());
            }
        }
        final PunishmentHolder holder = PunishmentManager.getInstance().get(player.getName()); //acc punishments
        if(holder != null){
            for(final Punishment p : holder.getPunishments()){
                p.getCombination().getType().apply(player);
                p.send(player, false);
            }
        }else{
            for(final PunishmentHolder h : PunishmentManager.getInstance().getHolders()){
                if(player.getName().equalsIgnoreCase(h.getVictimName())) //skip acc punishments ^ wouldve been previously applied
                    continue;
                for(final Punishment p : h.getPunishments()){
                    if((p.getCombination().getTarget() == Target.IP && p.getVictimIp().equals(player.getShortIP()))
                            || (p.getCombination().getTarget() == Target.MAC && p.getVictimMac() == player.getUID())
                            || (p.getCombination().getTarget() == Target.SPECIAL && Arrays.equals(p.getVictimSpecialUid(), player.specialUid))){
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
                        if(fReturnCode != 2) {
                            player.getSession().close(false);
                        } else {
                            player.getActionSender().sendLogin();
                            //PlayerFiles.saveGame(player);
                        }
                    }
                });
        if(returnCode == 2) {
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
    public EntityList<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the npc list.
     *
     * @return The npc list.
     */
    public EntityList<NPC> getNPCs() {
        return npcs;
    }

    /**
     * Checks if a player is online.
     *
     * @param name The player's name.
     * @return <code>true</code> if they are online, <code>false</code> if not.
     */
    public boolean isPlayerOnline(String name) {
        LoginDebugger.getDebugger().log("Checking online players!");
        name = NameUtils.formatName(name);
        for(Player player : players) {
            if(player != null && player.getName().equalsIgnoreCase(name)) {
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
    public Player getPlayer(String name) {
        name = NameUtils.formatName(name);
        for(Player player : players) {
            if(player.getName().equalsIgnoreCase(name) && !player.isHidden()) { //yes
                return player;
            }
        }
        return null;
    }
    /**
     * Attempts to gracefully close a session
     * @param session The session that is about to be closed
     */
    public boolean gracefullyExitSession(IoSession session){
        if(session.containsAttribute("player")) {
            try {
                Player p = (Player)session.getAttribute("player");
                if(p != null) {
                    unregister(p);
                    return true;
                }
            }catch(ClassCastException e) {
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
    public void unregister(final Player player) {
		/*
		 * Combat.resetAttack(player.cE); final long xlog =
		 * System.currentTimeMillis();
		 */
        if(System.currentTimeMillis() - player.getExtraData().getLong("lastUnregister") < 1000)
            return;
        player.getExtraData().put("lastUnregister", System.currentTimeMillis());
        if(System.currentTimeMillis() - player.cE.lastHit >= 10000 && !player.isDead() && !player.isBusy() && player.duelAttackable < 1) {
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

    public void unregister2(final Player player) {
        //auto save upon being called.
        if(player.getLogging() != null)
            //player.getLogging().log("Logging out");
            Combat.logoutReset(player.cE);
        player.getDungoneering().fireOnLogout(player);
        player.setActive(false);
        LastManStanding.getLastManStanding().leaveGame(player, true);
        // Combat.resetAttack(player.cE);
        resetPlayersNpcs(player);
        resetSummoningNpcs(player);
        player.getPermExtraData().put("logintime", player.getPermExtraData().getLong("logintime") + (System.currentTimeMillis() - player.loginTime));
        player.getTicketHolder().fireOnLogout();

        try {
            ClanManager.leaveChat(player, false, false);
        } catch(Exception e) {
            e.printStackTrace();
        }
        // if(Server.dedi)
        // HighscoreConnection.updateHighscores(player.getName(),
        // ""+player.getRights().toInteger(), player.getSkills().getExps());
        if(player.duelAttackable <= 0) {
            Duel.declineTrade(player);
        } else {
            Duel.finishFullyDuel(player);
            player.setLocation(Location.create(3360 + Combat.random(17),
                    3274 + Combat.random(3), 0));
        }
        if(LastManStanding.getLastManStanding().gameStarted && LastManStanding.inLMSArea(player.cE.getAbsX(), player.cE.getAbsY())) {
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

        BarrowsFFA.barrowsFFA.exit(player);

        // logger.info("Unregistered player : " + player + " [online=" +
        // players.size() + "]");
        // System.out.println("Unregistered player : " + player.getName() +
        // " [online=" + players.size() + "]");
        engine.submitWork(new Runnable() {
            public void run() {

                if (!Rank.hasAbility(player, Rank.DEVELOPER))
                    getLogsConnection().offer(new AccountValuesRequest(player));

                player.getLogManager().add(LogEntry.logout(player));
                player.getLogManager().clearExpiredLogs();
                player.getLogManager().save();
                long dp = player.getAccountValue().getTotalValue();
                long pkp = player.getAccountValue().getPkPointValue();
                if(player.getValueMonitor().getValueDelta(dp) > 0 || player.getValueMonitor().getPKValueDelta(pkp) > 0)
                    World.getWorld().getLogsConnection().offer(String.format("INSERT INTO deltavalues (name,startvalue,startpkvalue,endvalue,endpkvalue,deltavalue,deltapkvalue) "  +
                             "VALUES ('%s',%d,%d,%d,%d,%d,%d)", player.getName(),player.getValueMonitor().getStartValue(),player.getValueMonitor().getStartPKValue(),
                            dp,pkp,player.getValueMonitor().getValueDelta(dp), player.getValueMonitor().getPKValueDelta(pkp)));
                if(player.verified)
                    loader.savePlayer(player, "world save");
                resetSummoningNpcs(player);
                if(World.getWorld().getLoginServerConnector() != null) {
                    World.getWorld().getLoginServerConnector().disconnected(player.getName());
                }
                player.destroy();
				/*
				 * player.getSkills().destroy();
				 * player.getActionSender().destroy();
				 * player.getInterfaceState().destroy();
				 */
                // player.getSession().removeAttribute("player");
            }
        });
        if(!Rank.hasAbility(player, Rank.ADMINISTRATOR) && player.getHighscores().needsUpdate())
            getDonationsConnection().offer(new HighscoresRequest(player.getHighscores()));
    }

    /**
     * Handles an exception in any of the pools.
     *
     * @param t The exception.
     */
    public void handleError(Throwable t) {
        logger.severe("An error occurred in an executor service! The server will be halted immediately.");
        t.printStackTrace();
        // System.exit(1);
    }

    public BanManager getBanManager() {
        return banManager;
    }

    public BountyHandler getBountyHandler() {
        return bountyHandler;
    }

    public void update(int time, final String reason) {
        //if(updateInProgress)
        //return;
        updateTimer = time; //modifies timer regardless
        updateInProgress = true;
        for(Player p : getPlayers()) {
            p.getActionSender().sendUpdate();
        }
        submit(new Event(1000) {
            @Override
            public void execute() {
                System.out.println("Seconds left: " + updateTimer);
                updateTimer--;
                if(!updateInProgress)
                    this.stop();
                if(updateTimer == 0) {
                    for(Player p : getPlayers()) {
                        Trade.declineTrade(p);
                    }
                    for(final Dungeon dungeon : Dungeon.activeDungeons) {
                        try {
                            dungeon.complete();
                        }catch(final Exception ex) {

                        }
                    }
                    ClanManager.save();
                    new Restart(reason).execute();
                }
            }
        });
    }

    public void stopUpdate() {
        updateInProgress = false;
    }

    static {
        CommandHandler.submit(new Command("npcssize", Rank.PLAYER) {
            @Override
            public boolean execute(Player player, String input) {
                player.getActionSender().sendMessage(
                        "Npcs: " + getWorld().getNPCs().size());
                player.getActionSender().sendMessage(
                        "Waiting list: " + getWorld().npcsWaitingList.size());
                return true;
            }
        });
    }
}