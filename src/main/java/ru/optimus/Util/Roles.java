package ru.optimus.Util;

import org.bukkit.ChatColor;
import ru.optimus.UltraClans;

import javax.swing.plaf.nimbus.State;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static ru.optimus.Clans.ClanManager.removeLast;

public class Roles {


    private ArrayList<String> permissions;

    private String name;
    private String clanName;
    private String color;

    public Roles(String name, String clanName, String color, ArrayList<String> permissions) {
        this.name = name;
        this.clanName = clanName;
        this.color = color;
        this.permissions = permissions;
    }

    public ArrayList<String> getPermissions() {
        return permissions;
    }

    public String getName() {
        return getColor() + name;
    }

    public String getClanName() {
        return clanName;
    }

    public String getColor() {
        return "§" + color;
    }

    public void setColor(ChatColor color) {
        char c = color.getChar();
        this.color = String.valueOf(c);
        updateColor();
    }

    public boolean hasPermission(String permission) {
        return getPermissions().contains(permission);
    }

    public void addPermission(String permission) {
        getPermissions().add(permission);
        updatePermissions();
    }

    public void removePermission(String permission) {
        getPermissions().remove(permission);
        updatePermissions();
    }

    public void setNameRole(String name) {
        this.name = name;
        updateName();
    }

    public void updateName() {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE Roles SET RoleName = '%name' WHERE RoleName='%name'".replace("%name", getName());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateColor() {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE Roles SET Color = '%color' WHERE RoleName='%name'".replace("%color", getColor().replace("§", "")).replace("%name", ChatColor.stripColor(getName()));
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updatePermissions() {
        String query = "SELECT * FROM " + "Roles WHERE RoleName='%name'".replace("%name", getName());
        try {
            Statement stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);

            StringBuilder result = new StringBuilder();

            for (String u : getPermissions()) {
                result.append(u).append(",");
            }

            String permissions = removeLast(result.toString(), 1);

            String queryUpdate = "UPDATE Roles SET Permissions = '%permissions' WHERE RoleName='%name'".replace("%name", getName()).replace("%permissions", permissions);
            stmt.executeUpdate(queryUpdate);
            stmt.close();
            resultSet.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
