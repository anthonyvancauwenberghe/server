package org.hyperion.rs2.model.joshyachievements.requirement;

public class DelegatedRequirement implements Requirement{

    private final int value;

    public DelegatedRequirement(final int value){
        this.value = value;
    }

    public String toString(){
        return String.format("%s(value=%,d)", getClass().getSimpleName(), value);
    }

    public Integer get(){
        return value;
    }
}
