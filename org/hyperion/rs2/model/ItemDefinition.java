package org.hyperion.rs2.model;

import org.hyperion.rs2.commands.Command;
import org.hyperion.rs2.commands.CommandHandler;
import org.hyperion.rs2.model.combat.weapons.Weapon;
import org.hyperion.rs2.model.combat.weapons.WeaponManager;
import org.hyperion.rs2.model.container.Equipment;
import org.hyperion.rs2.model.container.Equipment.EquipmentType;
import org.hyperion.rs2.model.container.impl.WeaponAnimManager;
import org.hyperion.rs2.model.content.minigame.FightPits;
import org.hyperion.rs2.saving.PlayerSaving;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The item definition manager.
 *
 * @author Vastico
 * @author Graham Edgecombe
 */
public class ItemDefinition {

    /**
     * The configuration file.
     */
    public static final File CONFIG_FILE = new File("./data/itemconfigs.cfg");

    /**
     * The maximum item id.
     */
    public static final int MAX_ID = 23000;
    private static final int[] ITEM_NON_TRADEABLE = {2412, 2413, 2414, 2570, 2571, 2560, 2561, 11056, 11057, 11051,
            11052, 11055, 11053, 2558, 11337, 11338, 2556, 2554, 4067, 4511, 4509, 4510, 4508, 4512, 10547, 10548,
            10549, 10550, 7806, 7807, 7808, 7809, 4566, 8850, 10551, 8839, 8840, 8842, 11663, 11664, 11665, 3842, 3844,
            3840, 8844, 8845, 8846, 8847, 8848, 8849, 8850, 10551, 6570, 7462, 7461, 7460, 7459, 7458, 7457, 7456, 7455,
            7454, 8839, 8840, 8842, 11663, 11664, 11665, 10499, 9748, 9754, 9751, 9769, 9757, 9760, 9763, 9802, 9808,
            9784, 9799, 9805, 9781, 9796, 9793, 9775, 9772, 9778, 9787, 9811, 9766, 9749, 9755, 9752, 9770, 9758, 9761,
            9764, 9803, 9809, 9785, 9800, 9806, 9782, 9797, 9794, 9776, 9773, 9779, 9788, 9812, 9767, 9747, 13350, 9753,
            9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810,
            9765, 11793, 11794, 11795, 11796, 11798, 6858, 6859, 6860, 6861, 6856, 6857, 15441, 15442, 15443, 15444,
            15600, 15606, 15612, 15618, 15602, 15608, 15614, 15620, 15604, 15610, 15616, 15622, 15021, 15022, 15023,
            15024, 15025, 15026, 15027, 15028, 15029, 15030, 15031, 15032, 15033, 15034, 15035, 15036, 15037, 15038,
            15039, 15040, 15041, 15042, 15043, 15044, 18350, 18352, 18354, 18356, 18358, 18360, 12158, 12159, 12160,
            12161, 12163, 12162, 12164, 12165, 12166, 12167, 12168, 19780, 13351, 19669, 19111, 19713, 19716, 19719,
            19815, 19816, 19817, 19815, 2430, 15332, 15333, 15334, 15335, 15334, 17061, 17193, 17339, 17215, 17317,
            16887, 16337, 18349, 18351, 18353, 12747, 12744, 10025, 10026, 17999};
    /**
     * The definition array.
     */
    public static ItemDefinition[] definitions;
    public static Map<Integer, Object> nonTradesables = new HashMap<Integer, Object>();

    static {
        final Object o = new Object();
        for(final int i : ITEM_NON_TRADEABLE){
            nonTradesables.put(i, o);
        }

    }

