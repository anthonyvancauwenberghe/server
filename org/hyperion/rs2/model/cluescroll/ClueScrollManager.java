package org.hyperion.rs2.model.cluescroll;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.hyperion.rs2.model.Item;
import org.hyperion.rs2.model.Player;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ClueScrollManager {

    private static final File FILE = new File("./data/cluescrolls.xml");
    private static final Map<Integer, ClueScroll> MAP = new HashMap<>();
    private static final Map<ClueScroll.Difficulty, List<ClueScroll>> DIFFICULTY_MAP = new HashMap<>();

    static{
        try{
            load();
            System.out.println("ClueScrolls Loaded: " + MAP.size());
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public static final int MIN_ID = 2677;
    public static final int MAX_ID = 2713;

    private ClueScrollManager(){}

    public static void trigger(final Player player, final int id){
        final ClueScroll cs = getInInventory(player);
        if(cs == null)
            return;
        if(cs.getTrigger().getId() != id) {
            if(player.debug)
                player.sendf("cluescroll trigger: %d | your trigger: %d", cs.getTrigger().getId(), id);
            return;
        }
        if(getInventoryCount(player) > 1){
            player.sendf("You are only allowed to have 1 clue scroll in your inventory");
            return;
        }
        if(!cs.hasAllRequirements(player)) {
            if(player.debug)
                player.sendf("you don't meet all requirements");
            return;
        }
        cs.apply(player);
    }

    public static void trigger(final Player player, final ClueScroll.Trigger trigger){
        trigger(player, trigger.getId());
    }

    public static ClueScroll getInInventory(final Player player){
        for(final Item i : player.getInventory().toArray()){
            if(i == null)
                continue;
            final ClueScroll cs = get(i.getId());
            if(cs != null)
                return cs;
        }
        return null;
    }

    public static int getInventoryCount(final Player player){
        int count = 0;
        for(final Item i : player.getInventory().toArray())
            if(i != null && get(i.getId()) != null)
                ++count;
        return count;
    }

    public static ClueScroll get(final int id){
        return MAP.get(id);
    }

    public static void add(final ClueScroll clueScroll){
        MAP.put(clueScroll.getId(), clueScroll);
    }

    public static void remove(final ClueScroll clueScroll){
        MAP.remove(clueScroll.getId());
    }

    public static Collection<ClueScroll> getAll(){
        return MAP.values();
    }

    public static Collection<ClueScroll> getAll(final ClueScroll.Difficulty difficulty){
        return DIFFICULTY_MAP.get(difficulty);
    }

    public static int size(){
        return MAP.size();
    }

    public static void save() throws Exception{
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder bldr = factory.newDocumentBuilder();
        final Document doc = bldr.newDocument();
        final Element root = doc.createElement("cluescrolls");
        for(final ClueScroll clueScroll : MAP.values())
            root.appendChild(clueScroll.toElement(doc));
        doc.appendChild(root);
        final Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(FILE)));
    }

    public static void load() throws Exception{
        if(!FILE.exists())
            return;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder bldr = factory.newDocumentBuilder();
        final Document doc = bldr.parse(FILE);
        final Element clueScrollsElement = (Element) doc.getElementsByTagName("cluescrolls").item(0);
        final NodeList list = clueScrollsElement.getElementsByTagName("cluescroll");
        for(int i = 0; i < list.getLength(); i++){
            final Node node = list.item(i);
            if(node.getNodeType() != Node.ELEMENT_NODE)
                continue;
            final Element element = (Element) node;
            final ClueScroll clueScroll = ClueScroll.parse(element);
            if(!DIFFICULTY_MAP.containsKey(clueScroll.getDifficulty()))
                DIFFICULTY_MAP.put(clueScroll.getDifficulty(), new ArrayList<>());
            DIFFICULTY_MAP.get(clueScroll.getDifficulty()).add(clueScroll);
            MAP.put(clueScroll.getId(), clueScroll);
        }
    }
}
