package ru.optimus.handlers.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;
import ru.optimus.Util.InviteHandler;

import java.util.Objects;

public class ClanCommand implements CommandExecutor {


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                ClanManager.getInformation(player);
                return true;
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (!ClanManager.isClan(player)) {
                    if (args.length < 3) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "/clan create <name> <tag>");
                        return true;
                    }
                    String name = UltraClans.getInstance().alternate(args[1]);
                    if (ClanManager.hasName(name)) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Клан с таким названием уже существует!");
                        return true;
                    }
                    String tag = args[2];
                    Clan created = ClanManager.createClan(name, player, tag);
                    if (created == null) {
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "По какой-то причине вы не смогли создать клан! Обратитесь к Администрации");
                    } else {
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Поздравляем! Вы создали свой собственный клан " + ChatColor.GOLD + name);
                        ClanManager.addClan(created);
                    }
                    return true;
                } else {
                    sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы уже состоите в клане " + ChatColor.GOLD + Objects.requireNonNull(ClanManager.getClanPlayer(player)).getNameClan());

                }
            }

            if (args[0].equalsIgnoreCase("kick")) {
                if (ClanManager.isClan(player)) {
                    if (args.length < 2) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "/clan kick <player>");
                        return true;
                    }
                    Clan clan = ClanManager.getClanPlayer(player);
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                        return true;
                    }
                    String name = args[1];
                    if (!clan.getMembers().contains(name)) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Данного игрока не существует в вашем клане!");
                        return true;
                    }


                    ClanManager.kickPlayer(clan, name);
                    clan.remove(name);
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Вы исключили " + ChatColor.GOLD + name + ChatColor.YELLOW + " из своего клана!");
                    return true;
                } else {
                    ClanManager.getInformation(player);
                }
            }

            if (args[0].equalsIgnoreCase("distribution")) {
                if (!UltraClans.getInstance().distribution) {
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Функция отключена");
                    return true;
                }
                if (ClanManager.isClan(player)) {
                    Clan clan = ClanManager.getClanPlayer(player);
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                        return true;
                    }
                    if (!clan.getLeader().equals(player.getName())) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы не Лидер клана!");
                        return true;
                    }
                    boolean toggle = clan.isDistributionExperience();
                    clan.setDistributionExperience(!toggle);
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Теперь распределение опыта между участниками " + ChatColor.GOLD + (clan.isDistributionExperience() ? "Включено!" : "Отключено!"));

                } else {
                    ClanManager.getInformation(player);
                }
            }

            if (args[0].equalsIgnoreCase("decline")) {
                if (ClanManager.isClan(player)) {
                    Clan clan = ClanManager.getClanPlayer(player);
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                        return true;
                    }
                    if (!clan.getLeader().equalsIgnoreCase(player.getName())) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Распустить клан может только Лидер!");
                        return true;
                    }

                    clan.decline();

                } else {
                    ClanManager.getInformation(player);
                }
            }

            if (args[0].equalsIgnoreCase("change")) {

                if (ClanManager.isClan(player)) {
                    Clan clan = ClanManager.getClanPlayer(player);
                    if (args.length < 3) {
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "/clan change <name/tag> <message>");
                        return true;
                    }
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                        return true;
                    }
                    String res = args[2];
                    if (args[1].equalsIgnoreCase("name")) {
                        clan.setClanName(ChatColor.stripColor(UltraClans.getInstance().alternate(res)));
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Название клана изменено на " + ChatColor.GREEN + ChatColor.stripColor(UltraClans.getInstance().alternate(res)));
                        return true;
                    } else if (args[1].equalsIgnoreCase("tag")) {
                        clan.setClanTag(res);
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Тэг клана изменен на " + ChatColor.GREEN + UltraClans.getInstance().alternate(res));
                        return true;
                    } else {
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "/clan change <name/tag> <message>");
                    }
                } else {
                    ClanManager.getInformation(player);
                }
            }

            if (args[0].equalsIgnoreCase("members")) {
                if (ClanManager.isClan(player)) {
                    Clan clan = ClanManager.getClanPlayer(player);
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору");
                        return true;
                    }
                    StringBuilder stringBuilder = new StringBuilder();

                    for (String member : clan.getMembers()) {
                        stringBuilder.append(Bukkit.getPlayer(member) != null ? (ChatColor.RED + member) : (ChatColor.GRAY + member)).append(ChatColor.GRAY + ", ");
                    }
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Онлайн вашего клана:");
                    String result = UltraClans.getInstance().getPrefix() + ClanManager.removeLast(stringBuilder.toString(), 2);
                    player.sendMessage(result);
                    return true;
                } else {
                    ClanManager.getInformation(player);
                }
            }

            if (args[0].equalsIgnoreCase("setLeader")) {
                if (args.length < 2) {
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "/clan setLeader <playerName>");
                    return true;
                }
                String name = args[1];
                Player toPlayer = Bukkit.getPlayer(name);
                if (toPlayer == null) {
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Игрок оффлайн!");
                    return true;
                }
                if (ClanManager.isClan(player)) {

                    Clan clan = ClanManager.getClanPlayer(player);
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                        return true;
                    }
                    if (!ClanManager.isClan(toPlayer)) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Игрок не в клане!");
                        return true;
                    }
                    if (!clan.getNameClan().equalsIgnoreCase(Objects.requireNonNull(ClanManager.getClanPlayer(toPlayer)).getNameClan())) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Игрок не в вашем клане!");
                        return true;
                    }
                    if (clan.getLeader().equalsIgnoreCase(player.getName())) {
                        clan.setLeader(toPlayer);
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Вы назначили " + ChatColor.GOLD + name + ChatColor.YELLOW + " лидером вместо себя!");
                        return true;
                    }
                } else {
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "У вас нет клана! Создайте его! /clan create <name>");
                }
            }

            if (args[0].equalsIgnoreCase("toggle")) {
                if (ClanManager.isClan(player)) {
                    Clan clan = ClanManager.getClanPlayer(player);
                    if (clan == null) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                        return true;
                    }
                    if (!clan.getLeader().equals(player.getName())) {
                        player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы не Лидер клана!");
                        return true;
                    }
                    boolean toggle = clan.isTogglePvP();
                    clan.setTogglePvP(!toggle);
                    player.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.YELLOW + "Теперь бой между союзниками " + ChatColor.GOLD + (clan.isTogglePvP() ? "Включен!" : "Отключен" + "!"));

                } else {
                    ClanManager.getInformation(player);
                }
            }

            if (args[0].equalsIgnoreCase("invite")) {
                Clan clan = ClanManager.getClanPlayer(player);
                if (clan == null) {
                    ClanManager.getInformation(player);
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("/clan add <playerName>");
                    return true;
                }
                Player toPlayer = Bukkit.getPlayer(args[1]);
                if (toPlayer == null) {
                    sender.sendMessage("Игрок оффлайн!");
                    return true;
                }

                if (toPlayer.getName().equals(sender.getName())) {
                    sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы не можете добавить самого себя!");
                    return true;
                }


                if (ClanManager.isClan(toPlayer)) {
                    if (Objects.requireNonNull(ClanManager.getClanPlayer(toPlayer)).getNameClan().equals(clan.getNameClan())) {
                        sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.AQUA + "Уже состоит в вашем клане!");
                        return true;
                    }
                    sender.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Уже состоит в клане " + ChatColor.GOLD + Objects.requireNonNull(ClanManager.getClanPlayer(toPlayer)).getNameClan());
                }

                String send = InviteHandler.sendCalled(player, toPlayer);
                player.sendMessage(send);


            }

            if (args[0].equalsIgnoreCase("accept")) {
                String accept = InviteHandler.acceptInvite(player);
                player.sendMessage(accept);
            }

        }
        return true;
    }
}
