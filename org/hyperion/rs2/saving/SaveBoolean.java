package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SaveBoolean extends SaveSingleValue {

    /**
     * Constructs a new SaveBoolean.
     *
     * @param name
     */
    public SaveBoolean(final String name) {
        super(name);
    }

    @Override
    public void setSingleValue(final Player player, final Object value) {
        setValue(player, (Boolean) value);
    }

    /**
     * Sets the value for the specified Player.
     *
     * @param player
     * @param value
     */
    public abstract void setValue(Player player, boolean value);

    /**
     * Gets the value of the Player.
     *
     * @param player
     * @return the value
     */
    @Override
    public abstract Boolean getValue(Player player);


    public abstract boolean getDefaultValue();

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        final boolean value = getValue(player);
        //if(value != getDefaultValue()) {
        writer.write(getName() + "=" + value);
        return true;
        //}
        //return false;
    }


    @Override
    public void load(final Player player, final String values, final BufferedReader reader) throws IOException {
        final boolean value = Boolean.parseBoolean(values);
        setValue(player, value);
    }


    @Override
    public Boolean getValue(final String columnName, final ResultSet rs) throws SQLException {
        return rs.getBoolean(columnName);
    }

}
