package org.hyperion.rs2.event.impl;

import org.hyperion.rs2.event.Event;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.util.rssfeed.Article;
import org.hyperion.rs2.util.rssfeed.ReadRss;
import org.hyperion.util.Time;

import java.io.IOException;
import java.util.*;

/**
 * Created by Gilles on 2/10/2015.
 */
public class RefreshNewsEvent extends Event {

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

            List<Date> dates = new ArrayList<>();
            for (Article article : news) {
                dates.add(article.getDate());
            }
            Collections.sort(dates);
            Article oldNews = latestNews[0];

            for (Article article : news)
                for(int i = 0; i < 3; i++)
                    if (article.getDate().equals(dates.get(dates.size() - (i + 1))))
                        latestNews[i] = article;

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
        refreshNews(true);
    }
}
