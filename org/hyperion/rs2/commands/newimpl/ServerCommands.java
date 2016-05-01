package org.hyperion.rs2.commands.newimpl;
//<editor-fold defaultstate="collapsed" desc="Imports">
import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.*;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Position;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.misc.ItemSpawning;
import org.hyperion.rs2.model.content.misc2.Afk;
import org.hyperion.rs2.model.content.misc2.Edgeville;
import org.hyperion.rs2.model.content.skill.agility.courses.GnomeStronghold;
import org.hyperion.rs2.model.punishment.cmd.CheckPunishmentCommand;
import org.hyperion.util.Misc;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
//</editor-fold>
/**
 * Created by DrHales on 3/4/2016.
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
                new TeleportCommand("dv", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3342, 3242, 0), false)
        );
    }
    //</editor-fold>
}
