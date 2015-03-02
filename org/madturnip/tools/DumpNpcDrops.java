package org.madturnip.tools;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.hyperion.rs2.model.DeathDrops;
import org.hyperion.rs2.model.ItemDefinition;
import org.hyperion.rs2.model.NPCDefinition;
import org.hyperion.rs2.model.NPCDrop;
import org.hyperion.rs2.model.content.skill.slayer.SlayerTask;
import org.hyperion.rs2.util.TextUtils;

public class DumpNpcDrops {
	public static void main(String[] args) throws Exception{
		ItemDefinition.init();
	}
	
	
	
	  public static void createNode(XMLEventWriter eventWriter, String name,
		      String value, int tabs) throws XMLStreamException {

		    XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		    XMLEvent end = eventFactory.createDTD("\n");
		    XMLEvent tab = eventFactory.createDTD("\t");
		    // create Start node
		    StartElement sElement = eventFactory.createStartElement("", "", name);
		    while(tabs-- != 0) {
		    	eventWriter.add(tab);
		    }
		    eventWriter.add(sElement);
		    // create Content
		    Characters characters = eventFactory.createCharacters(value);
		    eventWriter.add(characters);
		    // create End node
		    EndElement eElement = eventFactory.createEndElement("", "", name);
		    eventWriter.add(eElement);
		    eventWriter.add(end);
	}

    public static void startDump4() throws IOException {

        int index = 0;

        final File file = new File("./data/slayer_exp_dump.dump");
        if(!file.exists())
            file.createNewFile();
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(final SlayerTask task : SlayerTask.values()) {
                if(task == null) continue;
                Class<?> clazz = Class.forName("org.hyperion.rs2.model.content.skill.slayer.SlayerTask");
                final Field expMultiplier = clazz.getDeclaredField("EXP_MULTIPLIER");
                expMultiplier.setAccessible(true);
                writer.write("[NAME]: "+task.toString() + " EXP: "+(task.getXP()));
                writer.newLine();
                index++;
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }

        System.out.println("Dumped: "+index);
    }

    public static void startDump3() throws IOException {

        int index = 0;

        final File file = new File("./data/npc_info_dump.cfg");
        if(!file.exists())
            file.createNewFile();
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for(NPCDefinition def : NPCDefinition.getDefinitions()) {
                if(def == null) continue;
                writer.write("[NAME]: "+ def.getName()+ " [ID]: "+ def.getId());
                writer.newLine();
                writer.write("\t[BONUSES]: "+ Arrays.toString(def.getBonus()));
                writer.newLine();
                writer.write("\t[MAX_HP]: "+ def.maxHp());
                writer.newLine();
                writer.write("\t[COMBAT LEVEL]: " + def.combat());
                writer.newLine();
                writer.write("\t[SPAWN TIME]: " + def.spawnTime() * 0.5 + " seconds");
                writer.newLine();
                writer.newLine();
                index++;
            }
        } catch(final Exception e) {
            e.printStackTrace();
        }

