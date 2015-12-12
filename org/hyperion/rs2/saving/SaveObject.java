package org.hyperion.rs2.saving;

import org.hyperion.rs2.model.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author Arsen Maxyutov.
 */
public abstract class SaveObject {

    /**
     * The name identifier of the SaveObject.
     */
    private final String name;

    /**
     * Constructs a new SaveObject with the specified name.
     *
     * @param name
     */
    public SaveObject(final String name) {
        this.name = name;
    }

    /**
     * Gets the name of the SaveObject.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }


    /**
     * Saves the SaveObject to the Player's file.
     *
     * @param player
     * @param writer
     * @throws IOException
     */
    public abstract boolean save(Player player, BufferedWriter writer) throws IOException;

    /**
     * Loads a SaveObject from the Player's file.
     *
     * @param player
     * @param values
     * @param reader
     * @throws IOException
     */
    public abstract void load(Player player, String values, BufferedReader reader) throws IOException;

}
