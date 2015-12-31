package org.hyperion.rs2.model;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.hyperion.Server;
import org.hyperion.data.Persistable;
import org.hyperion.rs2.Constants;
import org.hyperion.rs2.GenericWorldLoader;
import org.hyperion.rs2.action.ActionQueue;
import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.event.impl.PlayerDeathEvent;
import org.hyperion.rs2.model.Damage.Hit;
import org.hyperion.rs2.model.Damage.HitType;
import org.hyperion.rs2.model.UpdateFlags.UpdateFlag;
import org.hyperion.rs2.model.achievements.AchievementData;
import org.hyperion.rs2.model.achievements.Difficulty;
import org.hyperion.rs2.model.cluescroll.ClueScrollManager;
import org.hyperion.rs2.model.combat.Combat;
import org.hyperion.rs2.model.combat.LastAttacker;
import org.hyperion.rs2.model.combat.npclogs.NPCKillsLogger;
import org.hyperion.rs2.model.combat.pvp.PvPArmourStorage;
import org.hyperion.rs2.model.container.Container;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Inventory;
import org.hyperion.rs2.model.container.ShopManager;
import org.hyperion.rs2.model.container.Trade;
import org.hyperion.rs2.model.container.bank.Bank;
import org.hyperion.rs2.model.container.bank.BankField;
import org.hyperion.rs2.model.container.bank.BankItem;
import org.hyperion.rs2.model.container.duel.Duel;
import org.hyperion.rs2.model.container.duel.DuelRule.DuelRules;
import org.hyperion.rs2.model.container.impl.TabbedContainer;
import org.hyperion.rs2.model.content.ContentEntity;
import org.hyperion.rs2.model.content.bounty.BountyHunter;
import org.hyperion.rs2.model.content.bounty.BountyPerks;
import org.hyperion.rs2.model.content.clan.Clan;
import org.hyperion.rs2.model.content.clan.ClanManager;
import org.hyperion.rs2.model.content.ge.GrandExchange;
import org.hyperion.rs2.model.content.grandexchange.GrandExchangeV2.GEItem;
import org.hyperion.rs2.model.content.jge.tracker.JGrandExchangeTracker;
import org.hyperion.rs2.model.content.minigame.DangerousPK.ArmourClass;
import org.hyperion.rs2.model.content.minigame.barrowsffa.BarrowsFFAHolder;
import org.hyperion.rs2.model.content.misc.ItemDropping;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc.Mail;
import org.hyperion.rs2.model.content.misc.SkillingData;
import org.hyperion.rs2.model.content.misc.TriviaSettings;
import org.hyperion.rs2.model.content.misc2.Dicing;
import org.hyperion.rs2.model.content.misc2.RunePouch;
import org.hyperion.rs2.model.content.misc2.SpawnTab;
import org.hyperion.rs2.model.content.misc2.teamboss.TeamBossSession;
import org.hyperion.rs2.model.content.polls.PollInterface;
import org.hyperion.rs2.model.content.pvptasks.PvPTask;
import org.hyperion.rs2.model.content.skill.Farming;
import org.hyperion.rs2.model.content.skill.Farming.Farm;
import org.hyperion.rs2.model.content.skill.Prayer;
import org.hyperion.rs2.model.content.skill.RandomEvent;
import org.hyperion.rs2.model.content.skill.agility.Agility;
import org.hyperion.rs2.model.content.skill.dungoneering.DungoneeringHolder;
import org.hyperion.rs2.model.content.skill.slayer.SlayerHolder;
import org.hyperion.rs2.model.content.ticket.TicketHolder;
import org.hyperion.rs2.model.itf.InterfaceManager;
import org.hyperion.rs2.model.joshyachievementsv2.tracker.AchievementTracker;
import org.hyperion.rs2.model.log.LogManager;
import org.hyperion.rs2.model.recolor.RecolorManager;
import org.hyperion.rs2.model.region.Region;
import org.hyperion.rs2.model.sets.CustomSetHolder;
import org.hyperion.rs2.model.shops.LegendaryStore;
import org.hyperion.rs2.net.ActionSender;
import org.hyperion.rs2.net.ISAACCipher;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.net.Packet;
import org.hyperion.rs2.packet.NpcClickHandler;
import org.hyperion.rs2.packet.ObjectClickHandler;
import org.hyperion.rs2.saving.SavedSingleValue;
import org.hyperion.rs2.util.AccountLogger;
import org.hyperion.rs2.util.AccountValue;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.TextUtils;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a player-controller character.
 *
 * @author Graham Edgecombe
 */
public class Player extends Entity implements Persistable, Cloneable{

	private int databaseId = -1;

	public int getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(int id) {
		databaseId = id;
	}

	private LinkedList<SavedSingleValue> savedValues = new LinkedList<SavedSingleValue>();

	public LinkedList<SavedSingleValue> getSavedValues() {
		return savedValues;
	}

	public static final int MAX_NAME_LENGTH = 12;
	public final long loginTime = System.currentTimeMillis();
	public final List<DuelRules> duelRules = new ArrayList<>(20);
	private final InterfaceManager interfaceManager = new InterfaceManager(this);
	private final ValueMonitor valueMonitor = new ValueMonitor(this);
	private final GrandExchange grandExchange = new GrandExchange(this);
	private final BarrowsFFAHolder barrowsFFA = new BarrowsFFAHolder();
	private final TicketHolder ticketHolder = new TicketHolder();
	private final AchievementTracker achievementTracker = new AchievementTracker(this);
	private final RandomEvent randomEvent = new RandomEvent(this);
	private final DungoneeringHolder dungoneeringHolder = new DungoneeringHolder();
	private final ExtraData permExtraData = new ExtraData();
	private final CustomSetHolder customSetHolder = new CustomSetHolder(this);
	private final RecolorManager recolorManager = new RecolorManager(this);
	/**
	 * Holds the beginning time of the player's game session.
	 */
	private final long logintime = System.currentTimeMillis();
	private final List<TeamBossSession> teamBossSessions = new ArrayList<>();
	/**
	 * The <code>IoSession</code>.
	 */
	private final IoSession session;
	/**
	 * The ISAAC cipher for incoming data.
	 */
	private final ISAACCipher inCipher;
	/**
	 * The ISAAC cipher for outgoing data.
	 */
	private final ISAACCipher outCipher;
	/**
	 * The action sender.
	 */
	private final ActionSender actionSender = new ActionSender(this);
	/**
	 * A queue of pending chat messages.
	 */
	private final Queue<ChatMessage> chatMessages = new LinkedList<ChatMessage>();
	/**
	 * A queue of actions.
	 */
	private final ActionQueue actionQueue = new ActionQueue();
	/**
	 * The interface state.
	 */
	private final InterfaceState interfaceState = new InterfaceState(this);
	/**
	 * A queue of packets that are pending.
	 */
	private final Queue<Packet> pendingPackets = new LinkedList<Packet>();
	/**
	 * The request manager which manages trading and duelling requests.
	 */
	private final RequestManager requestManager = new RequestManager(this);
	/**
	 * The UID, i.e. number in <code>random.dat</code>.
	 */
	private final int uid;
	/**
	 * Bounty hunter targets etc
	 */
	private final BountyHunter bountyHunter = new BountyHunter(this);
	private final BountyPerks bhperks = new BountyPerks();
	private final SlayerHolder slayTask = new SlayerHolder();
	/**
	 * The player's equipment.
	 */
	private final Container equipment = new Container(Container.Type.STANDARD, Equipment.SIZE);
	/**
	 * The player's skill levels.
	 */
	private final Skills skills = new Skills(this);
	/**
	 * The player's inventory.
	 */
	private final Container inventory = new Container(Container.Type.STANDARD, Inventory.SIZE);
	private final Container trade = new Container(Container.Type.STANDARD, Trade.SIZE);
	private final Container duel = new Container(Container.Type.STANDARD, Duel.SIZE);
	private final Container runePouch = new Container(Container.Type.ALWAYS_STACK, RunePouch.SIZE);
	/**
	 * The player's bank.
	 */
	private final TabbedContainer bank = new TabbedContainer(Container.Type.ALWAYS_STACK, Bank.SIZE, this);
	/**
	 * The player's settings.
	 */
	private final Settings settings = new Settings();
	public int pin = -1;
	public String lastIp;
	public boolean verified;
	public int tutorialProgress = 0;
	public NPCKillsLogger npckillLogger = new NPCKillsLogger();
	public int maxCapePrimaryColor = 0;
	public int maxCapeSecondaryColor = 0;
	public int compCapePrimaryColor;
	public int compCapeSecondaryColor;
	/**
	 * Amount of charges on your shadow silk hood
	 */

