package cc.baka9.catseedlogin.task;

import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class TaskSendLoginMessage extends Task {

    @Override
    public void run() {
        if (!Cache.isLoaded) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!LoginPlayerHelper.isLogin(player.getName())) {
                if (!LoginPlayerHelper.isRegister(player.getName())) {
                    player.sendTitle(Config.Language.REGISTER_REQUEST , "" , 0 , 110 , 0);
                    continue;
                }
                player.sendTitle(Config.Language.LOGIN_REQUEST , "" , 0 , 110 , 0);

            }
        }
    }
}
