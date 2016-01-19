package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.rssfeed.Article;
import org.hyperion.rs2.util.rssfeed.ReadRss;
import org.hyperion.util.Time;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Gilles on 2/10/2015.
 */
public class RefreshNewsEvent extends Event {

    static class NewsComparator implements Comparator<Article> {
        @Override
        public int compare(Article o1, Article o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

    public static Article[] latestNews = new Article[3];
    public static long lastNewsChange = System.currentTimeMillis();

    public RefreshNewsEvent() {
        super(Time.ONE_MINUTE * 10);
    }

    static {
        refreshNews(false);
    }

    public static void refreshNews(boolean announce) {
        List<Article> news;
        try {
            news = ReadRss.readFeed("http://forums.arteropk.com/forum/10-news.xml");
            news.addAll(ReadRss.readFeed("http://forums.arteropk.com/forum/12-updates.xml"));
            news.addAll(ReadRss.readFeed("http://forums.arteropk.com/forum/58-tweaks/.xml"));
            news.sort(new NewsComparator().reversed());
            Article oldNews = latestNews[0];

            for(int i = 0; i < 3; i++) {
                latestNews[i] = news.get(i);
            }

            if(announce && !oldNews.getContent().equalsIgnoreCase(latestNews[0].getContent())) {
                for(Player player : World.getWorld().getPlayers()) {
                    player.sendServerMessage("There is some news! Do ::news to check it out!");
                    lastNewsChange = System.currentTimeMillis();
                }
            }
        } catch(Exception e) {
            System.out.println("Could not load news.");
            lastNewsChange = 0;
        }
    }

    @Override
    public void execute() throws IOException {
        //refreshNews(true);
    }
}
