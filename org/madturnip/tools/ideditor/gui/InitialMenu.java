package org.madturnip.tools.ideditor.gui;

import org.madturnip.tools.ideditor.FileOps;
import org.madturnip.tools.ideditor.Utilities;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
/**
 * The dialog that asks whether you're using the .cfg or not.
 * For the dialogs, there is less documentation.  Most of it would just repeat.
 * Check FileOps or Interface if you're really curious.
 */
public class InitialMenu extends JDialog {

	private JDialog initialMenu;
	private JLabel label;
	private JCheckBox checkBox;
	private JButton proceedButton;

	/**
	 * Creates the initial dialog.  Used for selecting what loading system to use.
	 */
	public InitialMenu() {
		init();
		initialMenu.setVisible(true);
	}

	private void init() {
		initialMenu = new JDialog();
		label = new JLabel("Using file with bonuses included?");
		checkBox = new JCheckBox("Yes, I am.");
		proceedButton = new JButton("Proceed");

		proceedButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				proceedPress(evt);
			}
		});

		checkBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				checkBoxAction(evt);
			}
		});

		javax.swing.GroupLayout jDialog3Layout = new javax.swing.GroupLayout(initialMenu.getContentPane());
		initialMenu.getContentPane().setLayout(jDialog3Layout);
		jDialog3Layout.setHorizontalGroup(
				jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jDialog3Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(label, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(checkBox, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(proceedButton))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		jDialog3Layout.setVerticalGroup(
				jDialog3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jDialog3Layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(label)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(checkBox)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(proceedButton)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		initialMenu.setSize(250, 135);

		pack();
	}

	private void proceedPress(ActionEvent evt) {
		new FileOps();
		initialMenu.dispose();
	}

	private void checkBoxAction(ActionEvent evt) {
		Utilities.bonusFile = checkBox.getSelectedObjects() == null ? false : true;
	}

}
