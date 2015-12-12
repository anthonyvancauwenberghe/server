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
public abstract class SaveInteger extends SaveSingleValue {

    /**
     * Constructs a new SaveInteger.
     *
     * @param name
     */
    public SaveInteger(final String name) {
        super(name);
    }

    @Override
    public boolean save(final Player player, final BufferedWriter writer) throws IOException {
        final int value = getValue(player);
        if(value != getDefaultValue()){
            writer.write(getName() + "=" + value);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void load(final Player player, final String value, final BufferedReader reader) {
        final int valueInt = Integer.parseInt(value);
        setValue(player, valueInt);
    }

    @Override
    public void setSingleValue(final Player player, final Object value) {
        setValue(player, (Integer) value);
    }

    /**
     * Sets the value for the specified Player.
     *
     * @param player
     * @param value
     */
    public abstract void setValue(Player player, int value);

    @Override
    public Integer getValue(final String columnName, final ResultSet rs) throws SQLException {
        return rs.getInt(columnName);
    }

    /**
     * Gets the value of the Player.
     *
     * @param player
     * @return the value
     */
    @Override
    public abstract Integer getValue(Player player);

    /**
     * The default value for the SaveObject,
     * only values different from the default value are saved into files.
     *
     * @return
     */
    public abstract int getDefaultValue();


}
