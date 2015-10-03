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

    public static List<Article> latestNews = new ArrayList();
    public static long lastNewsChange = System.currentTimeMillis();

    public RefreshNewsEvent() {
        super(Time.ONE_MINUTE * 10);
    }

    static {
        List<Article> news;
        news = ReadRss.readFeed("http://forums.arteropk.com/forum/10-news.xml");
        news.addAll(ReadRss.readFeed("http://forums.arteropk.com/forum/12-updates.xml"));

        List<Date> dates = new ArrayList<>();
        for(Article article : news) {
            dates.add(article.getDate());
        }
        Collections.sort(dates);
        latestNews.clear();
        for(Article article : news) {
            if(latestNews.size() == 3)
                break;
            if(article.getDate().equals(dates.get(dates.size() - 1))) {
                if(!latestNews.contains(article)) {
                    latestNews.add(article);
                }
            }
            if(article.getDate().equals(dates.get(dates.size() - 2))) {
                if(!latestNews.contains(article)) {
                    latestNews.add(article);
                }
            }
            if(article.getDate().equals(dates.get(dates.size() - 3))) {
                if(!latestNews.contains(article)) {
                    latestNews.add(article);
                }
            }
        }
    }

    @Override
    public void execute() throws IOException {
        List<Article> news;
        news = ReadRss.readFeed("http://forums.arteropk.com/forum/10-news.xml");
        news.addAll(ReadRss.readFeed("http://forums.arteropk.com/forum/12-updates.xml"));

        List<Date> dates = new ArrayList<>();
        for(Article article : news) {
            dates.add(article.getDate());
        }
        Collections.sort(dates);
        Article oldNews = latestNews.get(0);
        latestNews.clear();
        for(Article article : news) {
            if(latestNews.size() == 3)
                break;
            if(article.getDate().equals(dates.get(dates.size() - 1))) {
                if(!latestNews.contains(article)) {
                    latestNews.add(article);
                }
            }
            if(article.getDate().equals(dates.get(dates.size() - 2))) {
                if(!latestNews.contains(article)) {
                    latestNews.add(article);
                }
            }
            if(article.getDate().equals(dates.get(dates.size() - 3))) {
                if(!latestNews.contains(article)) {
                    latestNews.add(article);
                }
            }
        }
        if(!oldNews.getContent().equalsIgnoreCase(latestNews.get(0).getContent())) {
            for(Player player : World.getWorld().getPlayers()) {
                player.sendServerMessage("There is some news! Do ::news to check it out!");
                lastNewsChange = System.currentTimeMillis();
            }
        }
    }
}
