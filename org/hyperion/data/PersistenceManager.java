package org.hyperion.data;

import com.thoughtworks.xstream.XStream;
import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.content.Door;
import org.hyperion.rs2.model.content.misc.CookingItem;
import org.hyperion.rs2.model.content.misc.FoodItem;
import org.hyperion.rs2.model.content.misc.Herb;
import org.hyperion.rs2.model.content.misc.PickpocketNpc;
import org.hyperion.rs2.model.content.misc.Potion;
import org.hyperion.rs2.model.content.misc.PrayerIcon;
import org.hyperion.rs2.model.content.misc.Rune;
import org.hyperion.rs2.model.content.misc.SmeltingItem;
import org.hyperion.rs2.model.content.misc.Stall;
import org.hyperion.rs2.model.content.misc.UnfinishedPotion;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Has the xstream object.
 *
 * @author Graham
 */
public class PersistenceManager {
    private static final XStream xstream;

    static {
        xstream = new XStream();
        xstream.alias("foodItem", FoodItem.class);
        xstream.alias("icon", PrayerIcon.class);
        xstream.alias("herb", Herb.class);
        xstream.alias("potion", Potion.class);
        xstream.alias("unfinPotion", UnfinishedPotion.class);
        xstream.alias("smeltingItem", SmeltingItem.class);
        xstream.alias("cookingItem", CookingItem.class);
        xstream.alias("npc", PickpocketNpc.class);
        xstream.alias("stall", Stall.class);
        xstream.alias("runes", Rune.class);
        xstream.alias("door", Door.class);
        xstream.alias("location", Location.class);
    }

    private PersistenceManager() {
    }

    public static void save(final Object object, final OutputStream out) {
        xstream.toXML(object, out);
    }

    public static Object load(final InputStream in) {
        return xstream.fromXML(in);
    }

}
