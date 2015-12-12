package org.hyperion.rs2.saving.impl;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;
import org.hyperion.rs2.util.TextUtils;

/**
 * Created by Administrator on 6/11/2014.
 */
public class SaveYellTag extends SaveString {

    public SaveYellTag(final String name) {
        super(name);
    }

    @Override
    public void setValue(final Player player, final String value) {
        if(value != null && !value.isEmpty())
            player.getYelling().setYellTitle(value);
    }

    @Override
    public String getValue(final Player player) {
        final String tag = player.getYelling().getTag();
        return TextUtils.titleCase(tag);
    }
}
