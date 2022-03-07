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


public class CommandBindEmail implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length == 0 || !(sender instanceof Player)) return false;
        Player player = (Player) sender;
        String name = player.getName();
        LoginPlayer lp = Cache.getIgnoreCase(name);

        if (lp == null) {
            CTitle.sendTitle((Player) sender, "§c你还未注册", "§c输入 /reg 密码 重复密码 来注册");
            return true;
        }
        if (!LoginPlayerHelper.isLogin(name)) {
            CTitle.sendTitle((Player) sender, "§c你还未登陆", "§c输入 /l 密码 来登陆");
            return true;
        }
        if (!Config.EmailVerify.Enable) {
            CTitle.sendTitle((Player) sender, "§c服务器没有开启邮箱功能");
            return true;
        }

        // command set email
        if (args[0].equalsIgnoreCase("set") && args.length > 1) {
            if (lp.getEmail() != null && Util.checkMail(lp.getEmail())) {
                CTitle.sendTitle((Player) sender, "§c你已经绑定过邮箱了");
            } else {
                String mail = args[1];
                Optional<EmailCode> bindEmailOptional = EmailCode.getByName(name, EmailCode.Type.Bind);
                if (bindEmailOptional.isPresent() && bindEmailOptional.get().getEmail().equals(mail)) {
                    CTitle.sendTitle((Player) sender, "§c已经发送验证码，请不要重复此操作", "§7邮箱为 " + mail);

                } else if (Util.checkMail(mail)) {
                    //创建有效期为20分钟的验证码
                    EmailCode bindEmail = EmailCode.create(name, mail, 1000 * 60 * 20, EmailCode.Type.Bind);
                    CTitle.sendTitle((Player) sender, "§6向邮箱发送验证码中...");
                    CatSeedLogin.instance.runTaskAsync(() -> {
                        try {
                            Mail.sendMail(mail, "邮箱绑定",
                                    "你的验证码是 <strong>" + bindEmail.getCode() + "</strong>" +
                                            "<br/>在服务器中使用帐号 " + name + " 输入指令<strong>/bindemail verify " + bindEmail.getCode() + "</strong> 来绑定邮箱" +
                                            "<br/>绑定邮箱之后可用于忘记密码时重置自己的密码" +
                                            "<br/>此验证码有效期为 " + (bindEmail.getDurability() / (1000 * 60)) + "分钟");
                            Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                CTitle.sendTitle((Player) sender, "§6验证码已经发送完成", "§7请查阅您的邮箱");
                            });
                        } catch (Exception e) {
                            Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> CTitle.sendTitle((Player) sender, "§c发送邮件失败", "§7请稍后再试或联系管理员"));
                            e.printStackTrace();
                        }
                    });


                } else {
                    CTitle.sendTitle((Player) sender, "§c邮箱格式不正确", "§7请重试");
                }
            }
            return true;
        }

        // command verify code
        if (args[0].equalsIgnoreCase("verify") && args.length > 1) {
            if (lp.getEmail() != null && Util.checkMail(lp.getEmail())) {
                CTitle.sendTitle((Player) sender, "§c你已经绑定过邮箱了");
            } else {
                Optional<EmailCode> emailOptional = EmailCode.getByName(name, EmailCode.Type.Bind);
                if (emailOptional.isPresent()) {
                    EmailCode bindEmail = emailOptional.get();
                    String code = args[1];
                    if (bindEmail.getCode().equals(code)) {
                        CTitle.sendTitle((Player) sender, "§e绑定邮箱中..", "§7请稍等..");
                        CatSeedLogin.instance.runTaskAsync(() -> {
                            try {
                                lp.setEmail(bindEmail.getEmail());
                                CatSeedLogin.sql.edit(lp);
                                Bukkit.getScheduler().runTask(CatSeedLogin.instance, () -> {
                                    Player syncPlayer = Bukkit.getPlayer(((Player) sender).getUniqueId());
                                    if (syncPlayer != null && syncPlayer.isOnline()) {
                                        CTitle.sendTitle(syncPlayer, "§a邮箱绑定成功", "§7邮箱为 " + bindEmail.getEmail());
                                        EmailCode.removeByName(name, EmailCode.Type.Bind);
                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                                CTitle.sendTitle((Player) sender, "§c服务器内部错误", "§7请稍后再试或联系管理员");
                            }
                        });

                    } else {
                        CTitle.sendTitle((Player) sender, "§c验证码错误", "§7请重试");
                    }

                } else {
                    CTitle.sendTitle((Player) sender, "§c你没有待绑定的邮箱", "§7或者验证码已过期");
                }


            }
            return true;
        }

        return true;
    }
}
