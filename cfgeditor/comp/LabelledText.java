package cfgeditor.comp;

import javax.swing.JLabel;
import javax.swing.JTextField;

public class LabelledText extends LabelledComp<JTextField, String>{

    public LabelledText(final String labelText, final String value){
        super(labelText, new JTextField(value));

        comp.setHorizontalAlignment(JLabel.CENTER);
    }

    public LabelledText(final String labelText){
        this(labelText, "");
    }

    public String value(){
        return comp.getText().trim();
    }

    protected void set$(final String value){
        comp.setText(value);
    }
}
