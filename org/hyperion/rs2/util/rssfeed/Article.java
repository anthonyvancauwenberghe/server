package org.hyperion.rs2.util.rssfeed;

import org.hyperion.util.Misc;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Date;

/**
 * Created by Gilles on 2/10/2015.
 */
public class Article {
    private Date date;
    private String title, link, content;

    //test
    public Article(final Date date, final String link, final String title, final String content) {
        this.date = date;
        this.link = link;
        this.title = title;
        this.content = content;
    }

    public static String reformat(final String text) {
        String output = "";
        final Document doc = Jsoup.parse(text);
        final Elements elements = doc.body().select("*");
        for(final Element element : elements){
            if(!element.ownText().isEmpty()){
                output += Misc.wrapString(element.ownText().replaceAll("\u00a0", " "), 45) + "\n";
            }
        }
        return output;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(final String link) {
        this.link = link;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }
}
