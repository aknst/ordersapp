package mirea.prac16test;

import mirea.prac16.GUI.components.ButtonEditor;
import mirea.prac16.GUI.components.ButtonRenderer;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * @version 1.0 11/09/98
 */
public class JButtonTableExample extends JFrame {

    public JButtonTableExample() {
        super("JButtonTable Example");

        DefaultTableModel dm = new DefaultTableModel();
        dm.setDataVector(new Object[][] { { "button 1", "foo" },
                { "button 2", "bar" } }, new Object[] { "Button", "String" });

        JTable table = new JTable(dm);
        table.getColumn("Button").setCellRenderer(new ButtonRenderer());
        table.getColumn("Button").setCellEditor(
                new ButtonEditor(new JCheckBox()));
        JScrollPane scroll = new JScrollPane(table);
        getContentPane().add(scroll);
        setSize(400, 100);
        setVisible(true);
    }

    public static void main(String[] args) {


        JButtonTableExample frame = new JButtonTableExample();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}

