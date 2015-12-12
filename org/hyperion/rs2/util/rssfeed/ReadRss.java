package org.hyperion.rs2.util.rssfeed;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ReadRss {
    static final String ITEM = "item";
    static final String TITLE = "title";
    static final String LINK = "link";
    static final String DESCRIPTION = "description";
    static final String PUB_DATE = "pubDate";
    static final DateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzzzz", Locale.getDefault());
    static XMLEvent event;

    public static List<Article> readFeed(final String feedUrl) {
        final URL url;
        try{
            url = new URL(feedUrl);
        }catch(final MalformedURLException e){
            throw new RuntimeException(e);
        }
        final List<Article> articles = new ArrayList<>();
        try{
            boolean isFeedHeader = true;
            // Set header values initial to the empty string
            String title = "";
            String link = "";
            String description = "";
            String date = "";

            // First create a new XMLInputFactory
            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            final InputStream in = read(url);
            final XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // read the XML document
            while(eventReader.hasNext()){
                event = eventReader.nextEvent();
                if(event.isStartElement()){
                    final String localPart = event.asStartElement().getName().getLocalPart();
                    switch(localPart){
                        case ITEM:
                            if(isFeedHeader){
                                isFeedHeader = false;
                            }
                            event = eventReader.nextEvent();
                            break;
                        case TITLE:
                            title = getCharacterData(eventReader);
                            break;
                        case DESCRIPTION:
                            description = getCharacterData(eventReader);
                            break;
                        case LINK:
                            link = getCharacterData(eventReader);
                            break;
                        case PUB_DATE:
                            date = getCharacterData(eventReader);
                            break;
                    }
                }else if(event.isEndElement()){
                    if(Objects.equals(event.asEndElement().getName().getLocalPart(), ITEM)){
                        Date formattedDate = Calendar.getInstance().getTime();
                        try{
                            formattedDate = format.parse(date);
                        }catch(final Exception e){
                            e.printStackTrace();
                        }
                        final Article message = new Article(formattedDate, link, title, description);
                        articles.add(message);
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        }catch(final XMLStreamException e){
            throw new RuntimeException(e);
        }
        return articles;
    }

    private static String getCharacterData(final XMLEventReader eventReader) throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if(event instanceof Characters){
            result = event.asCharacters().getData();
        }
        return result;
    }

    private static InputStream read(final URL url) {
        try{
            return url.openStream();
        }catch(final IOException e){
            throw new RuntimeException(e);
        }
    }
} 
