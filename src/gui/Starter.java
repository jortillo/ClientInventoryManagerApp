package gui;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.sql.*;

public class Starter extends JFrame{
    JPanel mainPanel = new JPanel();
    JFrame frame = new JFrame();
    private static JComboBox comboBox = new JComboBox();
    private static JTable table = new JTable();
    private static Connection conn = null;
    public static String dbURL = "jdbc:sqlite:src/database/Client_Inventory_DB.db";
    public Starter(){
        frame.setTitle("Client Inventory Manager");
        frame.setSize(900,700);
        frame.setLayout(new BorderLayout());
        mainPanel.setLayout(new FlowLayout(FlowLayout.CENTER    ));
        mainPanel.add(comboBox);

//Use JDBC to get table/view names from SQLite DB and add to JCombobox
        try{
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");
            ResultSet rs = null;
            DatabaseMetaData meta = conn.getMetaData();
            rs = meta.getTables(null, null, null, new String[]{"TABLE"});


            //System.out.println("Add info added to combo box");
            //comboBox.addItem("Add info");

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                if(!tableName.equals("CLIENT_CONTACTS")) {
                    System.out.println(tableName + " added to combo box");
                    comboBox.addItem(tableName);
                }
            }
            comboBox.updateUI();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                if (conn != null){
                    conn.close();
                }
            } catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
        comboBox.addActionListener(updateTable);
        mainPanel.setBackground(Color.white);

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);


        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(comboBox , BorderLayout.CENTER);
        frame.add(mainPanel,BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.add(mainPanel, BorderLayout.NORTH);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }


    //Update JTable upon selecting JComboBox
    private void SelectComboBox(){
        try{
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM [" + comboBox.getSelectedItem() +"_VIEW" +"];");

            //get column info
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            DefaultTableModel tm = (DefaultTableModel) table.getModel();
            tm.setColumnCount(0);
            for (int i = 1; i <= columnCount; i++ ) {
                tm.addColumn(rsmd.getColumnName(i));
            }

            // clear existing rows
            tm.setRowCount(0);

            // add rows to table
            while (rs.next()) {
                String[] a = new String[columnCount];
                for(int i = 0; i < columnCount; i++) {
                    a[i] = rs.getString(i+1);
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
                if (conn != null){
                    conn.close();
                }
            } catch(SQLException ex){
                System.out.println(ex.getMessage());
            }
        }
    }
    ActionListener updateTable = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            SelectComboBox();
        }
    };
}
