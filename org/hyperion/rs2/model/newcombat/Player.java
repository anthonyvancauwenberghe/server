package org.hyperion.rs2.model.newcombat;

public class Player extends Entity {

    private final Skills skills = new Skills();

    private final Prayers prayers = new Prayers(false);

    private final Container equipment = new BonusEquipment(Container.Type.STANDARD, Equipment.SIZE);

    private final EquipmentStats bonus = new EquipmentStats();

    public EquipmentStats getBonus() {
        return bonus;
    }

    public Container getEquipment() {
        return equipment;
    }

    public Skills getSkills() {
        return skills;
    }

    public Prayers getPrayers() {
        return prayers;
    }

    public void set(final String key, final String params) {
        if(key.equals("skill")){
            final String[] values = params.split("-");
            final int skill = Integer.parseInt(values[0]);
            final int level = Integer.parseInt(values[1]);
            final int xplevel = Integer.parseInt(values[2]);
            skills.setSkill(skill, level, skills.getXPForLevel(xplevel));
        }else if(key.equals("bonus")){
            final String[] values = params.split("-");
            final int slot = Integer.parseInt(values[0]);
            final int value = Integer.parseInt(values[1]);
            bonus.set(slot, value);
        }else if(key.equals("prayer")){
            final int id = Integer.parseInt(params);
            prayers.setEnabled(id, true);
        }
    }
}
