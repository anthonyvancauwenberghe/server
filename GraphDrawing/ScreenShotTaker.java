package GraphDrawing;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ScreenShotTaker {

	private BufferedImage image;

	public BufferedImage getImage() {
		return image;
	}

	public ScreenShotTaker() throws Exception {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle screenRectangle = new Rectangle(screenSize);
		Robot robot = new Robot();
		image = robot.createScreenCapture(screenRectangle);
	}

}