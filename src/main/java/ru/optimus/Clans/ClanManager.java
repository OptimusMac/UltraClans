package ru.optimus.Clans;

import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import ru.optimus.UltraClans;
import ru.optimus.Util.Abilities;
import ru.optimus.Util.Roles;
import ru.optimus.Util.RolesHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.List;

public class ClanManager {

    public static ArrayList<Clan> clans = new ArrayList<>();
    private static final String[] codeStyle = new String[]{"k", "l", "m", "n", "o"};

    public static HashMap<String, Roles> getRolePlayer = new HashMap<>();

    public static Clan createClan(String name, Player leader, String tag) {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            statement.execute("INSERT UltraClans(NameClan, Leader, Users, LevelClan, Experience, Tag, TogglePvP, DistributionExperience) VALUES ('%name', '%leader', '%users', '%level', '%experience', '%tag', '%togglePvP', '%distributionExp')".replace("%distributionExp", String.valueOf(false)).replace("%togglePvP", String.valueOf(false)).replace("%tag", UltraClans.getInstance().alternate(tag)).replace("%experience", String.valueOf(0)).replace("%level", String.valueOf(1)).replace("%users", UltraClans.getInstance().baseRole + ":" + leader.getName()).replace("%leader", leader.getName()).replace("%name", ChatColor.stripColor(UltraClans.getInstance().alternate(name))));
            statement.close();
            ArrayList<String> permissions = new ArrayList<>();
            permissions.add("ru.ultraclans.clanChat");
            HashMap<String, Roles> rolesHashMap = new HashMap<>();
            rolesHashMap.put(leader.getName(), new Roles(UltraClans.getInstance().baseRole, name, "f", permissions));
            Clan clan = new Clan(ChatColor.stripColor(UltraClans.getInstance().alternate(name)), leader.getName(), Arrays.asList(leader.getName()), 1, 0, tag, false, false, rolesHashMap);
            RolesHandler.createRole(UltraClans.getInstance().baseRole, clan.getNameClan(), "f", permissions);
            return clan;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addPlayer(Clan clan, Player player) {
        clan.addMember(player.getName());
        String query = "SELECT * FROM " + "UltraClans WHERE NameClan='%name'".replace("%name", clan.getNameClan());
        try {
            Statement stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            List<String> users = new ArrayList<>();
            users.add(UltraClans.getInstance().baseRole + ":" + player.getName());
            while (resultSet.next()) {
                String string = resultSet.getString(4);
                for (int i = 0; i < Math.max(1, Arrays.stream(string.split(",")).count()); i++) {
                    if (string.split(",")[i] == null) continue;
                    String member = string.split(",")[i];
                    users.add(member);
                }
            }

            StringBuilder result = new StringBuilder();

            for (String u : users) {
                result.append(u).append(",");
            }

            String getMembers = removeLast(result.toString(), 1);

            String queryUpdate = "UPDATE UltraClans SET Users = '%newMembers'".replace("%newMembers", getMembers);
            stmt.executeUpdate(queryUpdate);
            stmt.close();
            resultSet.close();
            return true;


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean kickPlayer(Clan clan, String player) {

        String query = "SELECT * FROM " + "UltraClans WHERE NameClan='%name'".replace("%name", clan.getNameClan());
        try {
            Statement stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            List<String> users = new ArrayList<>();
            while (resultSet.next()) {
                String string = resultSet.getString(4);
                for (int i = 0; i < Math.max(1, Arrays.stream(string.split(",")).count()); i++) {
                    if (string.split(",")[i] == null || string.split(",")[i].equals(player)) continue;
                    users.add(string.split(",")[i]);
                }
            }

            StringBuilder result = new StringBuilder();

            for (String u : users) {
                result.append(u).append(",");
            }

            String getMembers = removeLast(result.toString(), 1);

            String queryUpdate = "UPDATE UltraClans SET Users = '%newMembers'".replace("%newMembers", getMembers);
            stmt.executeUpdate(queryUpdate);
            stmt.close();
            resultSet.close();
            UltraClans.getInstance().getToggleClanChat().removeIf(e -> {
                Player p = Bukkit.getPlayer(e);
                if (p != null && p.getName().equals(player)) {
                    return true;
                }
                return false;
            });
            return true;


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void sendAll(Clan clan, String message) {
        clan.getMembers().stream().filter(e -> Bukkit.getPlayer(e) != null).forEach(player -> {
            Objects.requireNonNull(Bukkit.getPlayer(player)).sendMessage(UltraClans.getInstance().getPrefix() + message);
        });
    }

    public static void sendChatAll(Clan clan, String message) {
        clan.getMembers().stream().filter(e -> Bukkit.getPlayer(e) != null).forEach(player -> {
            Objects.requireNonNull(Bukkit.getPlayer(player)).sendMessage(message);
        });
    }

    public static Set<Player> getPlayersClan(Clan clan) {
        Set<Player> players = new HashSet<>();
        for (String member : clan.getMembers()) {
            Player player = Bukkit.getPlayer(member);
            if (player != null)
                players.add(player);
        }
        return players;
    }

    public static ArrayList<Clan> getClans() {
        return clans;
    }

    public static void addClan(Clan clan) {
        clans.add(clan);
    }

    public static void updateAll() {
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                for (Clan clan : getClans()) {
                    clan.updateExperience();
                }
                getClans().clear();
                RolesHandler.getRoles().clear();
                initClans();
                RolesHandler.initRoles();
                Bukkit.getLogger().info("[UC]: Reloaded!");
            }
        };
        runnable.runTaskTimer(UltraClans.getInstance(), UltraClans.getInstance().updateMinutes(), UltraClans.getInstance().updateMinutes());
    }

    public static String removeLast(String str, int step) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length() - step; i++) {
            stringBuilder.append(str.charAt(i));
        }
        return stringBuilder.toString();
    }

    public static boolean isClan(Player player) {
        return getClans().stream().anyMatch(e -> e.getMembers().contains(player.getName()));

    }

    public static Clan getClanPlayer(Player player) {
        if (isClan(player)) {
            for (Clan clan : getClans()) {
                if (clan.getMembers().contains(player.getName())) {
                    return clan;
                }
            }
        }
        return null;

    }

    public static void getInformation(Player player) {
        if (!isClan(player)) {
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "У вас нет клана! Создайте его! /clan create <name> <tag>");
        } else {
            Clan clan = Objects.requireNonNull(getClanPlayer(player));
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Информация о клане: " + ChatColor.YELLOW + clan.getNameClan());
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Тэг: " + ChatColor.YELLOW + UltraClans.getInstance().alternate(Objects.requireNonNull(clan).getTag()));
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Лидер клана: " + ChatColor.YELLOW + clan.getLeader());
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Участников: " + ChatColor.RED + clan.getOnlinePlayers() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + Objects.requireNonNull(getClanPlayer(player)).getCountMembers());
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Бой между союзниками: " + ChatColor.RED + (clan.isTogglePvP() ? "Включено" : "Выключено"));
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Уровень клана: " + ChatColor.RED + clan.getLevel() + ChatColor.DARK_GRAY + "/" + ChatColor.GRAY + UltraClans.getInstance().maxLevel + ChatColor.RED + " LvL");
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Опыт клана: " + ChatColor.RED + clan.getExperienceClan() + ChatColor.DARK_GRAY + "/" + ChatColor.DARK_RED + clan.neededExperience());
            if (Abilities.DISTRIBUTION_EXPERIENCE.isEnable()) {
                player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Распределение опыта между участниками: " + ChatColor.RED + (clan.isDistributionExperience() ? "Включено" : "Выключено"));

            }
            sendBossBar(clan, player, false);
        }
    }


