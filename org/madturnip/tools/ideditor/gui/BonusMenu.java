package org.madturnip.tools.ideditor.gui;

import org.madturnip.tools.ideditor.FileOps;

import javax.swing.*;
import java.awt.event.ActionEvent;


@SuppressWarnings("serial")
/**
 * Dialog that asks for the 12 different stats that a piece of equipment can give.
 * For the dialogs, there is less documentation.  Most of it would just repeat.
 * Check FileOps or Interface if you're really curious.
 */
public class BonusMenu extends JDialog {

	private javax.swing.JDialog bonusMenu;
	private javax.swing.JLabel stabLabelA;
	private javax.swing.JLabel slashLabelA;
	private javax.swing.JLabel crushLabelA;
	private javax.swing.JLabel magicLabelA;
	private javax.swing.JLabel rangeLabelA;
	private javax.swing.JLabel strengthLabelA;
	private javax.swing.JLabel stabLabelD;
	private javax.swing.JLabel slashLabelD;
	private javax.swing.JLabel crushLabelD;
	private javax.swing.JLabel magicLabelD;
	private javax.swing.JLabel rangeLabelD;
	private javax.swing.JLabel prayerLabel;
	private javax.swing.JTextField stabFieldA;
	private javax.swing.JTextField slashFieldA;
	private javax.swing.JTextField crushFieldA;
	private javax.swing.JTextField magicFieldA;
	private javax.swing.JTextField rangeFieldA;
	private javax.swing.JTextField strengthField;
	private javax.swing.JTextField stabFieldD;
	private javax.swing.JTextField slashFieldD;
	private javax.swing.JTextField crushFieldD;
	private javax.swing.JTextField magicFieldD;
	private javax.swing.JTextField rangeFieldD;
	private javax.swing.JTextField prayerField;
	private javax.swing.JButton modifyButton;
	private javax.swing.JButton cancelButton;
	private boolean modifying;

	/**
	 * Creates the menu.
	 *
	 * @param modifying Whether or not we're modifying or adding bonuses.
	 */
	public BonusMenu(boolean modifying) {
		this.modifying = modifying;
		init();
		bonusMenu.setVisible(true);
	}

	private void init() {
		bonusMenu = new javax.swing.JDialog();
		stabLabelA = new javax.swing.JLabel("Stab");
		slashLabelA = new javax.swing.JLabel("Slash");
		crushLabelA = new javax.swing.JLabel("Crush");
		magicLabelA = new javax.swing.JLabel("Magic");
		rangeLabelA = new javax.swing.JLabel("Range");
		stabLabelD = new javax.swing.JLabel("Stab");
		slashLabelD = new javax.swing.JLabel("Slash");
		crushLabelD = new javax.swing.JLabel("Crush");
		magicLabelD = new javax.swing.JLabel("Magic");
		rangeLabelD = new javax.swing.JLabel("Range");
		strengthLabelA = new javax.swing.JLabel("Strength");
		prayerLabel = new javax.swing.JLabel("Prayer");
		stabFieldA = new javax.swing.JTextField();
		slashFieldA = new javax.swing.JTextField();
		crushFieldA = new javax.swing.JTextField();
		magicFieldA = new javax.swing.JTextField();
		rangeFieldA = new javax.swing.JTextField();
		strengthField = new javax.swing.JTextField();
		stabFieldD = new javax.swing.JTextField();
		slashFieldD = new javax.swing.JTextField();
		crushFieldD = new javax.swing.JTextField();
		magicFieldD = new javax.swing.JTextField();
		rangeFieldD = new javax.swing.JTextField();
		prayerField = new javax.swing.JTextField();
		modifyButton = new javax.swing.JButton(modifying ? "Modify" : "Add");
		cancelButton = new javax.swing.JButton("Cancel");

		modifyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				modifyPress(evt);
			}
		});
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				cancelPress(evt);
			}
		});

		javax.swing.GroupLayout bonusMenuLayout = new javax.swing.GroupLayout(bonusMenu.getContentPane());
		bonusMenu.getContentPane().setLayout(bonusMenuLayout);
		bonusMenuLayout.setHorizontalGroup(
				bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(bonusMenuLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(modifyButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(strengthField, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(strengthLabelA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(rangeFieldA, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(rangeLabelA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(magicFieldA, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(magicLabelA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(slashLabelA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(crushLabelA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(crushFieldA, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(slashFieldA, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(stabFieldA, javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(stabLabelA, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE,
												86, Short.MAX_VALUE))
								.addGap(18, 18, 18)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(prayerField)
										.addComponent(prayerLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(rangeFieldD)
										.addComponent(rangeLabelD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(magicLabelD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(crushLabelD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(magicFieldD)
										.addComponent(crushFieldD)
										.addComponent(slashFieldD)
										.addComponent(slashLabelD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(stabLabelD, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(stabFieldD, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		bonusMenuLayout.setVerticalGroup(
				bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(bonusMenuLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(stabLabelA)
										.addComponent(stabLabelD))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(stabFieldA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(stabFieldD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(slashLabelA)
										.addComponent(slashLabelD))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(slashFieldA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(slashFieldD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(crushLabelA)
										.addComponent(crushLabelD))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(crushFieldA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(crushFieldD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(magicLabelA)
										.addComponent(magicLabelD))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(magicFieldA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(magicFieldD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(rangeLabelA)
										.addComponent(rangeLabelD))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(rangeFieldA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(rangeFieldD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(strengthLabelA)
										.addComponent(prayerLabel))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(strengthField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(prayerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addGroup(bonusMenuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(modifyButton)
										.addComponent(cancelButton))
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		if(modifying) {
			bonusMenu.setBounds(0, 0, 250, 425);
		} else {
			bonusMenu.setBounds(250, 0, 250, 425);
		}

		pack();
	}

	private void modifyPress(ActionEvent evt) {
		if(modifying) {
			FileOps.interf.modifyBonuses(getBonus());
		} else {
			FileOps.interf.addValue();
		}
	}

	private void cancelPress(ActionEvent evt) {
		if(! modifying) {
			FileOps.interf.addmenu.disposeDialog();
		} else {
			bonusMenu.dispose();
		}
	}

	int[] getBonus() {
		int[] bonusArray = new int[12];
		try {
			bonusArray[0] = Integer.decode(stabFieldA.getText());
			bonusArray[1] = Integer.decode(slashFieldA.getText());
			bonusArray[2] = Integer.decode(crushFieldA.getText());
			bonusArray[3] = Integer.decode(magicFieldA.getText());
			bonusArray[4] = Integer.decode(rangeFieldA.getText());
			bonusArray[5] = Integer.decode(stabFieldD.getText());
			bonusArray[6] = Integer.decode(slashFieldD.getText());
			bonusArray[7] = Integer.decode(crushFieldD.getText());
			bonusArray[8] = Integer.decode(magicFieldD.getText());
			bonusArray[9] = Integer.decode(rangeFieldD.getText());
			bonusArray[10] = Integer.decode(strengthField.getText());
			bonusArray[11] = Integer.decode(prayerField.getText());
		} catch(NumberFormatException e) {
			System.err.println(e);
		}
		return bonusArray;
	}

	JDialog getBonusMenu() {
		return bonusMenu;
	}

}
