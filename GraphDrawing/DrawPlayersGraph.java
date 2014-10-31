package GraphDrawing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Arsen Maxyutov
 */

public class DrawPlayersGraph {

	public static final int INTERVAL = 24 * 60 * 3; //In Minutes

	private ArrayList<CoordinatePoint> coordinates = new ArrayList<CoordinatePoint>();

	public static final int HEIGHT = 400;
	public static final int WIDTH = 4000;

	public static final boolean START_FROM_ZERO = false;

	public static final Font TITLEFONT = new Font(null, Font.BOLD, 15);
	public static final Font RECORDFONT = new Font(null, 0, 20);

	public static final String DATE_STRING = "Starting 18 November, 10 PM";

	private Font defaultFont = null;

	public static void main(String[] args) {
		new DrawPlayersGraph();
	}

	public DrawPlayersGraph() {
		//loadData();
		loadMaxima();
		writeImage();
	}

	private void loadMaxima() {
		CoordinatePoint.updateMaxima(getMaxPlayers(), getMaxTime(), getMinTime());
	}

	private void writeImage() {
		try {
			BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, 1);
			setWhiteBackground(bi);
			Graphics gc = bi.createGraphics();
			gc.setColor(new Color(0));
			gc.drawRect(0, 0, 100, 100);
	        /*defaultFont = gc.getFont();
			gc.setColor(new Color(0));
			for(int i = 0; i < coordinates.size() - 1;){
				gc.drawLine(coordinates.get(i).getX(), coordinates.get(i).getY(), coordinates.get(i++).getX(), coordinates.get(i).getY());
			}
			drawHeader(gc);
			gc.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);
			drawYaxis(gc);
			drawXaxis(gc);
			drawAverageLine(gc);
			gc.drawImage(bi, null, 0, 0);*/
			//ImageUploader.upload(bi);
			ImageIO.write(bi, "png", new File(System.getProperty("user.home") + "/" + new Random().nextInt() + ".png"));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void setWhiteBackground(BufferedImage bi) {
		for(int i = 0; i < bi.getHeight(); i++) {
			for(int j = 0; j < bi.getWidth(); j++) {
				bi.setRGB(j, i, 16777215); // Setting white
			}
		}
	}

	private void drawYaxis(Graphics2D gc) {
		for(int i = 0; i < CoordinatePoint.maxPlayers; i += 25) {
			int y = getYForPlayercount(i);
			if(y == - 1)
				continue;
			gc.drawString(i + "", 2, y + 2);
		}
		gc.setColor(new Color(11674146));
		gc.setFont(RECORDFONT);
		gc.drawString(CoordinatePoint.maxPlayers + "", 2, getYForPlayercount(CoordinatePoint.maxPlayers));
	}

	private void drawHeader(Graphics2D gc) {
		drawTitle(gc);
		gc.setFont(defaultFont);
		gc.drawString(DATE_STRING, (int) (WIDTH * 0.3), (int) (HEIGHT * 0.08));
		gc.setColor(new Color(205));
		gc.drawString("Average : " + getAverage() + " players.", (int) (WIDTH * 0.3), (int) (HEIGHT * 0.12));
		gc.setColor(new Color(0));
	}

	private void drawAverageLine(Graphics2D gc) {
		int y = getYForPlayercount(getAverage());
		int x = getXForMinutes(CoordinatePoint.minMinutes);
		gc.setColor(new Color(205));
		gc.drawLine(x, y, WIDTH, y);
	}

	private void drawTitle(Graphics2D gc) {
		gc.setFont(TITLEFONT);
		gc.drawString("Playercount in function of time", (int) (WIDTH * 0.3), (int) (HEIGHT * 0.05));
	}

	private void drawXaxis(Graphics2D gc) {
		gc.setColor(new Color(0));
		gc.setFont(defaultFont);
		int unit = findBestUnit();
		for(int i = CoordinatePoint.minMinutes; i < CoordinatePoint.maxMinutes; i += INTERVAL) {
			int x = getXForMinutes(i);
			int tries = 0;
			while(x == - 1) {
				x = getXForMinutes(i++);
				tries++;
				if(tries > 10)
					continue;
			}
			int value = Time.getTime(i)[unit];
			if(unit == 1 && value > 12)
				value -= 12;
			String unitStr = getUnit(unit, i);
			String toDraw = value + unitStr;
			gc.drawString(toDraw, x, (int) (HEIGHT * 0.95));
		}
	}

	private int findBestUnit() {
		int unit = 2;
		/*int[] begindate = Time.getTime(CoordinatePoint.minMinutes);
		int[] enddate = Time.getTime(CoordinatePoint.maxMinutes);
		int unit = begindate.length - 1;
		for (; unit >= 0; unit--) {
			if (Math.abs(begindate[unit] - enddate[unit]) > 5)
				break;
			if (begindate[unit] != enddate[unit]) {
				unit--;
				break;
			}
		}*/
		return unit;
	}

	private int getYForPlayercount(int players) {
		for(CoordinatePoint cp : coordinates) {
			if(cp.players == players)
				return cp.getY();
		}
		return - 1;
	}

	private String getUnit(int unit, int value) {
		if(unit == 1) {
			if(value > 12)
				return "PM";
			return "AM";
		}
		if(unit == 2) {
			int u = Time.getTime(value)[3];
			return "/" + u;
		}
		return getUnit(unit);
	}

	private String getUnit(int unit) {
		switch(unit) { // mins,hours,days,month,year
			case 0:
				return "Mins";
			case 2:
				return "Day";
			case 3:
				return "Months";
			case 4:
				return "Years";
		}
		return "";
	}

	private int getXForMinutes(int minutes) {
		for(CoordinatePoint cp : coordinates) {
			if(cp.minutes == minutes)
				return cp.getX();
		}
		return - 1;
	}

	private void loadData() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("C:/Users/SaosinHax/Dropbox/New folder/RecklessPk/data/output.txt"));
			String s = "";
			while((s = br.readLine()) != null) {
				//System.out.println(s);
				if(s.startsWith("--")) {
					//System.out.println("FOUND --");
					//coordinates.clear();
					continue;
				}
				String[] parts = s.split(",");
				int players = Integer.parseInt(parts[0]);
				int minutes = Integer.parseInt(parts[1]);
				coordinates.add(new CoordinatePoint(players, minutes));
			}
			br.close();
			System.out.println(coordinates.size() + " Measurements loaded!");
		} catch(Exception e) {
			e.printStackTrace();
		}

	}

	private int getMaxPlayers() {
		int max = 0;
		for(CoordinatePoint cp : coordinates) {
			if(cp.players > max)
				max = cp.players;
		}
		return max;
	}

	private int getMaxTime() {
		int max = 0;
		for(CoordinatePoint cp : coordinates) {
			if(cp.minutes > max)
				max = cp.minutes;
		}
		return max;
	}

	private int getMinTime() {
		int min = Integer.MAX_VALUE;
		for(CoordinatePoint cp : coordinates) {
			if(cp.minutes < min)
				min = cp.minutes;
		}
		return min;
	}

	private int getAverage() {
		int counter = 0;
		for(CoordinatePoint cp : coordinates) {
			counter += cp.players;
		}
		if(coordinates.size() != 0)
			return counter / coordinates.size();
		return 1;
	}
}
