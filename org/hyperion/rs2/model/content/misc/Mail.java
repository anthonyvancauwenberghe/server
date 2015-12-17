package org.hyperion.rs2.model.content.misc;

import org.hyperion.Server;
import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.Player;
import org.hyperion.rs2.model.Rank;
import org.hyperion.rs2.model.World;
import org.hyperion.rs2.sql.requests.MailRequest;
import org.hyperion.util.Misc;

/**
 * @author Arsen Maxyutov.
 */
public class Mail {

	public static final String[] ALLOWEDHOSTS = {
			"hotmail", "live", "gmail", "yahoo"
	};

	public static final char[] ALLOWEDCHARACTERS = {
			'_', '-', 'a', 'b', 'c',
			'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
			'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9', '@', '.'
	};

	public Mail(Player owner) {
		player = owner;
	}

	private String mail = "";

	private String tempmail = "";

	private Player player;

	public void setTempmail(String tempmail) {
		if(this.mail.length() > 1) {
			player.getActionSender().sendMessage("Mail already set.");
			return;
		}
		if(tempmail.equals(this.tempmail)) {
			setMail(tempmail);
			return;
		}
		this.tempmail = tempmail;
		player.getActionSender().sendMessage("Please re-enter your e-mail.");
	}

    public void setMail(String mail, final boolean showMsg){
        if(mail == null)
            mail = "";
        mail = mail.toLowerCase();
        char[] charArray = mail.toCharArray();
        for(int i = 0; i < charArray.length; i++) {
            if(! Misc.contains(charArray[i], ALLOWEDCHARACTERS))
                return;
        }
        if(mail.length() < 3) {
            if(showMsg)
                player.getActionSender().sendMessage("Entered e-mail is too short.");
            return;
        }
        if(! mail.contains(".") || ! mail.contains("@")) {
            if(showMsg)
                player.getActionSender().sendMessage("Entered e-mail is invalid.");
            return;
        }
        String host = mail.split("@")[1];
        for(String s : ALLOWEDHOSTS) {
            if(host.contains(s)) {
                this.mail = mail;
				if (Server.getConfig().getBoolean("logssql"))
					World.getWorld().getLogsConnection().offer(new MailRequest(player));
                return;
            }
        }
        if(showMsg)
            player.getActionSender().sendMessage("Invalid host.");
    }

	public void setMail(String mail) {
		setMail(mail, true);
	}

	public String toString() {
		if(mail == null || mail.equals(""))
			return "";
		else
			return mail;
	}

	static {
		CommandHandler.submit(new Command("getmail", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				System.out.println("Mail:" + player.getMail());
				return true;
			}
		});
		CommandHandler.submit(new Command("setmail", Rank.PLAYER) {
			@Override
			public boolean execute(Player player, String input) {
				input = input.replaceAll("setmail ", "");
				player.getMail().setTempmail(input);
				//System.out.println(input);
				return true;
			}
		});
	}

}
