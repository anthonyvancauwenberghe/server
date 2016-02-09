package org.madturnip.tools.ideditor.gui;

import org.madturnip.tools.ideditor.FileOps;
import org.madturnip.tools.ideditor.Utilities;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
/**
 * Copyright (C) 2010 Blake Cornelius - Full details in FileOps.java.
 * @author Blake
 * This class deals with everything that has to do with the interface itself.
 * Displaying, altering, etc.
 */
public class Interface extends JFrame {

	private JScrollPane jScrollPane1;
	private JList nameList;
	private JComboBox selectionBox;
	private JButton modifyButton;
	private JButton saveButton;
	private JButton addButton;
	private JButton searchButton;
	private JButton jumpButton;
	private JButton bonusButton;
	private JTextField valueField;
	private String lastSearch = "";
	private int lastSearchIndex = 0;
	public boolean addFlag = false;

	/**
	 * Arrays used for the list, and later, savingnew.
	 * Now that I understand definitions a bit better, I should just outright use it.
	 */
	private String[] names, examines, names2, examines2;
	private Boolean[] noteds, noteables, stackables, members, prices, bonuseses;
	private Integer[] parentIDs, notedIDs, shopValues, highAlchValues, lowAlchValues;
	private Integer[][] bonuses, bonuses2;
	private Boolean[] noteds2, noteables2, stackables2, members2, prices2;
	private Integer[] parentIDs2, notedIDs2, shopValues2, highAlchValues2, lowAlchValues2;

	/**
	 * Instance of the menu for adding items.
	 */
	AddMenu addmenu;
	BonusMenu bonusmenu;

	public Interface() {
		setArrays(0);
		init();
	}

