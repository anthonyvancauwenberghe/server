package org.hyperion;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arsen Maxyutov.
 */
public class Configuration {

	/**
	 * The file from which the configurations are loaded.
	 */
	public static final File CONFIG_FILE = new File("./config.cfg");

	/**
	 * Holds all configurations..
	 */
	private Map<String, String> configs = new HashMap<String, String>();

	/**
	 * Sets the default configurations.
	 */
	public void setDefaultConfigs() {
		configs.put("port", "43599");
		configs.put("version", "317");
		configs.put("minplayers", "0");
		configs.put("owner", "graham");
		configs.put("name", "Hyperion");
		configs.put("spawncommand", "adminspawn");
		configs.put("localhost", "false");
		configs.put("restarterthread", "false");
		configs.put("donating", "false");
		configs.put("voting", "false");
		configs.put("mail", "false");
		configs.put("highscores", "false");
		configs.put("spawn", "false");
	}

	/**
	 * (Re)Loads the configuration file.
	 */
	public void loadConfigFile() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
			String line;
			while((line = in.readLine()) != null) {
				if(line.startsWith("//") || line.length() <= 1)
					continue;
				String[] parts = line.split("::");
				configs.put(parts[0], parts[1]);
			}
			in.close();
		} catch(FileNotFoundException e) {
			System.out.println("Config file was not found! Loading default settings");
			System.out.println(CONFIG_FILE.getAbsolutePath());
		} catch(IOException e) {
			System.out.println("IOException in Configuration loading!");
		} catch(Exception e) {
			System.out.println("Config file could not be parsed. Shutting down server");
			System.exit(0);
		}
	}

	/**
	 * Constructs a new configuration.
	 */
	public Configuration() {
		setDefaultConfigs();
		loadConfigFile();
	}

	/**
	 * Gets the String for the specified key.
	 *
	 * @param key
	 * @return String value
	 */
	public String getString(String key) {
		String value = configs.get(key);
		if(value == null) return "";
		//System.out.println("Loading key " + key + " : " + value);
		return value;
	}

	/**
	 * Gets the boolean for the specified key.
	 *
	 * @param key
	 * @return the boolean value for the key, false if no such key exists.
	 */
	public boolean getBoolean(String key) {
		String value = configs.get(key);
		if(value == null) return false;
		return Boolean.parseBoolean(value);
	}

	/**
	 * Gets the Integer value for the specified key.
	 *
	 * @param key
	 * @return Integer value
	 */
	public int getInteger(String key) {
		String value = configs.get(key);
		if(value == null) return 0;
		return Integer.parseInt(value);
	}

	static {

	}

}
