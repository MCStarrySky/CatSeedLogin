package cc.baka9.catseedlogin.command;

import cc.baka9.catseedlogin.CTitle;
import cc.baka9.catseedlogin.CatSeedLogin;
import cc.baka9.catseedlogin.Config;
import cc.baka9.catseedlogin.database.Cache;
import cc.baka9.catseedlogin.object.EmailCode;
import cc.baka9.catseedlogin.object.LoginPlayer;
import cc.baka9.catseedlogin.object.LoginPlayerHelper;
import cc.baka9.catseedlogin.util.Mail;
import cc.baka9.catseedlogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandResetPassword implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0 || !(sender instanceof Player)) return false;
        Player player = (Player) sender;
        String name = player.getName();
        LoginPlayer lp = Cache.getIgnoreCase(name);

        if (lp == null) {
            CTitle.sendTitle((Player) sender, "§c你还没有注册", "§7因此你不能修改密码");
            return true;
        }
        if (!Config.EmailVerify.Enable) {
            CTitle.sendTitle((Player) sender, "§6服务器没有启用邮箱", "§7无法绑定你的邮箱与账户");
            return true;
        }
        //command forget
        if (args[0].equalsIgnoreCase("forget")) {
            if (lp.getEmail() == null) {
                CTitle.sendTitle((Player) sender, "§6邮箱设置有误", "§7无法绑定你的邮箱与账户");
            } else {
                Optional<EmailCode> optionalEmailCode = EmailCode.getByName(name, EmailCode.Type.ResetPassword);
                if (optionalEmailCode.isPresent()) {
                    CTitle.sendTitle((Player) sender, "§6已经向您发送验证码", "§7请勿重复操作");
                } else {
                    //20分钟有效期的验证码
                    EmailCode emailCode = EmailCode.create(name, lp.getEmail(), 1000 * 60 * 20, EmailCode.Type.ResetPassword);
                    CTitle.sendTitle((Player) sender, "§6正在发送验证码", "§7邮箱为 " + lp.getEmail());
                    CatSeedLogin.instance.runTaskAsync(() -> {
                        try {
                            Mail.sendMail(emailCode.getEmail(), "重置密码",
                                    "你的验证码是 <strong>" + emailCode.getCode() + "</strong>" +
                                            "<br/>在服务器中使用帐号 " + name + " 输入指令<strong>/resetpassword re " + emailCode.getCode() + " 新密码</strong> 来重置新密码" +
                                            "<br/>此验证码有效期为 " + (emailCode.getDurability() / (1000 * 60)) + "分钟");
                            Bukkit.getScheduler().runTask(CatSeedLogin.instance, () ->
                                    CTitle.sendTitle((Player) sender, "§6验证码已经发送完成", "§7请查阅您的邮箱"));
                        } catch (Exception e) {
                            Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> CTitle.sendTitle((Player) sender, "§c邮件发送错误", "§7请稍后再试或联系管理员"));
                            e.printStackTrace();
                        }
                    });
                }
            }
            return true;
        }
        //command re
        if (args[0].equalsIgnoreCase("re") && args.length > 2) {
            if (lp.getEmail() == null) {
                CTitle.sendTitle((Player) sender, "§c你没有绑定邮箱", "§7因此你无法这样重置密码");
            } else {
                Optional<EmailCode> optionalEmailCode = EmailCode.getByName(name, EmailCode.Type.ResetPassword);
                if (optionalEmailCode.isPresent()) {
                    EmailCode emailCode = optionalEmailCode.get();
                    String code = args[1], pwd = args[2];

                    if (emailCode.getCode().equals(code)) {
                        if (!Util.passwordIsDifficulty(pwd)) {
                            CTitle.sendTitle((Player) sender, "§c密码过于简单", "§7请更换更复杂的密码");
                            return true;
                        }
                        CTitle.sendTitle((Player) sender, "§e密码重置中..", "§7请稍等..");
                        CatSeedLogin.instance.runTaskAsync(() -> {
                            lp.setPassword(pwd);
                            lp.crypt();
                            try {
                                CatSeedLogin.sql.edit(lp);
                                LoginPlayerHelper.remove(lp);
                                EmailCode.removeByName(name, EmailCode.Type.ResetPassword);
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                    Player p = Bukkit.getPlayer(lp.getName());
                                    if (p != null && p.isOnline()) {
                                        if (Config.Settings.CanTpSpawnLocation) {
//                                            PlayerTeleport.teleport(p, Config.Settings.SpawnLocation);
                                            p.teleport(Config.Settings.SpawnLocation);
                                        }
                                        CTitle.sendTitle((Player) sender, "§e密码修改成功", "§7你可以使用新密码登陆了");
                                    }

                                });
                            } catch (Exception e) {
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> CTitle.sendTitle((Player) sender, "§c数据库异常", "§7请稍后再试或联系管理员"));
                                e.printStackTrace();
                            }


                        });
                    } else {
                        CTitle.sendTitle((Player) sender, "§c你的验证码出错", "§7请重新查阅邮件并填写");
                    }

                } else {
                    CTitle.sendTitle((Player) sender, "§c密码重置出错", "§7没有待重置密码的请求操作 或者验证码已过期");
                }
            }
            return true;
        }
        return true;
    }
}
