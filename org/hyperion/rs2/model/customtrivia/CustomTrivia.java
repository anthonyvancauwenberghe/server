package org.hyperion.rs2.model.customtrivia;

import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.util.TextUtils;

public class CustomTrivia{

    public final Player creator;
    public final String question;
    public final String answer;
    public final Item prize;

    public final String blurredAnswer;

    public CustomTrivia(final Player creator, final String question, final String answer, final Item prize){
        this.creator = creator;
        this.question = TextUtils.titleCase(question);
        this.answer = answer;
        this.prize = prize;

        blurredAnswer = blur(answer, '[', ']', '*');
    }

    public void send(final Player player, final boolean alert){
        if(alert){
            player.sendf("Alert##%s's Trivia for %s x %,d##%s##::answertrivia %s | ::viewtrivia",
                    creator.getSafeDisplayName(),
                    prize.getDefinition().getProperName(), prize.getCount(),
                    question, blurredAnswer);
        }else{
            player.sendf("@red@----------------------------------------------------------------------------------------");
            player.sendf("@blu@%s@bla@'s Trivia for @blu@%s @bla@x@blu@ %,d@bla@!", player.getSafeDisplayName(), prize.getDefinition().getProperName(), prize.getCount());
            player.sendf("@blu@%s", question);
            player.sendf("@blu@::answertrivia @red@%s", blurredAnswer);
            player.sendf("@red@----------------------------------------------------------------------------------------");
        }
    }

    private static String blur(final String text, final char encOpen, final char encClose, final char blurChar){
        boolean blur = true;
        final StringBuilder bldr = new StringBuilder();
        for(final char c : text.toCharArray()){
            if(Character.isWhitespace(c) || (!Character.isLetterOrDigit(c) && c != encOpen && c != encClose)){
                bldr.append(c);
                continue;
            }
            if(c == encOpen)
                blur = false;
            else if(c == encClose)
                blur = true;
            else
                bldr.append(blur ? blurChar : c);

        }
        return bldr.toString();
    }
}
