package org.hyperion.rs2.model.cluescroll;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ClueScrollManager {

    private static final File FILE = new File("cluescrolls.xml");
    private static final Map<Integer, ClueScroll> MAP = new HashMap<>();

    private ClueScrollManager(){}

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
            MAP.put(clueScroll.getId(), clueScroll);
        }
    }
}
