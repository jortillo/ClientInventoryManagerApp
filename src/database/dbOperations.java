package database;

import gui.Starter;

import java.sql.*;

public class dbOperations {
    //connection to SQLite DB using JDBC
    private static Connection conn = null;
    public static String dbURL = "jdbc:sqlite:src/database/Client_Inventory_DB.db";

    //method to close connection
    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Send a query to database
    public static void sendQuery(String query) {
        try {
            conn = DriverManager.getConnection(dbURL);
            conn.createStatement().execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("ID: " + key);
        return (int) key;
    }

    //return ID of a table
    public static int getID(String tableName, String i, String condition, String column) {
        int id = 0;
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + i + " FROM " + tableName + " WHERE " + column + " LIKE " + "'" + condition + "';");
            while (rs.next()) {
                id = rs.getInt(i);
            }


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
            return id;
        }
    }

    //delete row from commission table
    public static void deleteCommissionRow(String ID, String tableName, String column, String condition) {
        int id = 0;
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + ID + " FROM " + "CLIENT" + " WHERE " + column + " LIKE " + "'" + condition + "';");
            while (rs.next()) {
                id = rs.getInt(ID);
            }
            System.out.println("DELETE FROM " + tableName + " WHERE " + ID + " LIKE '" + id + "';");
            conn.createStatement().execute("DELETE FROM " + tableName + " WHERE " + ID + " LIKE '" + id + "';");

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //delete a selected row from table
    public static void deleteSelectedRow(String ID, String tableName, String column, String condition) {
        int id = 0;
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + ID + " FROM " + tableName + " WHERE " + column + " LIKE " + "'" + condition + "';");
            while (rs.next()) {
                id = rs.getInt(ID);
            }
            System.out.println("DELETE FROM " + tableName + " WHERE " + ID + " LIKE '" + id + "';");
            conn.createStatement().execute("DELETE FROM " + tableName + " WHERE " + ID + " LIKE '" + id + "';");

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //update value from table
    public static void updateValue(String ID, String tableName, String column, String condition, String values) {
        int id = 0;
        try {
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connection to SQLite has been established");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT " + ID + " FROM " + tableName + " WHERE " + column + " LIKE " + "'" + condition + "';");
            while (rs.next()) {
                id = rs.getInt(ID);
            }

            System.out.println("UPDATE " + tableName + " SET " + column + " = '" + values + "' WHERE " + ID + " LIKE '" + id + "';");
            conn.createStatement().execute("UPDATE " + tableName + " SET " + column + " = '" + values + "' WHERE " + ID + " LIKE '" + id + "';");

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Send a query and return a ResultSet
    public static ResultSet queryReturnResult(String query) {
        ResultSet result = null;
        try {
            result = conn.createStatement().executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