	public int sshCharges;
	public int turkeyKills;
	public boolean cleaned = false;
	public boolean loggedOut = false;
	public String lastEnemyName = "";
	public double prayerDrain = 0;
	public boolean showEP = true;
	public int EP = 0;
	public boolean[] gnomeCourse = new boolean[7];
	public int blackMarks = 0;
	/**
	 * Active flag: if the player is not active certain changes (e.g. items)
	 * should not send packets as that indicates the player is still loading.
	 */
	public boolean active = false;
	/**
	 * Informed of hybrid area
	 */
	public boolean hasBeenInformed;
	/**
	 * is FFA games interface displayed?
	 */
	public boolean ffaDisplayed;
	public boolean receivedStarter = true;
	public boolean tradeAccept1 = false;
	public boolean tradeAccept2 = false;
	public boolean onConfirmScreen = false;
	public boolean openingTrade = false;
	public boolean duelRule[] = new boolean[24];
	public int duelAttackable = 0;
	public int duelRuleOption = 0;
	public boolean banEquip[] = new boolean[14];
	public int[] skillRecoverTimer = new int[Skills.SKILL_COUNT];
	public byte levelupSkillId = -1;
	public boolean inGame;
	public int RFDLevel = 0;
	public int WGLevel = 0;
	public int rangeMiniShots = -1;
	public int rangeMiniScore = 0;
	public long splitDelay = 0L;
	public int fightCavesWave = 0;
	public int fightCavesKills = 0;
	public Player duelWith2 = null;
	public int hintIcon = 0;
	public boolean attackOption = false;
	public boolean duelOption = false;
	public boolean splitPriv = true;
	public long lastTicketRequest;
	/**
	 * confirmed bh tele too lazy tbh to do soething else
	 */
	public boolean bhConfirmedTeleport = false;
	/**
	 * Right-click moderation
	 */
	public Player onModeration = null;
	public byte currentInterfaceStatus = 0;
	public int SummoningCounter = 0;
	public int killId = -1;// the npc id to kill
	public int shouldKill = 0;// the required kill count will apply to all
	public boolean specOn = false;
	public Player challengedBy = null;
	public int[] checkersRecord;
	public Player beingFollowed = null;
	public Player isFollowing = null;
	public boolean isMoving = false;
	public Player tradeWith2 = null;
	public boolean autoRetailate = true;
	public long foodTimer = System.currentTimeMillis();
	public long comboFoodTimer = System.currentTimeMillis();
	public long specPotionTimer = 0;
	public long chargeTill;
	//i hate doing this but ugh
	public boolean joiningPits = false;
	public int smithingMenu = -1;
	public int[] delayObjectClick = new int[4];// id,x,y,type
	public int wildernessLevel = -1;
	public boolean isInMuli = false;
	public int headIconId = -1;
	public boolean cannotSwitch = false;
	/*
	 * Cached details.
	 */
	public int logoutTries = 0;
	public String display;
	public int[] specialUid;
	public boolean newCharacter = false;
	public boolean ignoreOnLogin = false;
	public boolean oldFag = false;
	public boolean decided = false;
	public boolean inAction;
	public List<Long> ignores = new ArrayList<Long>(1);
	public int[] chatStatus = new int[3];// normal,friends,trade, 0 - on, 1
	public int membershipDay = 1;
	public int membershipYear = 2005;
	public int membershipTerm = 31;
	public int forceWalkX1;
	public int forceWalkY1;
	public int forceWalkX2;
	public int forceWalkY2;
	public int forceSpeed1;
	public int forceSpeed2;
	public int forceDirection;
	public long teleBlockTimer = System.currentTimeMillis() - 3600000;
	public int slayerTask = 0;
	public String bankPin = "";
	public int[] pinOrder = new int[10];
	public String enterPin = "";
	public boolean debug;
	public int skillMenuId = 0;
	public boolean isMuted = false;
	public int[] godWarsKillCount = new int[4];
	public boolean[] invSlot = new boolean[28];
	public boolean[] equipSlot = new boolean[14];
	public int[] itemKeptId = new int[4];
	public boolean forcedIntoSkilling = false;
	public GEItem[] geItem = new GEItem[40];
	public List<GEItem> geItems = new LinkedList<GEItem>();
	public boolean closeChatInterface = false;
	public int yellMessage = 0;
	public boolean vengeance = false;
	public long LastTimeLeeched;
	public long lastTimeSoulSplit;
	public int slayerAm = 0;
	public double slayerExp = 0;
	public int clueStage = 8;
	public int slayerCooldown = 0;
	public boolean yellMuted = false;
	public int tutIsland = 10;
	public int tutSubIsland = 0;
	public boolean resetingPin = false;
	public String tempPass = "43g9g3er";
	public int slayerPoints = 0;
	public long potionTimer = 0;
	public long contentTimer = 0;
	public String lastSearch = "";
	public String bloodName = null;
	public String tempBlood = null;
	public boolean xpLock = false;


	/*
	 * Attributes specific to our session.
	 */
	public long lastScoreCheck = System.currentTimeMillis() + 20000;
	public boolean isOverloaded;
	public long lastVeng = 0;
	public long antiFireTimer = 0;
	public boolean superAntiFire = false;
	public long overloadTimer = 0;
	public boolean openedBoB = false;
	public ArmourClass pickedClass = null;
	public long lastAccountValueTime = System.currentTimeMillis();
	private boolean doublechar = false;
	private boolean needsNamechange = false;
	/**
	 * This is for whether the player is using the bank or the player is using the grand exchange, to open the right
	 * interface after entering your pin.
	 */
	private boolean isBanking;

	/*
	 * Core login details.
	 */
	private int pid = -1;
	private int treasureScroll;
	private int gameMode;
	private boolean completedTG;
	private boolean hasMaxCape = false;
	private boolean hasCompCape = false;
	private Agility agility = new Agility(this);
	private PlayerChecker playerChecker = PlayerChecker.create();
	private HashMap<AchievementData, Integer> achievementsProgress = new HashMap<>();
	private Difficulty viewingDifficulty = Difficulty.VERY_EASY;
	private long previousSessionTime = System.currentTimeMillis();
	private long lastHonorPointsReward = System.currentTimeMillis();
	private AccountValue accountValue = new AccountValue(this);
	private AccountLogger logger = new AccountLogger(this);
	private PlayerPoints playerPoints = new PlayerPoints(this);
	private ExpectedValues expectedValues = new ExpectedValues(this);
	private Spam spam = new Spam(this);
	private SpecialBar specbar = new SpecialBar(this);
	private SummoningBar summoningBar = new SummoningBar(this);
	private Yelling yelling = new Yelling();
	private ExtraData extraData = new ExtraData();
	private QuestTab questtab = new QuestTab(this);
	private PollInterface poll = new PollInterface(this);
	private SpawnTab spawntab = new SpawnTab(this);
	private AchievementTab achievementtab = new AchievementTab(this);
	private News news = new News(this);
	private ItemDropping itemDropping = new ItemDropping();
	private TriviaSettings ts = new TriviaSettings(0, false);
	private Mail mail = new Mail(this);
	private SkillingData sd = new SkillingData();
	private boolean canSpawnSet = true;
	private boolean hasTarget = false;
	private long created;
	private long disconnectedTimer = System.currentTimeMillis();
	private int diced = 0;
	private int skullTimer = 0;
	private long lastSQL = 0;
	private long lastVoted = 0;
	private long lastEPIncrease = System.currentTimeMillis();
	/**
	 * The current chat message.
	 */
	private ChatMessage currentChatMessage;
	/**
	 * The name.
	 */
	private String name;
	/**
	 * The name expressed as a long.
	 */
	private long nameLong;
	/**
	 * The password.
	 */
	private Password password = new Password();
	private int initialSource = GenericWorldLoader.MERGED;
	private int source = GenericWorldLoader.MERGED;
	/**
	 * The rights level.
	 */
	private long playerRank = 1;
	/**
	 * Overload timer, allows for resetting
	 */

	private AtomicInteger overloadCounter = new AtomicInteger(0);
	/**
	 * PvP Armour storage, initialized in saving
	 */

