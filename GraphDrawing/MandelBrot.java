package GraphDrawing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author Arsen Maxyutov.
 */
public class MandelBrot {

	public static void main(String[] args) {
		new MandelBrot();
	}

	public static final int WIDTH = 10000;
	public static final int HEIGHT = 10000;

	public static final int MAX_ITERATIONS = 200;

	public static final int POWER = 2;

	public MandelBrot() {
		BufferedImage bi = new BufferedImage(WIDTH, HEIGHT, 1);
		/**
		 * Draw pixel black if it doesn't diverge.
		 */
		for(int i = 0; i < WIDTH; i++) {
			if(i % 100 == 0)
				System.gc();
			for(int j = 0; j < HEIGHT; j++) {
				ComplexNumber z = convertCoordinateToComplexNumber(i, j);
				//System.out.println(z);
				if(z.diverges())
					bi.setRGB(i, j, z.getRGB());
			}
		}
		try {
			ImageIO.write(bi, "png", new File("C:/data/mand.png"));
		} catch(IOException e) {
			e.printStackTrace();
		}
		//ImageUploader.upload(bi);
	}

	private ComplexNumber convertCoordinateToComplexNumber(double x, double y) {
		y -= HEIGHT / 2;
		y /= HEIGHT / 4;
		x -= WIDTH / 2;
		x /= WIDTH / 4;
		return new ComplexNumber(x, y);
	}

	public int getRGB(int red, int green, int blue) {
		return red * 256 * 256 + green * 256 + blue;
	}

}
