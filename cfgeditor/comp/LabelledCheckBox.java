package cfgeditor.comp;

import javax.swing.JCheckBox;

public class LabelledCheckBox extends LabelledComp<JCheckBox, Boolean>{

    public LabelledCheckBox(final String labelText, final boolean selected){
        super(labelText, new JCheckBox("", selected));
    }

    public LabelledCheckBox(final String labelText){
        this(labelText, false);
    }

    public Boolean value(){
        return comp.isSelected();
    }

    protected void set$(final Boolean value){
        comp.setSelected(true);
    }
}
