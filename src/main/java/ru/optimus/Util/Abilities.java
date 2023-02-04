package ru.optimus.Util;

import org.bukkit.entity.Player;
import ru.optimus.Clans.Clan;
import ru.optimus.UltraClans;

public enum Abilities {

    SPEED {
        public double result(Clan clan) {
            return (UltraClans.getInstance().getConfig().getDouble("Skills.speed"));
        }
        public int toLevel(Clan clan) {
            return (UltraClans.getInstance().getConfig().getInt("OpenAbilities.speed"));
        }
        public boolean isOpen(Clan clan) {
            return UltraClans.getInstance().getConfig().getInt("OpenAbilities.speed") <= clan.getLevel();
        }
        public boolean isEnable() {
            return UltraClans.getInstance().getConfig().getBoolean("EnableAbilities.speed");
        }
    },
    EXPERIENCE {
        public double result(Clan clan) {
            return UltraClans.getInstance().getConfig().getDouble("Skills.experience_boost") * clan.getLevel();
        }
        public int toLevel(Clan clan) {
            return (UltraClans.getInstance().getConfig().getInt("OpenAbilities.experience_boost"));
        }
        public boolean isOpen(Clan clan) {
            return UltraClans.getInstance().getConfig().getInt("OpenAbilities.experience_boost") <= clan.getLevel();
        }
        public boolean isEnable() {
            return UltraClans.getInstance().getConfig().getBoolean("EnableAbilities.experience_boost");
        }
    },
    HEALTH {
        public double result(Clan clan) {
            return UltraClans.getInstance().getConfig().getDouble("Skills.health") * clan.getLevel();
        }
        public int toLevel(Clan clan) {
            return (UltraClans.getInstance().getConfig().getInt("OpenAbilities.health"));
        }
        public boolean isOpen(Clan clan) {
            return UltraClans.getInstance().getConfig().getInt("OpenAbilities.health") <= clan.getLevel();
        }
        public boolean isEnable() {
            return UltraClans.getInstance().getConfig().getBoolean("EnableAbilities.health");
        }
    },
    STRENGTH {
        public double result(Clan clan) {
            return UltraClans.getInstance().getConfig().getDouble("Skills.strength") * clan.getLevel();
        }
        public int toLevel(Clan clan) {
            return (UltraClans.getInstance().getConfig().getInt("OpenAbilities.strength"));
        }
        public boolean isOpen(Clan clan) {
            return UltraClans.getInstance().getConfig().getInt("OpenAbilities.strength") <= clan.getLevel();
        }
        public boolean isEnable() {
            return UltraClans.getInstance().getConfig().getBoolean("EnableAbilities.strength");
        }
    },
    DISTRIBUTION_EXPERIENCE{
        public double result(Clan clan) {
            return 0;
        }
        public int toLevel(Clan clan) {
            return (UltraClans.getInstance().getConfig().getInt("OpenAbilities.distributionExperience"));
        }
        public boolean isOpen(Clan clan) {
            return UltraClans.getInstance().getConfig().getInt("OpenAbilities.distributionExperience") <= clan.getLevel();
        }
        public boolean isEnable() {
            return UltraClans.getInstance().getConfig().getBoolean("EnableAbilities.distributionExperience");
        }
    };
    public abstract double result(Clan clan);
    public abstract int toLevel(Clan clan);
    public abstract boolean isOpen(Clan clan);
    public abstract boolean isEnable();
}
