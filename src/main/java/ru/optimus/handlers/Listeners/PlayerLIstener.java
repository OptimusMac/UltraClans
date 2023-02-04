package ru.optimus.handlers.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;
import ru.optimus.Util.InviteHandler;

public class PlayerLIstener implements Listener {

    @EventHandler
    public void removeInvites(PlayerQuitEvent e) {
        InviteHandler.remove(e.getPlayer());
        UltraClans.getInstance().getToggleClanChat().remove(e.getPlayer().getUniqueId());

    }

    @Deprecated
    @EventHandler
    public void onSendChat(AsyncPlayerChatEvent e) {
        if (UltraClans.getInstance().format_chat)
            if (ClanManager.isClan(e.getPlayer())) {
                Clan clan = ClanManager.getClanPlayer(e.getPlayer());
                if (clan != null && !UltraClans.getInstance().getToggleClanChat().contains(e.getPlayer().getUniqueId())) {
                    String format = UltraClans.getInstance().format;
                    format = format.replace("%tag", clan.getTag());
                    format = format.replace("%player%", e.getPlayer().getName());
                    format = format.replace("%message%", e.getMessage());
                    format = PlaceholderAPI.setPlaceholders(e.getPlayer(), format);
                    format = UltraClans.getInstance().alternate(format);
                    e.setFormat(format);

                }
            }
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damage = (Player) e.getDamager();
            Player entity = (Player) e.getEntity();
            if (ClanManager.isClan(damage)) {
                Clan clan = ClanManager.getClanPlayer(damage);
                if (clan == null) return;
                if (clan.getMembers().contains(entity.getName())) {
                    if (!clan.isTogglePvP()) {
                        damage.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GOLD + "Вы не можете навредить союзнику");
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

}