	/**
	 * Thank you Netbeans for the generated UI, lol.
	 */
	private void init() {
		jScrollPane1 = new javax.swing.JScrollPane();
		nameList = new javax.swing.JList();
		selectionBox = new javax.swing.JComboBox();
		modifyButton = new javax.swing.JButton();
		saveButton = new javax.swing.JButton();
		addButton = new javax.swing.JButton();
		searchButton = new javax.swing.JButton();
		jumpButton = new javax.swing.JButton();
		bonusButton = new javax.swing.JButton();
		valueField = new javax.swing.JTextField();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		nameList.setModel(new javax.swing.AbstractListModel() {
			public int getSize() {
				return Utilities.arraySize;
			}

			public Object getElementAt(int i) {
				if(selectionBox.getSelectedIndex() == 12) {
					String bonus = "";
					for(int i2 = 0; i2 < 12; i2++) {
						bonus = bonus + bonuses[i][i2] + ", ";
					}
					return bonus;
				}
				return getArray(selectionBox.getSelectedIndex())[i];
			}
		});
		nameList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		nameList.setVisibleRowCount(25);
		nameList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
			public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
				nameChange(evt);
			}
		});
		jScrollPane1.setViewportView(nameList);

		selectionBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{
				"Names", "Examine Info", "Noted", "Noteable", "Stackable", "ParentID",
				"NotedID", "Members", "Prices", "Shop Value", "High Alch Value", "Low Alch Value", "Bonuses"}));
		selectionBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SelectionChange(evt);
			}
		});

		modifyButton.setText("Modify");
		modifyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ModifyPress(evt);
			}
		});

		saveButton.setText("Save");
		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SavePress(evt);
			}
		});

		addButton.setText("Add");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AddPress(evt);
			}
		});

		searchButton.setText("Search");
		searchButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				SearchPress(evt);
			}
		});

		jumpButton.setText("Jump To");
		jumpButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JumpPress(evt);
			}
		});

		bonusButton.setText("Bonuses");
		bonusButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				BonusPress(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
										.addComponent(selectionBox, javax.swing.GroupLayout.Alignment.LEADING, 0,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING,
												javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(valueField, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
														.addComponent(jumpButton, javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(addButton, javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(modifyButton, javax.swing.GroupLayout.Alignment.LEADING,
																javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
														.addComponent(bonusButton, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE,
																javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
														.addComponent(saveButton, javax.swing.GroupLayout.DEFAULT_SIZE,
																86, Short.MAX_VALUE))))
								.addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(selectionBox, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(valueField, javax.swing.GroupLayout.PREFERRED_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(modifyButton)
														.addComponent(saveButton))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(addButton)
														.addComponent(searchButton))
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
														.addComponent(jumpButton)
														.addComponent(bonusButton)))
										.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 523, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addContainerGap(33, Short.MAX_VALUE))
		);

		pack();
	}

	/**
	 * Called when the save button is pressed.
	 * Loops through arrays to make sure none are null, since we will need them for writing.
	 * Then calls <code>FileOps</code> to begin writing process.
	 *
	 * @param evt The event being passed.
	 */
	private void SavePress(ActionEvent evt) {
		for(int i = 0; i < 13; i++) {
			setArrays(i);
		}
		FileOps.setDefinitions();
		FileOps.fileops.writeChanges();
	}

	/**
	 * Triggers when the modify button is pressed.
	 * This sets the new value from the <code>valueField</code> into the selected array and index.
	 *
	 * @param evt The event being passed.
	 */
	private void ModifyPress(ActionEvent evt) {
		switch(selectionBox.getSelectedIndex()) {
			case 0:
			case 1:
				modifyList(nameList.getSelectedIndex(), valueField.getText());
				break;
			case 2:
			case 3:
			case 4:
			case 7:
			case 8:
				modifyList(nameList.getSelectedIndex(), valueField.getText().equals("true") ? true : false);
				break;
			case 5:
			case 6:
			case 9:
			case 10:
			case 11:
				try {
					modifyList(nameList.getSelectedIndex(), Integer.decode(valueField.getText()));
				} catch(NumberFormatException e) {
					System.err.println(e);
				}
				break;
			case 12:
				if(bonusmenu == null || ! bonusmenu.isVisible()) {
					bonusmenu = new BonusMenu(true);
				}
				break;
		}
		FileOps.putChangeFlags(nameList.getSelectedIndex(), true);
	}

	/**
	 * Opens the menu for setting the new item up.
	 *
	 * @param evt The event being passed.
	 */
	private void AddPress(ActionEvent evt) {
		if(addmenu == null || ! addmenu.isVisible()) {
			addmenu = new AddMenu();
		}
		if(bonusmenu == null || ! bonusmenu.isVisible()) {
			bonusmenu = new BonusMenu(false);
		}
	}

	/**
	 * First, it checks to see if you're continuing a previous search.
	 * Then if you are, it'll continue in the array where you left off.
	 * If not, it'll begin a new search.
	 * Loops through the names of the items until it finds a match.
	 *
	 * @param evt You know what this is.
	 */
	private void SearchPress(ActionEvent evt) {
		String search = valueField.getText();
		if(lastSearch.equals(search)) {
			lastSearchIndex++;
		} else {
			lastSearch = search;
			lastSearchIndex = 0;
		}
		for(int i = lastSearchIndex; i < names.length; i++) {
			if(names[i].toLowerCase().contains(valueField.getText().toLowerCase())) {
				nameList.setSelectedIndex(i);
				nameList.ensureIndexIsVisible(i + 15);
				lastSearchIndex = i;
				break;
			}
		}
	}

	/**
	 * Simply takes the number from the <code>valueField</code> and scrolls to it in the <code>nameList</code>.
	 *
	 * @param evt You know what this is.
	 */
	private void JumpPress(ActionEvent evt) {
		try {
			if(nameList.getSelectedIndex() <= Integer.decode(valueField.getText())) {
				nameList.setSelectedIndex(Integer.decode(valueField.getText()));
				nameList.ensureIndexIsVisible(Integer.decode(valueField.getText()) + 15);
			} else {
				nameList.setSelectedIndex(Integer.decode(valueField.getText()));
				nameList.ensureIndexIsVisible(Integer.decode(valueField.getText()) - 10);
			}
		} catch(NumberFormatException e) {
			System.err.println(e);
		}
	}

	/**
	 * Takes the bonuses from the .cfg file and writes them into the .bin file,
	 * Creating one nice file for the whole thing.
	 */
	private void BonusPress(ActionEvent evt) {
		if(! Utilities.bonusFile) {
			if(bonuses == null) {
				setArrays(12);
			}
			FileOps.fileops.setWritingBonuses(true);
			FileOps.setDefinitions();
			FileOps.fileops.calcChanges(null, null, 1, - 1);
			FileOps.fileops.writeChanges();
		}
	}

	/**
	 * This is for when you change what array you're looking at.
	 */
	private void SelectionChange(ActionEvent evt) {
		setArrays(selectionBox.getSelectedIndex());
		nameList.repaint();
	}

	/**
	 * This is triggered when you click somewhere in the list.
	 * Pointless now, but you can uncomment all of that if you want to see the printout.
	 */
	private void nameChange(ListSelectionEvent evt) {
		if(! evt.getValueIsAdjusting()) {
			//System.out.print(nameList.getSelectedValue() + ", ");
			//System.out.print(nameList.getSelectedIndex() + ": ");
			for(int i = 0; i < 12; i++) {
				//System.out.print(bonuses[nameList.getSelectedIndex()][i] + ", ");
			}
			//System.out.println();
		}
	}

	/**
	 * @return This instance of the Interface.
	 */
	public Interface getInterface() {
		return this;
	}

	/**
	 * If the array isn't already set, initializes it.  If it is, it does nothing.
	 * If the flag for adding is true, it's setting the new array with the added item.
	 * Loop through this before writing to prevent null exceptions.
	 *
	 * @param a Which array to set.
	 */
	private void setArrays(int a) {
		switch(a) {
			case 0:
				if(names == null) {
					names = new String[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							names[i] = names2[i];
						} else {
							names[i] = FileOps.forID(i).getName();
						}
					}
				}
				break;
			case 1:
				if(examines == null) {
					examines = new String[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							examines[i] = examines2[i];
						} else {
							examines[i] = FileOps.forID(i).getExamine();
						}
					}
				}
				break;
			case 2:
				if(noteds == null) {
					noteds = new Boolean[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							noteds[i] = noteds2[i];
						} else {
							noteds[i] = FileOps.forID(i).getNoted();
						}
					}
				}
				break;
			case 3:
				if(noteables == null) {
					noteables = new Boolean[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							noteables[i] = noteables2[i];
						} else {
							noteables[i] = FileOps.forID(i).getNoteable();
						}
					}
				}
				break;
			case 4:
				if(stackables == null) {
					stackables = new Boolean[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							stackables[i] = stackables2[i];
						} else {
							stackables[i] = FileOps.forID(i).getStackable();
						}
					}
				}
				break;
			case 5:
				if(parentIDs == null) {
					parentIDs = new Integer[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							parentIDs[i] = parentIDs2[i];
						} else {
							parentIDs[i] = FileOps.forID(i).getParentID();
						}
					}
				}
				break;
			case 6:
				if(notedIDs == null) {
					notedIDs = new Integer[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							notedIDs[i] = notedIDs2[i];
						} else {
							notedIDs[i] = FileOps.forID(i).getNotedID();
						}
					}
				}
				break;
			case 7:
				if(members == null) {
					members = new Boolean[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							members[i] = members2[i];
						} else {
							members[i] = FileOps.forID(i).getMembers();
						}
					}
				}
				break;
			case 8:
				if(prices == null) {
					prices = new Boolean[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							prices[i] = prices2[i];
						} else {
							prices[i] = FileOps.forID(i).getPrices();
						}
					}
				}
			case 9:
				if(shopValues == null) {
					shopValues = new Integer[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							shopValues[i] = shopValues2[i];
						} else {
							shopValues[i] = FileOps.forID(i).getShopValue();
						}
					}
				}
				break;
			case 10:
				if(highAlchValues == null) {
					highAlchValues = new Integer[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							highAlchValues[i] = highAlchValues2[i];
						} else {
							highAlchValues[i] = FileOps.forID(i).getHighAlchValue();
						}
					}
				}
				break;
			case 11:
				if(lowAlchValues == null) {
					lowAlchValues = new Integer[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						if(addFlag) {
							lowAlchValues[i] = lowAlchValues2[i];
						} else {
							lowAlchValues[i] = FileOps.forID(i).getLowAlchValue();
						}
					}
				}
				break;
			case 12:
				if(bonuses == null) {
					bonuses = new Integer[Utilities.arraySize][12];
					bonuseses = new Boolean[Utilities.arraySize];
					for(int i = 0; i < Utilities.arraySize; i++) {
						for(int i2 = 0; i2 < 12; i2++) {
							if(addFlag) {
								bonuses[i][i2] = bonuses2[i][i2];
							} else {
								bonuses[i][i2] = FileOps.forID(i).getBonus()[i2];
							}
						}
						bonuseses[i] = hasBonuses(i);
					}
				}
				break;
			default:
				return;
		}
	}

	/**
	 * This mess is all for adding a single item.  There has to be a better way.. Anyways.
	 * Set the adding flag, initialize secondary arrays, set them, add the new item's info,
	 * Make the old ones null, re-set them, and nullify the secondary arrays.
	 * Oh, you also have to completely reset the list, or it'll bug out.
	 */
	void addValue() {
		for(int i = 0; i < 13; i++) {
			setArrays(i);
		}
		addFlag = true;
		Utilities.arraySize++;
		names2 = new String[Utilities.arraySize];
		examines2 = new String[Utilities.arraySize];
		noteds2 = new Boolean[Utilities.arraySize];
		noteables2 = new Boolean[Utilities.arraySize];
		stackables2 = new Boolean[Utilities.arraySize];
		parentIDs2 = new Integer[Utilities.arraySize];
		notedIDs2 = new Integer[Utilities.arraySize];
		members2 = new Boolean[Utilities.arraySize];
		prices2 = new Boolean[Utilities.arraySize];
		shopValues2 = new Integer[Utilities.arraySize];
		highAlchValues2 = new Integer[Utilities.arraySize];
		lowAlchValues2 = new Integer[Utilities.arraySize];
		bonuses2 = new Integer[Utilities.arraySize][12];
		for(int i = 0; i < Utilities.arraySize - 1; i++) {
			names2[i] = names[i];
			examines2[i] = examines[i];
			noteds2[i] = noteds[i];
			noteables2[i] = noteables[i];
			stackables2[i] = stackables[i];
			parentIDs2[i] = parentIDs[i];
			notedIDs2[i] = notedIDs[i];
			members2[i] = members[i];
			prices2[i] = prices[i];
			shopValues2[i] = shopValues[i];
			highAlchValues2[i] = highAlchValues[i];
			lowAlchValues2[i] = lowAlchValues[i];
			for(int i2 = 0; i2 < 12; i2++) {
				bonuses2[i][i2] = bonuses[i][i2];
			}
		}
		names2[Utilities.arraySize - 1] = addmenu.getNameField();
		examines2[Utilities.arraySize - 1] = addmenu.getExamine();
		noteds2[Utilities.arraySize - 1] = addmenu.getNoted();
		noteables2[Utilities.arraySize - 1] = addmenu.getNoteable();
		stackables2[Utilities.arraySize - 1] = addmenu.getStackable();
		parentIDs2[Utilities.arraySize - 1] = addmenu.getParentID();
		notedIDs2[Utilities.arraySize - 1] = addmenu.getNotedID();
		members2[Utilities.arraySize - 1] = addmenu.getMembers();
		prices2[Utilities.arraySize - 1] = addmenu.getPrices();
		shopValues2[Utilities.arraySize - 1] = addmenu.getValue();
		highAlchValues2[Utilities.arraySize - 1] = (int) (addmenu.getValue() * 0.6D);
		lowAlchValues2[Utilities.arraySize - 1] = (int) (addmenu.getValue() * 0.4D);
		for(int i2 = 0; i2 < 12; i2++) {
			bonuses2[Utilities.arraySize - 1][i2] = bonusmenu.getBonus()[i2];
		}
		names = null;
		examines = null;
		noteds = null;
		noteables = null;
		stackables = null;
		parentIDs = null;
		notedIDs = null;
		members = null;
		prices = null;
		shopValues = null;
		lowAlchValues = null;
		highAlchValues = null;
		bonuseses = null;
		bonuses = null;
		for(int i = 0; i < 13; i++) {
			setArrays(i);
		}
		addFlag = false;
		FileOps.fileops.calcChanges(names2[Utilities.arraySize - 1], "", 0, Utilities.arraySize - 1);
		FileOps.fileops.calcChanges(examines2[Utilities.arraySize - 1], "", 2, Utilities.arraySize - 1);
		names2 = null;
		examines2 = null;
		noteds2 = null;
		noteables2 = null;
		stackables2 = null;
		parentIDs2 = null;
		notedIDs2 = null;
		members2 = null;
		prices2 = null;
		shopValues2 = null;
		lowAlchValues2 = null;
		highAlchValues2 = null;
		bonuses2 = null;
		addmenu.disposeDialog();
		FileOps.putChangeFlags(Utilities.arraySize - 1, true);
		nameList.setModel(new javax.swing.AbstractListModel() {
			public int getSize() {
				return Utilities.arraySize;
			}

			public Object getElementAt(int i) {
				if(selectionBox.getSelectedIndex() == 12) {
					String bonus = "";
					for(int i2 = 0; i2 < 12; i2++) {
						bonus = bonus + bonuses[i][i2] + ", ";
					}
					return bonus;
				}
				return getArray(selectionBox.getSelectedIndex())[i];
			}
		});
	}

	/**
	 * A get method that returns the different arrays.
	 *
	 * @param i Which array you want to get.
	 * @return The array Object[]
	 */
	private Object[] getArray(int i) {
		switch(i) {
			case 0:
				return names;
			case 1:
				return examines;
			case 2:
				return noteds;
			case 3:
				return noteables;
			case 4:
				return stackables;
			case 5:
				return parentIDs;
			case 6:
				return notedIDs;
			case 7:
				return members;
			case 8:
				return prices;
			case 9:
				return shopValues;
			case 10:
				return highAlchValues;
			case 11:
				return lowAlchValues;
			default:
				break;
		}
		return null;
	}

	/**
	 * This method changes the arrays around.
	 *
	 * @param index The index of the array we're changing.
	 * @param obj   Object to check what kind of value we're changing.  Prevents format exceptions.
	 */
	private void modifyList(int index, Object obj) {
		switch(selectionBox.getSelectedIndex()) {
			case 0:
			case 1:
				if(obj.getClass().toString().contains("String"))
					FileOps.fileops.calcChanges(obj, getArray(selectionBox.getSelectedIndex())[index], 0, - 1);
				getArray(selectionBox.getSelectedIndex())[index] = obj;
				break;
			case 2:
			case 3:
			case 4:
			case 7:
			case 8:
				if(obj.getClass().toString().contains("Boolean"))
					getArray(selectionBox.getSelectedIndex())[index] = obj;
				break;
			case 5:
			case 6:
			case 9:
			case 10:
			case 11:
				if(obj.getClass().toString().contains("Integer")) {
					if(selectionBox.getSelectedIndex() == 9) {
						if(((Integer) getArray(selectionBox.getSelectedIndex())[index] == - 1) && ((Integer) obj != - 1)) {
							FileOps.fileops.calcChanges(null, null, 4, 0);
						} else {
							FileOps.fileops.calcChanges(null, null, 4, 1);
						}
					}
					getArray(selectionBox.getSelectedIndex())[index] = obj;
				}
				break;
		}
		nameList.repaint();
	}

	/**
	 * Checks to see if a certain item actually has bonuses.
	 * 0 and -1 are both considered as the "not" bonuses.  Anything else is.
	 * By eliminating items with only those two numbers, it drops the size of the .cfg
	 * that comes with the source from 700kb, to only using 23kb of it.
	 *
	 * @param i
	 * @return
	 */
	public boolean hasBonuses(int i) {
		for(int i2 = 0; i2 < 12; i2++) {
			if(bonuses[i][i2] != 0 && bonuses[i][i2] != - 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Changes the numbers for the bonus array.
	 *
	 * @param b The array of bonuses for the item.
	 */
	void modifyBonuses(int[] b) {
		for(int i = 0; i < 12; i++) {
			bonuses[nameList.getSelectedIndex()][i] = b[i];
		}
		if(bonuseses[nameList.getSelectedIndex()] != hasBonuses(nameList.getSelectedIndex())) {
			bonuseses[nameList.getSelectedIndex()] = hasBonuses(nameList.getSelectedIndex());
			if(bonuseses[nameList.getSelectedIndex()]) {
				FileOps.fileops.calcChanges(null, null, 3, 0);
			} else {
				FileOps.fileops.calcChanges(null, null, 3, 1);
			}
		}
		nameList.repaint();
		bonusmenu.getBonusMenu().dispose();
	}

	/*
	 * These all return a certain array, for <code>FileOps</code>
	 * Thinking on it, I should use <code>getArray</code> for this.
	 */
	public String getName(int i) {
		return names[i];
	}

	public String getExamine(int i) {
		return examines[i];
	}

	public boolean getNoted(int i) {
		return noteds[i];
	}

	public boolean getNoteable(int i) {
		return noteables[i];
	}

	public boolean getStackable(int i) {
		return stackables[i];
	}

	public int getParentID(int i) {
		return parentIDs[i];
	}

	public int getNotedID(int i) {
		return notedIDs[i];
	}

	public boolean getMembers(int i) {
		return members[i];
	}

	public boolean getPrices(int i) {
		return prices[i];
	}

	public int getShopValue(int i) {
		return shopValues[i];
	}

	public int getHighAlchValue(int i) {
		return highAlchValues[i];
	}

	public int getLowAlchValue(int i) {
		return lowAlchValues[i];
	}

	public boolean getBonuseses(int i) {
		return bonuseses[i];
	}

	public int getBonuses(int i, int i2) {
		return bonuses[i][i2];
	}

	public int[] getBonuses(int i) {
		int[] bonuses = new int[12];
		for(int i2 = 0; i2 < 12; i2++) {
			bonuses[i2] = this.bonuses[i][i2];
		}
		return bonuses;
	}

	public AddMenu getAddMenu() {
		return addmenu;
	}

}
