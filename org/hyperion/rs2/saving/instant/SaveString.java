package org.hyperion.rs2.saving.instant;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arsen Maxyutov.
 */
public abstract class SaveString extends SaveSingleValue {

    /**
     * Constructs a new SaveInteger.
     *
     * @param name
     */
    public SaveString(final String name) {
        super(name);
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        final String value = getValue(player);
        writer.write(getName() + "=" + value);
        return true;
    }

    @Override
    public void load(final Player player, final String value, final BufferedReader reader) {
        setValue(player, value);
    }

    @Override
    public String getValue(final String columnName, final ResultSet rs) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public void setSingleValue(final Player player, final Object value) {
        setValue(player, (String) value);
    }

    /**
     * Sets the value for the specified Player.
     *
     * @param player
     * @param value
     */
    public abstract void setValue(Player player, String value);

    /**
     * Gets the value of the Player.
     *
     * @param player
     * @return the value
     */
    @Override
    public abstract String getValue(Player player);

}
