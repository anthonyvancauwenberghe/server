package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Arsen Maxyutov.
 */
public abstract class SaveLong extends SaveSingleValue {

	/**
	 * Constructs a new SaveInteger.
	 *
	 * @param name
	 */
	public SaveLong(String name) {
		super(name);
	}

	@Override
	public boolean save(Player player, BufferedWriter writer) throws IOException {
		long value = getValue(player);
		if(value != getDefaultValue()) {
			writer.write(getName() + " = " + value);
			return true;
		}
		return false;
	}

	@Override
	public void load(Player player, String value, BufferedReader reader) {
		long valueInt = Long.parseLong(value);
		setValue(player, valueInt);
	}

	/**
	 * The default value for the SaveObject,
	 * only values different from the default value are saved into files.
	 *
	 * @return
	 */
	public abstract long getDefaultValue();

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
	public abstract Long getValue(Player player);

}
