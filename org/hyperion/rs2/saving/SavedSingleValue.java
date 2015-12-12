package org.hyperion.rs2.saving;

public class SavedSingleValue {

    private SaveSingleValue sv;

    private Object previousValue;

    public SavedSingleValue(final SaveSingleValue sv, final Object value) {
        this.setSavedSingleValue(sv);
        this.setPreviousValue(value);
    }

    public Object getPreviousValue() {
        return previousValue;
    }

    public void setPreviousValue(final Object previousValue) {
        this.previousValue = previousValue;
    }

    public SaveSingleValue getSavedSingleValue() {
        return sv;
    }

    public void setSavedSingleValue(final SaveSingleValue sv) {
        this.sv = sv;
    }

}
