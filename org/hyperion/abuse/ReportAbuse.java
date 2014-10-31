package org.hyperion.abuse;

import org.hyperion.rs2.model.Location;
import org.hyperion.rs2.model.Player;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ReportAbuse {// extends Thread {

	public static class Abuse {
		private long timeStamp;
		private String message;
		private int xCoord;
		private int yCoord;
		private int zCoord;

		public Abuse(String message, int xCoord, int yCoord, int zCoord) {
			this.timeStamp = System.currentTimeMillis();
			this.message = message;
			this.xCoord = xCoord;
			this.yCoord = yCoord;
			this.zCoord = zCoord;
		}

		public long getTime() {
			return timeStamp;
		}

		public String getMessage() {
			return message;
		}

		public int xCoord() {
			return xCoord;
		}

		public int yCoord() {
			return yCoord;
		}

		public int zCoord() {
			return zCoord;
		}
	}

	private List<Abuse> cachedMessages = new LinkedList<Abuse>();

	private void cacheMessage(Player player, String message) {
		cachedMessages.add(new Abuse(message, player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
	}

	private boolean reportAbuse(Player player, String reported, int rule) {
		try {
			String filename = new Date() + "";
			filename = filename.replace(":", "-");
			BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter("./logs/abuse/" + filename + ".txt", true));
			writeToFile("", bufferedwriter);
			writeToFile("" + new Date(), bufferedwriter);
			writeToFile(reported, bufferedwriter);
			writeToFile(player.getName(), bufferedwriter);
			writeToFile(player.getLocation().getX() + ":" + player.getLocation().getY() + ":" + player.getLocation().getZ(), bufferedwriter);
			writeToFile("", bufferedwriter);
			writeToFile("", bufferedwriter);
			writeToFile("", bufferedwriter);
			for(Abuse a : cachedMessages) {
				Location location = Location.create(a.xCoord(), a.yCoord(), a.zCoord());
				if(location == null) {
					System.out.println("Location is null");
				}
				if(player.getLocation() == null) {
					System.out.println("Player Location is null");
				}
				if(player.getLocation().isWithinDistance(location, 16) || a.getMessage().contains(reported)) {
					writeToFile(a.getMessage(), bufferedwriter);
				}
			}
			bufferedwriter.flush();
			bufferedwriter.close();
		} catch(IOException ioexception) {
			ioexception.printStackTrace();
			System.out.println("Critical error while writing log file!");
		}
		return true;
	}

	private void writeToFile(String message, BufferedWriter bufferedwriter) {
		try {
			bufferedwriter.write(message);
			bufferedwriter.newLine();
		} catch(IOException ioexception) {
			ioexception.printStackTrace();
			System.out.println("Critical error while writing log file!");
		}
	}

	private void clearOutList() {
		long time = System.currentTimeMillis();
		for(Iterator<Abuse> it$ = cachedMessages.iterator(); it$.hasNext(); )
			if((it$.next().getTime() + 180000) < time)
				it$.remove();
	}

	private ReportAbuse() {

	}

	private void run() {
		try {
			while(true) {
				Thread.sleep(1000);
				clearOutList();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


}
