package org.madturnip.tools.ideditor;

import java.nio.ByteBuffer;

/**
 * This is a utility class for the program.
 * Since readString is directly from Hyperion, credits to Graham for that.
 *
 * @author Blake
 */
public class Utilities {

	/**
	 * The int used to control the size of everything.
	 */
	public static int arraySize;

	/**
	 * Whether or not we're using the file modified for bonuses.
	 */
	public static boolean bonusFile = false;

	/**
	 * Limit to how negative a stat can be, for reading.
	 */
	public static final int negativeThreshold = 100;

	/**
	 * @param buffer The ByteBuffer we're reading.
	 * @return A completed String.
	 * @author Graham Edgecombe
	 */
	public static String readString(ByteBuffer buffer) {
		StringBuilder bldr = new StringBuilder();
		while(buffer.hasRemaining()) {
			byte b = buffer.get();
			if(b == 0) {
				break;
			}
			bldr.append((char) b);
		}
		return bldr.toString();
	}

	/**
	 * Takes a String and returns the byte array for it.
	 *
	 * @param text The String to write.
	 * @return The array of bytes for the String.
	 */
	public static byte[] writeBytes(String text) {
		return text.getBytes();
	}

}
