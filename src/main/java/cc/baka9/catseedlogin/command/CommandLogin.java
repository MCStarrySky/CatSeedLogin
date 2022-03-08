package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CTitle;
import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.event.CatSeedPlayerLoginEvent;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Crypt;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class CommandLogin implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lable, String[] args) {
        if (args.length == 0 || !(sender instanceof Player)) return false;
        Player player = (Player) sender;
        String name = player.getName();
        if (LoginPlayerHelper.isLogin(name)) {
            CTitle.sendTitle((Player) sender, "§a你已经登陆了", "§f无需重复登陆");
            return true;
        }
        LoginPlayer lp = Cache.getIgnoreCase(name);
        if (lp == null) {
            CTitle.sendTitle((Player) sender, "§c你还没有注册", "§f因此你无法登陆");
            return true;
        }
        if (Objects.equals(Crypt.encrypt(name, args[0]), lp.getPassword().trim())) {
            LoginPlayerHelper.add(lp);
            CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.SUCCESS);
            Bukkit.getServer().getPluginManager().callEvent(loginEvent);
            CTitle.sendTitle((Player) sender, "§a登陆成功", "§f欢迎回来");
            Bukkit.getScheduler().runTask(CatSeedLogin.plugin , () -> ((Player) sender).removePotionEffect(PotionEffectType.BLINDNESS));
            player.updateInventory();
            LoginPlayerHelper.recordCurrentIP(player, lp);
            if (Config.Settings.AfterLoginBack && Config.Settings.CanTpSpawnLocation) {
                Config.getOfflineLocation(player).ifPresent(player::teleport);
            }
        } else {
            CTitle.sendTitle((Player) sender, "§c登陆失败", "§f请重新输入密码登陆");
            CatSeedPlayerLoginEvent loginEvent = new CatSeedPlayerLoginEvent(player, lp.getEmail(), CatSeedPlayerLoginEvent.Result.FAIL);
            Bukkit.getServer().getPluginManager().callEvent(loginEvent);
            if (Config.EmailVerify.Enable) {
                CTitle.sendTitle((Player) sender, "§c密码是错误的", "§7重置密码请输入 /repw forget");
            }
        }
        return true;
    }
}
