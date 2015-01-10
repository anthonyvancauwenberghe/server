package org.hyperion.rs2.model.cluescroll.util;

import java.util.Random;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ClueScrollUtils {

    private static final Random RAND = new Random();

    private ClueScrollUtils(){}

    public static int rand(final int min, final int max){
        return min + RAND.nextInt(max - min + 1);
    }

    public static boolean isChance(final int chance){
        return chance > 0 && rand(1, 100) <= chance;
    }

    public static Element createElement(final Document doc, final String tag, final Object content){
        final Element element = doc.createElement(tag);
        element.setTextContent(content.toString());
        return element;
    }

    public static String getString(final Element root, final String tag){
        return root.getElementsByTagName(tag).item(0).getTextContent();
    }

    public static Integer getInteger(final Element root, final String tag){
        return Integer.parseInt(getString(root, tag));
    }
}
