package cfgeditor.comp;

import javax.swing.JComboBox;

public class LabelledComboBox<E> extends LabelledComp<JComboBox<E>, E>{

    public LabelledComboBox(final String labelText, final E[] items){
        super(labelText, new JComboBox<>(items));
    }

    public E value(){
        return (E) comp.getSelectedItem();
    }

    protected void set$(final E value){
        comp.setSelectedItem(value);
    }
}
