package org.hyperion.rs2;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.hyperion.rs2.WorldLoader.LoginResult;
import org.hyperion.rs2.model.BanManager;
import org.hyperion.rs2.model.Password;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.PlayerDetails;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.model.punishment.manager.PunishmentManager;
import org.hyperion.rs2.net.LoginDebugger;
import org.hyperion.rs2.saving.MergedSaving;
import org.hyperion.rs2.saving.PlayerSaving;
import org.hyperion.rs2.util.NameUtils;
import org.hyperion.rs2.util.PlayerFiles;
import org.hyperion.rs2.util.TextUtils;

/**
 * An implementation of the <code>WorldLoader</code> class that saves players
 * in binary, gzip-compressed files in the <code>data/players/</code>
 * directory.
 *
 * @author Graham Edgecombe
 */
public class GenericWorldLoader implements WorldLoader {

	public static final int MERGED = 0;
	public static final int ARTERO = 1;
	public static final int INSTANT = 2;

	@Override
	public LoginResult checkLogin(PlayerDetails pd) {
		System.out.println("Received pd login: " + pd.getName() + "/" + pd.getPassword());
		int source = -1;
		Player player = null;
		int code = 2;
		boolean needsNamechange = false;
		boolean newcharacter = true;
		boolean doublechar = false;
		if(World.getWorld().updateInProgress()) {
			code = LoginResult.UPDATE_IN_PROGRESS;
			LoginDebugger.getDebugger().log("Update progress in  Genericworldloader");
		} else if(PunishmentManager.getInstance().isBanned(pd.getName(), pd.IP.split(":")[0], pd.getUID(), pd.specialUid) || ConnectionHandler.blackList.containsKey(pd.IP)) {
			code = 4;
			LoginDebugger.getDebugger().log("Code 4 ban in Genericworldloader");
		} else if(MergedSaving.exists(pd.getName())) {
			String name = pd.getName();
			newcharacter = false;
			try {
				//You will never reach this code if there's double name because one of them will be renamed before it can be saved
				//An account that must be renamed will not be saved until the name is changed
				if(MergedSaving.existsMain(name)) {
					/*
					 * Artero has become Merged and wants to Login with Artero -> Login and let him play
					 * Artero has become Merged and wants to login with Instant -> Login and force to change name
					 * Instant has become merged and wants to login with Artero -> Login and let him play
					 * Instant has become merged adn wants to login with instant -> Login and force to change name
					 */
					Password pass = MergedSaving.getMainPass(name);
					if(pass.getSalt() != null && !pass.getSalt().equalsIgnoreCase("null")) {
						String encryptedPdPass = Password.encryptPassword(pd.getPassword(), pass.getSalt());
						//Compare encrypted passwords
						if(pass.getEncryptedPass().equalsIgnoreCase(encryptedPdPass)) {
							source = MERGED;
						} else {
							code = LoginResult.INVALID_USER_OR_PASS;
						}
					} else {
						if(pass.getRealPassword() == null)
							code = LoginResult.INVALID_USER_OR_PASS;
						else if (!pass.getRealPassword().equalsIgnoreCase(pd.getPassword())) {
							code = LoginResult.INVALID_USER_OR_PASS;
						} else {
							//MEANS everything went very well
							source = MERGED;
						}
					}
					if(code == LoginResult.INVALID_USER_OR_PASS) {
						//Couldnt login on Merged, maybe it can login with Artero or Instant?
						//Merge comes from one source so there have to be two sources for a conflict

						if(MergedSaving.existsArtero(name) && MergedSaving.existsInstant(name)) {
							int arteroPriority = MergedSaving.getArteroPriority(name);
							int instantPriority = MergedSaving.getInstantPriority(name);
							if(instantPriority > arteroPriority) {
								//The merge is from instant, let's check Artero
								String passStr = MergedSaving.getArteroPass(name);
								if(passStr != null && passStr.equalsIgnoreCase(pd.getPassword())) {
									source = ARTERO;
									code = LoginResult.SUCCESSFUL_LOGIN;
									needsNamechange = true;
								} else {
									code = LoginResult.INVALID_USER_OR_PASS;
								}
							} else {
								//The merge is from Artero, let's check Instant
								pass = MergedSaving.getInstantPass(pd.getName());
								if(pass.getSalt() != null && !pass.getSalt().equalsIgnoreCase("null")) {
									//Encrypt pd pass
									String encryptedPdPass = Password.encryptPassword(pd.getPassword(), pass.getSalt());
									//Compare encrypted passwords
									if(pass.getEncryptedPass().equalsIgnoreCase(encryptedPdPass)) {
										source = INSTANT;
										code = LoginResult.SUCCESSFUL_LOGIN;
										needsNamechange = true;
									} else {
										code = LoginResult.INVALID_USER_OR_PASS;
									}
								} else {
									if(pass.getRealPassword() == null)
										code = LoginResult.INVALID_USER_OR_PASS;
									else if (!pass.getRealPassword().equalsIgnoreCase(pd.getPassword())) {
										code = LoginResult.INVALID_USER_OR_PASS;
									} else {
										//MEANS everything went very well
										source = INSTANT;
										code = LoginResult.SUCCESSFUL_LOGIN;
										needsNamechange = true;
									}
								}
							}
						}

					}
				} else {
					/*
					 * There are 4 scenarios:
					 * artero has priority, person logs in with artero acc -> load and let him play
					 * artero has priority, person logs in with instant acc -> ask him to change his name
					 * instant has priority, person logs in with artero acc  -> ask him to change his name
					 * instant has priority, person logs in with instant acc -> load and let him play
					 */
					//determine which file has bigger priority
					int arteroPriority = MergedSaving.getArteroPriority(name);
					int instantPriority = MergedSaving.getInstantPriority(name);
					System.out.println("Instantprior: " + instantPriority + "/artero: " + arteroPriority);
					boolean instant = instantPriority > arteroPriority;
					if(instant) {
						/*
						 * First assume he logs in with InstantPk account
						 */
						Password pass = MergedSaving.getInstantPass(pd.getName());
						if(pass.getSalt() != null && !pass.getSalt().equalsIgnoreCase("null")) {
							//Encrypt pd pass
							String encryptedPdPass = Password.encryptPassword(pd.getPassword(), pass.getSalt());
							//Compare encrypted passwords
							if(pass.getEncryptedPass().equalsIgnoreCase(encryptedPdPass)) {
								source = INSTANT;
								 /*
								  * Check if maybe same pass with Artero Account
								  */
								String passStr = MergedSaving.getArteroPass(name);
								if(passStr != null && passStr.equalsIgnoreCase(pd.getPassword())) {
									doublechar = true;
								}
							} else {
								code = LoginResult.INVALID_USER_OR_PASS;
							}
						} else {
							if(pass.getRealPassword() == null)
								code = LoginResult.INVALID_USER_OR_PASS;
							else if (!pass.getRealPassword().equalsIgnoreCase(pd.getPassword())) {
								code = LoginResult.INVALID_USER_OR_PASS;
							} else {
								//MEANS everything went very well
								source = INSTANT;
								/*
								 * Check if maybe same pass with Artero Account
								 */
								String passStr = MergedSaving.getArteroPass(name);
								if(passStr != null && passStr.equalsIgnoreCase(pd.getPassword())) {
									doublechar = true;
								}
							}
						}
						//if he could not login with his instantpk account, maybe he is trying to login with Artero
						if(code == LoginResult.INVALID_USER_OR_PASS) {
							if(MergedSaving.existsArtero(name)) {
								String password = MergedSaving.getArteroPass(name);
								if(password.equalsIgnoreCase(pd.getPassword())) {
									//TODO ASK PLAYER TO CHANGE HIS USERNAME
									source = ARTERO;
									code = LoginResult.SUCCESSFUL_LOGIN;
									needsNamechange = true;
								}
							}
						}
					} else {
						//Artero Login
						System.out.println("Checking Artero login");
						String passStr = MergedSaving.getArteroPass(name);
						System.out.println("Correct pass is : " + passStr);
						if(passStr != null && passStr.equalsIgnoreCase(pd.getPassword())) {
							source = ARTERO;
							/*
							 * Check Instant Login
							 */
							Password pass = MergedSaving.getInstantPass(pd.getName());
							if(pass.getSalt() != null && !pass.getSalt().equalsIgnoreCase("null")) {
								//Encrypt pd pass
								String encryptedPdPass = Password.encryptPassword(pd.getPassword(), pass.getSalt());
								//Compare encrypted passwords
								if(pass.getEncryptedPass().equalsIgnoreCase(encryptedPdPass)) {
									doublechar = true;
								}
							} else {
								if(pass.getRealPassword() != null) {
									if(pass.getRealPassword().equalsIgnoreCase(pd.getPassword())) {
										doublechar = true;
									}

								}
							}
						} else {
							code = LoginResult.INVALID_USER_OR_PASS;
						}

						//If Artero login didn't work try Instant
						if(code == LoginResult.INVALID_USER_OR_PASS) {
							if(MergedSaving.existsInstant(name)) {
								System.out.println("Couldn't login to priority acc, try secondary");
								Password pass = MergedSaving.getInstantPass(pd.getName());
								if(pass.getSalt() != null && !pass.getSalt().equalsIgnoreCase("null")) {
									//Encrypt pd pass
									String encryptedPdPass = Password.encryptPassword(pd.getPassword(), pass.getSalt());
									//Compare encrypted passwords
									if(pass.getEncryptedPass().equalsIgnoreCase(encryptedPdPass)) {
										//ASK TO CHANGE USERNAME TODO
										code = LoginResult.SUCCESSFUL_LOGIN;
										needsNamechange = true;
										source = INSTANT;
									} else {
										code = LoginResult.INVALID_USER_OR_PASS;
									}
								} else {
									if(pass.getRealPassword() == null)
										code = LoginResult.INVALID_USER_OR_PASS;
									else if (!pass.getRealPassword().equalsIgnoreCase(pd.getPassword())) {
										code = LoginResult.INVALID_USER_OR_PASS;
									} else {
										//ASK TO CHANGE USERNAME TODO
										needsNamechange = true;
										code = LoginResult.SUCCESSFUL_LOGIN;
										source = INSTANT;
									}
								}
							}

						}

					}
				}
			} catch(Exception ex) {
				code = 11;
				ex.printStackTrace();
			}
		}
		LoginDebugger.getDebugger().log("Checking more in genericworldloader");
		//Logical verifications
		if(code == 2) {
			//System.out.println("Creating new player");
			LoginDebugger.getDebugger().log("About to make new player in GWL");
			player = new Player(pd, newcharacter);
			System.out.println("Loaded player");
			player.setSource(source);
			if(source == GenericWorldLoader.ARTERO || source == GenericWorldLoader.INSTANT) {
				player.setInitialSource(source);
			}
			if(needsNamechange) {
				player.setNeedsNameChange(true);
			}
			if(doublechar) {
				player.setDoubleChar(true);
			}
			LoginDebugger.getDebugger().log("Made new player in GWL");
		}
		LoginDebugger.getDebugger().log("Pre checking enabled disabled login debugger");
		if(LoginDebugger.getDebugger().isEnabled()) {
			String name = "unset";
			if(player != null)
				name = player.getName();
			LoginDebugger.getDebugger().log("5. Login Result: " + code + ": " + name);
		}
		return new LoginResult(code, player);
	}

	@Override
	public boolean savePlayer(Player player, String info) {
		if(! NameUtils.isValidName(player.getName())) {
			System.out.println("Trying to save for wrong player: " + player.getName() + "," + info);
		}
		final File file = new File("./data/logSaves.txt");
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		TextUtils.writeToFile(file, String.format("[%s]%s has logged out info: %s",
				new Date(System.currentTimeMillis()).toString(), player.getName(), info));
		PlayerSaving.getSaving().save(player);
		return true;
	}

	@Override
	public boolean loadPlayer(Player player) {
		try {
			if(MergedSaving.exists(player.getName())) {
				MergedSaving.load(player);

			}
			LoginDebugger.getDebugger().log("6. Generic worldloader loaded Player file " + player.getName());
			return true;
		} catch(Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

}