    public static void sendBossBar(Clan clan, Player player, boolean reverse) {
        BossBar bossBar = Bukkit.createBossBar("Опыт клана", BarColor.YELLOW, BarStyle.SOLID, BarFlag.DARKEN_SKY);
        final float[] delta = {(float) clan.getExperienceClan() / clan.neededExperience()};
        bossBar.setProgress(delta[0]);
        bossBar.addPlayer(player);
        int remove = 25 * clan.getLevel() + 1;
        if (!reverse) {
            BukkitRunnable run = new BukkitRunnable() {
                @Override
                public void run() {
                    bossBar.removePlayer(player);
                }
            };
            run.runTaskLater(UltraClans.getInstance(), 80L);
        } else {
            BukkitRunnable runnable = new BukkitRunnable() {
                int s = 0;

                @Override
                public void run() {
                    if (delta[0] == 0.0f || clan.getExperienceClan() - remove <= 0) {
                        cancel();
                        bossBar.removePlayer(player);
                        clan.setExperienceClan(0);
                        clan.updateExperience();
                        return;
                    }
                    delta[0] = (float) clan.getExperienceClan() / clan.neededExperience();
                    clan.removeExperience(remove);
                    bossBar.setProgress(delta[0]);
                    if (s % 8 == 0)
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0F, 1.0F);
                    s++;
                }
            };
            runnable.runTaskTimer(UltraClans.getInstance(), 0L, 1L);
        }
    }

    public static void sendSkillInfo(Player player) {
        Clan clan = getClanPlayer(player);
        assert clan != null;
        if (!Abilities.SPEED.isEnable() && !Abilities.HEALTH.isEnable() && !Abilities.STRENGTH.isEnable() && !Abilities.EXPERIENCE.isEnable()) {
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Способности отключены на этом сервере");
            return;
        }
        player.sendMessage(ChatColor.YELLOW + "==============" + ChatColor.GOLD + "Способности клана" + ChatColor.YELLOW + "==============");
        if (Abilities.SPEED.isEnable())
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Бонус скорости передвижения: " + ChatColor.RED + (Abilities.SPEED.isOpen(clan) ? (Abilities.SPEED.result(clan) * clan.getLevel()) + "%" : "Доступно на " + ChatColor.DARK_RED + Abilities.SPEED.toLevel(clan) + ChatColor.RED + " уровне!"));
        if (Abilities.HEALTH.isEnable())
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Бонус к здоровью: " + ChatColor.RED + (Abilities.HEALTH.isOpen(clan) ? Abilities.HEALTH.result(clan) : "Доступно на " + ChatColor.DARK_RED + Abilities.HEALTH.toLevel(clan) + ChatColor.RED + " уровне!"));
        if (Abilities.EXPERIENCE.isEnable())
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Бонус получения опыта: " + ChatColor.RED + (Abilities.EXPERIENCE.isOpen(clan) ? Abilities.EXPERIENCE.result(clan) + "%" : "Доступно на " + ChatColor.DARK_RED + Abilities.EXPERIENCE.toLevel(clan) + ChatColor.RED + " уровне!"));
        if (Abilities.STRENGTH.isEnable())
            player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GRAY + "Бонус к урону: " + ChatColor.RED + (Abilities.STRENGTH.isOpen(clan) ? Abilities.STRENGTH.result(clan) : "Доступно на " + ChatColor.DARK_RED + Abilities.STRENGTH.toLevel(clan) + ChatColor.RED + " уровне!"));
    }

    public static void spawnFirework(Player player, String name) {
        Random random = new Random();
        Location loc = player.getLocation().add(random.nextDouble(), 0, random.nextDouble());
        Firework f = player.getWorld().spawn(loc, Firework.class);
        f.setCustomName(name);
        f.setCustomNameVisible(true);
        FireworkMeta fm = f.getFireworkMeta();
        fm.addEffect(FireworkEffect.builder()
                .flicker(true)
                .trail(true)
                .withColor(Color.AQUA).build());
        fm.setPower(1);
        f.setFireworkMeta(fm);
    }

    public static boolean hasName(String name) {
        List<String> names = new ArrayList<>();
        for (Clan clan : getClans()) {
            names.add(clan.getNameClan());
        }
        return names.contains(name);
    }

    public static void initClans() {
        String query = "SELECT * FROM " + "UltraClans;";
        try {
            Statement stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            String nameClan;
            String leader;
            String tag;
            int level;
            int experience;
            boolean togglePvP;
            boolean distribution;
            HashMap<String, Roles> roles = new HashMap<>();
            List<String> members = new ArrayList<>();
            while (resultSet.next()) {
                String string = resultSet.getString(4);
                nameClan = resultSet.getString(2);
                leader = resultSet.getString(3);
                level = Integer.parseInt(resultSet.getString(5));
                experience = Integer.parseInt(resultSet.getString(6));
                tag = resultSet.getString(7);
                togglePvP = Boolean.parseBoolean(resultSet.getString(8));
                distribution = Boolean.parseBoolean(resultSet.getString(9));
                for (String str : string.split(",")) {
                    if (str == null) continue;
                    String member = str;
                    String role = null;
                    if (str.contains(":")) {
                        member = str.split(":")[1];
                        role = str.split(":")[0];
                    }
                    Roles baseRole = RolesHandler.getRoleByName(nameClan, role == null ? UltraClans.getInstance().baseRole : role);
                    members.add(member);

                    roles.put(member, baseRole);
                    getRolePlayer.put(member, RolesHandler.getRoleByName(nameClan, role));

                }

                Clan clan = new Clan(nameClan, leader, members, level, experience, tag, togglePvP, distribution, roles);
                addClan(clan);
            }

        } catch (SQLException ignored) {
        }
    }


    public static void removeClanByName(String name) {
        int index = -1;
        for (Clan clan : getClans()) {
            if (clan.getNameClan().equalsIgnoreCase(name)) {
                index = getClans().indexOf(clan);
                break;
            }
        }
        if (index == -1) return;
        getClans().remove(index);
    }

}
