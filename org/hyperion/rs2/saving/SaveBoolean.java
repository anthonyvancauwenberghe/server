package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public abstract class SaveBoolean extends SaveSingleValue {

	/**
	 * Constructs a new SaveBoolean.
	 *
	 * @param name
	 */
	public SaveBoolean(String name) {
		super(name);
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
	public abstract Boolean getValue(Player player);


	public abstract boolean getDefaultValue();

	@Override
	public boolean save(Player player, BufferedWriter writer) throws IOException {
		boolean value = (Boolean) getValue(player);
		if(value != getDefaultValue()) {
			writer.write(getName() + "=" + value);
			return true;
		}
		return false;
	}


	@Override
	public void load(Player player, String values, BufferedReader reader) throws IOException {
		boolean value = Boolean.parseBoolean(values);
		setValue(player, value);
	}

}
