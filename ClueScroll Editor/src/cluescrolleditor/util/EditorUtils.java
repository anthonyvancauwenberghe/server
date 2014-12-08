package cluescrolleditor.util;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.NumberFormatter;

public final class EditorUtils {

    private EditorUtils(){}

    public static JSpinner createSpinner(final int value, final int min, final int max){
        final JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, 1));
        final JFormattedTextField txt = ((JSpinner.NumberEditor) spinner.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false);
        return spinner;
    }
}