	private PvPArmourStorage pvpStorage = new PvPArmourStorage();
	private LastAttacker lastAttacker;
	/**
	 * The members flag.
	 */
	private boolean members = true;
	//private int[] bonus = new int[12];
	private EquipmentStats bonus = new EquipmentStats();
	private int shopId = -1;
	private Player tradeWith = null;
	private long lastDuelUpdate = 0L;
	private long dragonFireSpec = 0L;
	private boolean chargeSpell;
	private boolean isPlayerBusy = false;
	private boolean isSkilling = false;
	private boolean canWalk = false;
	private boolean npcState = false;
	private int npcId = -1;
	private Prayers prayers = new Prayers(true);
	private double drainRate;
	/**
	 * The player's appearance information.
	 */
	private Appearance appearance = new Appearance();
	/**
	 * The player's BoB.
	 */
	private Container bob;
	/**
	 * The cached update block.
	 */
	private Packet cachedUpdateBlock;
	private LogManager logManager;
	/*  public InterfaceManager getInterfaceManager(){
			return itfManager;
        }
    */
	private String IP;
	private boolean isDoingEmote = false;
	private int fightPitsDamage;
	// minigames
	private int damagedCorp;
	private SpellBook spellBook = new SpellBook(SpellBook.DEFAULT_SPELLBOOK);
	private FriendList friendList;
	private long lastTeleport = System.currentTimeMillis();
	private PvPTask currentPvPTask;
	private int pvpTaskAmount;
	private Farm farm = Farming.getFarming().new Farm();
	/**
	 * Clan Stuff.
	 */

	private int clanRank = 0;
	private String clanName = "";
	private AutoSaving autoSaving = new AutoSaving(this);
	private int playerUptime = 0;
	/**
	 * KillStreak stuff
	 */
	private int killStreak = 0;
	private String[] lastKills = {"", "", "", "", ""};
	private int bounty = 10;
	private int killCount = 0;
	private int deathCount = 0;
	private BankField bankField = new BankField(this);
	private Highscores highscores;
	private long firstVoteTime = -1;
	private int voteCount;
	private JGrandExchangeTracker geTracker;

	/**
	 * Creates a player based on the details object.
	 *
	 * @param details The details object.
	 */
	public Player(PlayerDetails details, boolean newCharacter) {
		super();
		//System.out.println("ok");
		LoginDebugger.getDebugger().log("In Player constructor");
		this.session = details.getSession();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();

		this.name = details.getName().toLowerCase();
		this.specialUid = details.specialUid;
		this.display = details.getName();
		if (!NameUtils.isValidName(name)) {
			System.out.println("Invalid name!!!!!" + name);
		}
		this.nameLong = NameUtils.nameToLong(this.name);
		this.password.setRealPassword(details.getPassword());
		this.uid = details.getUID();
		this.IP = details.IP;
		LoginDebugger.getDebugger().log("1.So far made new Player obj");
		LoginDebugger.getDebugger().log("2.So far made new Player obj");
		this.getUpdateFlags().flag(UpdateFlag.APPEARANCE);
		this.setTeleporting(true);
		this.resetPrayers();
		this.newCharacter = newCharacter;
		LoginDebugger.getDebugger().log("3.So far made new Player obj");
		//int banstatus = World.getWorld().getBanManager().getStatus(name);
		LoginDebugger.getDebugger().log("4.So far made new Player obj");
		//if(banstatus == BanManager.YELL)
		//	yellMuted = true;
		//else if(banstatus == BanManager.MUTE)
		//	isMuted = true;
		active = false;
		if (newCharacter) {
			this.created = System.currentTimeMillis();
			// for(int i = 0; i < )
		}
		lastAttacker = new LastAttacker(name);
		friendList = new FriendList();
		logManager = new LogManager(this);
		// itfManager = new InterfaceManager(this);
	}

	public Player() {
		this.inCipher = null;
		this.outCipher = null;
		this.session = null;
		this.uid = 0;
	}// used for checking accounts

	public static int getConfigId(int i) {
		switch (i) {
			case 0:
				return 0;

			case 1:
				return 1;

			case 2:
				return 2;

			case 3:
				return 3;
		}
		return 0;
	}

	public static void resetCorpDamage() {
		for (Player p : World.getWorld().getPlayers()) {
			if (p == null)
				continue;
			if (p.getCorpDamage() > 0)
				p.setCorpDamage(0);
		}
	}

	private static String getPeopleString() {
		String ppl = " ";
		switch (Misc.random(100)) {
			case 0:
				ppl += "idiots";
				break;
			case 1:
				ppl += "narbs";
				break;
			case 2:
				ppl += "shits";
				break;
			case 3:
				ppl += "enemies";
				break;
			case 4:
				ppl += "noobs";
				break;
			case 5:
				ppl += "chickens";
				break;
			case 6:
				ppl += "fleshbags";
				break;
			default:
				ppl += "people";
				break;
		}
		return ppl;
	}

	public boolean doubleChar() {
		return doublechar;
	}

	public void setDoubleChar(boolean b) {
		System.out.println("Double char case!");
		doublechar = b;
	}

	public boolean needsNameChange() {
		return needsNamechange;
	}

	public void setNeedsNameChange(boolean b) {
		needsNamechange = b;
	}

	@Override
	public Object clone() throws CloneNotSupportedException{
		Player clone = (Player)super.clone();
		return clone;
	}

	/*
	 * Player NPC
	 */

    public final InterfaceManager getInterfaceManager() {
        return interfaceManager;
    }

    public ValueMonitor getValueMonitor() {return valueMonitor;}

    public GrandExchange getGrandExchange() {return grandExchange;}

    public BarrowsFFAHolder getBarrowsFFA() { return barrowsFFA; }

    public int getTutorialProgress() {
		return tutorialProgress;
	}

    public void setTutorialProgress(int step) {
        tutorialProgress = step;
    }

    public final TicketHolder getTicketHolder() {
        return ticketHolder;
    }

	public boolean isBanking() {
		return isBanking;
	}

	public void setBanking(boolean status) {
		isBanking = status;
	}

	public NPCKillsLogger getNPCLogs() {
		return npckillLogger;
	}

    public boolean isPidSet(){
        return pid != -1;
    }

    public int getPid(){
        return pid;
    }

	/*
	 * Attributes.
	 */

    public void setPid(final int pid){
		this.pid = pid;
	}

    public boolean hardMode() {
        return gameMode == 1;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int mode) {
        this.gameMode = mode;
    }

	public void setMaxCape(boolean b) {
		hasMaxCape = b;
	}

	public boolean hasMaxCape() {
		return hasMaxCape;
	}

	public void setCompCape(boolean b) {
		hasCompCape = b;
	}

	public boolean hasCompCape() {
		return hasCompCape;
	}

	public Agility getAgility() {
		return agility;
	}

	public PlayerChecker getChecking() {
		return playerChecker;
	}

    public HashMap<AchievementData, Integer> getAchievementsProgress() {
        return achievementsProgress;
    }

	public AchievementTracker getAchievementTracker(){
		return achievementTracker;
	}

	public RandomEvent getRandomEvent() {
		return randomEvent;
	}

    public Difficulty getViewingDifficulty() {
		return viewingDifficulty;
	}

	public void setViewingDifficulty(Difficulty viewingDifficulty) {
		this.viewingDifficulty = viewingDifficulty;
    }

	public boolean checkMaxCapeRequirment() {
		for (int i = 7; i < this.getSkills().getLevels().length; i++) {
			if (i >= 21 && i != Skills.SUMMONING && i != Skills.DUNGEONEERING)
				continue;
			if(this.getSkills().getLevels()[i] < 99)
				return false;
		}
		return this.getPoints().getEloPeak() >= 1900;
	}

   // private InterfaceManager itfManager;

	public boolean checkCompCapeReq() {
		for(int i = 7; i < this.getSkills().getXps().length; i++) {
			if(i >= 21 && i != Skills.SUMMONING && i != Skills.DUNGEONEERING)
				continue;
			if(this.getSkills().getXps()[i] < 200000000)
				return hasCompCape;
		}
		if(this.getPoints().getEloPeak() < 2200)
			return hasCompCape;
		return true;
	}

    public void checkCapes() {
        checkContainers(12747, checkCompCapeReq(), "Completionist cape");
        checkContainers(12744, checkMaxCapeRequirment(), "Max cape");
        checkContainers(18509, skills.getRealLevels()[Skills.DUNGEONEERING]== 99, "Dungeoneering cape");
        checkContainers(19709, skills.getExperience(Skills.DUNGEONEERING) == Skills.MAXIMUM_EXP, "Dungeoneering master cape");
    }

