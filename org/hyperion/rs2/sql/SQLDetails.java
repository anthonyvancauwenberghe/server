package org.hyperion.rs2.sql;

/**
 * @author Arsen Maxyutov.
 */
public class SQLDetails {

	/**
	 * The URL
	 */
	private String URL;

	/**
	 * The username.
	 */
	private String username;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the uRL
	 */
	public String getURL() {
		return URL;
	}

	/**
	 * @param uRL the uRL to set
	 */
	public void setURL(String uRL) {
		URL = uRL;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Checks if the SQL details are configured.
	 *
	 * @return
	 */
	public boolean isSet() {
		if(password == null)
			return false;
		if(URL == null)
			return false;
		if(username == null)
			return false;
		return true;
	}

}
