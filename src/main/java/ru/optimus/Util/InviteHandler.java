package ru.optimus.Util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.optimus.Clans.Clan;
import ru.optimus.Clans.ClanManager;
import ru.optimus.UltraClans;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class InviteHandler {

    private static HashMap<String, String> invites = new HashMap<>(); //0-owner, 1-invite
    private static HashMap<String, Long> times = new HashMap<>(); //0-owner, 1-invite


    public static boolean hasCalled(Player player) {
        return invites.containsValue(player.getName());
    }

    public static String sendCalled(Player owner, Player invite) {

        if(owner.getUniqueId().equals(invite.getUniqueId())){
            return  UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы не можете пригласить сами себя!";
        }

        if(ClanManager.isClan(invite)){
            return UltraClans.getInstance().getPrefix() + ChatColor.RED + "Игрок уже состоит в клане!";
        }

        if (hasCalled(invite) && invites.containsKey(owner.getName())) {
            return UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы уже отослали этому игроку запрос. Подождите еще %time сек.".replace("%time", toTime(invite));
        }

        if (!hasCalled(invite)) {
            invites.put(owner.getName(), invite.getName());
            times.put(invite.getName(), System.currentTimeMillis() + (60 * 1000L));
            return UltraClans.getInstance().getPrefix() + ChatColor.GREEN + "Вы отослали запрос на присоединение в клан игроку " + ChatColor.GOLD + invite.getName();
        }
        return UltraClans.getInstance().getPrefix() + ChatColor.RED + "Игрок уже имеет приглашения!";
    }

    public static String acceptInvite(Player player) {
        AtomicReference<String> cName = new AtomicReference<>();
        if (hasCalled(player)) {
            cName.set(getKeyByValue(player.getName()));
            invites.remove(getKeyByValue(player.getName()));
            times.remove(player.getName());

            Player owner = Bukkit.getPlayer(cName.get());
            if (owner != null) {
                owner.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GREEN + "Игрок " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " принял приглашение в ваш клан!");
            }

            Player p = Bukkit.getPlayer(cName.get());

            if(ClanManager.isClan(player)){
                return UltraClans.getInstance().getPrefix() + ChatColor.RED + "Вы уже состоите в клане!";
            }

            if (p == null) {
                return UltraClans.getInstance().getPrefix() + ChatColor.GREEN + " Игрок вышел из игры :(";
            }

            Clan clan = ClanManager.getClanPlayer(p);
            if (clan != null) {
                if (clan.getCountMembers() + 1 > UltraClans.getInstance().maxUsers) {
                    p.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Игрок " + ChatColor.GREEN + player.getName() + ChatColor.GOLD + " не смог принять приглашение, так как в вашем клане максимальное количество участников!");
                    return UltraClans.getInstance().getPrefix() + ChatColor.RED + "В данном клане максимальное число участников!";
                }
            }
            if (clan == null) {
                p.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.");
                return UltraClans.getInstance().getPrefix() + ChatColor.RED + "Системная ошибка! Обратитесь к администратору.";
            }
            ClanManager.addPlayer(clan, player);
            return UltraClans.getInstance().getPrefix() + ChatColor.GREEN + "Вы приняли приглашение от игрока " + ChatColor.GOLD + cName.get();
        }

        if(cName.get() == null){
            return UltraClans.getInstance().getPrefix() + ChatColor.GREEN + "У вас нет предложений о вступлении в клан!";
        }

        Player owner = Bukkit.getPlayer(cName.get());
        if (owner != null) {
            owner.sendMessage(UltraClans.getInstance().getPrefix() + ChatColor.GREEN + "Игрок " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " отклонил приглашение в ваш клан!");
        }

        return UltraClans.getInstance().getPrefix() + ChatColor.GREEN + "Вы отклонили приглашение от игрока " + ChatColor.GOLD + cName.get();
    }

    private static String getKeyByValue(String value) {
        for (String str : invites.keySet()) {
            if (invites.get(str).equals(value)) {
                return str;
            }
        }
        return null;
    }

    public static void remove(Player player){
        invites.remove(player.getName());
    }

    public boolean canAccept(Player invite) {
        return times.get(invite.getName()) > System.currentTimeMillis();
    }

    public static boolean isTime(Player invite) {
        return times.containsKey(invite.getName()) && times.get(invite.getName()) > System.currentTimeMillis();
    }


    public static String toTime(Player player) {
        int second = times.containsKey(player.getName()) && times.get(player.getName()) > System.currentTimeMillis() ? (int) (((times.get(player.getName()) - System.currentTimeMillis())) / 1000) :
                0;

        int seconds = second % 60;

        return String.format("%02d", seconds);

    }

}
