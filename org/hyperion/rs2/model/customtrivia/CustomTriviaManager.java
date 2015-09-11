package org.hyperion.rs2.model.customtrivia;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.PushMessage;
import org.hyperion.util.Misc;

public final class CustomTriviaManager{

    private static final List<CustomTrivia> LIST = new ArrayList<>();

    private CustomTriviaManager(){}

    public static void addNew(final CustomTrivia trivia){
        LIST.add(trivia);
        for(final Player p : World.getWorld().getPlayers())
            if(p != null)
                trivia.send(p, true);
    }

    public static void send(final Player player, final boolean alert){
        LIST.forEach(c -> c.send(player, alert));
    }

    public static void send(final Player player){
        send(player, LIST.size() == 1);
    }

    public static synchronized void processAnswer(final Player player, final String answer){
        final Iterator<CustomTrivia> itr = LIST.iterator();
        while(itr.hasNext()){
            final CustomTrivia trivia = itr.next();
            if(trivia.answer.equalsIgnoreCase(answer)){
                itr.remove();
                player.getBank().add(trivia.prize);
                player.sendMessage((trivia.prize.getCount() == 1 ? Misc.ucFirst(Misc.aOrAn(trivia.prize.getDefinition().getName())) : trivia.prize.getCount()) + " '@dre@" + trivia.prize.getDefinition().getName() + "@bla@' " + (trivia.prize.getCount() == 1 ? "has" : "have") + " been added to your bank!");
                for(Player p : World.getWorld().getPlayers()) {
                    p.sendServerMessage(player.getSafeDisplayName() + " has answered " + trivia.creator.getSafeDisplayName() + "'s question correctly!");
                    p.sendLootMessage("Trivia", player.getSafeDisplayName() + " receives " + (trivia.prize.getCount() == 1 ? Misc.aOrAn(trivia.prize.getDefinition().getName()) : trivia.prize.getCount()) + " " + trivia.prize.getDefinition().getName() + (trivia.prize.getCount() == 1 ? "" : "s") + ".");
                }
                break;
            }
        }
    }
}
