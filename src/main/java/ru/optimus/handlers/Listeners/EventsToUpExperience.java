package ru.optimus.handlers.Listeners;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;

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
    public void DistributionExperience(PlayerPickupExperienceEvent e) {
        if (ClanManager.isClan(e.getPlayer())) {
            Clan clan = ClanManager.getClanPlayer(e.getPlayer());
            if (clan != null && clan.isDistributionExperience()) {
                int addExperience = e.getExperienceOrb().getExperience() / clan.getOnlinePlayers();
                clan.getMembers().stream().filter(f -> Bukkit.getPlayer(f) != null).forEach(player -> {
                    Objects.requireNonNull(Bukkit.getPlayer(player)).giveExp(addExperience);
                });
                e.getExperienceOrb().setExperience(0);
            }
        }
    }

}
