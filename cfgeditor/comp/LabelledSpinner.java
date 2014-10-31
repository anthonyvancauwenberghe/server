package cfgeditor.comp;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

public class LabelledSpinner extends LabelledComp<JSpinner, Integer>{

    public LabelledSpinner(final String labelText, final int value, final int min, final int max){
        super(labelText, new JSpinner());

        comp.setModel(new SpinnerNumberModel(value, min, max, 1));
        final JFormattedTextField txt = ((JSpinner.NumberEditor) comp.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
    }

    public LabelledSpinner(final String labelText, final int value){
        this(labelText, value, value, Integer.MAX_VALUE);
    }

    public LabelledSpinner(final String labelText){
        this(labelText, -1);
    }

    public Integer value(){
        return (Integer) comp.getValue();
    }

    protected void set$(final Integer value){
        comp.setValue(value);
    }
}
