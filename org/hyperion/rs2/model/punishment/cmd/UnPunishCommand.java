package org.hyperion.rs2.model.punishment.cmd;

import org.hyperion.rs2.commands.NewCommand;
import org.hyperion.rs2.commands.util.CommandInput;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.punishment.Combination;
import org.hyperion.rs2.model.punishment.Punishment;
import org.hyperion.rs2.model.punishment.Target;
import org.hyperion.rs2.model.punishment.Type;
import org.hyperion.rs2.model.punishment.holder.PunishmentHolder;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.saving.PlayerLoading;
import org.hyperion.rs2.util.TextUtils;

public class UnPunishCommand extends NewCommand {

    private final Combination combination;

    public UnPunishCommand(String key, Rank rank, Target target, Type type) {
        super(key, rank, new CommandInput<>(PlayerLoading::playerExists, "Player", "An Existing Player"));
        this.combination = Combination.of(target, type);
    }

    @Override
    public boolean execute(final Player player, final String[] input) {
        final String victim = input[0].trim();
        final PunishmentHolder holder = PunishmentManager.getInstance().get(victim);
        if (holder == null) {
            player.sendf("%s isn't punished", TextUtils.titleCase(victim));
            return true;
        }
        final Punishment punishment = holder.get(combination);
        if (punishment == null) {
            player.sendf("No %s found for %s", combination, TextUtils.titleCase(victim));
            return true;
        }
        punishment.getTime().setExpired(true);
        if (punishment.unapply()) {
            punishment.send(punishment.getVictim(), true);
        }
        punishment.send(player, true);
        punishment.getHolder().remove(punishment);
        punishment.setActive(false);
        return true;
    }
}
