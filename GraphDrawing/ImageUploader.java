package GraphDrawing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @authors Martin Chapman And Arsen Maxyutov.
 */

public class ImageUploader {

	public static String apiKey = "74f12e8a6e58b661664b6e5dacdb5d0b";

	public static class UploaderRunnable implements Runnable {

		private BufferedImage image;

		public UploaderRunnable(BufferedImage image) {
			this.image = image;
		}

		public void run() {
			try {
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				ImageIO.write(image, "PNG", output);
				FileOutputStream bw = new FileOutputStream("LastGraph.png");
				bw.write(output.toByteArray());
				bw.flush();
				bw.close();

				/**
				 * Create the output stream and input stream
				 */
				URL url = new URL("http://api.imgur.com/2/upload.xml");

				/**
				 * Encode the image into a base64 string using apache commons codec
				 */
				String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encodeBase64String(output.toByteArray()), "UTF-8");
				data += "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode(apiKey, "UTF-8");
				URLConnection connection = url.openConnection();
				connection.setDoOutput(true);
				/**
				 * Write the image data and api key
				 */
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(data);
				writer.flush();
				writer.close();

				/**
				 * Parse the URL from the response
				 */
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String imgurl = "";
				String line = "";
				while((line = in.readLine()) != null) {
					if(line.contains("<original>")) {
						int index = line.indexOf("<original>");
						line = line.substring(index, line.length());
						line = line.replace("<original>", "");
						index = line.indexOf("<");
						line = line.replace(line.substring(index, line.length()), "");
						imgurl = line;
						break;
					}
				}
				loadPage(imgurl);
				setClipboard(imgurl);
				//System.exit(0);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void loadPage(String imgurl) {
		try {
			URL url = new URL("http://www.eypic.net/upload.php?url=" + imgurl);
			HttpURLConnection conn = (HttpURLConnection) url
					.openConnection();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String eypicURL = reader.readLine();
			launchURL(eypicURL);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void launchURL(String url) {
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private static ExecutorService uploadService = Executors.newSingleThreadExecutor();

	public static void upload(BufferedImage image) {
		System.out.println("Starting Upload");
	    /*Toolkit toolkit = Toolkit.getDefaultToolkit()
	    Dimension oScreenSize = toolkit.getScreenSize();
	    Rectangle oScreen = new Rectangle(oScreenSize);
	 
	    // Create screen shot
	    Robot robot;
		try {
			robot = new Robot();

		    BufferedImage oImage = robot.createScreenCapture(oScreen);
			uploadService.execute(new UploaderRunnable(oImage));
		} catch (AWTException e) {
			e.printStackTrace();
		}*/
		uploadService.execute(new UploaderRunnable(image));
	}

	public static void setClipboard(String nodeValue) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(nodeValue);
		clipboard.setContents(transferable, null);
	}
}
