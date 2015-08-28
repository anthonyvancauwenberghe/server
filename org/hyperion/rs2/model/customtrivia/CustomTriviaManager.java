package org.hyperion.rs2.model.customtrivia;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.util.PushMessage;

public final class CustomTriviaManager{

    private static final List<CustomTrivia> LIST = new ArrayList<>();

    private CustomTriviaManager(){}

    public static void add(final CustomTrivia trivia){
        LIST.add(trivia);
    }

    public static void remove(final CustomTrivia trivia){
        LIST.remove(trivia);
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
                player.getBank().add(trivia.prize);
                player.sendf("@blu@%s@bla@ x @blu@%,d@bla@ has been added to your bank!", trivia.prize.getDefinition().getName(), trivia.prize.getCount());
                PushMessage.pushGlobalMessage(String.format(
                        "@blu@%s@bla@ has answered @blu@%s@bla@'s trivia question correctly for @red@%s@bla@ x @red@%,d@bla@",
                        player.getSafeDisplayName(), trivia.creator.getSafeDisplayName(),
                        trivia.prize.getDefinition().getName(), trivia.prize.getCount()));
                itr.remove();
            }
        }
    }
}
