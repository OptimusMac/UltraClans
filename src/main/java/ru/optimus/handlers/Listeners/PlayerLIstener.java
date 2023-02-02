package ru.optimus.handlers.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    }

    @Deprecated
    @EventHandler
    public void onSendChat(AsyncPlayerChatEvent e) {
        if (UltraClans.getInstance().format_chat)
            if (ClanManager.isClan(e.getPlayer())) {
                Clan clan = ClanManager.getClanPlayer(e.getPlayer());
                if (clan != null)
                    e.setFormat(ChatColor.DARK_GRAY + "[%tag§8] ".replace("%tag", clan.getTag()) + ChatColor.RESET + e.getPlayer().getName() + ": " + e.getMessage());
            }
    }

}
