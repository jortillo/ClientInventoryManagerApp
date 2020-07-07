package database;
import gui.Starter;

import java.sql.*;
public class dbOperations {
    //connection to SQLite DB using JDBC
    private static Connection conn = null;
    public static String dbURL = "jdbc:sqlite:src/database/Client_Inventory_DB.db";

    public void insert(String tableName, String values) {
        String sql = "INSERT INTO" + tableName +
                "VALUES(" + values + ")";

        try{
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");
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
    }

    public static void closeConnection(){
        try {
            conn.close();
        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    // Send a query to database
    public static void sendQuery(String query) {
        try {
            conn = DriverManager.getConnection(dbURL);
            conn.createStatement().execute(query);

        }
        catch (SQLException e) { e.printStackTrace(); }
    }

    //return last inserted ID
    public static int getlastID(String tableName) {
        long key = -1L;
        try {

            Statement statement = conn.createStatement();
            statement.execute("SELECT last_insert_rowid() AS LAST FROM " + tableName);
            ResultSet rs = statement.getGeneratedKeys();
            if (rs != null && rs.next()) {
                key = rs.getLong(1);
            }

        }
        catch (SQLException e) { e.printStackTrace();
        }
        System.out.println("ID: "+key);
        return (int) key;
    }

    // Send a query and return a ResultSet
    public static ResultSet queryReturnResult(String query) {
        ResultSet result = null;
        try { result = conn.createStatement().executeQuery(query); }
        catch (SQLException e) { e.printStackTrace(); }
        return result;
    }
}
