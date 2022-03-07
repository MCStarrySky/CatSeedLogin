package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CTitle;
import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.event.CatSeedPlayerRegisterEvent;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

public class CommandRegister implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
        if (args.length != 2) return false;
        Player player = (Player) sender;
        String name = sender.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            CTitle.sendTitle((Player) sender, "§c你已经注册过了", "§7改密请使用 /changepw");
            return true;
        }
        if (LoginPlayerHelper.isRegister(name)) {
            CTitle.sendTitle((Player) sender, "§c注册失败", "§7这个名字已经被注册过了");
            return true;
        }
        if (!args[0].equals(args[1])) {
            CTitle.sendTitle((Player) sender, "§c注册失败", "§7两次输入的密码不一样");
            return true;
        }
        if (!Util.passwordIsDifficulty(args[0])) {
            CTitle.sendTitle((Player) sender, "§c密码过于简单", "§7请更换更复杂的密码");
            return true;
        }
        if (!Cache.isLoaded) {
            return true;
        }
        CTitle.sendTitle((Player) sender, "§e正在注册中", "§7欢迎加入我们");
        CatSeedLogin.instance.runTaskAsync(() -> {
            try {
                String currentIp = player.getAddress().getAddress().getHostAddress();
                List<LoginPlayer> LoginPlayerListlikeByIp = CatSeedLogin.sql.getLikeByIp(currentIp);
                if (LoginPlayerListlikeByIp.size() >= Config.Settings.IpRegisterCountLimit) {
                    CTitle.sendTitle((Player) sender, "§c注册失败了", "§7你注册了太多帐号了");
                } else {
                    LoginPlayer lp = new LoginPlayer(name, args[0]);
                    lp.crypt();
                    CatSeedLogin.sql.add(lp);
                    LoginPlayerHelper.add(lp);
                    Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                        CatSeedPlayerRegisterEvent event = new CatSeedPlayerRegisterEvent(Bukkit.getPlayer(sender.getName()));
                        Bukkit.getServer().getPluginManager().callEvent(event);
                    });
                    CTitle.sendTitle((Player) sender, "§e注册成功", "§7欢迎加入我们");
                    Bukkit.getScheduler().runTask(CatSeedLogin.plugin , () -> ((Player) sender).removePotionEffect(PotionEffectType.BLINDNESS));
                    player.updateInventory();
                    LoginPlayerHelper.recordCurrentIP(player, lp);
                }


            } catch (Exception e) {
                e.printStackTrace();
                CTitle.sendTitle((Player) sender, "§c服务器内部错误!", "§7稍后再试或联系管理员");
            }
        });
        return true;

    }
}
