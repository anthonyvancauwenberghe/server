package org.hyperion.rs2.model.content.misc;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.commands.impl.SkillSetCommand;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.content.minigame.FightPits;

public class SpawnServerCommands {

    public static void init() {
        SkillSetCommand.init();
        CommandHandler.submit(new Command("max", Rank.PLAYER) {
            @Override
            public boolean execute(final Player player, final String input) {
                max(player);
                return true;
            }
        });
        CommandHandler.submit(new Command("master", Rank.PLAYER) {
            @Override
            public boolean execute(final Player player, final String input) {
                max(player);
                return true;
            }
        });
    }

    private static void max(final Player player) {
        if(!ItemSpawning.canSpawn(player))
            return;
        if(player.getLocation().cannotMax()){
            player.sendMessage("You cannot max here!");
            return;
        }
        if(!Server.SPAWN)
            return;
        if(FightPits.inGame(player))
            return;
        if(player.getLocation().inPvPArea()){
            player.getActionSender().sendMessage("You cannot do that in PvP zones.");
            return;
        }else if(player.getLocation().inCorpBeastArea()){
            player.getActionSender().sendMessage("You feel the presence of a mighty beast and you're too scared to max.");
            return;
        }else if(player.getLocation().inFunPk()){
            player.getActionSender().sendMessage("Don't be a pussy and fight like a man.");
            return;
        }else if(player.duelAttackable > 0){
            player.getActionSender().sendMessage("You cannot do that in the duel arena.");
            return;
        }
        for(int i = 0; i <= 6; i++){
            player.getSkills().setLevel(i, 99);
            player.getSkills().setExperience(i, Math.max(13100000, player.getSkills().getExperience(i)));
        }
    }

}
