package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveVerifyCode extends SaveString {

    public SaveVerifyCode() {
        super("VerifyCode");
    }

    @Override
    public String getValue(final Player player) {
        return player.verificationCode;
    }

    @Override
    public void setValue(final Player player, String value) {
        if(value == null || value.equalsIgnoreCase("null"))
            value = "";
        player.verificationCode = value;
        player.verificationCodeEntered = value.isEmpty();
    }
}