    private void checkContainers(final int id, final boolean add, String name) {
        final Container[] containers = new Container[]{bank, equipment, inventory};
        boolean contains = false;
        for(final Container container : containers) {
            if(container.contains(id)) {
                contains = true;
                if(!add) {
					container.remove(Item.create(id));
				}
            }
        }

        if(!contains && add) {
			for (Player p : World.getWorld().getPlayers()) {
				p.sendLootMessage("Achievement", getSafeDisplayName() + " has just achieved " + name + "!");
			}
			bank.add(new BankItem(0, id, 1));
		}
    }

	/**
	 * Gets the KDR value rounded to 3 decimals.
	 *
	 * @return
	 */
	public double getKDR() {
		double kdr = getKillCount();
		if(getDeathCount() != 0) {
			kdr = (double) getKillCount() / (double) getDeathCount();
		}
		kdr = Misc.round(kdr, 3);
		return kdr;
	}

	public long getLastHonorPointsReward() {
		return lastHonorPointsReward;
	}

	public void setLastHonorPointsReward(long time) {
		lastHonorPointsReward = time;
	}

	public long getPreviousSessionTime() {
		return previousSessionTime;
	}

	public void setPreviousSessionTime(long time) {
		previousSessionTime = time;
	}

    public DungoneeringHolder getDungeoneering() {
        return dungoneeringHolder;
    }

    public CustomSetHolder getCustomSetHolder() {
        return customSetHolder;
    }

	public TriviaSettings getTrivia() {
		return ts;
	}

    public RecolorManager getRecolorManager(){
        return recolorManager;
    }

	public SummoningBar getSummBar() {
		return summoningBar;
	}

	public Mail getMail() {
		return mail;
	}

	public LastAttacker getLastAttack() {
		return lastAttacker;
	}

	public SkillingData getSkillingData() {
		return sd;
	}

	public ItemDropping getDropping() {
		return itemDropping;
	}

	public QuestTab getQuestTab() {
		return questtab;
	}

	public PollInterface getPoll() { return poll; }

	public SpawnTab getSpawnTab() {
		return spawntab;
	}

	public AchievementTab getAchievementTab() {
		return achievementtab;
	}

	public News getNews() {
		return news;
	}

	public ExtraData getExtraData() {
		return extraData;
	}

    public ExtraData getPermExtraData() {
        return permExtraData;
    }

	public Yelling getYelling() {
		return yelling;
	}

	public SpecialBar getSpecBar() {
		return specbar;
	}

	public Spam getSpam() {
		return spam;
	}

	public PlayerPoints getPoints() {
		return playerPoints;
	}

	public ExpectedValues getExpectedValues() {
		return expectedValues;
	}

	public AccountLogger getLogging() {
		return logger;
	}

	public AccountValue getAccountValue() {
		return accountValue;
	}

	public void setCanSpawnSet(boolean b) {
		this.canSpawnSet = b;
	}

	public boolean canSpawnSet() {
		return canSpawnSet;
	}

    public void init() {


        try {
            File f = new File("./data/charfarm/"+getName()+".bin");
            InputStream is = new FileInputStream(f);
            IoBuffer buf = IoBuffer.allocate(1024);
            buf.setAutoExpand(true);
            while(true) {
                byte[] temp = new byte[1024];
                int read = is.read(temp, 0, temp.length);
                if(read == - 1) {
                    break;
                } else {
                    buf.put(temp, 0, read);
                }
            }
            buf.flip();
            Farming.deserialize(buf, this);
        }catch(final Exception ex) {

        }


    }

