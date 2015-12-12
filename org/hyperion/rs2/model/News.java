package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.event.impl.RefreshNewsEvent;
import org.hyperion.rs2.packet.ActionsManager;
import org.hyperion.rs2.packet.ButtonAction;
import org.hyperion.rs2.util.rssfeed.Article;

/**
 * Created by Gilles on 2/10/2015.
 */
public class News {

    private final static int interfaceId = 36000;

    static {
        CommandHandler.submit(new Command("news", Rank.PLAYER) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                player.getNews().sendNewsInterface();
                return true;
            }
        });

        CommandHandler.submit(new Command("refreshnews", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                RefreshNewsEvent.refreshNews(true);
                return true;
            }
        });

        ActionsManager.getManager().submit(-29532, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getNews().setSelectedPost(0);
            }
        });

        ActionsManager.getManager().submit(-29529, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getNews().setSelectedPost(1);
            }
        });

        ActionsManager.getManager().submit(-29526, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getNews().setSelectedPost(2);
            }
        });

        ActionsManager.getManager().submit(-29520, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.sendMessage("l4unchur13 https://twitter.com/arteropk1");
            }
        });

        ActionsManager.getManager().submit(-29519, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.sendMessage("l4unchur13 https://www.facebook.com/ArteroPk-841836079182865/timeline/");
            }
        });

        ActionsManager.getManager().submit(-29518, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getActionSender().removeAllInterfaces();
            }
        });

        ActionsManager.getManager().submit(-29516, new ButtonAction() {
            @Override
            public void handle(final Player player, final int id) {
                player.getNews().sendLink();
            }
        });
    }

    private final Player player;
    private int selectedPost = 0;

    public News(final Player player) {
        this.player = player;
    }

    public void sendNewsInterface() {
        if(sendNewsPost(selectedPost))
            player.getActionSender().showInterface(interfaceId);
    }

    public void setSelectedPost(final int number) {
        if(number >= 0 && number < 3)
            selectedPost = number;
        else
            selectedPost = 0;
        sendNewsInterface();
    }

    public void sendLink() {
        final Article newspost = RefreshNewsEvent.latestNews[selectedPost];
        player.sendMessage("l4unchur13 " + newspost.getLink());
    }

    public boolean sendNewsPost(final int id) {
        final Article newspost = RefreshNewsEvent.latestNews[id];

        if(newspost == null)
            return false;

        player.getActionSender().sendString(36014, newspost.getTitle());

        final String[] article = Article.reformat(newspost.getContent()).split("\n");
        for(int i = 0; i < article.length; i++){
            player.getActionSender().sendString(36023 + i, article[i]);
        }
        for(int i = article.length; i <= 200; i++){
            player.getActionSender().sendString(36023 + i, "");
        }
        return true;
    }
}