        System.out.println("Dumped: "+index);

    }
	
	public static void startDump2() {
        final File file = new File("./data/drop dump.cfg");
        if(file.exists())
            if(file.delete()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        int dumped = 0;
		long startTime = System.currentTimeMillis();
		for(NPCDefinition def : NPCDefinition.getDefinitions()) {
			if(def == null)
				continue;
			if(def.getDrops().size() > 1) {
				TextUtils.writeToFile("./data/drop dump.cfg", "[" + def.getName().toUpperCase() + "]");
				for(NPCDrop drop : def.getDrops()) {
					ItemDefinition itemDef = ItemDefinition.forId(drop.getId());
					TextUtils.writeToFile("./data/drop dump.cfg", 
							String.format("%s %1.2f%%\tAmount:%d-%d", 
									itemDef.getName(), (((double)drop.getChance() + 1D)/1001D) * 100D ,  drop.getMin(), drop.getMax()));
				}
				dumped++;
			}
		}
		System.out.printf("Dumped %d npc drops in %d ms%s", dumped, System.currentTimeMillis() - startTime, System.getProperty("line.separator"));
	}
	
	public static void startDump() throws IOException {
		String name = "./data/npcdrops.cfg";
		BufferedReader file = null;
		int lineInt = 1;
		try {
			file = new BufferedReader(new FileReader(name));
			while(true) {
				String line = file.readLine();
				if(line == null)
					break;
				int spot = line.indexOf('=');
				if(spot > - 1) {
					String values = line.substring(spot + 1);
					values = values.replaceAll("\t\t", "\t");
					values = values.trim();
					String[] valuesArray = values.split("\t");
					int id = Integer.valueOf(valuesArray[0]);

					//NPCDefinition.forId(id).dropId = new int[(valuesArray.length + 1)][4];
					int i = 1;
					NPCDefinition.forId(id).getDrops().clear();
					System.out.println("[COLOR=\"Orange\"][SIZE=\"4\"][B]" + NPCDefinition.forId(id).getName() + "[/B][/SIZE][/COLOR]");
					TextUtils.writeToFile("./data/drop dump.cfg", "[COLOR=\"Orange\"][SIZE=\"4\"][B]" + NPCDefinition.forId(id).getName() + "[/B][/SIZE][/COLOR]");
					try {
						for(i = 1; i < valuesArray.length; i++) {
							String[] itemData = valuesArray[i].split("-");
							int itemId = Integer.valueOf(itemData[0]);//itemId
							int minAmount = Integer.valueOf(itemData[1]);//minAm
							int maxAmount = Integer.valueOf(itemData[2]);//maxAm
							int chance = Integer.valueOf(itemData[3]);//chance
							NPCDefinition.forId(id).getDrops().add(NPCDrop.create(itemId, minAmount, maxAmount, chance));
							TextUtils.writeToFile("./data/drop dump.cfg", ItemDefinition.forId(Integer.valueOf(itemData[0])).getName());
						}
					} catch(Exception e) {
						e.printStackTrace();
						System.out.println("error on array: " + i + " npcId: " + id);
					}
					TextUtils.writeToFile("./data/drop dump.cfg", "");
				}
				lineInt++;
			}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("error on line: " + lineInt + " ");
		} finally {
			if(file != null)
				file.close();
		}
	}

    public static void startDumpAlchs() {
        Map<Integer ,Integer> list = new HashMap<>(ItemDefinition.MAX_ID);
        for(int i = 0; i < ItemDefinition.MAX_ID; i++) {
            final ItemDefinition def = ItemDefinition.forId(i);
            if(def == null) continue;
            list.put(i, DeathDrops.calculateAlchValue(0, i));
            if(i%1000 == 0)
                System.out.println("Loaded: "+i);
        }

        System.out.println("Sorting Map");
        list = sortHashMapByValues(list, false);
        System.out.println("Sorted Map");
        final File file = new File("./data/alchprices.txt");
        try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file)))  {
            file.createNewFile();
            for(final Map.Entry<Integer, Integer> entry : list.entrySet()) {
                final int id = entry.getKey();
                final ItemDefinition def = ItemDefinition.forId(id);
                writer.write(String.format("[NAME]: %s [ID]: %d [ALCH]: %,d",  def.getName(), id, entry.getValue()));
                writer.newLine();
            }
        } catch (Exception ex) {

        }

    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <K, V> LinkedHashMap<K, V> sortHashMapByValues(Map<K, V> passedMap, boolean ascending) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        if (!ascending)
            Collections.reverse(mapValues);

        LinkedHashMap someMap = new LinkedHashMap();
        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                if (passedMap.get(key).toString().equals(val.toString())) {
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    someMap.put(key, val);
                    break;
                }
            }
        }
        return someMap;
    }
}
