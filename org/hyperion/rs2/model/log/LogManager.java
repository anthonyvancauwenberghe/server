package org.hyperion.rs2.model.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class LogManager {

    private static final File DIR = new File(".", "logs2");

    static{
        if(!DIR.exists())
            DIR.mkdir();
    }

    private final String name;
    private final File dir;

    private final Map<LogEntry.Category, Set<LogEntry>> logs;
    private final Map<LogEntry.Category, Boolean> loaded;

    public LogManager(final String name){
        this.name = name;

        dir = new File(DIR, name.toLowerCase());

        logs = new HashMap<>();

        loaded = new HashMap<>();
    }

    public void clear(){
        logs.clear();
    }

    public void add(final LogEntry log){
        if(!logs.containsKey(log.category))
            logs.put(log.category, new TreeSet<>());
        logs.get(log.category).add(log);
    }

    public Set<LogEntry> getLogs(final LogEntry.Category category, final long startTime){
        if(!loaded.getOrDefault(category, false))
            load(category);
        final Set<LogEntry> logs = this.logs.get(category);
        if(logs == null)
            return null;
        return startTime == -1 ? logs : logs.stream().filter(
                l -> l.date.getTime() >= startTime
        ).collect(Collectors.toCollection(TreeSet::new));
    }

    public Set<LogEntry> getLogs(final LogEntry.Category category){
        return getLogs(category, -1);
    }

    private void load(final LogEntry.Category category){
        final File file = new File(dir, category.path);
        if(!file.exists()){
            loaded.put(category, true);
            return;
        }
        try(final Scanner input = new Scanner(file, "UTF-8")){
            while(input.hasNextLine()){
                final String line = input.nextLine().trim();
                if(line.isEmpty())
                    continue;
                try{
                    final LogEntry log = LogEntry.parse(line);
                    add(log);
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            loaded.put(category, true);
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void clearExpiredLogs(){
        final long expired = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30);
        for(final Set<LogEntry> logs : this.logs.values()){
            final Iterator<LogEntry> itr = logs.iterator();
            while(itr.hasNext()){
                final LogEntry log = itr.next();
                if(log.date.getTime() < expired)
                    itr.remove();
                else
                    break;
            }
        }
    }

    public void save(){
        if(!dir.exists())
            dir.mkdir();
        for(final Map.Entry<LogEntry.Category, Set<LogEntry>> entry : logs.entrySet()){
            final File file = new File(dir, entry.getKey().path);
            try(final BufferedWriter writer = new BufferedWriter(new FileWriter(file))){
                for(final LogEntry log : entry.getValue()){
                    writer.write(log.toString());
                    writer.newLine();
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
}
