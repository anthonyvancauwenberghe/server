package org.hyperion.rs2.model.content;

import org.hyperion.rs2.model.Player;

import java.io.FileNotFoundException;

public interface ContentTemplate {

	public abstract boolean clickObject(Player player, int type, int a, int b, int c, int d);//this will work for all items, objects , npcs etc, specify value -1 if the value is unused

	public abstract void init() throws FileNotFoundException;

	public abstract int[] getValues(int type);
}