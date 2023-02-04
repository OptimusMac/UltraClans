package ru.optimus.handlers.Listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.Util.Abilities;

import java.util.Objects;
import java.util.Optional;

public class AbilitiesListener implements Listener {


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (ClanManager.isClan(e.getPlayer())) {
            Clan clan = ClanManager.getClanPlayer(e.getPlayer());
            if (Abilities.HEALTH.isEnable() && Abilities.HEALTH.isOpen(clan)) {
                double health = Abilities.HEALTH.result(clan);
                if (!hasAttribute(e.getPlayer(), "Vitality")) {
                    Objects.requireNonNull(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).addModifier(new AttributeModifier("Vitality", health, AttributeModifier.Operation.ADD_NUMBER));
                }
            }
            if (Abilities.STRENGTH.isEnable() && Abilities.STRENGTH.isOpen(clan)) {
                double damage = Abilities.STRENGTH.result(clan);
                if (!hasAttribute(e.getPlayer(), "Damage")) {
                    Objects.requireNonNull(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).addModifier(new AttributeModifier("Damage", damage, AttributeModifier.Operation.ADD_NUMBER));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void boostAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {

            Player player = (Player) e.getDamager();
            if (ClanManager.isClan(player)) {
                Clan clan = ClanManager.getClanPlayer(player);
                if (Abilities.STRENGTH.isEnable() && Abilities.STRENGTH.isOpen(clan)) {
                    double damage = e.getDamage() + Abilities.STRENGTH.result(clan);
                    e.setDamage(damage);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onSpeed(PlayerMoveEvent e) {
        if (ClanManager.isClan(e.getPlayer())) {
            Clan clan = ClanManager.getClanPlayer(e.getPlayer());
            if (clan == null) return;
            if (Abilities.SPEED.isOpen(clan) && Abilities.SPEED.isEnable()) {
                double speed = 0.2f * ((Abilities.SPEED.result(clan) * 0.01) * clan.getLevel());
                e.getPlayer().setWalkSpeed((float) (0.2f + speed));
            }
            if (Abilities.HEALTH.isEnable() && Abilities.HEALTH.isOpen(clan)) {
                double health = Abilities.HEALTH.result(clan);
                boolean update = Objects.requireNonNull(getAttributeByName(e.getPlayer(), "Vitality")).getAmount() < health;
                if (update) {
                    removeAllToKey(e.getPlayer(), "Vitality");
                    Objects.requireNonNull(e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH)).addModifier(new AttributeModifier("Vitality", health, AttributeModifier.Operation.ADD_NUMBER));
                }
            }
        }
    }

    private void removeAllToKey(Player player, String key) {
        Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getModifiers().stream().filter(name -> name.getName().equalsIgnoreCase(key)).forEach(delete -> Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).removeModifier(delete));
    }

    private boolean hasAttribute(Player player, String name) {
        for (AttributeModifier modifier : Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getModifiers()) {
            if (modifier.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    private AttributeModifier getAttributeByName(Player player, String name) {
        for (AttributeModifier modifier : Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getModifiers()) {
            if (modifier.getName().equalsIgnoreCase(name)) {
                return modifier;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBoostExperience(PlayerPickupExperienceEvent e) {
        if (ClanManager.isClan(e.getPlayer())) {
            Clan clan = ClanManager.getClanPlayer(e.getPlayer());
            if (clan == null || !Abilities.EXPERIENCE.isEnable() || !Abilities.EXPERIENCE.isOpen(clan)) return;
            double experience = e.getExperienceOrb().getExperience() + ((e.getExperienceOrb().getExperience() * Abilities.EXPERIENCE.result(clan)) / 100);
            e.getExperienceOrb().setExperience(((int) experience));
        }
    }

}
