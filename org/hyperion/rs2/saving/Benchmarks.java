package org.hyperion.rs2.saving;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Benchmarks {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<Long> deltas = new LinkedList<Long>();
		List<String> messages = getList();
		int iterations = 100000;
		int size = 16 * 512;
		for(int i = 0; i < iterations; i++) {
			long delta = getDelta(messages, size);
			deltas.add(delta);
		}

		long total = 0;
		for(long delta : deltas) {
			total += delta;
		}
		System.out.println("Delta with size " + size + ", " + total / iterations);
	}

	public static long getDelta(List<String> messages, int size) throws Exception {
		long start = System.nanoTime();
		File file = new File("C:/data/io/test" + 10 + ".txt");
		CharArrayWriter out = new CharArrayWriter(1024);
		for(String message : messages) {
			out.write(message);
		}
		out.close();
		FileWriter bw = (new FileWriter(file));
		bw.write(out.toCharArray());
		bw.close();
		long delta = System.nanoTime() - start;
		//file.delete();
		return delta;
	}

	public static List<String> getList() {
		List<String> lines = new LinkedList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(
					"./data/characters/graham.txt"));
			String line;
			while((line = in.readLine()) != null) {
				lines.add(line);
			}
			in.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return lines;
	}
}
