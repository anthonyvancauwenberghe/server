package org.hyperion.rs2.model.content.randomgame;

import org.hyperion.util.Misc;

import java.util.Arrays;

public enum RandCharValues {
    LOWERCASE(1.0, "abcdefghijklmnopqrstuvwxyz,./".toCharArray()),
    NUMBERS(1.2, "1234567890".toCharArray()),
    UPPERCASE(1.5, "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()),
    SYMBOLS(1.8, "!@#$%^&*()_+=-<>".toCharArray());

    private static final int MINIMUM_SEQUENCE_LENGTH = 5, MAXIMUM_SEQUENCE_LENGTH = 15;
    private final double difficulty;
    private final char[] characters;

    RandCharValues(final double difficulty, final char[] characters) {
        this.difficulty = difficulty;
        this.characters = characters;
    }

    private static char getRandomCharacter() {
        final int value = Misc.random(values().length - 1);
        final char[] chars = values()[value].characters;
        return chars[Misc.random(chars.length - 1)];
    }

    public static String getRandomSequence() {
        final StringBuilder builder = new StringBuilder();
        final int length = MINIMUM_SEQUENCE_LENGTH + Misc.random(MAXIMUM_SEQUENCE_LENGTH - MINIMUM_SEQUENCE_LENGTH);
        for(int i = 0; i < length; i++){
            builder.append(getRandomCharacter());
        }
        return builder.toString();
    }

    private static double getDifficulty(final char c) {
        for(final RandCharValues val : values()){
            if(Arrays.asList(val.characters).contains(c)){ //must be no repeats, or first gets it
                return val.difficulty;
            }
        }
        return 0.0;
    }

    private static double getDifficulty(final String sequence) {
        double difficulty = 0.0;
        for(final char c : sequence.toCharArray()){
            difficulty += getDifficulty(c);
        }
        return difficulty;
    }

    public static int getApproximateDifficulty(final String sequence) {
        return (int) Math.round(getDifficulty(sequence));
    }

    public final double getDifficulty() {
        return difficulty;
    }

    public final char[] getCharacters() {
        return characters;
    }
}
