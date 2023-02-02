package ru.optimus.Clans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.optimus.UltraClans;

import javax.xml.transform.Result;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class Clan {

    private String nameClan;
    private String leader;
    private List<String> members;

    private int experienceClan;
    private int level;

    private String tag;
    private boolean togglePvP;
    private boolean DistributionExperience;

    public Clan(String nameClan, String leader, List<String> members, int level, int experienceClan, String tag, boolean togglePvP, boolean DistributionExperience) {
        this.nameClan = nameClan;
        this.leader = leader;
        this.members = members;
        this.level = level;
        this.tag = tag;
        this.togglePvP = togglePvP;
        this.experienceClan = experienceClan;
        this.DistributionExperience = DistributionExperience;
    }

    public String getNameClan() {
        return nameClan;
    }

    public String getLeader() {
        return leader;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getTag() {
        return tag;
    }

    public int getLevel() {
        return level;
    }

    public void upLevel(int i) {
        this.level += i;
    }

    public void removeExperience(int value) {
        this.setExperienceClan(getExperienceClan() - value);
    }

    public void setTogglePvP(boolean togglePvP) {
        this.togglePvP = togglePvP;
        updateToggle();
    }

    public boolean isDistributionExperience() {
        return UltraClans.getInstance().distribution && DistributionExperience;
    }

    public void setDistributionExperience(boolean distributionExperience) {
        DistributionExperience = distributionExperience;
        updateDistributionExperience();
    }

    public int getOnlinePlayers() {
        return (int) getMembers().stream().filter(e -> Bukkit.getPlayer(e) != null).count();
    }

    public void decline() {
        String sql = "DELETE FROM UltraClans WHERE NameClan='%name'".replace("%name", getNameClan());
        Statement stmt = null;
        try {
            stmt = UltraClans.getInstance().getDb().getConnection().createStatement();
            stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        getMembers().stream().filter(e -> Bukkit.getPlayer(e) != null).forEach(player -> {
            Objects.requireNonNull(Bukkit.getPlayer(player)).sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.AQUA + "Ваш клан был распущен Лидером " + ChatColor.GOLD + getLeader());
        });
        ClanManager.removeClanByName(getNameClan());
    }

    public int getCountMembers() {
        return members.size();
    }

    public int getExperienceClan() {
        return experienceClan;
    }

    public void setExperienceClan(int experienceClan) {
        this.experienceClan = experienceClan;
    }

    public void addExperience(int experienceClan, Player player) {

        if (this.experienceClan + experienceClan > neededExperience()) {
            upLevel();
            ClanManager.sendBossBar(this, player, true);
            if (UltraClans.getInstance().spawnFireWorks) {
                BukkitRunnable runnable = new BukkitRunnable() {
                    int c = 0;

                    @Override
                    public void run() {
                        if (c == 4) {
                            cancel();
                            return;
                        }
                        ClanManager.spawnFirework(player, ChatColor.RED + "Поднятие уровня");
                        c++;
                    }
                };
                runnable.runTaskTimer(UltraClans.getInstance(), 0L, 15L);
                return;
            }
        }

        this.experienceClan += experienceClan;
    }

    public int neededExperience() {
        return getLevel() * UltraClans.getInstance().multiply_level_to_experience;
    }

    public void upLevel() {
        String name = getNameClan();
        int upLevel = getLevel() + 1;
        String query = "UPDATE UltraClans SET LevelClan = '%level' WHERE NameClan='%name'".replace("%level", String.valueOf(upLevel)).replace("%name", name);
        try {
            Statement statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        getMembers().stream().filter(p -> Bukkit.getPlayer(p) != null).forEach(send -> {
            Objects.requireNonNull(Bukkit.getPlayer(send)).sendTitle("Повышение уровня клана!", getLevel() + " >> " + upLevel, 10, 20, 10);
        });
        upLevel(1);
    }

    public void updateExperience() {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE UltraClans SET Experience = '%exp' WHERE NameClan='%name'".replace("%exp", String.valueOf(getExperienceClan())).replace("%name", getNameClan());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateDistributionExperience() {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE UltraClans SET DistributionExperience = '%destr' WHERE NameClan='%name'".replace("%destr", String.valueOf(isDistributionExperience())).replace("%name", getNameClan());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setClanName(String name) {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE UltraClans SET NameClan = '%newname' WHERE NameClan='%name'".replace("%newname", name).replace("%name", getNameClan());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.nameClan = name;
    }

    public void setClanTag(String name) {
        Statement statement = null;
        String resName = UltraClans.getInstance().alternate(name);
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE UltraClans SET Tag = '%newtag' WHERE NameClan='%name'".replace("%newtag", resName).replace("%name", getNameClan());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.tag = resName;
    }

    public void setLeader(Player player) {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE UltraClans SET Leader = '%newLeader' WHERE NameClan='%name'".replace("%newLeader", player.getName()).replace("%name", getNameClan());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remove(String user) {
        getMembers().remove(user);
    }

    public boolean isTogglePvP() {
        return togglePvP;
    }

    public void updateToggle() {
        Statement statement = null;
        try {
            statement = UltraClans.getInstance().getDb().getConnection().createStatement();
            String sql = "UPDATE UltraClans SET TogglePvP = '%toggle' WHERE NameClan='%name'".replace("%toggle", String.valueOf(isTogglePvP())).replace("%name", getNameClan());
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
