package gui;

import database.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.event.*;
import java.sql.*;

public class Starter extends JFrame {
    JPanel mainPanel = new JPanel();
    JFrame frame = new JFrame();
    private static JComboBox comboBox = new JComboBox();
    private static JTable table = new JTable();
    DefaultTableModel tm = (DefaultTableModel) table.getModel();
    private static JButton addButton = new JButton("Add Data");
    private static JButton deleteButton = new JButton("Delete");
    //connection to SQLite DB using JDBC
    private static Connection conn = null;
    public static String dbURL = "jdbc:sqlite:src/database/Client_Inventory_DB.db";

    public Starter() {
        frame.setTitle("Client Inventory Manager");
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        mainPanel.add(comboBox);
        mainPanel.add(addButton);
        mainPanel.add(deleteButton);


//Use JDBC to get table/view names from SQLite DB and add to JCombobox
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");
            ResultSet rs = null;
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, null, new String[]{"TABLE"});

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if (!tableName.equals("CLIENT_CONTACTS")) {
                    System.out.println(tableName + " added to combo box");
                    comboBox.addItem(tableName);
                }
            }
            comboBox.updateUI();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }

        addButton.addActionListener(action1);
        comboBox.addActionListener(updateTable);
        deleteButton.addActionListener(deleteData);
        mainPanel.setBackground(Color.white);


        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);

        //action listener for Jtable
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                int col = table.columnAtPoint(evt.getPoint());

                if (row >= 0 && col >= 0) {
                    String old_value = "";
                    old_value = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
                    String finalOld_value = old_value;
                    tm.addTableModelListener(new TableModelListener() {
                        //update values in db when jtable is edited
                        @Override
                        public void tableChanged(TableModelEvent tableModelEvent) {
                            String value = "";
                            if (table.isEditing()) {
                                value = (String) table.getValueAt(table.getSelectedRow(), table.getSelectedColumn());
                            }
                            System.out.println("Old Value: " + finalOld_value);
                            System.out.println("Value: " + value);
                            if (comboBox.getSelectedItem().equals("CLIENT")) {
                                dbOperations.updateValue("ID", "CLIENT", table.getColumnName(table.getSelectedColumn()), finalOld_value, value);
                            } else if (comboBox.getSelectedItem().equals("PRODUCT")) {
                                dbOperations.updateValue("PID", "PRODUCT", table.getColumnName(table.getSelectedColumn()), finalOld_value, value);
                            } else if (comboBox.getSelectedItem().equals("COMMISSION")) {
                                switch (table.getColumnName(table.getSelectedColumn())) {
                                    case "NAME":
                                        System.out.println("Name");
                                        dbOperations.updateValue("ID", "CLIENT", "Name", finalOld_value, value);
                                        break;
                                    case "Commissioned Product":
                                        dbOperations.updateValue("PID", "PRODUCT", "Description", finalOld_value, value);
                                        break;
                                    case "Price":
                                        dbOperations.updateValue("PID", "PRODUCT", "Price", finalOld_value, value);
                                        break;
                                    default:
                                        dbOperations.updateValue("ID", "COMMISSION", table.getColumnName(table.getSelectedColumn()), finalOld_value, value);
                                }
                            }
                        }
                    });
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(comboBox, BorderLayout.CENTER);
        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //Update JTable upon selecting JComboBox
    private void SelectComboBox() {
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM [" + comboBox.getSelectedItem() + "_VIEW" + "];");

            //get column info
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();


            tm.setColumnCount(0);

            for (int i = 1; i <= columnCount; i++) {
                tm.addColumn(rsmd.getColumnName(i));
            }

            // clear existing rows
            tm.setRowCount(0);

            // add rows to table
            while (rs.next()) {
                String[] a = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    a[i] = rs.getString(i + 1);
                }
                tm.addRow(a);
            }
            tm.fireTableDataChanged();
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    //Create new window for add button
    private void addFrame() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame addFrame = new JFrame();
                JPanel topPanel = new JPanel();
                JPanel addPanel = new JPanel();
                JPanel cards = new JPanel(new CardLayout());
                ;
                JPanel card1 = new JPanel(new FlowLayout());
                JPanel card2 = new JPanel(new BorderLayout());
                JPanel card3 = new JPanel(new FlowLayout());

                JComboBox comboBox2 = new JComboBox();
                JButton addToTableButton = new JButton("Add to Database");
                addFrame.setTitle("Add Data");
                addFrame.setSize(900, 300);
                addFrame.setLayout(new BorderLayout());

                int size = comboBox.getItemCount();

                for (int i = 0; i < size; i++) {
                    String item = (String) comboBox.getItemAt(i);
                    System.out.println(item);
                    comboBox2.addItem(item);
                }

                cards.add(card1, "CLIENT");
                cards.add(card2, "COMMISSION");
                cards.add(card3, "PRODUCT");

                //CLIENT PANEL
                int textfieldSize = 10;
                JLabel clientName = new JLabel("Name");
                JTextField NAME = new JTextField();
                JLabel clientStreet = new JLabel("Street");
                JTextField Street = new JTextField();
                JLabel clientCity = new JLabel("City");
                JTextField City = new JTextField();
                JLabel clientZip = new JLabel("Zip");
                JTextField Zip = new JTextField();
                JLabel clientContact = new JLabel("Contact");
                JTextField Contact_Info = new JTextField();

                NAME.setColumns(textfieldSize);
                Street.setColumns(textfieldSize);
                City.setColumns(textfieldSize);
                Zip.setColumns(textfieldSize);
                Contact_Info.setColumns(textfieldSize);

                card1.add(clientName);
                card1.add(NAME);
                card1.add(Street);
                card1.add(clientStreet);
                card1.add(Street);
                card1.add(clientCity);
                card1.add(City);
                card1.add(clientZip);
                card1.add(Zip);
                card1.add(clientContact);
                card1.add(Contact_Info);

                JComboBox clientBox = new JComboBox();
                try {
                    conn = DriverManager.getConnection(dbURL);
                    System.out.println("Connection to SQLite has been established");

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM CLIENT");

                    while (rs.next()) {
                        String client = rs.getString("NAME");
                        System.out.println(client + " added to combo box");
                        clientBox.addItem(client);

                    }
                    clientBox.updateUI();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                JComboBox productBox = new JComboBox();
                try {
                    conn = DriverManager.getConnection(dbURL);
                    System.out.println("Connection to SQLite has been established");

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT * FROM PRODUCT");

                    while (rs.next()) {
                        String description = rs.getString("DESCRIPTION");
                        System.out.println(description + " added to combo box");
                        productBox.addItem(description);

                    }
                    productBox.updateUI();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                JLabel clientLabel = new JLabel("Client");
                JLabel productLabel = new JLabel("Product");
                JLabel orderDateLabel = new JLabel("Order Date");
                JTextField Order_Date = new JTextField("yyyy-dd-mm");
                JLabel OrderStatusLabel = new JLabel("Order Status");
                JTextField Order_Status = new JTextField();
                JLabel paymentStatusLabel = new JLabel("Payment Status");
                JTextField Payment_Status = new JTextField();
                JLabel etcLabel = new JLabel("Estimated Time of Completion");
                JTextField ETC = new JTextField();

                Order_Date.setColumns(textfieldSize);
                Order_Status.setColumns(textfieldSize);
                Payment_Status.setColumns(textfieldSize);
                ETC.setColumns(textfieldSize);

                JPanel topCard2 = new JPanel(new FlowLayout());
                topCard2.add(clientLabel);
                topCard2.add(clientBox);
                topCard2.add(productLabel);
                topCard2.add(productBox);
                JPanel centerCard2 = new JPanel(new FlowLayout());
                centerCard2.add(orderDateLabel);
                centerCard2.add(Order_Date);
                centerCard2.add(OrderStatusLabel);
                centerCard2.add(Order_Status);
                centerCard2.add(paymentStatusLabel);
                centerCard2.add(Payment_Status);
                centerCard2.add(etcLabel);
                centerCard2.add(ETC);
                card2.add(topCard2, BorderLayout.NORTH);
                card2.add(centerCard2, BorderLayout.CENTER);


                JLabel descLabel = new JLabel("Description");
                JTextField Description = new JTextField();
                JLabel priceLabel = new JLabel("Price");
                JTextField Price = new JTextField();
                JLabel quantityLabel = new JLabel("Quantity");
                JTextField Quantity = new JTextField();

                Description.setColumns(50);
                Price.setColumns(textfieldSize);
                Quantity.setColumns(textfieldSize);

                card3.add(descLabel);
                card3.add(Description);
                card3.add(priceLabel);
                card3.add(Price);
                card3.add(quantityLabel);
                card3.add(Quantity);

                //change cards of jframe
                ActionListener changeCard = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("action");
                        CardLayout cl = (CardLayout) cards.getLayout();
                        if (comboBox2.getSelectedItem().equals("COMMISSION")) {
                            cl.show(cards, "COMMISSION");
                        } else if (comboBox2.getSelectedItem().equals("CLIENT")) {
                            cl.show(cards, "CLIENT");
                        } else if (comboBox2.getSelectedItem().equals("PRODUCT")) {
                            cl.show(cards, "PRODUCT");
                        }
                    }
                };

                //add data to db
                ActionListener addData = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.out.println("Send Query");
                        if (comboBox2.getSelectedItem().equals("COMMISSION")) {
                            int pid = dbOperations.getID("PRODUCT", "PID", (String) productBox.getSelectedItem(), "DESCRIPTION");
                            int id = dbOperations.getID("CLIENT", "ID", (String) clientBox.getSelectedItem(), "NAME");
                            dbOperations.sendQuery("INSERT INTO COMMISSION VALUES" +
                                    "(" + id + "," + pid + "," + "'" + Order_Date.getText() + "'," + "'" + Order_Status.getText() + "','" + Payment_Status.getText() + "','" + ETC.getText() + "');");
                        } else if (comboBox2.getSelectedItem().equals("CLIENT")) {
                            dbOperations.sendQuery("INSERT INTO CLIENT VALUES(NULL," + "'" + NAME.getText() + "'" + "," +
                                    "'" + Street.getText() + "'" + "," + "'" + City.getText() + "'" + "," + "'"
                                    + Zip.getText() + "'" + ");");

                            int id = dbOperations.getlastID("CLIENT");
                            dbOperations.closeConnection();
                            System.out.println("Insert Into Client");
                            dbOperations.sendQuery("INSERT INTO CLIENT_CONTACTS VALUES(" + id + "," + "'" + Contact_Info.getText() + "');");
                            dbOperations.closeConnection();
                        } else if (comboBox2.getSelectedItem().equals("PRODUCT")) {
                            dbOperations.sendQuery("INSERT INTO PRODUCT VALUES(NULL," + "'" + Description.getText() + "'" + "," +
                                    "'" + Price.getText() + "'" + "," + "'" + Quantity.getText() + "'" + ");");
                            dbOperations.closeConnection();
                        }
                        SelectComboBox();
                        addFrame.dispose();
                    }
                };

                addPanel.add(cards);
                addToTableButton.addActionListener(addData);
                comboBox2.addActionListener(changeCard);
                topPanel.add(addToTableButton);
                topPanel.add((comboBox2));
                addFrame.add(topPanel, BorderLayout.NORTH);
                addFrame.add(addPanel, BorderLayout.CENTER);

                addFrame.setVisible(true);
            }
        });
    }

    //ACTION LISTENERS

    ActionListener deleteData = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = "";

            s = (String) table.getValueAt(table.getSelectedRow(), 0);
            System.out.println(s);
            if (comboBox.getSelectedItem().equals("CLIENT")) {
                dbOperations.deleteSelectedRow("ID", (String) comboBox.getSelectedItem(), table.getColumnName(0), s);
            } else if (comboBox.getSelectedItem().equals("PRODUCT")) {
                dbOperations.deleteSelectedRow("PID", (String) comboBox.getSelectedItem(), table.getColumnName(0), s);
            } else if (comboBox.getSelectedItem().equals("COMMISSION")) {
                dbOperations.deleteCommissionRow("ID", (String) comboBox.getSelectedItem(), table.getColumnName(0), s);
            }
            SelectComboBox();

        }
    };

    ActionListener updateTable = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SelectComboBox();
        }
    };
    ActionListener action1 = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            addFrame();
        }
    };
}