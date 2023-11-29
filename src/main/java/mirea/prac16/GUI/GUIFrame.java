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

    private final TableOrdersManager tableOrdersManager = new TableOrdersManager();
    private final InternetOrdersManager internetOrdersManager = new InternetOrdersManager();

    private final Object[][] menuDrinkObjects = {
            {"Пиво", "500 мл", 700, 0, DrinkTypeEnum.BEER, 0.7},
            {"Вино", "750 мл", 1200, 0, DrinkTypeEnum.WINE, 0.2},
            {"Водка", "700 мл", 2000, 0, DrinkTypeEnum.VODKA, 0.6},
            {"Виски", "750 мл", 3500, 0, DrinkTypeEnum.WHISKEY, 0.5},
            {"Бренди", "700 мл", 2500, 0, DrinkTypeEnum.BRANDY, 0.4},
            {"Шампанское", "750 мл", 1500, 0, DrinkTypeEnum.CHAMPAGNE, 0.15},
            {"Текила", "500 мл", 1000, 0, DrinkTypeEnum.TEQUILA, 0.6},
            {"Ром", "700 мл", 2000, 0, DrinkTypeEnum.RUM, 0.6},
            {"Вермут", "750 мл", 1200, 0, DrinkTypeEnum.VERMOUTH, 0.2},
            {"Ликер", "700 мл", 3000, 0, DrinkTypeEnum.LIQUEUR, 0.3},
            {"Йегермейстер", "500 мл", 1500, 0, DrinkTypeEnum.JAGERMEISTER, 0.3},
            {"Сок", "300 мл", 200, 0, DrinkTypeEnum.JUICE, 0.05},
            {"Вода", "500 мл", 100, 0, DrinkTypeEnum.WATER, 0.0},
            {"Кофе", "200 мл", 300, 0, DrinkTypeEnum.COFFEE, 0.0},
            {"Зеленый чай", "300 мл", 200, 0, DrinkTypeEnum.GREEN_TEA, 0.0},
            {"Черный чай", "300 мл", 250, 0, DrinkTypeEnum.BLACK_TEA, 0.0},
            {"Молоко", "500 мл", 200, 0, DrinkTypeEnum.MILK, 0.0},
            {"Газировка", "500 мл", 150, 0, DrinkTypeEnum.SODA, 0.0},
            {"Кола", "500 мл", 200, 0, DrinkTypeEnum.COLA, 0.0}
    };

    private final Object[][] menuDishObjects = {
            {"Картошка с гречкой", "250 г", 150, 0},
            {"Курица с грибами", "300 г", 200, 0}
    };

    public GUIFrame() {
        /* Init frame */
        setContentPane(rootPanel);
        setTitle("Менеджер заказов");
        setSize(800, 600);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /* Setup customer form*/
        setPlaceholder(firstNameField, "Имя");
        setPlaceholder(lastNameField, "Фамилия");

        setupMenuItemsTable();

        selectAgeField.setMaximumRowCount(10);
        for (int age = 14; age <= 60; age++) {
            String ageLabel = age + " " + getAgeForm(age);
            selectAgeField.addItem(new SelectItem(ageLabel, age));
        }

        updateTableOrAddressForm();
        selectOrderType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setupMenuItemsTable();
                updateTableOrAddressForm();
            }

        });
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validateForm();
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
        iDm.setColumnIdentifiers(new String[]{"Номер в очереди", "Клиент", "Адрес", "Заказ", "Стоимость", "Действия"});

        internetOrdersTable.setDragEnabled(false);
        internetOrdersTable.getTableHeader().setReorderingAllowed(false);


        ButtonEditor button2 = new ButtonEditor(new JCheckBox());
        internetOrdersTable.getColumnModel().getColumn(5).setCellEditor(button2);
        internetOrdersTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());

        button2.getButton().addActionListener(e -> {
            int row = button2.getCurrentRow();
            button2.stopCellEditing();
            button2.fireEditingStopped();
            if (row == iDm.getRowCount() - 1) {
                internetOrdersManager.remove();
                updateInternetOrders();
            } else {
                JOptionPane.showMessageDialog(
                        waiterPanel,
                        "Удаляйте интернет-заказы в порядке очереди!",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

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
        tableOrdersSum.setText(String.format("Всего заказов: %d, на сумму: %d", tableOrdersManager.ordersQuantity(),  tableOrdersManager.ordersCostSummary()));
    }

    private void updateInternetOrders() {
        Order[] orders = internetOrdersManager.getOrders();

        DefaultTableModel tDm = (DefaultTableModel) internetOrdersTable.getModel();
        tDm.getDataVector().removeAllElements();
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == null) continue;
            Order order = orders[i];
            int pos = i + 1;
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
            String address = order.getCustomer().getAddress().toString();
            tDm.addRow(new Object[]{pos, customer, address, orderMenuItems, costTotal, "Удалить"});
        }
        internetOrdersSum.setText(String.format("Всего заказов: %d, на сумму: %d", internetOrdersManager.ordersQuantity(),  internetOrdersManager.ordersCostSummary()));

    }


    private void setupMenuItemsTable() {
        finalLabel.setText("Итоговое количество: 0, цена: 0");
        DefaultTableModel dm = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                //Only the third column
                return column == 3;
            }
        };
        String[] columnNames = {"Пункты меню", "Описание", "Цена", "Кол-во"};
        dm.setColumnIdentifiers(columnNames);
        for (Object[] rowData : menuDrinkObjects) {
            dm.addRow(rowData);
        }
        for (Object[] rowData : menuDishObjects) {
            dm.addRow(rowData);
        }
        menuItemsTable.setModel(dm);
        menuItemsTable.setDragEnabled(false);
        menuItemsTable.getTableHeader().setReorderingAllowed(false);
        SpinnerEditor spinner = new SpinnerEditor();
        menuItemsTable.getColumnModel().getColumn(3).setCellEditor(spinner);

        TableOrder s = new TableOrder();
        spinner.getSpinner().addChangeListener(e -> {
            int row = spinner.getCurrentRow();

            String menuItemName = menuItemsTable.getValueAt(row, 0).toString();
            Object[] drinkData = null;

            for (Object[] rowData : menuDrinkObjects) {
                if (rowData[0].equals(menuItemName)) {
                    drinkData = rowData;
                }
            }

            String menuItemDesc = menuItemsTable.getValueAt(row, 1).toString();
            int menuItemPrice = (int) menuItemsTable.getValueAt(row, 2);

            int menuItemQuantity = s.itemsQuantity(menuItemName);
            int currentQuantity = (int) spinner.getCellEditorValue();

            if (menuItemQuantity != currentQuantity) {
                int dif = currentQuantity - menuItemQuantity;
                while (dif != 0) {
                    if (dif > 0) {
                        if (drinkData != null) {
                            SelectItem ageItem = (SelectItem) selectAgeField.getSelectedItem();
                            int age = ageItem.getValue();
                            double alcVol = (double) drinkData[5];
                            boolean canAdd = false;

                            if (age >= 18) {
                                canAdd = true;
                            } else {
                                if (alcVol == 0.0) {
                                    canAdd = true;
                                }
                            }

                            if (canAdd) {
                                s.add(new Drink(
                                        menuItemName,
                                        menuItemDesc,
                                        menuItemPrice,
                                        (DrinkTypeEnum) drinkData[4],
                                        alcVol)
                                );
                            } else {
                                spinner.setValue(currentQuantity - 1);
                                JOptionPane.showMessageDialog(
                                        customerPanel,
                                        "Вам должно быть 18+ лет!",
                                        "Ошибка",
                                        JOptionPane.ERROR_MESSAGE
                                );
                            }
                            dif -= 1;
                        } else {
                            s.add(new MenuItem(menuItemName, menuItemDesc, menuItemPrice));
                            dif -= 1;
                        }
                    } else {
                        s.remove(menuItemName);
                        dif += 1;
                    }
                }
            }
            finalLabel.setText(String.format("Итоговое количество: %d, цена: %d", s.itemsQuantity(), s.costTotal()));
            spinner.stopCellEditing();
            spinner.fireEditingStopped();
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
                JOptionPane.showMessageDialog(
                        customerPanel,
                        "Новый заказ к столу успешно добавлен!",
                        "Новый заказ",
                        JOptionPane.INFORMATION_MESSAGE
                );
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

            /*
            *     private JTextField zipCodeField;
    private JTextField streetNameField;
    private JTextField buildingNumberField;
    private JTextField buildingLetterField;
    private JTextField apartmentNumberField;*/

            String city = cityNameField.getText();
            String street = streetNameField.getText();
            String zipCodeTxt = zipCodeField.getText();
            String buildingNumber = buildingNumberField.getText();
            String buildingLetter = buildingLetterField.getText();
            String apNumber = apartmentNumberField.getText();
            int bNumber = parseToInt(buildingNumber.replaceAll("[^0-9]", ""), 0);
            int aNumber = parseToInt(apNumber.replaceAll("[^0-9]", ""), 0);
            int zipCode = parseToInt(zipCodeTxt.replaceAll("[^0-9]", ""), 0);
            char letter = buildingLetter.charAt(0);

            Address iAddress = new Address(city, street, bNumber, aNumber,  zipCode, letter);
            Customer iCustomer = new Customer(firstName, lastName, iAddress, ageItem.getValue());
            iOrder.setCustomer(iCustomer);

            for (MenuItem menuItem : menuItemsList) {
                iOrder.add(menuItem);
            }

            if (iOrder.itemsQuantity() > 0) {
                internetOrdersManager.add(iOrder);
                updateInternetOrders();
                JOptionPane.showMessageDialog(
                        customerPanel,
                        "Новый интернет-заказ успешно добавлен в очередь!",
                        "Новый заказ",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        customerPanel,
                        "Добавьте что-нибудь в заказ!",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE
                );
            }


            return validateInternetOrder();
        }
        return true;
    }

    public static int parseToInt(String stringToParse, int defaultValue) {
        try {
            return Integer.parseInt(stringToParse);
        } catch(NumberFormatException ex) {
            return defaultValue; //Use default value if parsing failed
        }
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

