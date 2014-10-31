package org.madturnip.tools;

import org.madturnip.tools.SQLAdminManager.AdminClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

public class SQLAdminClient implements KeyListener {


	public static TextArea con;
	public static TextField field = new TextField(40);
	public static JFrame frame;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		frame = new JFrame("Server Controler");
		frame.setLayout(new BorderLayout());
		frame.setResizable(true);
		//frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JPanel console = new JPanel();
		console.setLayout(new BorderLayout());
		con = new TextArea("", 15, 80, TextArea.SCROLLBARS_VERTICAL_ONLY);
		con.setEditable(false);
		con.setBackground(Color.black);
		con.setForeground(Color.white);
		console.add(con, BorderLayout.NORTH);
		console.add(field, BorderLayout.SOUTH);

		frame.getContentPane().add(console, BorderLayout.CENTER);
		//frame.getContentPane().add(tabs, BorderLayout.SOUTH);
		frame.pack();
		frame.setVisible(true);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		field.addKeyListener(new SQLAdminClient());
		try {
			Socket socket = new Socket("69.65.41.217", 43600);
			reference = new AdminClient(socket);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static AdminClient reference;

	public static void append(String substring) {
		con.append(substring + "\n");
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent keyevent) {
		int i = keyevent.getKeyCode();
		if(i == 10) {
			try {
				reference.out.write(field.getText());
				reference.out.newLine();
				reference.socket.getOutputStream().flush();
				field.setText("");
			} catch(IOException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}


}
