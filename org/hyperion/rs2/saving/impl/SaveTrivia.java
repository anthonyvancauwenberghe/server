package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.misc.TriviaSettings;
import org.hyperion.rs2.saving.SaveBoolean;

public class SaveTrivia extends SaveBoolean {

	public SaveTrivia(String name) {
		super(name);
	}

	@Override
	public void setValue(Player player, boolean value) {
		player.getTrivia().setEnabled(value);
	}

	@Override
	public Boolean getValue(Player player) {
		return player.getTrivia().isEnabled();
	}

	@Override
	public boolean getDefaultValue() {
		return TriviaSettings.DEFAULT_ENABLED;
	}

}
