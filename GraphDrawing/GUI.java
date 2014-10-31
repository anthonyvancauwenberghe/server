package GraphDrawing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

	public static void main(String[] args) {
		GUI gui = new GUI();
		gui.setSize(200, 200);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.setVisible(true);
	}

	private JButton submitButton;

	public GUI() {
		super("ScreenshotTaker");
		setLayout(new FlowLayout());
		getContentPane().setBackground(Color.DARK_GRAY);
		ActionHandler ahandler = new ActionHandler();
		submitButton = new JButton("Take Screenshot");
		submitButton.addActionListener(ahandler);
		add(submitButton);
	}

	private class ActionHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == submitButton) {
				try {
					ImageUploader.upload(new ScreenShotTaker().getImage());
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		}

	}
}
