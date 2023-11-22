package mirea.prac16.GUI;

import javax.swing.*;

import com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme;
import mirea.prac16.GUI.components.*;
import mirea.prac16.backend.*;
import mirea.prac16.backend.MenuItem;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.table.*;
import javax.swing.JTable;

public class GUIFrame extends JFrame {
    private JPanel rootPanel;
    private JTabbedPane tabbedPane;
    private JPanel waiterPanel;
    private JPanel customerPanel;
    private JScrollPane itemsPane;
    private JComboBox selectOrderType;
    private JButton confirmButton;
    private JLabel newOrderLabel;
    private JLabel orderTypeLabel;
    private JLabel finalLabel;
    private JFormattedTextField firstNameField;
    private JFormattedTextField lastNameField;
    private JComboBox selectAgeField;
    private JPanel tableOrAddressForm;
    private JTable menuItemsTable;
    private JScrollPane tableOrders;
    private JScrollPane internetOrders;
    private JLabel tableOrdersSum;
    private JLabel internetOrdersSum;
    private JTable internetOrdersTable;
    private JTable tableOrdersTable;


    private JComboBox selectTable;
    private JTextField cityNameField;
    private JTextField zipCodeField;
    private JTextField streetNameField;
    private JTextField buildingNumberField;
    private JTextField buildingLetterField;
    private JTextField apartmentNumberField;

    private TableOrdersManager tableOrdersManager = new TableOrdersManager();
    private InternetOrdersManager internetOrdersManager = new InternetOrdersManager();
    

