package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Arsen Maxyutov.
 */ //ok
public abstract class SaveLong extends SaveSingleValue {

    /**
     * Constructs a new SaveInteger.
     *
     * @param name
     */
    public SaveLong(final String name) {
        super(name);
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        final long value = getValue(player);
        if(value != getDefaultValue()){
            writer.write(getName() + " = " + value);
            return true;
        }
        return false;
    }

    @Override
    public void load(final Player player, final String value, final BufferedReader reader) {
        final long valueInt = Long.parseLong(value);
        setValue(player, valueInt);
    }

    @Override
    public Long getValue(final String columnName, final ResultSet rs) throws SQLException {
        return rs.getLong(columnName);
    }


    /**
     * The default value for the SaveObject,
     * only values different from the default value are saved into files.
     *
     * @return
     */
    public abstract long getDefaultValue();

    @Override
    public void setSingleValue(final Player player, final Object value) {
        setValue(player, (Long) value);
    }

    /**
     * Sets the value for the specified Player.
     *
     * @param player
     * @param value
     */
    public abstract void setValue(Player player, long value);

    /**
     * Gets the value of the Player.
     *
     * @param player
     * @return the value
     */
    @Override
    public abstract Long getValue(Player player);

}
