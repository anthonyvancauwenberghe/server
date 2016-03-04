package org.hyperion.rs2.commands.newimpl;

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.NewCommandExtension;
import org.hyperion.rs2.commands.impl.cmd.*;
import org.hyperion.rs2.model.Position;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Position;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.punishment.cmd.CheckPunishmentCommand;
import org.hyperion.util.Time;

import java.util.Arrays;
import java.util.List;

/**
 * Created by DrHales on 3/4/2016.
 */
public class ServerCommands implements NewCommandExtension {

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
                new TeleportCommand("arreplace", Rank.OWNER, Position.create(3207, 3219, 2), false),
                new TeleportCommand("nathanplace", Rank.MODERATOR, Position.create(2108, 4452, 3), false),
                new TeleportCommand("marcusplace", Rank.MODERATOR, Position.create(2123, 4913, 4), false),
                new TeleportCommand("darrenplace", Rank.MODERATOR, Position.create(1971, 5002, 0), false),
                new TeleportCommand("aliplace", Rank.MODERATOR, Position.create(3500, 3572, 0), false),
                new TeleportCommand("joshplace", Rank.MODERATOR, Position.create(1891, 4523, 2), false),
                new TeleportCommand("seanplace", Rank.MODERATOR, Position.create(3292, 3163, 2), false),
                new TeleportCommand("a3place", Rank.MODERATOR, Position.create(3108, 3159, 3), false),
                new TeleportCommand("sz", Rank.HELPER, Position.create(2846, 5213, 0), false),
                new TeleportCommand("sdpvm", Rank.SUPER_DONATOR, Position.create(3506, 9494, 4), false),
                new TeleportCommand("sdp", Rank.SUPER_DONATOR, Position.create(2037, 4532, 4), false),
                new TeleportCommand("market", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3009, 3383, 0), false),
                new TeleportCommand("gdz", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3287, 3386, 0), false),
                new TeleportCommand("skilling", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3810, 2832, 0), false),
                new TeleportCommand("ge", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3009, 3383, 0), false),
                new TeleportCommand("barrelchest", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(2801, 4723, 0), false),
                new TeleportCommand("edge", Rank.PLAYER, Time.FIFTEEN_SECONDS, Position.create(3086, 3516, 0), false)
        );
    }

}
