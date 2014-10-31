package GraphDrawing;

/**
 * @author Arsen Maxyutov
 */
public class CoordinatePoint {

	public int minutes;
	public int players;

	public CoordinatePoint(int players, int minutes) {
		this.minutes = minutes;
		this.players = players;
	}

	public int getY() {
		double ratio = (double) players / (double) (maxPlayers * 1.2);
		int y = (int) (ratio * DrawPlayersGraph.HEIGHT);
		if(y == DrawPlayersGraph.HEIGHT)
			y -= 1;
		return DrawPlayersGraph.HEIGHT - y;
	}

	public int getX() {
		double ratio = (double) (minutes - minMinutes) / (double) (maxMinutes - minMinutes);
		if(DrawPlayersGraph.START_FROM_ZERO) {
			ratio = (double) minutes / (double) maxMinutes;
		}
		int x = (int) (ratio * DrawPlayersGraph.WIDTH);
		if(x == DrawPlayersGraph.WIDTH)
			x -= 1;
		return x;
	}

	public static void updateMaxima(int maxplayers, int maxminutes, int minminutes) {
		maxPlayers = maxplayers;
		maxMinutes = maxminutes;
		minMinutes = minminutes;
	}

	public static int maxPlayers;

	public static int maxMinutes;

	public static int minMinutes;

}
