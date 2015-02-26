package org.hyperion.rs2.model.ge;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jet on 2/25/2015.
 */
public final class GrandExchange {

    private static final File FILE = new File("./data/grandexchange.xml");

    private static final Map<Integer, List<Entry>> ITEMS = new HashMap<>();
    private static final Map<Integer, Entry> ENTRIES = new HashMap<>();

    private GrandExchange(){}

    public static int getNextEntryId(){
        for(int i = 0; i < Short.MAX_VALUE; i++)
            if(!ENTRIES.containsKey(i))
                return i;
        return -1; //wont happen
    }

    public static void addEntry(final Entry entry, final boolean notify){
        ENTRIES.put(entry.getId(), entry);
        if(!ITEMS.containsKey(entry.getItemId()))
            ITEMS.put(entry.getItemId(), new ArrayList<>());
        ITEMS.get(entry.getItemId()).add(entry);
        if(!notify)
            return;
        for(final Player p : World.getWorld().getPlayers())
            if(p.getGrandExchangeTracker().isOpen() && p.getGrandExchangeTracker().getViewingItemId() == entry.getItemId())
                p.write(GrandExchangeInterface.get().createAddEntry(entry));
    }

    public static void removeEntry(final Entry entry, final boolean notify){
        ENTRIES.remove(entry.getId());
        ITEMS.get(entry.getItemId()).remove(entry);
        if(ITEMS.get(entry.getItemId()).isEmpty())
            ITEMS.remove(entry.getItemId());
        if(!notify)
            return;
        for(final Player p : World.getWorld().getPlayers())
            if(p.getGrandExchangeTracker().isOpen() && p.getGrandExchangeTracker().getViewingItemId() == entry.getItemId())
                p.write(GrandExchangeInterface.get().createRemoveEntry(entry));

    }

    public static Entry getEntry(final int id){
        return ENTRIES.get(id);
    }

    public static Stream<Entry> entries(){
        return ENTRIES.values().stream();
    }

    public static List<Entry> getEntries(final Predicate<Entry> filter){
        return entries().filter(
                filter != null ? filter : Objects::nonNull
        ).collect(Collectors.toList());
    }

    public static Stream<Entry> streamEntries(){
        return ENTRIES.values().stream();
    }

    public static boolean reload(){
        ITEMS.clear();
        ENTRIES.clear();
        return load();
    }

    public static boolean save(){
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder bldr = factory.newDocumentBuilder();
            final Document doc = bldr.newDocument();
            final Element entries = doc.createElement("entries");
            entries().map(e -> e.toElement(doc)).forEach(entries::appendChild);
            doc.appendChild(entries);
            final Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(FILE)));
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean load(){
        if(!FILE.exists())
            return true;
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try{
            final DocumentBuilder bldr = factory.newDocumentBuilder();
            final Document doc = bldr.parse(FILE);
            final Element entries = (Element) doc.getElementsByTagName("entries").item(0);
            final NodeList list = entries.getElementsByTagName("entry");
            for(int i = 0; i < list.getLength(); i++){
                final Node n = list.item(i);
                if(n.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                final Element e = (Element) n;
                final Entry entry = Entry.parse(e);
                addEntry(entry, false);
            }
            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
}
