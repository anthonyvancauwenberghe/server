package org.hyperion.rs2.saving.impl;

import java.util.Date;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.saving.SaveString;

public class SaveCreatedString extends SaveString {

	public SaveCreatedString(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setValue(Player player, String value) {

	}

	@Override
	public String getValue(Player player) {
		Date date = new Date(player.getCreatedTime());
		@SuppressWarnings("deprecation")
		String value = date.getDay() + "," + date.getMonth();
		return value;
	}

}
