package org.hyperion.rs2.model.content.minigame.poker.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wasay
 * Date: 8/3/15
 * Time: 2:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class Deck {

    public final List<Card> cards;

    public Deck() {
        this.cards = getAllCards();
    }

    public synchronized void shuffleDeck() {
        Collections.shuffle(cards);
    }

    public synchronized Card draw() {
        return cards.remove(0);
    }

    public static List<Card> getAllCards() {
        final List<Card> cards = new ArrayList<>(52);
        for(Type type : Type.values()) {
            for(CardNumber number : CardNumber.values()) {
                cards.add(new Card(type, number));
            }
        }
        return cards;
    }

}
