package ru.optimus.handlers.Listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;
import ru.optimus.Util.Abilities;

import java.util.Objects;

public class EventsToUpExperience implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (ClanManager.isClan(e.getPlayer())) {
            Clan clan = ClanManager.getClanPlayer(e.getPlayer());
            if (clan != null && clan.getLevel() < UltraClans.getInstance().maxLevel) {
                clan.addExperience(UltraClans.getInstance().getCountExperience(UltraClans.getInstance().breakBlock), e.getPlayer());
            }
        }
    }

    @EventHandler
    public void onDamaged(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            Player player = (Player) e.getDamager();
            if (ClanManager.isClan(player)) {
                Clan clan = ClanManager.getClanPlayer(player);
                if (clan != null && clan.getLevel() < UltraClans.getInstance().maxLevel) {
                    clan.addExperience(UltraClans.getInstance().getCountExperience(UltraClans.getInstance().attack), player);
                }
            }
        }
    }

    @EventHandler
    public void onSmelting(FurnaceExtractEvent e){
        Player player = e.getPlayer();
        if (ClanManager.isClan(player)) {
            Clan clan = ClanManager.getClanPlayer(player);
            if (clan != null && clan.getLevel() < UltraClans.getInstance().maxLevel) {
                clan.addExperience(UltraClans.getInstance().getCountExperience(UltraClans.getInstance().smelting), player);
            }
        }

    }

    @EventHandler
    public void DistributionExperience(PlayerPickupExperienceEvent e) {
        if (ClanManager.isClan(e.getPlayer())) {
            Clan clan = ClanManager.getClanPlayer(e.getPlayer());
            if (clan == null) return;
            double experience = 0;
            if (Abilities.EXPERIENCE.isEnable() && Abilities.EXPERIENCE.isOpen(clan)) {
                experience = e.getExperienceOrb().getExperience() + ((e.getExperienceOrb().getExperience() * Abilities.EXPERIENCE.result(clan)) / 100);
                e.getExperienceOrb().setExperience((int) experience);
            }

            if (clan.isDistributionExperience()) {
                int addExperience = e.getExperienceOrb().getExperience() / clan.getCountMembers();
                clan.getMembers().stream().filter(f -> Bukkit.getPlayer(f) != null && !f.equals(e.getPlayer().getName())).forEach(player -> {
                    Objects.requireNonNull(Bukkit.getPlayer(player)).giveExp(addExperience);
                });
                if (experience > 0) {
                    e.getExperienceOrb().setExperience((int) (experience - addExperience));
                } else {
                    e.getExperienceOrb().setExperience(addExperience);
                }
            }
        }
    }
}
