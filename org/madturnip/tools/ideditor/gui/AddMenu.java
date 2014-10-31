package org.madturnip.tools.ideditor.gui;

import org.madturnip.tools.ideditor.FileOps;

import javax.swing.*;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
/**
 * Dialog that asks for basic information about the item.
 * For the dialogs, there is less documentation.  Most of it would just repeat.
 * Check FileOps or Interface if you're really curious.
 */
public class AddMenu extends JDialog {

	private javax.swing.JDialog addMenu;
	private javax.swing.JButton addButton;
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel nameLabel;
	private javax.swing.JLabel examineLabel;
	private javax.swing.JLabel notedLabel;
	private javax.swing.JLabel noteableLabel;
	private javax.swing.JLabel stackableLabel;
	private javax.swing.JLabel parentIDLabel;
	private javax.swing.JLabel notedIDLabel;
	private javax.swing.JLabel membersLabel;
	private javax.swing.JLabel pricesLabel;
	private javax.swing.JLabel valueLabel;
	private javax.swing.JTextField nameField;
	private javax.swing.JTextField examineField;
	private javax.swing.JTextField notedField;
	private javax.swing.JTextField noteableField;
	private javax.swing.JTextField stackableField;
	private javax.swing.JTextField parentIDField;
	private javax.swing.JTextField notedIDField;
	private javax.swing.JTextField membersField;
	private javax.swing.JTextField pricesField;
	private javax.swing.JTextField valueField;

	/**
	 * Creates a new menu, and makes it show.
	 */
	public AddMenu() {
		init();
		addMenu.setVisible(true);
	}

	private void init() {
		addMenu = new javax.swing.JDialog();
		nameField = new javax.swing.JTextField();
		nameLabel = new javax.swing.JLabel("Name");
		examineLabel = new javax.swing.JLabel("Examine");
		notedLabel = new javax.swing.JLabel("Noted?");
		noteableLabel = new javax.swing.JLabel("Noteable?");
		stackableLabel = new javax.swing.JLabel("Stackable?");
		parentIDLabel = new javax.swing.JLabel("Parent ID");
		notedIDLabel = new javax.swing.JLabel("Noted ID");
		membersLabel = new javax.swing.JLabel("Members?");
		pricesLabel = new javax.swing.JLabel("Prices?");
		valueLabel = new javax.swing.JLabel("Shop Value");
		examineField = new javax.swing.JTextField();
		notedField = new javax.swing.JTextField();
		noteableField = new javax.swing.JTextField();
		stackableField = new javax.swing.JTextField();
		parentIDField = new javax.swing.JTextField();
		notedIDField = new javax.swing.JTextField();
		membersField = new javax.swing.JTextField();
		pricesField = new javax.swing.JTextField();
		valueField = new javax.swing.JTextField();
		addButton = new javax.swing.JButton("Add");
		cancelButton = new javax.swing.JButton("Cancel");

		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				addPress(evt);
			}
		});
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelPress(evt);
			}
		});

		javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(addMenu.getContentPane());
		addMenu.getContentPane().setLayout(jDialog1Layout);
		jDialog1Layout.setHorizontalGroup(
				jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jDialog1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
												.addComponent(valueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(pricesLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(membersLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(notedIDLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(parentIDLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(examineLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(nameLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														54, Short.MAX_VALUE)
												.addComponent(notedLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(noteableLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(stackableLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
										.addComponent(addButton))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(valueField)
										.addComponent(nameField)
										.addComponent(examineField)
										.addComponent(notedField, javax.swing.GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)
										.addComponent(noteableField, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
										.addComponent(stackableField, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
										.addComponent(parentIDField)
										.addComponent(notedIDField, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
										.addComponent(membersField)
										.addComponent(pricesField)
										.addComponent(cancelButton, javax.swing.GroupLayout.Alignment.TRAILING))
								.addContainerGap(10, Short.MAX_VALUE))
		);
		jDialog1Layout.setVerticalGroup(
				jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(jDialog1Layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(nameLabel)
										.addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(examineLabel)
										.addComponent(examineField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(notedField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(notedLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(noteableField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(noteableLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(stackableField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(stackableLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(parentIDLabel)
										.addComponent(parentIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(notedIDLabel)
										.addComponent(notedIDField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(membersLabel)
										.addComponent(membersField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(pricesLabel)
										.addComponent(pricesField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(valueLabel)
										.addComponent(valueField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(cancelButton)
										.addComponent(addButton))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		addMenu.setSize(250, 350);

		pack();
	}

	private void addPress(ActionEvent evt) {
		FileOps.interf.addValue();
	}

	private void cancelPress(ActionEvent evt) {
		disposeDialog();
	}

	String getNameField() {
		return nameField.getText();
	}

	String getExamine() {
		return examineField.getText();
	}

	boolean getNoted() {
		return notedField.getText().equals("true");
	}

	boolean getNoteable() {
		return noteableField.getText().equals("true");
	}

	boolean getStackable() {
		return stackableField.getText().equals("true");
	}

	int getParentID() {
		return Integer.decode(parentIDField.getText());
	}

	int getNotedID() {
		return Integer.decode(notedIDField.getText());
	}

	boolean getMembers() {
		return membersField.getText().equals("true");
	}

	boolean getPrices() {
		return pricesField.getText().equals("true");
	}

	int getValue() {
		return Integer.decode(valueField.getText());
	}

	/**
	 * Uses the dispose method to get rid of the menu.
	 */
	void disposeDialog() {
		addMenu.dispose();
		FileOps.interf.bonusmenu.getBonusMenu().dispose();
	}

}
