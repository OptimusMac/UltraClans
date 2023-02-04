package ru.optimus.Util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RolesHandler {

    private static Set<Roles> roles = new HashSet<>();

    public static void initRoles() {
        String query = "SELECT * FROM " + "Roles;";
        try {
            Statement stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            String role;
            String nameClan;
            String color;
            ArrayList<String> permissions = new ArrayList<>();
            List<String> perms = new ArrayList<>();
            while (resultSet.next()) {
                String string = resultSet.getString(5);
                for (String str : string.split(",")) {
                    if (str == null) continue;
                    perms.add(str);
                }
                permissions.addAll(perms);
                nameClan = resultSet.getString(2);
                role = resultSet.getString(3);
                color = resultSet.getString(4);

                Roles clan = new Roles(role, nameClan, color, permissions);
                addRole(clan);
            }

        } catch (SQLException ignored) {
        }
    }


    private static void addRole(Roles role) {
        roles.add(role);
    }

    public static Set<Roles> getRoles() {
        return roles;
    }

    public static Roles getRoleByName(String clan, String name) {
        for (Roles role : getRoles()) {
            if (ChatColor.stripColor(role.getName()).equalsIgnoreCase(ChatColor.stripColor(name))) {
                return role;
            }
        }
        return returnRoleByName(name, clan);
    }

    public static boolean hasRoleByName(String name){
        for(Roles r : getRoles()){
            if(ChatColor.stripColor(r.getName()).equalsIgnoreCase(ChatColor.stripColor(name))){
                return true;
            }
        }
        return false;
    }

    private static Roles returnRoleByName(String name, String clan) {
        String query = "SELECT * FROM " + "Roles WHERE RoleName='%role' AND NameClan='%clan';".replace("%clan", clan).replace("%role", ChatColor.stripColor(name));
        try {
            Statement stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            String role;
            String nameClan;
            String color;
            ArrayList<String> permissions = new ArrayList<>();
            List<String> perms = new ArrayList<>();
            while (resultSet.next()) {
                String string = resultSet.getString(5);
                for (String str : string.split(",")) {
                    if (str == null) continue;
                    perms.add(str);
                }
                permissions.addAll(perms);
                nameClan = resultSet.getString(2);
                role = resultSet.getString(3);
                color = resultSet.getString(4);
                Roles r = new Roles(role,nameClan, color, permissions);
                getRoles().add(r);
                return r;
            }

        } catch (SQLException ignored) {
        }
        return null;
    }

    public static void createRole(String name, String nameClan, String color, ArrayList<String> permissions) {
        Roles roles = new Roles(name, nameClan, color, permissions);
        getRoles().add(roles);
        permissions = new ArrayList<>();
        permissions.add("ru.ultraclans.clanChat");
        StringBuilder builder = new StringBuilder();

        for (String u : permissions) {
            builder.append(u).append(",");
        }

        String perm = ClanManager.removeLast(builder.toString(), 1);

        String sql = "INSERT Roles(NameClan, RoleName, Color, Permissions) VALUES ('%name', '%role', '%color', '%permissions')".replace("%permissions", perm).replace("%color", color).replace("%role", name).replace("%name", nameClan);

        try {
            Statement statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {

        }
    }

}
