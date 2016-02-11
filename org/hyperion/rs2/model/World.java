package org.hyperion.rs2.model;

import org.apache.mina.core.session.IoSession;
import org.hyperion.Configuration;
import org.hyperion.Server;
import org.hyperion.map.BlockPoint;
import org.hyperion.map.DirectionCollection;
import org.hyperion.rs2.*;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.EventManager;
import org.hyperion.rs2.event.impl.*;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.content.bounty.BountyHunter;
import org.hyperion.rs2.model.content.bounty.BountyHunterEvent;
import org.hyperion.rs2.model.content.bounty.BountyHunterLogout;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.jge.event.PulseGrandExchangeEvent;
import org.hyperion.rs2.model.content.minigame.Bork;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.model.content.minigame.LastManStanding;
import org.hyperion.rs2.model.log.LogEntry;
import org.hyperion.rs2.model.punishment.event.PunishmentExpirationEvent;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.PacketBuilder;
import org.hyperion.rs2.net.PacketManager;
import org.hyperion.rs2.packet.PacketHandler;
import org.hyperion.rs2.task.Task;
import org.hyperion.rs2.util.ConfigurationParser;
import org.hyperion.rs2.util.EntityList;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.util.Misc;

import java.io.FileInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

/**
 * Holds data global to the game world.
 *
 * @author Gilles
 */
public final class World {

    /**
     * TEMP
     */
    private final static Set<String> unlockedPlayers = new HashSet<>();

    private final static Set<String> unlockedRichPlayers = new HashSet<>();

    public static int worldmapobjects = 10331; // 10331, 5116
    @SuppressWarnings("unchecked")
    public static Map<BlockPoint, DirectionCollection>[] World_Objects = new Hashtable[worldmapobjects];

    static {
        for (int i = 0; i < worldmapobjects; i++) {
            World_Objects[i] = null;
            World_Objects[i] = new Hashtable<>();
        }
    }

    public static void submit(Event event) {
        EventManager.submit(event);
    }

    public static void submit(Task task) {
        Server.getLoader().getEngine().pushTask(task);
    }

    /**
     * END OF THE TEMP LEFTOVER CODE
     */

    /** Private constructor to prevent instancing **/
    private World() {}

    /** The highest playercount that happened while the server was online in this session **/
    private static int maxPlayerCount = 0;

    /** The current WorldLoader that is being used to load the Players **/
    private static WorldLoader loader;

    /** The queue of {@link Player}s waiting to be logged in. **/
    private final static Queue<Player> logins = new ConcurrentLinkedQueue<>();

    /**The queue of {@link Player}s waiting to be logged out. **/
    private final static Queue<Player> logouts = new ConcurrentLinkedQueue<>();

    /** All of the registered players. */
    private final static EntityList<Player> players = new EntityList<>(Constants.MAX_PLAYERS);

    /** All of the registered NPCs. */
    public final static EntityList<NPC> npcs = new EntityList<>(Constants.MAX_NPCS);

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Queue<Player> getLoginQueue() {
        return logins;
    }

    public static Queue<Player> getLogoutQueue() {
        return logouts;
    }

    public static EntityList<Player> getPlayers() {
        return players;
    }

    public static EntityList<NPC> getNpcs() {
        return npcs;
    }

    public static Set<String> getUnlockedPlayers() {
        return unlockedPlayers;
    }

    public static Set<String> getUnlockedRichPlayers() {
        return unlockedRichPlayers;
    }

    public static void register(Entity entity) {
        EntityHandler.register(entity);
    }

    public static void unregister(Entity entity) {
        EntityHandler.deregister(entity);
    }

    public static void loadConfiguration() {
        try(FileInputStream fis = new FileInputStream("data/configuration.cfg")) {
            ConfigurationParser p = new ConfigurationParser(fis);
            Map<String, String> mappings = p.getMappings();

            if (mappings.containsKey("worldLoader")) {
                String worldLoaderClass = mappings.get("worldLoader");
                Class<?> loader = Class.forName(worldLoaderClass);
                World.loader = (WorldLoader) loader.newInstance();
            } else {
                loader = new GenericWorldLoader();
            }
            Map<String, Map<String, String>> complexMappings = p
                    .getComplexMappings();
			/*
			 * Packets configuration.
			 */
            if (complexMappings.containsKey("packetHandlers")) {
                for (Map.Entry<String, String> handler : complexMappings.get("packetHandlers").entrySet()) {
                    int id = Integer.parseInt(handler.getKey());
                    Class<?> handlerClass = Class.forName(handler.getValue());
                    PacketManager.getPacketManager().bind(id, (PacketHandler)handlerClass.newInstance());
                    Server.getLogger().fine("Bound " + handler.getValue() + " to opcode: " + id);
                }
            }
        } catch(Exception e) {
            Server.getLogger().log(Level.SEVERE, "Something went wrong while loading the World configuration file.");
        }
    }

    public static void registerGlobalEvents() {
        submit(new UpdateEvent());
        submit(new CleanupEvent());
        submit(new BankersFacing());
        submit(new PlayerEvent36Seconds());
        submit(new PlayerEvent1Second());
        submit(new EPEvent());
        submit(new HunterEvent());
        submit(new DisconnectEvent());
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
        submit(new PunishmentExpirationEvent());
        submit(new WildernessBossEvent(true));
        submit(new PulseGrandExchangeEvent());
}

    /**
     * Loads a player's game in the work service.
     */
    public static void load(final PlayerDetails playerDetails) {
        Server.getLoader().getEngine().submitWork(() -> {
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
     * Registers a new player.
     *
     * @param player The player to register.
     */
    public static void register(final Player player) {
        int returnCode = 2;
        if (returnCode == 2) {
            if (!players.add(player)) {
                returnCode = 7;
                LoginDebugger.getDebugger().log("Could not register player " + player.getName());
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
        final int fReturnCode = returnCode;
        PacketBuilder bldr = new PacketBuilder();
        bldr.put((byte) returnCode);
        bldr.put((byte) Rank.getPrimaryRankIndex(player));
        bldr.put((byte) 0);
        player.getSession().write(bldr.toPacket()).addListener(future -> {
                    if(fReturnCode != 2){
                        player.getSession().close(false);
                    } else {
                        player.getActionSender().sendLogin();
                    }
                });
        if (returnCode == 2)
            HostGateway.enter(player.getShortIP());
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
        if (player.getLogging() != null)
            Combat.logoutReset(player.cE);
        player.getDungeoneering().fireOnLogout(player);
        player.setActive(false);
        LastManStanding.getLastManStanding().leaveGame(player, true);
        Bork.doDeath(player);
        resetPlayersNpcs(player);
        resetSummoningNpcs(player);
        player.getPermExtraData().put("logintime", player.getPermExtraData().getLong("logintime") + (System.currentTimeMillis() - player.getLogintime()));
        player.getTicketHolder().fireOnLogout();

        try {
            ClanManager.leaveChat(player, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        Server.getLoader().getEngine().submitWork(() -> {
            player.getLogManager().add(LogEntry.logout(player));
            player.getLogManager().clearExpiredLogs();
            player.getLogManager().save();
            if (player.verified)
                loader.savePlayer(player);
            resetSummoningNpcs(player);
            player.destroy();
        });
    }

    static {
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