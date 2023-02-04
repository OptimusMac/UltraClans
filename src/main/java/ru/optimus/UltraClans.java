package ru.optimus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import ru.optimus.Clans.ClanManager;
import ru.optimus.Util.Database;
import ru.optimus.Util.RolesHandler;
import ru.optimus.handlers.Commands.ClanChatCommand;
import ru.optimus.handlers.Commands.ClanCommand;
import ru.optimus.handlers.Listeners.AbilitiesListener;
import ru.optimus.handlers.Listeners.ClanChatEvent;
import ru.optimus.handlers.Listeners.EventsToUpExperience;
import ru.optimus.handlers.Listeners.PlayerLIstener;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class UltraClans extends JavaPlugin {

    public String prefix;
    private static UltraClans instance;
    public File fileDatabase;
    public FileConfiguration database;

    private Database db;

    public int maxUsers;
    public int maxLevel;
    public int multiply_level_to_experience;
    public boolean format_chat;
    public boolean spawnFireWorks;
    //Events
    public String breakBlock;
    public String attack;
    public String crafting;
    public String smelting;
    public String baseRole;
    public String format;
    private Set<UUID> toggleClanChat = new HashSet<>();


    @Override
    public void onEnable() {
        saveDefaultConfig();
        checkAPI();
        instance = this;
        prefix = alternate(getInstance().getConfig().getString("prefix"));
        initFiles();
        db = new Database(getDatabase().getString("host"), getDatabase().getString("username"), getDatabase().getString("port"), getDatabase().getString("password"), getDatabase().getString("database"));
        db.Connection();
        initTables();
        Objects.requireNonNull(getCommand("UltraClan")).setExecutor(new ClanCommand());
        Objects.requireNonNull(getCommand("ClanChat")).setExecutor(new ClanChatCommand());
        initSettings();
        RolesHandler.initRoles();
        ClanManager.initClans();
        ClanManager.updateAll();
        Bukkit.getPluginManager().registerEvents(new PlayerLIstener(), this);
        Bukkit.getPluginManager().registerEvents(new EventsToUpExperience(), this);
        Bukkit.getPluginManager().registerEvents(new AbilitiesListener(), this);
        Bukkit.getPluginManager().registerEvents(new ClanChatEvent(), this);
    }

    @Override
    public void onDisable() {
        db.exit();
    }

    private void checkAPI() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            getLogger().warning("Could not find PlaceholderAPI! This plugin is required.");
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }


    public FileConfiguration getDatabase() {
        return database;
    }

    public static UltraClans getInstance() {
        return instance;
    }

    public Database getDb() {
        return db;
    }

    public Set<UUID> getToggleClanChat() {
        return toggleClanChat;
    }

    private void initSettings() {
        this.maxUsers = getInstance().getConfig().getInt("ClanSettings.max_users");
        this.maxLevel = getInstance().getConfig().getInt("ClanSettings.max_level");
        this.multiply_level_to_experience = getInstance().getConfig().getInt("multiply_level_to_experience");
        this.format_chat = getInstance().getConfig().getBoolean("format_chat");
        this.spawnFireWorks = getInstance().getConfig().getBoolean("spawnFireWorks");

        this.baseRole = getInstance().getConfig().getString("BaseRole");
        this.format = getInstance().getConfig().getString("chatFormat");


        this.breakBlock = getInstance().getConfig().getString("events_upExperience.block");
        this.attack = getInstance().getConfig().getString("events_upExperience.attack");
        this.crafting = getInstance().getConfig().getString("events_upExperience.crafting");
        this.smelting = getInstance().getConfig().getString("events_upExperience.smelting");

    }


    public String getPrefix() {
        return prefix + " ";
    }

    private void initTables() {
        db.createTable("CREATE TABLE IF NOT EXISTS UltraClans (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, NameClan VARCHAR(20), Leader VARCHAR(30), Users VARCHAR(1000), LevelClan VARCHAR(1000), Experience VARCHAR(1000), Tag VARCHAR(10), TogglePvP VARCHAR(5), DistributionExperience VARCHAR(5));");
        db.createTable("CREATE TABLE IF NOT EXISTS Roles (id SMALLINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY, NameClan VARCHAR(20), RoleName VARCHAR(20), Color VARCHAR(1000), Permissions VARCHAR(1000));");
    }

    private void initFiles() {
        fileDatabase = new File(getInstance().getDataFolder(), "database.yml");
        try {
            if (fileDatabase.createNewFile()) {
                Bukkit.getLogger().info(getPrefix() + "database.yml is created!");
                database = YamlConfiguration.loadConfiguration(fileDatabase);
                database.set("host", "localhost");
                database.set("port", "3306");
                database.set("username", "db");
                database.set("database", "db");
                database.set("password", "password");
                database.save(fileDatabase);
                Bukkit.getLogger().info(getPrefix() + "Database is reloaded!");
                return;
            }
            database = YamlConfiguration.loadConfiguration(fileDatabase);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public String alternate(String t) {
        return ChatColor.translateAlternateColorCodes('&', t);
    }


    public int updateMinutes() {

        return (getInstance().getConfig().getInt("UpdateInMinutes") * 20) * 60;
    }


    public int getCountExperience(String s) {
        int min = Integer.parseInt(s.split("-")[0]);
        int max = Integer.parseInt(s.split("-")[1]);
        return Math.max(min, new Random().nextInt(max));
    }


}
