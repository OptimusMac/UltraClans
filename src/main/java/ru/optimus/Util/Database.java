package ru.optimus.Util;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {

    private String url;
    private String host;
    private String username;
    private String port;
    private String password;

    public Connection connection;

    public Database(String host, String username, String port, String password, String database) {
        this.host = host;
        this.username = username;
        this.port = port;
        this.password = password;
        this.url = "jdbc:mysql://%host:%port/%name";
        this.url = this.url.replace("%host", host);
        this.url = this.url.replace("%port", port);
        this.url = this.url.replace("%name", database);
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Connection getConnection() {
        return connection;
    }

    public void Connection(){
        try {
            connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void exit(){
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createTable(String SqlExecute) {
        try {
            PreparedStatement stmt = connection.prepareStatement(SqlExecute);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("Table is created!");

    }


    public void dropTable(String table) {
        String sql = "DROP TABLE %table;".replace("%table", table);
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Bukkit.getLogger().info("Table is dropped!");
    }


}