    static {
        //System.out.println("About to load stuff");
        try{
            init();

        }catch(final IOException e){
            e.printStackTrace();
        }
        CommandHandler.submit(new Command("setalchvalue", Rank.MODERATOR) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                final int[] values = this.getIntArray(input);
                final int id = values[0];
                final int price = values[1];
                definitions[id].setHighAlcValue(price);
                PlayerSaving.getSaving().submit(new Runnable() {
                    @Override
                    public void run() {
                        dumpItemDefinitions();
                    }
                });
                return true;
            }
        });

        CommandHandler.submit(new Command("unstack", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                try{
                    final int[] values = this.getIntArray(input);
                    final int id = values[0];
                    definitions[id].setStackable(false);
                    PlayerSaving.getSaving().submit(new Runnable() {
                        @Override
                        public void run() {
                            dumpItemDefinitions();
                        }
                    });
                }catch(final Exception e){
                    player.getActionSender().sendMessage("Use as ::unstack 11694");
                }
                return true;
            }
        });

        CommandHandler.submit(new Command("stack", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                try{
                    final int[] values = this.getIntArray(input);
                    final int id = values[0];
                    definitions[id].setStackable(true);
                    PlayerSaving.getSaving().submit(new Runnable() {
                        @Override
                        public void run() {
                            dumpItemDefinitions();
                        }
                    });
                }catch(final Exception e){
                    player.getActionSender().sendMessage("Use as ::stack 11694");
                }
                return true;
            }
        });

        CommandHandler.submit(new Command("reloaditems", Rank.ADMINISTRATOR) {
            @Override
            public boolean execute(final Player player, final String input) throws Exception {
                ItemDefinition.loadItems();
                player.getActionSender().sendMessage("Reloaded");
                return true;
            }
        });
    }

    /**
     * Id.
     */
    private final int id;
    /**
     * Noted flag.
     */
    private final boolean noted;
    /**
     * Noteable flag.
     */
    private final boolean noteable;
    /**
     * Non-noted id.
     */
    private final int parentId;
    /**
     * Noted id.
     */
    private final int notedId;
    /**
     * Item Speed (If Weapon)
     */
    private final int weaponSpeed;
    /**
     * Name.
     */
    private String name;
    /**
     * Description.
     */
    private String examine;
    /**
     * Stackable flag.
     */
    private boolean stackable;
    /**
     * High alc value.
     */
    private int highAlcValue;
    /**
     * The armour slot.
     */
    private int armourSlot;
    /**
     * The item bonuses.
     */
    private int[] bonus = new int[12];

    /**
     * Creates the item definition.
     *
     * @param id           The id.
     * @param name         The name.
     * @param examine      The description.
     * @param noted        The noted flag.
     * @param noteable     The noteable flag.
     * @param stackable    The stackable flag.
     * @param parentId     The non-noted id.
     * @param notedId      The noted id.
     * @param highAlcValue The high alc value.
     * @param armourSlot   The armour slot.
     * @param weaponSpeed  The weapon speed
     * @param bonus        The item bonuses
     */
    private ItemDefinition(final int id, final String name, final String examine, final boolean noted, final boolean noteable, final boolean stackable, final int parentId, final int notedId, final int highAlcValue, final int armourSlot, final int weaponSpeed, final int[] bonus) {
        //System.out.println("New itemdef for id : " + id);
        this.id = id;
        this.name = name;
        this.examine = examine;
        this.noted = noted;
        this.noteable = noteable;
        this.stackable = stackable;
        this.parentId = parentId;
        this.notedId = notedId;
        this.highAlcValue = highAlcValue > 0 ? highAlcValue : 1;
        this.armourSlot = armourSlot;
        this.bonus = bonus;
        this.weaponSpeed = weaponSpeed;
        if(Equipment.equipmentTypes.get(id) != null)
            return;
        final Weapon weapon = Weapon.getWeapon(name, id);

        if(weapon != null){
            WeaponManager.getManager().put(id, weapon);

        }
        if(this.armourSlot == 16){
            Equipment.equipmentTypes.put(id, EquipmentType.FULL_HELM);
        }
        if(this.armourSlot == 17){
            Equipment.equipmentTypes.put(id, EquipmentType.FULL_MASK);
        }
        if(this.armourSlot == 1){
            Equipment.equipmentTypes.put(id, EquipmentType.CAPE);
        }
        if(this.armourSlot == 10){
            Equipment.equipmentTypes.put(id, EquipmentType.BOOTS);
        }
        if(this.armourSlot == 9){
            Equipment.equipmentTypes.put(id, EquipmentType.GLOVES);
        }
        if(this.armourSlot == 5){
            Equipment.equipmentTypes.put(id, EquipmentType.SHIELD);
        }
        if(this.armourSlot == 0){
            Equipment.equipmentTypes.put(id, EquipmentType.HAT);
        }
        if(this.armourSlot == 2){
            Equipment.equipmentTypes.put(id, EquipmentType.AMULET);
        }
        if(this.armourSlot == 13){
            Equipment.equipmentTypes.put(id, EquipmentType.ARROWS);
        }
        if(this.armourSlot == 12){
            Equipment.equipmentTypes.put(id, EquipmentType.RING);
        }
        if(this.armourSlot == 4){
            Equipment.equipmentTypes.put(id, EquipmentType.BODY);
        }
        if(this.armourSlot == 7){
            Equipment.equipmentTypes.put(id, EquipmentType.LEGS);
        }
        if(this.armourSlot == 15){
            Equipment.equipmentTypes.put(id, EquipmentType.PLATEBODY);
        }

    }

    /**
     * Dumps the configuration file into the <code>CONFIG_FILE</code>
     */
    public static void dumpItemDefinitions() {
        try{
            final BufferedWriter out = new BufferedWriter(new FileWriter(CONFIG_FILE));
            for(final ItemDefinition def : definitions){
                if(def == null)
                    continue;
                out.write(def.toString());
                out.newLine();
            }
            out.close();
        }catch(final Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Gets a definition for the specified id.
     *
     * @param id The id.
     * @return The definition.
     */
    public static ItemDefinition forId(final int id) {
        if(id >= 0 && id < definitions.length)
            return definitions[id];
        return null;
    }

    /**
     * Loads the item definitions.
     *
     * @throws IOException           if an I/O error occurs.
     * @throws IllegalStateException if the definitions have been loaded already.
     */


    public static void init() throws IOException {
        if(definitions != null){
            throw new IllegalStateException("Definitions already loaded.");
        }
        definitions = new ItemDefinition[MAX_ID];
        loadItems();
    }

    public static void loadItems() throws IOException {
        final BufferedReader in = new BufferedReader(new FileReader(CONFIG_FILE));
        String line;
        while((line = in.readLine()) != null){
            //System.out.println(line);
            try{
                final ItemDefinition definition = ItemDefinition.forString(line);
                definitions[definition.getId()] = definition;
            }catch(final Exception e){
                e.printStackTrace();
                System.out.println("Error reading config file: " + line);
            }
        }
        in.close();
    }

    public static ItemDefinition forString(String line) {
        line = line.replaceAll(", ", ",");
        line = line.replaceAll(" ,", ",");
        line = line.replaceAll(" = ", "=");
        //System.out.println(line);
        final String[] parts = line.split(",");
        final String idString = getValue(parts[0]);
        final int id = Integer.parseInt(idString);
        final String name = getValue(parts[1]);
        final String description = getValue(parts[2]);
        final String notedStr = getValue(parts[3]);
        final boolean noted = Boolean.parseBoolean(notedStr);
        final String noteableStr = getValue(parts[4]);
        final boolean noteable = Boolean.parseBoolean(noteableStr);
        final String stackableStr = getValue(parts[5]);
        final boolean stackable = Boolean.parseBoolean(stackableStr);
        final String parentStr = getValue(parts[6]);
        final int parent = Integer.parseInt(parentStr);
        final String notedIdStr = getValue(parts[7]);
        final int notedId = Integer.parseInt(notedIdStr);
        final String highAlcStr = getValue(parts[8]);
        final int highAlc = Integer.parseInt(highAlcStr);
        final String armourSlotStr = getValue(parts[9]);
        final int armourslot = Integer.parseInt(armourSlotStr);
        final int[] bonus = new int[12];
        for(int i = 0; i < bonus.length; i++){
            final String bonusStr = getValue(parts[10 + i]);
            bonus[i] = Integer.parseInt(bonusStr);
        }
        final int weaponSpeed = WeaponAnimManager.getSpeed(name, id);
        final ItemDefinition definition = new ItemDefinition(id, name, description, noted, noteable, stackable, parent, notedId, highAlc, armourslot, weaponSpeed, bonus);
        return definition;
    }

    private static String getValue(final String part) {
        return part.split("=")[1];
    }

    /**
     * Gets the weapon speed.
     *
     * @return
     */
    public int getWeaponSpeed() {
        return weaponSpeed;
    }

    /**
     * Gets the id.
     *
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the name.
     *
     * @return The name.
     */
    public String getName() {
        return name;
    }

    public void setName(final String s) {
        name = s;
    }

    public String getProperName() {
        return name.replace('_', ' ');
    }

    /**
     * Gets the description.
     *
     * @return The description.
     */
    public String getDescription() {
        return examine;
    }

    public void setDescription(final String examine) {
        this.examine = examine;
    }

    /**
     * Gets the noted flag.
     *
     * @return The noted flag.
     */
    public boolean isNoted() {
        return noted;
    }

    /**
     * Gets the noteable flag.
     *
     * @return The noteable flag.
     */
    public boolean isNoteable() {
        return noteable;
    }

    /**
     * Gets the stackable flag.
     *
     * @return The stackable flag.
     */
    public boolean isStackable() {
        return stackable || noted;
    }

    /**
     * @param b
     * @return
     */
    public void setStackable(final boolean b) {
        stackable = b;
    }

    public int getParentId() {
        return parentId;
    }

    /**
     * Gets the normal id.
     *
     * @return The normal id.
     */
    public int getNormalId() {
        if(noted){
            return parentId;
        }else{
            return id;
        }
    }

    /**
     * Gets the noted id.
     *
     * @return The noted id.
     */
    public int getNotedId() {
        return notedId;
    }

    /**
     * Gets the low alc value.
     *
     * @return The low alc value.
     */
    public int getLowAlcValue() {
        return highAlcValue * 2 / 3;
    }

    /**
     * Gets the high alc value.
     *
     * @return The high alc value.
     */
    public int getHighAlcValue() {
        if(FightPits.rewardItems.contains(id))
            return 3000000;
        return highAlcValue;
    }

    /**
     * Sets the high alc value.
     *
     * @param value
     */
    public void setHighAlcValue(final int value) {
        highAlcValue = value;
    }

    public int getArmourSlot() {
        return armourSlot;
    }

    public void setArmourSlot(final int armourSlot) {
        this.armourSlot = armourSlot;
    }

    public int[] getBonus() {
        return bonus;
    }

    public void setBonus(final int idx, final int value) {
        bonus[idx] = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("id = ").append(this.getId()).append(", ");
        sb.append("name = ").append(this.getName().replaceAll(",", "")).append(", ");
        sb.append("examine = ").append(this.getDescription().replaceAll(",", "")).append(", ");
        sb.append("noted = ").append(this.isNoted()).append(", ");
        sb.append("noteable = ").append(this.isNoteable()).append(", ");
        sb.append("stackable = ").append(this.isStackable()).append(", ");
        sb.append("parentid = ").append(this.getNormalId()).append(", ");
        sb.append("notedid = ").append(this.getNotedId()).append(", ");
        sb.append("highalc = ").append(this.getHighAlcValue()).append(", ");
        sb.append("armourslot = ").append(this.getArmourSlot()).append(", ");
        int idx = 0;
        for(final int bonus : this.getBonus()){
            sb.append("bonus").append(idx).append(" = ").append(bonus).append(", ");
            idx++;
        }
        return sb.toString();
    }

}