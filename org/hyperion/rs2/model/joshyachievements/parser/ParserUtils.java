package org.hyperion.rs2.model.joshyachievements.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public final class ParserUtils{

    private ParserUtils(){}

    public static Element first(final Element root, final String tag){
        final NodeList list = root.getElementsByTagName(tag);
        return (Element)(list.getLength() > 0 ? list.item(0) : null);
    }

    public static String firstText(final Element root, final String tag){
        return first(root, tag).getTextContent();
    }

    public static int intAttr(final Element e, final String attribute){
        return Integer.parseInt(e.getAttribute(attribute));
    }

    public static Stream<Element> elements(final Element e, final String tag){
        final NodeList nodes = e.getElementsByTagName(tag);
        final List<Element> list = new ArrayList<>(nodes.getLength());
        for(int i = 0; i < nodes.getLength(); i++){
            final Node n = nodes.item(i);
            if(n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            list.add((Element)n);
        }
        return list.stream();
    }
}
