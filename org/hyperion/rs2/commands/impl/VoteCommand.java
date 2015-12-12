package org.hyperion.rs2.commands.impl;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;

public class VoteCommand extends Command {


    //private static final String RS_HIGHSCORE_FORMAT_URL = "http://services.runescape.com/m=hiscore/ranking?category_type=0&table=0&time_filter=0&date=1407429134912&page=%d";


    public VoteCommand() {
        super("vote", Rank.PLAYER);
    }

    @Override
    public boolean execute(final Player player, final String input) throws Exception {
        try{

            player.getActionSender().sendWebpage("http://vote.arteropk.com/index.php?toplist_id=0&username=" + player.getName());
        }catch(final Exception e){
            e.printStackTrace();
        }
        return true;
    }




    /*private static void writeUsernames(final int page, final Object lock, final BufferedWriter writer) throws Exception{
        final long start = System.currentTimeMillis();
        final URL url = new URL(String.format(RS_HIGHSCORE_FORMAT_URL, page));
        final URLConnection con = url.openConnection();
        con.setReadTimeout(TIMEOUT);
        con.setConnectTimeout(TIMEOUT);
        con.setRequestProperty("User-Agent", USER_AGENT);
        final Scanner input = new Scanner(con.getInputStream(), "UTF-8");
        while(input.hasNextLine()){
            if(!input.nextLine().startsWith("<img class=\"avatar\" src="))
                continue;
            synchronized(lock){
                writer.write(input.nextLine());
                writer.flush();
                writer.newLine();
            }
        }
        input.close();
        System.out.printf("Scraped 25 usernames from page %d in %d ms\n", page, System.currentTimeMillis() - start);
    }

    private static void writeUsernames(final int pages) throws Exception{
        final Object lock = new Object();
        final ExecutorService service = Executors.newFixedThreadPool(10);
        if(!FILE.exists())
            FILE.createNewFile();
        final BufferedWriter writer = new BufferedWriter(new FileWriter(FILE));
        for(int i = 1; i <= pages; i++){
            final int page = i;
            service.execute(
                    new Runnable(){
                        public void run(){
                            try {
                                writeUsernames(page, lock, writer);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }finally{
                                if(page == pages){
                                    try {
                                        writer.flush();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        writer.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
            );
        }
        service.shutdown();
    }

    public static void main(String[] args) throws Exception{
        writeUsernames(100);
    }*/
}
