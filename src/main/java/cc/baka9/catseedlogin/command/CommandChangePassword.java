package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CTitle;
import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Crypt;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandChangePassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
        if (args.length != 3 || !(sender instanceof Player)) {
            return false;
        }
        String name = sender.getName();
        LoginPlayer lp = Cache.getIgnoreCase(name);
        if (lp == null) {
            CTitle.sendTitle((Player) sender, "§c你还没有注册", "§7因此你无法修改密码");
            return true;
        }
        if (!LoginPlayerHelper.isLogin(name)) {
            CTitle.sendTitle((Player) sender, "§c你还没有登陆", "§7因此你无法修改密码");
            return true;
        }
        if (!Objects.equals(Crypt.encrypt(name, args[0]), lp.getPassword().trim())) {
            CTitle.sendTitle((Player) sender, "§c旧密码错误", "§7因此你无法修改密码");
            return true;

        }
        if (!args[1].equals(args[2])) {
            CTitle.sendTitle((Player) sender, "§c两次密码不一致 ", "§7因此你无法修改密码");
            return true;
        }
        if (!Util.passwordIsDifficulty(args[1])) {
            CTitle.sendTitle((Player) sender, "§c你的密码太简单", "§7因此你无法修改密码");
            return true;
        }
        if (!Cache.isLoaded) {
            return true;
        }
        CTitle.sendTitle((Player) sender, "§e密码正在修改", "§7修改中..");
        CatSeedLogin.instance.runTaskAsync(() -> {
            try {
                lp.setPassword(args[1]);
                lp.crypt();
                CatSeedLogin.sql.edit(lp);
                LoginPlayerHelper.remove(lp);

                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                    Player player = Bukkit.getPlayer(((Player) sender).getUniqueId());
                    if (player != null && player.isOnline()) {
                        CTitle.sendTitle((Player) sender, "§e密码修改成功", "§7你可以使用新密码登陆了");
                        Config.setOfflineLocation(player);
                        if (Config.Settings.CanTpSpawnLocation) {
                            player.teleport(Config.Settings.SpawnLocation);
                            if (CatSeedLogin.loadProtocolLib) {
                                LoginPlayerHelper.sendBlankInventoryPacket(player);
                            }
                        }

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                CTitle.sendTitle((Player) sender, "§c服务器内部错误", "§7请稍后再试或联系管理员");
            }
        });
        return true;
    }
}
