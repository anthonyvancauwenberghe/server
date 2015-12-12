package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.SpellBook;
import org.hyperion.rs2.saving.SaveInteger;

public class SaveMagicbook extends SaveInteger {

    public SaveMagicbook(final String name) {
        super(name);
    }

    @Override
    public int getDefaultValue() {
        return SpellBook.DEFAULT_SPELLBOOK;
    }

    @Override
    public void setValue(final Player player, final int value) {
        player.getSpellBook().changeSpellBook(value);
    }

    @Override
    public Integer getValue(final Player player) {
        return player.getSpellBook().toInteger();
    }

}