        public void serialize() {
            try {
                OutputStream os = new FileOutputStream("data/charfarm/"+this.getName()+".bin");
                IoBuffer buf = IoBuffer.allocate(1024);
                buf.setAutoExpand(true);
                Farming.serialize(buf,this);
                buf.flip();
                byte[] data = new byte[buf.limit()];
                buf.get(data);
                os.write(data);
                os.flush();
                os.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
		}

	public boolean hasTarget() {
		return hasTarget;
	}

	public void setHasTarget(boolean b) {
		hasTarget = b;
	}

	public boolean isServerOwner() {
		return getName().equalsIgnoreCase(Server.getConfig().getString("owner"));
	}

	public long getCreatedTime() {
		return created;
	}

	public void setCreatedTime(long created) {
		this.created = created;
	}

	/**
	 * Gets the carried risk value of the player, the value of
	 * the Inventory + Equipment which is expressed in Pk Points
	 * where 1 Donator Point = 1000 Pk Points.
	 *
	 * @return
	 */
	public int getRisk() {
		int totalvalue = 0;
		List<Item> list = new ArrayList<Item>(Equipment.SIZE + Inventory.SIZE);
		for(Item item : getInventory().toArray()) {
			list.add(item);
		}
		for(Item item : getEquipment().toArray()) {
			list.add(item);
		}
		for(Item item : list) {
			if(item == null)
				continue;
			if(ItemSpawning.allowedMessage(item.getId()).length() > 0) {
				int value = ShopManager.getPoints(63, item.getId());
				if(value != 50000 && value != 0)
					totalvalue += value * 1000 * item.getCount();
				else {
					value = ShopManager.getPoints(75, item.getId());
					if(value != 50000 && value != 0) {
						totalvalue += value * 1000 * item.getCount();
					} else {
						value = ShopManager.getPoints(71, item.getId());
						if(value != 50000 && value != 0) {
							totalvalue += value * item.getCount();
						}
					}
				}

			}
		}
		//List<Item> keepItems = DeathDrops.itemsKeptOnDeath(this);
		/*for(Item item: keepItems) {
			if(item == null)
				continue;
			if(ItemSpawning.allowedMessage(item.getId()).length() > 0) {
				int value = ShopManager.getPoints(63, item.getId());
				if(value != 50000 && value != 0)
					totalvalue -= value*1000*item.getCount();
				else {
					value = ShopManager.getPoints(75, item.getId());
					if(value != 50000 && value != 0) {
						totalvalue -= value*1000*item.getCount();
					} else {
						value = ShopManager.getPoints(71, item.getId());
						if(value != 50000 && value != 0) {
							totalvalue -= value * item.getCount();
						}
					}
				}

			}
		}*/
		return totalvalue;
	}

	public boolean isDisconnected() {
		return System.currentTimeMillis() - disconnectedTimer > 15000;
	}

	public void updateDisconnectedTimer() {
		disconnectedTimer = System.currentTimeMillis();
	}

	/**
	 * Used to see the duration of the player's session.
	 *
	 * @returns for how long the player was online in milliseconds.
	 */
	public long onlineTime() {
		return System.currentTimeMillis() - logintime;
	}

	/**
	 * Sets the player's password.
	 *
	 */


    public final List<TeamBossSession> getTeamSessions() {
		return teamBossSessions;
	}

	public int getDiced() {
		return diced;
	}

	public void setDiced(int diced) {
		this.diced = diced;
	}

	public boolean isSkulled() {
		return skullTimer > 0;
	}

	public void setSkulled(boolean skulled) {
		if(! isSkulled() && skulled) {
			Prayer.setHeadIcon(this);
		}
		if(skulled) {
			skullTimer = 1200;
		} else {
			Prayer.setHeadIcon(this);
			skullTimer = 0;
		}
	}

	public void decreaseSkullTimer() {
		if(skullTimer > 0) {
			skullTimer--;
			if(skullTimer == 0) {
				setSkulled(false);
			}
		}

	}

	public int getSkullTimer() {
		return skullTimer;
	}

	public void setSkullTimer(int timer) {
		skullTimer = timer;
	}

	public long getLastVoted() {
		return lastVoted;
	}

	public void setLastVoted(long time) {
		lastVoted = time;
	}

	public long getLastSQL() {
		return lastSQL;
	}

	public void updateLastSQL() {
		lastSQL = System.currentTimeMillis();
	}

	public long getLastEPIncrease() {
		return lastEPIncrease;
	}

	public void increaseEP() {
		if(EP == 100 || getLocation().getZ() != 0)
			return;
		int addEP = Misc.random(15) + 15;
		if(EP + addEP > 100)
			EP = 100;
		else
			EP += addEP;
		sendPkMessage("Your earn potention has increased by " + addEP +"!");
		if(wildernessLevel > 0)
			getActionSender().sendWildLevel(wildernessLevel);
		else
			getActionSender().sendPvPLevel(false);
		lastEPIncrease = System.currentTimeMillis();
	}

	public void removeEP() {
		EP = (int) (EP * Math.random() / 2);
		if(wildernessLevel > 0)
			getActionSender().sendWildLevel(wildernessLevel);
		else
			getActionSender().sendPvPLevel(false);
	}

	public int getInitialSource() {
		return initialSource;
	}

	public void setInitialSource(int source) {
		this.initialSource = source;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void resetOverloadCounter() {
		overloadCounter.set(0);
	}

	public AtomicInteger getOverloadCounter() {
		return overloadCounter;
	}

	public PvPArmourStorage getPvPStorage() {
		return pvpStorage;
	}

	public BountyHunter getBountyHunter() {
		return bountyHunter;
	}

    public BountyPerks getBHPerks() {
        return bhperks;
    }

	public void refreshDuelTimer() {
		lastDuelUpdate = System.currentTimeMillis();
	}

	public boolean hasDuelTimer() {
		return System.currentTimeMillis() - lastDuelUpdate < 5000;
	}

	public void resetDFS() {
		dragonFireSpec = System.currentTimeMillis();
	}

	public boolean canDFS() {
		return (System.currentTimeMillis() - dragonFireSpec) > 160000; //160 secs, 2:30
	}

	public long lastTickReq() {
		return lastTicketRequest;
	}

    public final SlayerHolder getSlayer() {
        return slayTask;
    }

	public void refreshTickReq() {
		this.lastTicketRequest = System.currentTimeMillis();
	}

	/**
	 * Getters/Setters for person ready to be moderated
	 */
	public Player getModeration() {
		return onModeration;
	}

	public void setModeration(Player p) {
		this.onModeration = p;
	}

	public boolean isBusy() {
		return isPlayerBusy;
	}

	public void setBusy(boolean b) {
		isPlayerBusy = b;
	}

	public boolean isSkilling() {
		return isSkilling;
	}

	public void setSkilling(boolean b) {
		isSkilling = b;
	}

	public boolean canWalk() {
		return canWalk;
	}

	public void setCanWalk(boolean b) {
		canWalk = b;
	}

	public boolean handleClickNow() {
		if(delayObjectClick[3] > 0) {
			int i = 0;
			while(delayObjectClick[3] >= 4) {
				delayObjectClick[3] -= 4;
				i++;
			}
			if(i == 0) {// objects
				ObjectClickHandler.clickObject(this, delayObjectClick[0], delayObjectClick[1], delayObjectClick[2], delayObjectClick[3]);
			} else if(i == 1) {// npcs
				NpcClickHandler.handle(this, delayObjectClick[3], delayObjectClick[0]);
			}
			delayObjectClick[3] = - 1;
			return true;
		}
		return false;
	}

	public void setPNpc(int id) {
		this.npcId = id;
		this.npcState = id > - 1 ? true : false;
		getUpdateFlags().flag(UpdateFlag.APPEARANCE);
	}

	public boolean getNpcState() {
		return npcState;
	}

	public int getNpcId() {
		return npcId;
	}

	public Prayers getPrayers() {
		return prayers;
	}

	public double getDrainRate() {
		return drainRate;
	}

	public double setDrainRate(double i) {
		return drainRate = i;
	}

	public void resetDrainRate() {
		drainRate = 0;
	}

	public void resetPrayers() {
		getPrayers().clear();
		resetDrainRate();
		/*
		 * for(int a = 83; a < 108; a++) getActionSender().sendClientConfig(a,
		 * 0);
		 */
		/*
		 * for(int a = 601; a < 609; a++) getActionSender().sendClientConfig(a,
		 * 0);
		 */
		Prayer.resetInterface(this);
		Prayer.setHeadIcon(this);
		// reset headicon;
		// clears arraylist
	}

    public Container getRunePouch() {
        return runePouch;
    }

    public LogManager getLogManager(){
        return logManager;
    }

	public String getFullIP() {
		return IP;
	}

	public String getShortIP() {
		return TextUtils.shortIp(IP);
	}

	public boolean isNew() {
		return newCharacter;
	}

    public boolean isNewlyCreated() {
		return getTotalOnlineTime() < Time.FIVE_MINUTES * 3;
    }
	// friends, 2 off

    public long getTotalOnlineTime() {
        return getPermExtraData().getLong("logintime") + (System.currentTimeMillis() - loginTime);
    }

	/**
	 * Gets the request manager.
	 *
	 * @return The request manager.
	 */
	public RequestManager getRequestManager() {
		return requestManager;
	}

	/**
	 * Gets the player's name expressed as a long.
	 *
	 * @return The player's name expressed as a long.
	 */
	public long getNameAsLong() {
		return nameLong;
	}

	/**
	 * Gets the player's settings.
	 *
	 * @return The player's settings.
	 */
	public Settings getSettings() {
		return settings;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int a) {
		shopId = a;
	}

	public Player getTrader() {
		return tradeWith;
	}

	public void setTradeWith(Player p) {
		tradeWith = p;
	}

	/**
	 * Writes a packet to the <code>IoSession</code>. If the player is not yet
	 * active, the packets are queued.
	 *
	 * @param packet The packet.
	 */
	public void write(Packet packet) {
		synchronized(this) {
			if(! active) {
				pendingPackets.add(packet);
			} else {
				for(Packet pendingPacket : pendingPackets) {
					session.write(pendingPacket);
				}
				pendingPackets.clear();
                getExtraData().put("packetsWrite", getExtraData().getInt("packetsWrite")+1);
				session.write(packet);
			}
		}
	}

    public int getPendingPacketsCount(){
        return pendingPackets.size();
    }

	/**
	 * Gets the player's bank.
	 *
	 * @return The player's bank.
	 */
	public Container getBank() {
		return bank;
	}

	/**
	 * Gets the player's BoB.
	 *
	 * @return The player's BoB.
	 */
	public Container getBoB() {
		return bob;
	}

	public void setBob(int size) {
		bob = new Container(Container.Type.STANDARD, size);
	}

	/**
	 * Gets the interface state.
	 *
	 * @return The interface state.
	 */
	public InterfaceState getInterfaceState() {
		return interfaceState;
	}

	/**
	 * Checks if there is a cached update block for this cycle.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean hasCachedUpdateBlock() {
		return cachedUpdateBlock != null;
	}

	/**
	 * Gets the cached update block.
	 *
	 * @return The cached update block.
	 */
	public Packet getCachedUpdateBlock() {
		return cachedUpdateBlock;
	}

	/**
	 * Sets the cached update block for this cycle.
	 *
	 * @param cachedUpdateBlock The cached update block.
	 */
	public void setCachedUpdateBlock(Packet cachedUpdateBlock) {
		this.cachedUpdateBlock = cachedUpdateBlock;
	}

	/**
	 * Resets the cached update block.
	 */
	public void resetCachedUpdateBlock() {
		cachedUpdateBlock = null;
	}

	/**
	 * Gets the current chat message.
	 *
	 * @return The current chat message.
	 */
	public ChatMessage getCurrentChatMessage() {
		return currentChatMessage;
	}

	/**
	 * Sets the current chat message.
	 *
	 * @param currentChatMessage The current chat message to set.
	 */
	public void setCurrentChatMessage(ChatMessage currentChatMessage) {
		this.currentChatMessage = currentChatMessage;
	}

	/**
	 * Gets the queue of pending chat messages.
	 *
	 * @return The queue of pending chat messages.
	 */
	public Queue<ChatMessage> getChatMessageQueue() {
		return chatMessages;
	}

	/**
	 * Gets the player's appearance.
	 *
	 * @return The player's appearance.
	 */
	public Appearance getAppearance() {
		return appearance;
	}

	/**
	 * Gets the player's equipment.
	 *
	 * @return The player's equipment.
	 */
	public Container getEquipment() {
		return equipment;
	}

	/**
	 * Gets the player's skills.
	 *
	 * @return The player's skills.
	 */
	public Skills getSkills() {
		return skills;
	}

	/**
	 * Gets the action sender.
	 *
	 * @return The action sender.
	 */
	public ActionSender getActionSender() {
		return actionSender;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 *
	 * @return The incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 *
	 * @return The outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}

	/**
	 * Gets the player's name.
	 *
	 * @return The player's name.
	 */
	public String getName() {
		if (!NameUtils.isValidName(name)) {
			System.out.println("Glitched name at : " + this.getLocation());
		}
		return name;
	}

	public void setName(String playerName) {
		if(NameUtils.isValidName(playerName)) {
			name = playerName;
		} else {
			for(int i = 0; i < 100; i++) {
				System.out.println("Trying to set name: " + playerName);
			}
		}
	}

    public String getSafeDisplayName(){
        return getDisplay() != null && !getDisplay().isEmpty() ? TextUtils.titleCase(getDisplay()) : getName();
    }

	public String getDisplay() {
		return display;
	}

	/**
	 * Gets the player's password.
	 *
	 * @return The player's password.
	 */
	public Password getPassword() {
		return password;
	}

	/**
	 * Gets the player's UID.
	 *
	 * @return The player's UID.
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * Gets the <code>IoSession</code>.
	 *
	 * @return The player's <code>IoSession</code>.
	 */
	public IoSession getSession() {
		return session;
	}

	public long getPlayerRank() {
		return playerRank;
	}

	public void setPlayerRank(long playerRank) {
		this.playerRank = playerRank;
	}

	/**
	 * Checks if this player has a member's account.
	 *
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isMembers() {
		return members;
	}

	/**
	 * Sets the members flag.
	 *
	 * @param members The members flag.
	 */
	public void setMembers(boolean members) {
		this.members = members;
	}

	@Override
	public String toString() {
		return Player.class.getName() + " [name=" + name + " playerRank=" + playerRank
				+ " members=" + members + " index=" + this.getIndex() + "]";
	}

	/**
	 * Gets the active flag.
	 *
	 * @return The active flag.
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active flag.
	 *
	 * @param active The active flag.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Gets the action queue.
	 *
	 * @return The action queue.
	 */
	public ActionQueue getActionQueue() {
		return actionQueue;
	}

	/**
	 * Gets the inventory.
	 *
	 * @return The inventory
	 */
	public Container getInventory() {
		return inventory;
	}

	public Container getTrade() {
		return trade;
	}

	public Container getDuel() {
		return duel;
	}

	public EquipmentStats getBonus() {
		return bonus;
	}

	/**
	 * Updates the players' options when in a PvP area.
	 */
	public void updatePlayerAttackOptions(boolean enable) {
		if(enable) {
			actionSender.sendInteractionOption("Attack", 1, true);
			// actionSender.sendOverlay(381);
		} else {
			if(Rank.hasAbility(getPlayerRank(), Rank.ADMINISTRATOR))
				actionSender.sendInteractionOption("Moderate", 1, false);
		}
	}

	/**
	 * Manages updateflags and HP modification when a hit occurs.
	 *
	 * @param source The Entity dealing the blow.
	 */
	public void inflictDamage(Hit inc, Entity source) {
        if(inc.getDamage() < 0)
            return;
		if(! getUpdateFlags().get(UpdateFlag.HIT)) {
			getDamage().setHit1(inc);
			getUpdateFlags().flag(UpdateFlag.HIT);
		} else {
			if(! getUpdateFlags().get(UpdateFlag.HIT_2)) {
				getDamage().setHit2(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_2);
			} else {
				getDamage().setHit3(inc);
				getUpdateFlags().flag(UpdateFlag.HIT_3);
			}

		}
		skills.detractLevel(Skills.HITPOINTS, inc.getDamage());
		if(skills.getLevel(Skills.HITPOINTS) <= 0) {
			if(! this.isDead()) {
				Prayer.retribution(this);
				World.getWorld().submit(new PlayerDeathEvent(this));

			}
			this.setDead(true);
		}
	}

	public void debugMessage(String s) {
		if(debug)
			this.getActionSender().sendMessage(s);
	}

	public void heal(int hp) {
		heal(hp, 3);
	}

	public void heal(int hp, int skill) {
		int cHp = skills.getLevel(skill);
		if(skill == 3) {
			if((cHp + hp) > skills.calculateMaxLifePoints())
				skills.setLevel(3, skills.calculateMaxLifePoints());
			else
				skills.setLevel(skill, (cHp + hp));
		} else if((cHp + hp) > skills.getLevelForExp(skill))
			skills.setLevel(skill, skills.getLevelForExp(skill));
		else
			skills.setLevel(skill, (cHp + hp));
		if(skills.getLevel(3) <= 0) {
			World.getWorld().submit(new PlayerDeathEvent(this));
			this.setDead(true);
		}
	}

	public void heal(int hp, boolean brew) {
		int cHp = skills.getLevel(3);
		int j = 3;
		int brewBonus = (int)(skills.calculateMaxLifePoints() * .15);
		if(j == 3) {
			if((cHp + hp) > skills.calculateMaxLifePoints() + brewBonus)
				skills.setLevel(3, skills.calculateMaxLifePoints() + brewBonus);
			else
				skills.setLevel(3, (cHp + hp));
		}
		if(skills.getLevel(3) <= 0) {
			World.getWorld().submit(new PlayerDeathEvent(this));
			this.setDead(true);
		}
	}

	public void inflictDamage(Hit inc) {
		this.inflictDamage(inc, null);
	}

	public int getInflictDamage(int damg, Entity source, boolean poison, int style) {
		HitType hitType = HitType.NORMAL_DAMAGE;
		boolean npc = source instanceof NPC;
		if(npc) {
			NPC n = (NPC)source;
			if(n.getDefinition().getId() == 50 || ( n.getDefinition().getId() == 8133 && (style == Constants.MAGE || style == Constants.RANGE)))
				npc = false;
		}
		//getActionSender().sendMessage("Generated damg: " + damg + ", npc: " + npc + ", style = " + style);
		int trueStyle = style;
		if(trueStyle >= 5)
			trueStyle -= 5;
        if(source != null) {
            if (trueStyle == Constants.MELEE) {
                if (getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_MELEE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                } else if (getPrayers().isEnabled(Prayers.CURSE_DEFLECT_MELEE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                    if (Misc.random(3) == 1) {
                        this.playAnimation(Animation.create(12573));
                        this.playGraphics(Graphic.create(2230));
                        source.cE.hit(damg / 10, this, false, Constants.DEFLECT);
                    }
                }
            } else if (trueStyle == Constants.MAGE) {
                if (getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_MAGE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                } else if (getPrayers().isEnabled(Prayers.CURSE_DEFLECT_MAGIC)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                    if (Misc.random(3) == 1) {
                        this.playAnimation(Animation.create(12573));
                        this.playGraphics(Graphic.create(2228));
                        source.cE.hit(damg / 10, this, false, Constants.DEFLECT);
                    }
                }
            } else if (trueStyle == Constants.RANGE) {
                if (getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_RANGE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                } else if (getPrayers().isEnabled(Prayers.CURSE_DEFLECT_RANGED)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                    if (Misc.random(3) == 1) {
                        this.playAnimation(Animation.create(12573));
                        this.playGraphics(Graphic.create(2229));
                        source.cE.hit(damg / 10, this, false, Constants.DEFLECT);
                    }
                }
            }
        }

        /** Ring of life */
        if (Combat.ringOfLifeEqupped(this) && !Combat.usingPhoenixNecklace(this)) {
            if (duelAttackable < 1 && !Duel.inDuelLocation(this)) {
                final int newhp = getSkills().getLevel(3) - damg;
                if (newhp < Math.floor(getSkills().calculateMaxLifePoints() * .13) && newhp > 0) {  //10% of hp
                    if (!Duel.inDuelLocation(this) || !isTeleBlocked()) { //Ring of life surpasses teleblocks n shit, also it was just wrong lol
                        getEquipment().set(Equipment.SLOT_RING, null);
                        getWalkingQueue().reset();
                        ContentEntity.playerGfx(this, 1684);
                        ContentEntity.startAnimation(this, 9603);
                        extraData.put("combatimmunity", System.currentTimeMillis() + 4000L);
                        World.getWorld().submit(new Event(0x258) {
                            int loop = 0;

                            public void execute() {
                                if (loop == 5) {
                                    setTeleportTarget(Location.create(3225, 3218, 0));
                                    sendMessage("Your ring of life saves you, but is destroyed in the process.");
                                    this.stop();
                                }
                                loop++;
                                return;
                            }
                        });
                        return 0;
                    }
                }
            }
        }

        /** The phoenix necklace effect. */
        if (Combat.usingPhoenixNecklace(this)) {
            int newhp = getSkills().getLevel(3) - damg;
            if (newhp < Math.floor(getSkills().calculateMaxLifePoints() / 3.5) && newhp > 0) {
                getEquipment().set(Equipment.SLOT_AMULET, null);
                heal(damg);
                ContentEntity.playerGfx(this, 436);
                extraData.put("combatimmunity", System.currentTimeMillis() + 300L);
                sendMessage("Your phoenix necklace heals you, but is destroyed in the process.");
                return 0;
            }
        }

		return damg;
	}

	public int inflictDamage(int damg, Entity source, boolean poison, int style) {
		getInterfaceState().resetInterfaces();
		HitType hitType = HitType.NORMAL_DAMAGE;
		boolean npc = source instanceof NPC;
		getInterfaceState().resetInterfaces();
		/*if(npc) {
			NPC n = (NPC)source;
			if(n.getDefinition().getId() == 8133 && (style == Constants.MAGE || style == Constants.RANGE))
				npc = false;
		}
		//getActionSender().sendMessage("Generated damg: " + damg + ", npc: " + npc + ", style = " + style);
		int trueStyle = style;
		if(trueStyle >= 5)
			trueStyle -= 5;
        if(source != null) {
            if (trueStyle == Constants.MELEE) {
                if (getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_MELEE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                } else if (getPrayers().isEnabled(Prayers.CURSE_DEFLECT_MELEE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                    if (Misc.random(3) == 1) {
                        this.playAnimation(Animation.create(12573));
                        this.playGraphics(Graphic.create(2230));
                        source.cE.hit(damg / 10, this, false, Constants.DEFLECT);
                    }
                }
            } else if (trueStyle == Constants.MAGE) {
                if (getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_MAGE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                } else if (getPrayers().isEnabled(Prayers.CURSE_DEFLECT_MAGIC)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                    if (Misc.random(3) == 1) {
                        this.playAnimation(Animation.create(12573));
                        this.playGraphics(Graphic.create(2228));
                        source.cE.hit(damg / 10, this, false, Constants.DEFLECT);
                    }
                }
            } else if (trueStyle == Constants.RANGE) {
                if (getPrayers().isEnabled(Prayers.PRAYER_PROTECT_FROM_RANGE)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                } else if (getPrayers().isEnabled(Prayers.CURSE_DEFLECT_RANGED)) {
                    damg *= 0.6;
                    if (npc)
                        damg = 0;
                    if (Misc.random(3) == 1) {
                        this.playAnimation(Animation.create(12573));
                        this.playGraphics(Graphic.create(2229));
                        source.cE.hit(damg / 10, this, false, Constants.DEFLECT);
                    }
                }
            }
        }
	*/
		//If hitting more than hitpoints
        if(source instanceof Player && LegendaryStore.ThirdAgeSet.setFor(style).has(((Player) source).getEquipment())) {
            damg *= 1.15;

        }
		if(damg > skills.getLevel(Skills.HITPOINTS)) {
			damg = skills.getLevel(Skills.HITPOINTS);
		}
       // if(Rank.hasAbility(this, Rank.ADMINISTRATOR)) {
            if(extraData.getLong("combatimmunity") > System.currentTimeMillis())
                damg = 0;
       // }

        if(damg < 0)
            damg = 0;
		if(poison)
			hitType = HitType.POISON_DAMAGE;
		else if(damg <= 0)
			hitType = HitType.NO_DAMAGE;
		if(source instanceof Player) {
			try {
			if(duelAttackable > 0 && World.getWorld().getPlayers().get(source.getIndex()).isDead())
				return 0;
			}catch(Exception e) {

			}
		}
		Hit hit = new Hit(damg, hitType, style);
		inflictDamage(hit, source);
		Prayer.redemption(this);
		return damg;
	}

	@Override
	public void deserialize(IoBuffer buf, boolean accCheckerMode) {// load
		System.out.println("Calling deserializable");
	}

	public void emoteTabPlay(Animation anim) {
		if(isDoingEmote) {
			getActionSender().sendMessage("You are already doing an emote.");
			return;
		} else {
			playAnimation(anim);
			isDoingEmote = true;
			inAction = true;
			World.getWorld().submit(new Event(1000, "checked") {
				@Override
				public void execute() {
					isDoingEmote = false;
					inAction = false;
					this.stop();
				}

			});
            ClueScrollManager.trigger(this, anim.getId());
		}
	}

	public void startUpEvents() {
		// getActionSender().sendString(29161,
		// "Players Online: "+World.getWorld().getPlayers().size());
		// getActionSender().sendString(29162, Server.getOnlineTime());
		FriendsAssistant.initialize(this);
		for(int i = 0; i < 5; i++) {
			getInterfaceState().setNextDialogueId(i, - 1);
		}
	}

	@Override
	public void serialize(IoBuffer buf) {// save method
		System.out.println("Calling serialize");
	}

	@Override
	public void addToRegion(Region region) {
		region.addPlayer(this);
	}

	@Override
	public void removeFromRegion(Region region) {
		region.removePlayer(this);
	}

	@Override
	public int getClientIndex() {
		return this.getIndex() + 32768;
	}

	@Override
	public void inflictDamage(int damage, HitType type) {
		// TODO Auto-generated method stub

	}

	public void decreaseEnergy(int am) {

	}

	public int getEnergy() {
		return 100;
	}

	public void playerSoundEffect() {
		// TODO: Develop a optcode 174 from server to client for sfx
		// This will play multiple sounds

	}

	public void playerMusic() {
		// -1 turns off the player music
		// TODO: Develop a optcode 74 from server to the Client for music

	}

	public void increasePitsDamage(int fightPitsDamage) {
		this.fightPitsDamage += fightPitsDamage;
	}

	public int getPitsDamage() {
		return fightPitsDamage;
	}

	public void setPitsDamage(int fightPitsDamage) {
		this.fightPitsDamage = fightPitsDamage;
	}

	public void increaseCorpDamage(int i) {
		damagedCorp += i;
	}

	public int getCorpDamage() {
		return damagedCorp;
	}

	public void setCorpDamage(int i) {
		damagedCorp = i;
	}

	/**
	 * Use to get the current spellbook.
	 *
	 * @return
	 */
	public SpellBook getSpellBook() {
		return spellBook;
	}

	public FriendList getFriends() {
		return friendList;
	}

	/*
	 * public int activityPoints = 0;
	 * 
	 * public int getActivityPoints(){ return activityPoints; } public void
	 * setActivityPoints(int points){ this.activityPoints = points; }
	 * 
	 * public void increaseplayerUptime(){ playerUptime++; increaseActivity(); }
	 * public void increaseActivity(){ int multiplier = (playerUptime/120) + 1;
	 * activityPoints += multiplier; if(playerUptime % 120 == 0){
	 * getActionSender().sendMessage("@blu@You have been online for " +
	 * playerUptime/120 + " hours! Your ActivityBonus has been increased!"); }
	 * getActionSender().sendString("@or2@Activity Points: @gre@" +
	 * activityPoints, 7346); }
	 */

	public void updateTeleportTimer() {
		lastTeleport = System.currentTimeMillis();
	}

	public long getTimeSinceLastTeleport() {
		return System.currentTimeMillis() - lastTeleport;
	}

	public boolean isMagicTeleporting() {
		return System.currentTimeMillis() - lastTeleport <= 5000;
	}

	public void setTeleBlock(long l) {
		// TODO Auto-generated method stub
		teleBlockTimer = l;
	}

	public PvPTask getPvPTask() {
		return currentPvPTask;
	}

	public void setPvPTask(PvPTask task) {
		currentPvPTask = task;
	}

	public int getPvPTaskAmount() {
		return pvpTaskAmount;
	}

	public void setPvPTaskAmount(int am) {
		pvpTaskAmount = am;
	}

	public void decrementPvPTask(int delta) {
		pvpTaskAmount = pvpTaskAmount - delta;
	}

	public int pvpTaskToInteger() {
		return PvPTask.toInteger(currentPvPTask);
	}

	public boolean isTeleBlocked() {
		return System.currentTimeMillis() <= teleBlockTimer;
	}

	public int getClanRank() {
		return clanRank;
	}

	public void setClanRank(int r) {
		clanRank = r;
	}
	
    public boolean isClanMainOwner() {
        if(clanName == null || clanName.isEmpty())
            return false;
        Clan clan = ClanManager.clans.get(clanName);
        return clan != null && clan.getOwner().equalsIgnoreCase(getName());
    }
	
	public String getPlayersNameInClan() {
		//System.out.println("Clanranker is " + clanRank);
        if(isClanMainOwner())
                return "[Owner] " + getDisplay();
        return getClanRankName() + getDisplay();
	}

    public String getClanRankName() {
        String rank = "";
        switch(clanRank) {
            case 0:
                return "";
            case 1:
                rank = "Recruit";
                if(Dicing.diceClans.contains(clanName)) rank = "100K max";
                break;
            case 2:
                rank = "Corporal";
                if(Dicing.diceClans.contains(clanName)) rank = "500K max";
                break;
            case 3:
                rank = "Sergeant";
                if(Dicing.diceClans.contains(clanName)) rank = "Unlimited";
                break;
            case 4:
                rank = "Lieutenant";
                break;
            case 5:
                rank = "Owner";
                break;
            case 6:
                rank = "Mod";
                break;
            case 7:
                rank = "Admin";
                break;
        }
        return "[" + rank + "] ";
    }

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public void resetClanName() {
		this.clanName = "";
	}

	public AutoSaving getAutoSaving() {
		return autoSaving;
	}

	public int getPlayerUptime() {
		return playerUptime;
	}

	public int getBounty() {
		return bounty;
	}

	public void resetBounty() {
		bounty = 10;
	}
	
	public void resetKillStreak() {
		killStreak = 0;
	}
	
	public int getKillStreak() {
		return killStreak;
	}

    public void setKillStreak(int killStreak) {
		this.killStreak = killStreak;
	}

	public void increaseKillStreak() {
		killStreak++;
		getAchievementTracker().onKillstreak(killStreak);
        actionSender.sendString(36505, "Killstreak: @red@" + killStreak);
		bounty = (int)(4 * Math.pow(killStreak, 1.87));
        if(bounty > 40_000)
            bounty = 40_000;
		if(bounty < 10)
			bounty = 10;
		switch(killStreak) {
			case 5:
				for(Player p : World.getWorld().getPlayers())
					if(p != null)
						p.sendPkMessage(getSafeDisplayName() + " is on a "
						+ killStreak + " killstreak!");
				break;
			case 7:
				for(Player p : World.getWorld().getPlayers())
					if(p != null)
						p.sendPkMessage(getSafeDisplayName()
						+ " has begun a rampage with a killstreak of " + killStreak);
				break;
			case 9:
				for(Player p : World.getWorld().getPlayers())
					if(p != null)
						p.sendPkMessage(getSafeDisplayName()
						+ " is on a massacre with " + killStreak + " kills!");
				break;
		}
		if(killStreak >= 10) {
			if(Math.random() > 0.5) {
				for (Player p : World.getWorld().getPlayers()) {
					if (p != null) {
						p.sendPkMessage(getSafeDisplayName() + " now has "
								+ killStreak + " kills in a row! Kill him and gain "
								+ bounty + " Pk points!");
					}
				}
			} else {
				String ppl = getPeopleString();
				for(Player p : World.getWorld().getPlayers()) {
					if (p != null) {
						p.sendPkMessage(getSafeDisplayName() + " has killed "
								+ killStreak + ppl + " in a row! Kill him and gain "
								+ bounty + " Pk points!");
					}
				}
			}
		}
	}

	public void addLastKill(String name) {
		lastKills[0] = lastKills[1];
		lastKills[1] = lastKills[2];
		lastKills[2] = lastKills[3];
		lastKills[3] = lastKills[4];
		lastKills[4] = name;
	}

	public boolean killedRecently(String name) {
		for(String s : lastKills) {
			if(s.equals(name))
				return true;
		}
		return false;
	}

	public void increaseKillCount() {
		killCount++;
		getQuestTab().updateQuestTab();
	}

	public ActionSender sendHeadedMessage(final String color, final String header, final Object... message) {
		for(Object o : message) {
			actionSender.sendMessage((color == null ? "" : color) + (header == null ? "" : header + " ") + o.toString());
		}
		return getActionSender();
	}

	public ActionSender sendMessage(Object... message) {
		return sendHeadedMessage(null, null, message);
	}

	public ActionSender sendPkMessage(Object... message) {
		if(!getPermExtraData().getBoolean("disabledPkMessages"))
			return sendHeadedMessage("@dbl@", "[APk]", message);
		return getActionSender();
	}

	public ActionSender sendLootMessage(String tag, Object... message) {
		if(!getPermExtraData().getBoolean("disabledLootMessages"))
			return sendHeadedMessage("@gre@", "[" + tag + "]", message);
		return getActionSender();
	}

	public ActionSender sendServerMessage(Object... message) {
		return sendHeadedMessage("@whi@", "[Server]", message);
	}

	public ActionSender sendStaffMessage(Object... message) {
				return sendHeadedMessage("@blu@", "[Staff]", message);
	}

	public ActionSender sendClanMessage(Object... message) {
			return sendHeadedMessage("@dre@", null, message);
	}

	public ActionSender sendImportantMessage(Object... message) {
		return sendHeadedMessage("@dre@", "[Important]", message);
	}

	public ActionSender sendf(String message, Object... args) {
		try {
			actionSender.sendMessage(String.format(message, args));
		}catch(Exception e) {
			//fail in formatter, ignore
		}
		return getActionSender();
	}

	public void increaseDeathCount() {
		deathCount++;
		getQuestTab().updateQuestTab();
	}

	public BankField getBankField() {
		return bankField;
	}

	public int getKillCount() {
		return killCount;
	}

	public void setKillCount(int kc) {
		killCount = kc;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public void setDeathCount(int dc) {
		deathCount = dc;
	}

	public boolean isOverloaded() {
		return isOverloaded;
	}

	public void setOverloaded(boolean b) {
		isOverloaded = b;
	}

	public void resetDeathItemsVariables() {
		for(int i = 0; i < invSlot.length; i++) {
			invSlot[i] = false;
			if(i <= 13)
				equipSlot[i] = false;
			if(i <= 3)
				itemKeptId[i] = 0;
		}

	}

	public Farm getFarm() {
		// TODO Auto-generated method stub
		return farm;
	}

	public void removeAsTax(int amount) {
		if(ContentEntity.getItemAmount(this, 995) >= amount) {
			ContentEntity.deleteItemA(this, 995, amount);
		} else {
			for(int i = 0; i < getBank().size(); i++) {
				Item item = getBank().get(i);
				if(item != null && item.getId() == 995) {
                    if (item.getCount() <= amount)
						getBank().remove(item);
                    else
						getBank().remove(new BankItem(0, 995, amount));
				}
			}
		}
	}

	/**
	 * Returns the PlayerRights
	 */
	public String getQuestTabRank() {
		return Rank.getPrimaryRank(getPlayerRank()).toString();
	}

	/**
	 * Gets the highscores, initializes them if needed.
	 *
	 * @return
	 */
	public Highscores getHighscores() {
		if(highscores == null)
			highscores = new Highscores(this);
		return highscores;
	}

	public long getFirstVoteTime() {
		return firstVoteTime;
	}

    public void setFirstVoteTime(final long firstVoteTime){
        this.firstVoteTime = firstVoteTime;
    }

	public int getVoteCount() {
		return voteCount;
	}

    public void setVoteCount(final int voteCount){
        this.voteCount = voteCount;
    }

    @SuppressWarnings("deprecation")
    public void addCharge(int seconds) {
        if (chargeTill < System.currentTimeMillis())
            chargeTill = System.currentTimeMillis();
        Date date = new Date(chargeTill);
        date.setSeconds((date.getSeconds() + seconds));
        chargeTill = date.getTime();
    }

	@Override
	public boolean equals(Object other) {
		if(other == null)
			return false;
		if(! (other instanceof Player))
			return false;
		if(other == this)
			return true;
		return getName().equalsIgnoreCase(((Player) other).getName());

	}

	/** Does the player have a active charge?*/
    public boolean hasCharge() {
    return chargeSpell || chargeTill > System.currentTimeMillis();
    }

    public int getTurkeyKills() {
        return turkeyKills;
    }

	public void setTurkeyKills(int turkeyKills) {
		this.turkeyKills = turkeyKills;
    }

    public void completeTGEvent(boolean b) {
       b = completedTG;
    }

    public boolean hasFinishedTG() {
        return turkeyKills >= 50;
    }

     public int setTreasureScroll(int treasureScroll) {
        return this.treasureScroll = treasureScroll;
     }

    public int getTreasureScroll() {
        return treasureScroll;
    }

	public JGrandExchangeTracker getGrandExchangeTracker(){
		if(geTracker == null)
			geTracker = new JGrandExchangeTracker(this);
		return geTracker;
	}

	public int getStartValue() {
		return startValue;
	}

	public void setStartValue(int value) {
		this.startValue = value;
	}

	private int startValue = -1;

	public String verificationCode = "";
	public boolean verificationCodeEntered = true;
	public int verificationCodeAttemptsLeft = 3;
}
