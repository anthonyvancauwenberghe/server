package cfgeditor.itemdef;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import org.hyperion.rs2.model.ItemDefinition;

public final class ItemDefinitionManager {

    public interface Listener{
        public void onUpdate(final boolean saving, final int current, final double progress, final ItemDefinition def);
    }

    private static final File FILE = new File("./data/itemconfigs2.cfg");

    public static final int MAX = ItemDefinition.MAX_ID;
    private static final ItemDefinition[] DEFINITIONS = new ItemDefinition[MAX];

    private static Listener listener;

    private ItemDefinitionManager(){}

    public static void setListener(final Listener listener){
        ItemDefinitionManager.listener = listener;
    }

    public static void load() throws Exception{
        final Scanner reader = new Scanner(FILE);
        int current = 0;
        while(reader.hasNextLine()){
            final ItemDefinition def = ItemDefinition.forString(reader.nextLine());
            DEFINITIONS[def.getId()] = def;
            if(listener != null)
                listener.onUpdate(false, current, current * 100d / MAX, def);
            ++current;
        }
        if(listener != null)
            listener.onUpdate(false, MAX, 100, null);
        reader.close();
    }

    public static void save() throws Exception{
        final BufferedWriter writer = new BufferedWriter(new FileWriter(FILE));
        int current = 0;
        for(final ItemDefinition def : DEFINITIONS){
            if(def != null){
                writer.write(def.toString());
                writer.newLine();
            }
            if(listener != null)
                listener.onUpdate(true, current, current * 100d / MAX, def);
            ++current;
        }
        writer.flush();
        writer.close();
        if(listener != null)
            listener.onUpdate(true, MAX, 100, null);
    }

    public static ItemDefinition[] getDefinitions(){
        return DEFINITIONS;
    }
}
