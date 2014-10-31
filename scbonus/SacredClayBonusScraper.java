package scbonus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.content.minigame.FightPits;

public class SacredClayBonusScraper {

	static List<String> lines = new LinkedList<String>();
    public static void main(String[] args) throws Exception{
    	final File file = new File(System.getProperty("user.home")+"/itemconfigs2.cfg");
    	if(!file.createNewFile())
    		if(file.delete())
    			file.createNewFile();
        final Map<Integer, ItemDefinition> map = new HashMap<>();
        loadDefinitions(map);
        FightPits pits = new FightPits();
        pits.init();
        final URL url = new URL("http://www1.mediafire.com/conversion_server.php?da58&quickkey=ba4syvc2vp4bw83&output=html&doc_type=d&metadata=0&page=1&initial=0&timestamp=1356950077&version=110017&domain=mediafire.com");
        final URLConnection con = url.openConnection();
        con.setReadTimeout(60000);
        con.setConnectTimeout(60000);
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        final Scanner input = new Scanner(con.getInputStream(), "UTF-8");
        while(input.hasNextLine()){
            final String line = input.nextLine().trim().replace("\t", " ");
            if(!line.contains("(class_") && !line.contains("sacred_clay"))
                continue;
            
            final String[] split = line.split(" +");
            final int id = Integer.parseInt(split[2]);
            if(!FightPits.rewardItems.contains(id))
            	continue;
            final ItemDefinition def = map.get(id);
            if(def == null){
                System.out.println("null def at id: " + id);
                continue;
            }
            if(map.get(id-1) != null && map.get(id-1).getName().equalsIgnoreCase(def.getName())) //prevent putting bonuses on noted items lol
                continue;
            //System.out.printf("[%s - %d] OLD: %s \n", def.getName(), def.getId(), Arrays.toString(def.getBonus()));
            for(int bi = 0; bi < def.getBonus().length; bi++)
                def.getBonus()[bi] = Integer.parseInt(split[bi + 8].trim());
            //id = 14094, name = Sacred clay platebody, examine = The_sacred_clay_top_has_transformed_into_a_spiked_platemail_body., 
            //noted = false, noteable = false, stackable = false, parentid = 14094, notedid = -1, 
            //highalc = 1, armourslot = 15, bonus0 = 0, bonus1 = 0, bonus2 = 0, bonus3 = -30, bonus4 = -10,
            //bonus5 = 82, bonus6 = 80, bonus7 = 72, bonus8 = -6, bonus9 = 80, bonus10 = 0, bonus11 = 0, 
            StringBuilder builder = new StringBuilder(
            		String.format("id = %d, name = %s, examine = %s, noted = false, noteable = false," +
        					"stackable = false, parentid = %d, notedid = -1, highalc = 1, armourslot = %d,"
        					, def.getId(), def.getName(), def.getDescription(), def.getId(), def.getArmourSlot())
        	);
            for(int appendBonus = 0; appendBonus < def.getBonus().length; appendBonus++) {
            	builder.append(" ").append(String.format("bonus%d = %d", appendBonus, def.getBonus()[appendBonus])).append(",");
            }
            System.out.println(builder.toString());
            lines.add(builder.toString());
            //System.out.printf("[%s - %d] NEW: %s \n", def.getName(), def.getId(), Arrays.toString(def.getBonus()));
        }
        dumpDefinitions(file);
        input.close();
    }

    private static void dumpDefinitions(File file) throws Exception{
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for(final String def : lines){
            writer.write(def);
            writer.newLine();
       }
        writer.flush();
        writer.close();
    }

    private static void loadDefinitions(final Map<Integer, ItemDefinition> map) throws Exception{
        final Scanner input = new Scanner(new File("./data/itemconfigs.cfg"));
        ItemDefinition.loadItems();
        while(input.hasNextLine()){
            final String line = input.nextLine();
            try{
                final ItemDefinition def = ItemDefinition.forString(line);
                map.put(def.getId(), def);
            }catch(Exception ex){
                System.out.println("Error loading: " + line);
            }
        }
        input.close();
    }
}
