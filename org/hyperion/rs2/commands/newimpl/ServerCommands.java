package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.*;
import org.hyperion.rs2.model.Position;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.misc2.Afk;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.skill.agility.courses.GnomeStronghold;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.cmd.CheckPunishmentCommand;
import org.hyperion.rs2.model.punishment.cmd.PunishCommand;
import org.hyperion.rs2.model.punishment.cmd.UnPunishCommand;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;
//</editor-fold>

/**
 * @author DrHales
 *         3/4/2016
 */
public class ServerCommands implements NewCommandExtension {

    //<editor-fold defaultstate="collapsed" desc="Commands List">
    @Override
    public List<NewCommand> init() {
        return Arrays.asList(
                new WikiCommand(),
                new SendiCommand(),
                new YellCommand(),
                new ViewPacketActivityCommand(),
                new SpawnCommand("item"),
                new SpawnCommand("spawn"),
                new SpawnCommand("pickup"),
                new MaxCommand("max"),
                new MaxCommand("master"),
                new CheckPunishmentCommand("checkpunish"),
                new PromoteCommand("givefmod", Rank.OWNER, Rank.FORUM_MODERATOR),
                new PromoteCommand("givevet", Rank.OWNER, Rank.VETERAN),
                new PromoteCommand("giveglobal", Rank.OWNER, Rank.GLOBAL_MODERATOR),
                new CreatePositionCommand("duel", Rank.OWNER, Position.create(3375, 3274, 0)),
                new CreatePositionCommand("pits", Rank.OWNER, Position.create(2399, 5177, 0)),
                new TeleportCommand("arreplace", Rank.OWNER, Position.create(3207, 3219, 2), false),
                new TeleportCommand("marcusplace", Rank.MODERATOR, Position.create(1971, 5002, 0), false),
                new TeleportCommand("darrenplace", Rank.MODERATOR, Position.create(2123, 4913, 0), false),
                new TeleportCommand("aliplace", Rank.MODERATOR, Position.create(3500, 3572, 0), false),
                new TeleportCommand("joshplace", Rank.MODERATOR, Position.create(1891, 4523, 2), false),
                new TeleportCommand("seanplace", Rank.MODERATOR, Position.create(2980, 9871, 72), false),
                new TeleportCommand("sz", Rank.HELPER, Position.create(2846, 5213, 0), false),
                new TeleportCommand("sdpvm", Rank.SUPER_DONATOR, Time.FIFTEEN_SECONDS, Position.create(3503, 9493, 4), false),
                new TeleportCommand("sdp", Rank.SUPER_DONATOR, Time.FIFTEEN_SECONDS, Position.create(2037, 4532, 4), false),
                new TeleportCommand("helpzone", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2607, 9672, 0), false),
                new TeleportCommand("helpplace", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2607, 9672, 0), false),
                new TeleportCommand("afk", Rank.PLAYER, Time.FIFTEEN_SECONDS, Afk.POSITION, false),
                new TeleportCommand("home", Rank.PLAYER, Time.FIFTEEN_SECONDS, Edgeville.POSITION, false),
                new TeleportCommand("multi", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3234, 3650, 0), false),
                new TeleportCommand("mb", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2539, 4718, 0), false),
                new TeleportCommand("market", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3009, 3383, 0), false),
                new TeleportCommand("gdz", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3289, 3886, 0), false),
                new TeleportCommand("skilling", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3810, 2832, 0), false),
                new TeleportCommand("ge", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3009, 3383, 0), false),
                new TeleportCommand("barrelchest", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2801, 4723, 0), false),
                new TeleportCommand("edge", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3086, 3516, 0), false),
                new TeleportCommand("fightpits", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2399, 5178, 0), false),
                new TeleportCommand("dangerouspk", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2480, 5174, 0), false),
                new TeleportCommand("easts", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3358, 3673, 0), false),
                new TeleportCommand("agility", Rank.PLAYER, Time.FIFTEEN_SECONDS, GnomeStronghold.position, false),
                new TeleportCommand("funpk", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2594, 3156, 0), false),
                new TeleportCommand("train1", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2709, 3718, 0), false),
                new TeleportCommand("train2", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3566 - Misc.random(1), 9952 - Misc.random(1), 0), false),
                new TeleportCommand("dv", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3342, 3242, 0), false),
                new PunishCommand("jail", Rank.HELPER, Target.ACCOUNT, Type.JAIL),
                new PunishCommand("ipjail", Rank.HELPER, Target.IP, Type.JAIL),
                new PunishCommand("macjail", Rank.MODERATOR, Target.MAC, Type.JAIL),
                new PunishCommand("suidjail", Rank.DEVELOPER, Target.SPECIAL, Type.JAIL),
                new PunishCommand("yellmute", Rank.HELPER, Target.ACCOUNT, Type.YELL_MUTE),
                new PunishCommand("ipyellmute", Rank.MODERATOR, Target.IP, Type.YELL_MUTE),
                new PunishCommand("macyellmute", Rank.MODERATOR, Target.MAC, Type.YELL_MUTE),
                new PunishCommand("suidyellmute", Rank.DEVELOPER, Target.SPECIAL, Type.YELL_MUTE),
                new PunishCommand("mute", Rank.MODERATOR, Target.ACCOUNT, Type.MUTE),
                new PunishCommand("ipmute", Rank.MODERATOR, Target.IP, Type.MUTE),
                new PunishCommand("macmute", Rank.MODERATOR, Target.MAC, Type.MUTE),
                new PunishCommand("suidmute", Rank.DEVELOPER, Target.SPECIAL, Type.MUTE),
                new PunishCommand("ban", Rank.MODERATOR, Target.ACCOUNT, Type.BAN),
                new PunishCommand("ipban", Rank.MODERATOR, Target.IP, Type.BAN),
                new PunishCommand("macban", Rank.MODERATOR, Target.MAC, Type.BAN),
                new PunishCommand("suidban", Rank.ADMINISTRATOR, Target.SPECIAL, Type.BAN),
                new PunishCommand("wildyforbid", Rank.DEVELOPER, Target.ACCOUNT, Type.WILDY_FORBID),
                new PunishCommand("ipwildyforbid", Rank.DEVELOPER, Target.IP, Type.WILDY_FORBID),
                new PunishCommand("macwildyforbid", Rank.DEVELOPER, Target.MAC, Type.WILDY_FORBID),
                new PunishCommand("suidwildyforbid", Rank.DEVELOPER, Target.SPECIAL, Type.WILDY_FORBID),
                new UnPunishCommand("unjail", Rank.HELPER, Target.ACCOUNT, Type.JAIL),
                new UnPunishCommand("unipjail", Rank.HELPER, Target.IP, Type.JAIL),
                new UnPunishCommand("unmacjail", Rank.MODERATOR, Target.MAC, Type.JAIL),
                new UnPunishCommand("unsuidjail", Rank.DEVELOPER, Target.SPECIAL, Type.JAIL),
                new UnPunishCommand("unyellmute", Rank.HELPER, Target.ACCOUNT, Type.YELL_MUTE),
                new UnPunishCommand("unipyellmute", Rank.MODERATOR, Target.IP, Type.YELL_MUTE),
                new UnPunishCommand("unmacyellmute", Rank.MODERATOR, Target.MAC, Type.YELL_MUTE),
                new UnPunishCommand("unsuidyellmute", Rank.DEVELOPER, Target.SPECIAL, Type.YELL_MUTE),
                new UnPunishCommand("unmute", Rank.MODERATOR, Target.ACCOUNT, Type.MUTE),
                new UnPunishCommand("unipmute", Rank.MODERATOR, Target.IP, Type.MUTE),
                new UnPunishCommand("unmacmute", Rank.MODERATOR, Target.MAC, Type.MUTE),
                new UnPunishCommand("unsuidmute", Rank.DEVELOPER, Target.SPECIAL, Type.MUTE),
                new UnPunishCommand("unban", Rank.MODERATOR, Target.ACCOUNT, Type.BAN),
                new UnPunishCommand("unipban", Rank.MODERATOR, Target.IP, Type.BAN),
                new UnPunishCommand("unmacban", Rank.MODERATOR, Target.MAC, Type.BAN),
                new UnPunishCommand("unsuidban", Rank.ADMINISTRATOR, Target.SPECIAL, Type.BAN),
                new UnPunishCommand("unwildyforbid", Rank.DEVELOPER, Target.ACCOUNT, Type.WILDY_FORBID),
                new UnPunishCommand("unipwildyforbid", Rank.DEVELOPER, Target.IP, Type.WILDY_FORBID),
                new UnPunishCommand("unmacwildyforbid", Rank.DEVELOPER, Target.MAC, Type.WILDY_FORBID),
                new UnPunishCommand("unsuidwildyforbid", Rank.DEVELOPER, Target.SPECIAL, Type.WILDY_FORBID)
        );
    }
    //</editor-fold>
}
