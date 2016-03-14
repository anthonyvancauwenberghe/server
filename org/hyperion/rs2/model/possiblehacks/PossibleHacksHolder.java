package org.hyperion.rs2.model.possiblehacks;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.hyperion.Server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 * @author DrHales
 */
public final class PossibleHacksHolder {

    private static final File folder = new File("./data/");

    private static final File file = new File(folder, "possiblehacks.json");

    private static final List<PossibleHack> list = new ArrayList<>();

    private PossibleHacksHolder() {
    }

    public static List<PossibleHack> getList() {
        return list;
    }

    public static List<PossibleHack> getHacks(final String value) {
        final List<PossibleHack> hacks = new ArrayList<>();
        list.stream().filter(hack -> hack != null && hack.name.equalsIgnoreCase(value)).forEach(hack -> hacks.add(hack));
        return hacks;
    }

    public static synchronized void add(final PossibleHack hack) {
        list.add(hack);
    }

    public static void init() {
        loadPossibleHacks();
    }

    private static void loadPossibleHacks() {
        final long initial = System.currentTimeMillis();
        final JsonParser parser = new JsonParser();
        try (final FileReader reader = new FileReader(file)) {
            //TODO: rewrite
        } catch (Exception ex) {
            Server.getLogger().log(Level.WARNING, String.format("Error loading %s", file.getName()), ex);
        }
    }


}
