package ru.optimus.handlers.Listeners;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;
import ru.optimus.Util.RolesHandler;

public class ClanChatEvent implements Listener {

    @Deprecated
    @EventHandler
    public void onChatClan(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        if (ClanManager.isClan(player)) {
            Clan clan = ClanManager.getClanPlayer(player);
            if (clan == null) return;
            String message = UltraClans.getInstance().alternate(UltraClans.getInstance().getConfig().getString("ClanChat.ccTag"));
            message = message.replace("%player%", player.getName())
                    .replace("%message%", e.getMessage())
                    .replace("%role%", RolesHandler.getRoleByName(clan.getNameClan(), clan.getRolePlayer(player.getName())).getName());

            message = UltraClans.getInstance().alternate(PlaceholderAPI.setPlaceholders(player, message));

            if (UltraClans.getInstance().getToggleClanChat().contains(player.getUniqueId())) {
                e.getRecipients().clear();
                e.getRecipients().addAll(ClanManager.getPlayersClan(clan));
                e.setFormat(message);
            }
        }
    }

}
