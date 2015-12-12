package org.hyperion.rs2.saving.instant.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.content.misc.TriviaSettings;
import org.hyperion.rs2.saving.instant.SaveBoolean;

public class SaveTrivia extends SaveBoolean {

    public SaveTrivia(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final boolean value) {
        player.getTrivia().setEnabled(value);
    }

    @Override
    public Boolean getValue(final Player player) {
        return player.getTrivia().isEnabled();
    }

    @Override
    public boolean getDefaultValue() {
        return TriviaSettings.DEFAULT_ENABLED;
    }

}
