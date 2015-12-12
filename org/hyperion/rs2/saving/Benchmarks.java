package org.hyperion.rs2.saving;

import java.io.BufferedReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

public class Benchmarks {

    /**
     * @param args
     * @throws Exception
     */
    public static void main(final String[] args) throws Exception {
        final List<Long> deltas = new LinkedList<Long>();
        final List<String> messages = getList();
        final int iterations = 100000;
        final int size = 16 * 512;
        for(int i = 0; i < iterations; i++){
            final long delta = getDelta(messages, size);
            deltas.add(delta);
        }

        long total = 0;
        for(final long delta : deltas){
            total += delta;
        }
        System.out.println("Delta with size " + size + ", " + total / iterations);
    }

    public static long getDelta(final List<String> messages, final int size) throws Exception {
        final long start = System.nanoTime();
        final File file = new File("C:/data/io/test" + 10 + ".txt");
        final CharArrayWriter out = new CharArrayWriter(1024);
        for(final String message : messages){
            out.write(message);
        }
        out.close();
        final FileWriter bw = (new FileWriter(file));
        bw.write(out.toCharArray());
        bw.close();
        final long delta = System.nanoTime() - start;
        //file.delete();
        return delta;
    }

    public static List<String> getList() {
        final List<String> lines = new LinkedList<String>();
        try{
            final BufferedReader in = new BufferedReader(new FileReader("./data/characters/graham.txt"));
            String line;
            while((line = in.readLine()) != null){
                lines.add(line);
            }
            in.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
        return lines;
    }
}
