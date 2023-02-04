package ru.optimus.handlers.Commands;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;
import ru.optimus.Util.RolesHandler;

public class ClanChatCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (ClanManager.isClan(player)) {
                Clan clan = ClanManager.getClanPlayer(player);
                if(clan == null) return true;
                if (args.length == 0) {
                    if (!UltraClans.getInstance().getToggleClanChat().contains(player.getUniqueId())) {
                        UltraClans.getInstance().getToggleClanChat().add(player.getUniqueId());
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Клановый чат включен!");
                    } else {
                        UltraClans.getInstance().getToggleClanChat().remove(player.getUniqueId());
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GOLD + "Клановый чат выключен!");
                    }
                    return true;
                }

                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    builder.append(args[i]).append(" ");
                }
                String message = UltraClans.getInstance().alternate(UltraClans.getInstance().getConfig().getString("ClanChat.ccTag"));
                message = message.replace("%player%", player.getName());
                message = message.replace("%message%", builder.toString());
                message = message.replace("%role%",RolesHandler.getRoleByName(clan.getNameClan(),  clan.getRolePlayer(player.getName())).getName());
                message = UltraClans.getInstance().alternate(PlaceholderAPI.setPlaceholders(player, message));
                ClanManager.sendChatAll(clan, message);

            } else {
                ClanManager.getInformation(player);
            }

        }
        return true;
    }
}
