package mirea.prac16.GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SpinnerRenderer extends JSpinner implements TableCellRenderer {
    public SpinnerRenderer() {
        super();
        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) super.getEditor();
        editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected && !hasFocus) {
            setForeground(table.getSelectionForeground());
            super.setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(table.getBackground());
        }
        setValue(value);
        return this;
    }
}
