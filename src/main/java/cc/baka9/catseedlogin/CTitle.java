package cc.baka9.catseedlogin;

import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CTitle {

    public static HashMap<Player, Boolean> titlem = new HashMap<>();

    public static void sendTitle(Player p, String s, String s1) {
        titlem.put(p, true);
        p.sendTitle(s, s1, 0, 30, 0);
        p.playSound(p.getLocation() , Sound.BLOCK_NOTE_BLOCK_BELL  , 1 , 1);
        Bukkit.getScheduler().runTaskLater(CatSeedLogin.plugin, () -> {
            titlem.put(p, false);
            if (!LoginPlayerHelper.isLogin(p.getName())) {
                if (!LoginPlayerHelper.isRegister(p.getName())) {
                    p.sendTitle(ChatColor.COLOR_CHAR + "e欢迎 初来乍到,请注册", ChatColor.COLOR_CHAR + "7输入 /reg 密码 重复密码 来注册", 0, 110, 0);
                } else {
                    p.sendTitle(ChatColor.COLOR_CHAR + "a欢迎回来 请登陆", ChatColor.COLOR_CHAR + "7输入 /l 密码 来登陆", 0, 110, 0);
                }
            }
        }, 28L);
    }

    public static void sendTitle(Player p, String s) {
        titlem.put(p, true);
        p.sendTitle(s, "", 0, 30, 0);
        p.playSound(p.getLocation() , Sound.BLOCK_NOTE_BLOCK_BELL  , 1 , 1);
        Bukkit.getScheduler().runTaskLater(CatSeedLogin.plugin, () -> {
            titlem.put(p, false);
            if (!LoginPlayerHelper.isLogin(p.getName())) {
                if (!LoginPlayerHelper.isRegister(p.getName())) {
                    p.sendTitle(ChatColor.COLOR_CHAR + "e欢迎 初来乍到,请注册", ChatColor.COLOR_CHAR + "7输入 /reg 密码 重复密码 来注册", 0, 110, 0);
                } else {
                    p.sendTitle(ChatColor.COLOR_CHAR + "a欢迎回来 请登陆", ChatColor.COLOR_CHAR + "7输入 /l 密码 来登陆", 0, 110, 0);
                }
            }
        }, 28L);
    }
}
