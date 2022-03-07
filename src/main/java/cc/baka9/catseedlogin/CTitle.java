package cc.baka9.catseedlogin;

import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CTitle {

    public static HashMap<Player, Boolean> titlem = new HashMap<>();

    public static void sendTitle(Player p, String s, String s1) {
        titlem.put(p, true);
        p.sendTitle(s, s1, 0, 30, 0);
        Bukkit.getScheduler().runTaskLater(CatSeedLogin.plugin, () -> {
            titlem.put(p, false);
            if (!LoginPlayerHelper.isLogin(p.getName())) {
                if (!LoginPlayerHelper.isRegister(p.getName())) {
                    p.sendTitle(Config.Language.REGISTER_REQUEST, ChatColor.COLOR_CHAR + "7输入/reg 密码 重复密码 来注册", 0, 60, 0);
                } else {
                    p.sendTitle(Config.Language.LOGIN_REQUEST, ChatColor.COLOR_CHAR + "7输入/l 密码 来登陆", 0, 60, 0);
                }
            }
        }, 28L);
    }

    public static void sendTitle(Player p, String s) {
        titlem.put(p, true);
        p.sendTitle(s, "", 0, 30, 0);
        Bukkit.getScheduler().runTaskLater(CatSeedLogin.plugin, () -> {
            titlem.put(p, false);
            if (!LoginPlayerHelper.isLogin(p.getName())) {
                if (!LoginPlayerHelper.isRegister(p.getName())) {
                    p.sendTitle(Config.Language.REGISTER_REQUEST, ChatColor.COLOR_CHAR + "7输入/reg 密码 重复密码 来注册", 0, 60, 0);
                } else {
                    p.sendTitle(Config.Language.LOGIN_REQUEST, ChatColor.COLOR_CHAR + "7输入/l 密码 来登陆", 0, 60, 0);
                }
            }
        }, 28L);
    }
}
