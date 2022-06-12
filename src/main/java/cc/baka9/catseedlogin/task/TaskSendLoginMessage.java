package cc.baka9.catseedlogin.task;

import cc.baka9.catseedlogin.CTitle;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class TaskSendLoginMessage extends Task {

    @Override
    public void run() {
        if (!Cache.isLoaded) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!LoginPlayerHelper.isLogin(player.getName()) && !CTitle.titlem.get(player)) {
                if (!LoginPlayerHelper.isRegister(player.getName())) {
                    player.sendTitle(ChatColor.COLOR_CHAR + "e欢迎 初来乍到,请注册", ChatColor.COLOR_CHAR + "7输入 /reg 密码 重复密码 来注册", 0, 110, 0);
                    continue;
                }
                player.sendTitle(ChatColor.COLOR_CHAR + "a欢迎回来 请登陆", ChatColor.COLOR_CHAR + "7输入 /l 密码 来登陆", 0, 110, 0);

            }
        }
    }
}
