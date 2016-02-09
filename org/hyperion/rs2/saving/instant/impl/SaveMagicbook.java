package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.SpellBook;
import org.hyperion.rs2.saving.instant.SaveInteger;

public class SaveMagicbook extends SaveInteger {

	public SaveMagicbook(String name) {
		super(name);
	}

	@Override
	public int getDefaultValue() {
		return SpellBook.DEFAULT_SPELLBOOK;
	}

	@Override
	public void setValue(Player player, int value) {
		player.getSpellBook().changeSpellBook(value);
	}

	@Override
	public Integer getValue(Player player) {
		return player.getSpellBook().toInteger();
	}

}
