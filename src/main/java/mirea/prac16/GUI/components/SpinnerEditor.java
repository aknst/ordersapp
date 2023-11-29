package mirea.prac16.GUI.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.DefaultFormatter;
import java.awt.*;

public class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {

    protected JSpinner editorComponent;
    private int currentRow;

    public SpinnerEditor() {
        editorComponent = new JSpinner();


        editorComponent.setModel(new SpinnerNumberModel(0, 0, 100, 1)); // Customize as needed

        JSpinner.NumberEditor editor = (JSpinner.NumberEditor) editorComponent.getEditor();
        DefaultFormatter formatter = (DefaultFormatter) editor.getTextField().getFormatter();
        formatter.setCommitsOnValidEdit(true);
        editor.getTextField().setHorizontalAlignment(SwingConstants.LEFT);

        editorComponent.setBorder(new EmptyBorder(0, 0, 0, 0));
        editorComponent.addChangeListener(e -> {
            SpinnerEditor.this.stopCellEditing();
        });
    }

    public void setValue(Object value) {
        editorComponent.setValue(value);
    }

    public Component getComponent() {
        return editorComponent;
    }
    public JSpinner getSpinner() {
        return editorComponent;
    }

    public Object getCellEditorValue() {
        return editorComponent.getValue();
    }

    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        this.currentRow = row;
        setValue(value);
        return editorComponent;
    }

    public int getCurrentRow() {
        return this.currentRow;
    }

    public void fireEditingStopped() {
        super.fireEditingStopped();
    }
}