    public GUIFrame() {
        /* Init frame */
        setContentPane(rootPanel);
        setTitle("Order Manager App");
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /* Setup customer form*/
        setPlaceholder(firstNameField, "Ваше имя");
        setPlaceholder(lastNameField, "Ваша фамилия");

        setupMenuItemsTable();

        selectAgeField.setMaximumRowCount(10);
        for (int age = 14; age <= 60; age++) {
            String ageLabel = age + " " + getAgeForm(age);
            selectAgeField.addItem(new SelectItem(ageLabel, age));
        }



        updateTableOrAddressForm();
        selectOrderType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTableOrAddressForm();
            }

        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateForm();
                JOptionPane.showMessageDialog(customerPanel, "Тест");
                System.out.println(firstNameField.getText());
                updateTableOrAddressForm();
            }
        });

        setupWaiterTables();

        setupTableTooltips();
    }

    private String getAgeForm(int age) {
        // Определяем форму слова для возраста в зависимости от последних цифр
        int lastDigit = age % 10;
        int secondToLastDigit = (age / 10) % 10;

        if (secondToLastDigit == 1) {
            return "лет";
        } else {
            // В остальных случаях определяем форму по последней цифре
            return switch (lastDigit) {
                case 1 -> "год";
                case 2, 3, 4 -> "года";
                default -> "лет";
            };
        }
    }

    private void setupWaiterTables() {
        DefaultTableModel tDm = (DefaultTableModel) tableOrdersTable.getModel();
        tableOrdersTable.setDragEnabled(false);
        tableOrdersTable.getTableHeader().setReorderingAllowed(false);
        tDm.setColumnIdentifiers(new String[]{"Номер стола", "Клиент", "Заказ", "Стоимость", "Действия"});

        ButtonEditor button = new ButtonEditor(new JCheckBox());
        tableOrdersTable.getColumnModel().getColumn(4).setCellEditor(button);
        tableOrdersTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());

        button.getButton().addActionListener(e -> {
            int row = button.getCurrentRow();
            int tableNumber = (int) tableOrdersTable.getValueAt(row, 0);
            tableOrdersManager.remove(tableNumber - 1);
            button.stopCellEditing();
            button.fireEditingStopped();
            updateTableOrders();
            updateTableOrAddressForm();
        });

        DefaultTableModel iDm = (DefaultTableModel) internetOrdersTable.getModel();
        internetOrdersTable.setDragEnabled(false);
        internetOrdersTable.getTableHeader().setReorderingAllowed(false);
        iDm.setColumnIdentifiers(new String[]{"Номер в списке", "Клиент", "Адрес", "Заказ", "Стоимость", "Действия"});
    }

    private void updateTableOrders() {
        Order[] orders = tableOrdersManager.getOrders();

        DefaultTableModel tDm = (DefaultTableModel) tableOrdersTable.getModel();

        tDm.getDataVector().removeAllElements();

        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == null) continue;
            Order order = orders[i];
            int table = i + 1;
            int costTotal = order.costTotal();
            String[] menuItemsNames = order.itemsNames();
            StringBuilder orderItems = new StringBuilder();
            for (String menuItemName : menuItemsNames) {
                int itemQuantity = order.itemsQuantity(menuItemName);
                String orderItem = String.format("%s x %d, ", menuItemName, itemQuantity);
                orderItems.append(orderItem);
            }

            String orderMenuItems;
            orderMenuItems = orderItems.substring(0, orderItems.length() - 2);
            String customer = order.getCustomer().toString();
            tDm.addRow(new Object[]{table, customer, orderMenuItems, costTotal, "Удалить"});
        }
    }


    private void setupMenuItemsTable() {
        DefaultTableModel dm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //Only the third column
                return column == 3;
            }
        };
        String[] columnNames = {"Пункты меню", "Описание", "Цена", "Кол-во"};
        Object[] data1 = {"Dish1", "Description1 dsfsdfsdfsafsdfsadfsdfsfsadfsadfsadfasdf", 10, 0};
        Object[] data2 = {"Dish2", "Description2", 15, 0};
        dm.setColumnIdentifiers(columnNames);
        dm.addRow(data1);
        dm.addRow(data2);
        menuItemsTable.setModel(dm);
        menuItemsTable.setDragEnabled(false);
        menuItemsTable.getTableHeader().setReorderingAllowed(false);
        SpinnerEditor spinner = new SpinnerEditor();
        menuItemsTable.getColumnModel().getColumn(3).setCellEditor(spinner);

        TableOrder s = new TableOrder();
        spinner.getSpinner().addChangeListener(e -> {
            int row = spinner.getCurrentRow();

            String menuItemName = menuItemsTable.getValueAt(row, 0).toString();
            String menuItemDesc = menuItemsTable.getValueAt(row, 1).toString();
            int menuItemPrice = (int) menuItemsTable.getValueAt(row, 2);

            int menuItemQuantity = s.itemsQuantity(menuItemName);
            int currentQuantity = (int) spinner.getCellEditorValue();

            if (menuItemQuantity != currentQuantity) {
                int dif = currentQuantity - menuItemQuantity;
                while (dif != 0) {
                    if (dif > 0) {
                        s.add(new MenuItem(menuItemName, menuItemDesc, menuItemPrice));
                        dif -= 1;
                    } else {
                        s.remove(menuItemName);
                        dif += 1;
                    }
                }
            }
            finalLabel.setText(String.format("%d", s.itemsQuantity()));
            spinner.stopCellEditing();
        });
        menuItemsTable.getColumnModel().getColumn(3).setCellRenderer(new SpinnerRenderer());
    }
    
    private void setupTableTooltips() {
        JTable[] tables = {menuItemsTable, tableOrdersTable, internetOrdersTable};
        
        for (JTable table : tables) {
           table.addMouseMotionListener(new MouseMotionAdapter() {
               @Override
               public void mouseMoved(MouseEvent e) {
                   table.setToolTipText("");

                   Object value = table.getValueAt(
                           table.rowAtPoint(e.getPoint()),
                           table.columnAtPoint(e.getPoint())
                   );

                   String res = (value == null) ? null : value.toString();

                   if (res != null && res.length() > 15) {
                       table.setToolTipText(res);
                   }
               }
           }); 
        }
    }


    private void updateSelectTableItems(JComboBox obj) {
        obj.removeAllItems();
        obj.setMaximumRowCount(5);
        int[] freeTables = tableOrdersManager.freeTableNumbers();
        for (int i = 0; i < freeTables.length; i++) {
            obj.addItem(new SelectItem(String.format("Стол №%d", freeTables[i]+1), freeTables[i]));
        }
    }

    private void updateTableOrAddressForm() {
        String selectedOrderType = (String) selectOrderType.getSelectedItem();
        tableOrAddressForm.removeAll();

        if ("Обычный заказ".equals(selectedOrderType)) {
            tableOrAddressForm.setLayout(new GridLayout(1, 4));
            JLabel tableLabel = new JLabel("Выберите свободный стол:");
            tableOrAddressForm.add(tableLabel);
            selectTable = new JComboBox();

            updateSelectTableItems(selectTable);

            tableOrAddressForm.add(selectTable);
        } else if ("Интернет заказ".equals(selectedOrderType)) {
            tableOrAddressForm.setLayout(new GridLayout(3, 4, 10, 10));

            cityNameField = new JTextField();
            setPlaceholder(cityNameField, "Город");
            tableOrAddressForm.add(cityNameField);

            zipCodeField = new JTextField();
            setPlaceholder(zipCodeField, "Zip-код");
            tableOrAddressForm.add(zipCodeField);

            streetNameField = new JTextField();
            setPlaceholder(streetNameField, "Улица");
            tableOrAddressForm.add(streetNameField);

            buildingNumberField = new JTextField();
            setPlaceholder(buildingNumberField, "Номер дома");
            tableOrAddressForm.add(buildingNumberField);

            buildingLetterField = new JTextField();
            setPlaceholder(buildingLetterField, "Буква дома");
            tableOrAddressForm.add(buildingLetterField);

            apartmentNumberField = new JTextField();
            setPlaceholder(apartmentNumberField, "Номер квартиры");
            tableOrAddressForm.add(apartmentNumberField);
        }

        tableOrAddressForm.repaint();
        tableOrAddressForm.revalidate();
    }

    private void setPlaceholder(JTextField textField, String placeholder) {
        textField.getForeground();
        textField.setText(placeholder);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    // textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private boolean validateForm() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        SelectItem ageItem = (SelectItem) selectAgeField.getSelectedItem();

        Customer customer = new Customer(firstName, lastName, Address.EMPTY_ADDRESS, ageItem.getValue());

        System.out.println(ageItem.getValue());
        String selectedOrderType = (String) selectOrderType.getSelectedItem();

        ArrayList<MenuItem> menuItemsList = getMenuItemsFromTable();

        for (MenuItem menuItem : menuItemsList) {
            System.out.println(menuItem);
        }

        if ("Обычный заказ".equals(selectedOrderType)) {
            SelectItem tableItem = (SelectItem) selectTable.getSelectedItem();
            int tableNumber = tableItem.getValue();
            TableOrder tOrder;
            tOrder = new TableOrder();
            tOrder.setCustomer(customer);

            for (MenuItem menuItem : menuItemsList) {
                tOrder.add(menuItem);
            }

            if (tOrder.itemsQuantity() > 0) {
                tableOrdersManager.add(tOrder, tableNumber);
                updateTableOrders();
            } else {
                JOptionPane.showMessageDialog(
                    customerPanel,
                    "Добавьте что-нибудь в заказ!",
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE
                );
            }

            return validateTableOrder();
        } else if ("Интернет заказ".equals(selectedOrderType)) {
            InternetOrder iOrder;
            iOrder = new InternetOrder();
            iOrder.setCustomer(customer);


            return validateInternetOrder();
        }
        return true;
    }



    private ArrayList<MenuItem> getMenuItemsFromTable() {
        DefaultTableModel model = (DefaultTableModel) menuItemsTable.getModel();
        int rowCount = model.getRowCount();
        ArrayList<MenuItem> itemsList = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            String menuItemName = model.getValueAt(i, 0).toString();
            String menuItemDesc = model.getValueAt(i, 1).toString();
            int menuItemPrice = Integer.parseInt(model.getValueAt(i, 2).toString());
            int currentQuantity = (int) model.getValueAt(i, 3); // Assuming quantity is in column index 3

            for (int j = 0; j < currentQuantity; j++) {
                itemsList.add(new MenuItem(menuItemName, menuItemDesc, menuItemPrice));
            }
        }

        return itemsList;
    }


    private boolean validateInternetOrder() {
        return true;
    }

    private boolean validateTableOrder() {

        return true;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightFlatIJTheme());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GUIFrame();
    }
}

