package GraphDrawing;

import java.io.*;
import java.util.LinkedList;

public class CleanFile {

	private static final File PLAYERCOUNT_LOG_FILE = new File("C:/Users/SaosinHax/Dropbox/New folder/RecklessPk/data/playercountlog.txt");
	private static final File PLAYERCOUNT_OUTPUT_FILE = new File("C:/Users/SaosinHax/Dropbox/New folder/RecklessPk/data/output.txt");

	private LinkedList<String> lines = new LinkedList<String>();

	private int counter = 0;

	private void loadLines() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(PLAYERCOUNT_LOG_FILE));
		String line;
		while((line = br.readLine()) != null) {
			counter++;
			if(counter % 3 == 0)
				lines.add(line);
		}
		br.close();
	}

	private void writeLines() throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(PLAYERCOUNT_OUTPUT_FILE));
		for(String line : lines) {
			bw.write(line);
			bw.newLine();
		}
		bw.close();
	}

	public CleanFile() throws IOException {
		loadLines();
		writeLines();
	}

	public static void main(String[] args) {
		try {
			new CleanFile();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
