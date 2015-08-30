package org.hyperion.rs2;

import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;

/**
 * An interface which describes the methods for loading persistent world
 * information such as players.
 *
 * @author Graham Edgecombe
 */
public interface WorldLoader {


	/**
	 * Represents the result of a login request.
	 *
	 * @author Graham Edgecombe
	 */
	public static class LoginResult {

		public static final int EXCHANGES_DATA = 0;

		public static final int WAIT_AND_TRY_AGAIN = 1;

		public static final int SUCCESSFUL_LOGIN = 2;

		public static final int INVALID_USER_OR_PASS = 3;

		public static final int ACCOUNT_DISABLED = 4;

		public static final int ALREADY_LOGGED_IN = 5;

		public static final int SERVER_UPDATED = 6;

		public static final int WORLD_FULL = 7;

		public static final int UNABLE_TO_CONNECT = 8;

		public static final int LOGIN_LIMIT_EXCEEDED = 9;

		public static final int BAD_SESSION_ID = 10;

		public static final int LOGIN_SERVER_REJECTED = 11;

		public static final int MEMBERS_ONLY = 12;

		public static final int COULD_NOT_COMPLETE = 13;

		public static final int UPDATE_IN_PROGRESS = 14;

		public static final int LOGIN_ATTEMPTS_EXCEEDED = 16;

		/**
		 * The return code.
		 */
		private int returnCode;

		/**
		 * The player object, or <code>null</code> if the login failed.
		 */
		private Player player;

		/**
		 * Creates a login result that failed.
		 *
		 * @param returnCode The return code.
		 */
		public LoginResult(int returnCode) {
			this(returnCode, null);
		}

		/**
		 * Creates a login result that succeeded.
		 *
		 * @param returnCode The return code.
		 * @param player     The player object.
		 */
		public LoginResult(int returnCode, Player player) {
			this.returnCode = returnCode;
			this.player = player;
		}

		/**
		 * Gets the return code.
		 *
		 * @return The return code.
		 */
		public int getReturnCode() {
			return returnCode;
		}

		/**
		 * Gets the player.
		 *
		 * @return The player.
		 */
		public Player getPlayer() {
			return player;
		}

	}

	/**
	 * Checks if a set of login details are correct. If correct, creates but
	 * does not load, the player object.
	 *
	 * @param pd The login details.
	 * @return The login result.
	 */
	public LoginResult checkLogin(PlayerDetails pd);

	/**
	 * Loads player information.
	 *
	 * @param player The player object.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	public boolean loadPlayer(Player player);

	/**
	 * Saves player information.
	 *
	 * @param player The player object.
	 * @return <code>true</code> on success, <code>false</code> on failure.
	 */
	public boolean savePlayer(Player player, String info);

}
