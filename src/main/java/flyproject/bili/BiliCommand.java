package flyproject.bili;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static flyproject.bili.Color.color;

public class BiliCommand implements CommandExecutor {
    private static void sendHelp(CommandSender sender) {
        sender.sendMessage(color(BilibiliReward.instance.getConfig().getString("header")));
        sender.sendMessage(color("&f/breward bind <b站id> &7———— &b绑定您的B站用户id"));
        if (sender.isOp()) {
            sender.sendMessage(color("&f/breward force &7———— &b强制刷新"));
            sender.sendMessage(color("&f/breward reload &7———— &b重载配置"));
            sender.sendMessage(color("&f/breward set <玩家名称> <b站id> &7———— &b设置玩家绑定的B站用户"));
            sender.sendMessage(color("&f/breward look <玩家名称> &7———— &b查看玩家绑定的B站用户"));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (sender.isOp()) {
                if (args[0].equals("force")) {
                    Bukkit.getScheduler().runTaskAsynchronously(BilibiliReward.instance, () -> {
                        try {
                            RewardUtil.excute();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                    sender.sendMessage(color("&c!已强制更新数据!"));
                } else if (args[0].equals("reload")) {
                    BilibiliReward.instance.reloadConfig();
                    sender.sendMessage(color("&f配置已重载"));
                } else {
                    sendHelp(sender);
                }
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 2) {
            if (args[0].equals("bind")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(color("&c此命令仅可由玩家执行!"));
                    return false;
                }
                Player player = (Player) sender;
                String uid = args[1];
                if (MySQL.ENABLED) {
                    if (MySQL.getData(player.getUniqueId().toString()) != null) {
                        player.sendMessage(color("&c您的账号已绑定了B站用户 请勿重复绑定!"));
                        return false;
                    } else {
                        if (RewardUtil.hasBinded(uid)){
                            player.sendMessage(color("&c该UID已绑定过其它玩家"));
                            return false;
                        }
                        MySQL.savePlayer(new Gson().toJson(DataUtil.initJson(uid)), player.getUniqueId().toString());
                        player.sendMessage(color("&fB站账号绑定成功!"));
                        return true;
                    }
                } else {
                    if (YAML.hasData(player.getUniqueId())) {
                        player.sendMessage(color("&c您的账号已绑定了B站用户 请勿重复绑定!"));
                        return false;
                    } else {
                        if (RewardUtil.hasBinded(uid)){
                            player.sendMessage(color("&c该UID已绑定过其它玩家"));
                            return false;
                        }
                        YAML.setData(player.getUniqueId(), new Gson().toJson(DataUtil.initJson(uid)));
                        player.sendMessage(color("&fB站账号绑定成功!"));
                        return true;
                    }
                }
            } else if (args[0].equals("look")) {
                if (!sender.isOp()) return false;
                String name = args[1];
                String uid;
                if (MySQL.ENABLED) {
                    uid = MySQL.getData(Bukkit.getOfflinePlayer(name).getUniqueId().toString());
                } else {
                    uid = YAML.getData(Bukkit.getOfflinePlayer(name).getUniqueId());
                }
                if (uid == null) {
                    sender.sendMessage(color("&c该玩家未绑定B站用户"));
                    return false;
                }
                sender.sendMessage(color("&f该玩家绑定的B站用户为: " + DataUtil.getUID(new JsonParser().parse(uid).getAsJsonObject())));
            } else {
                sendHelp(sender);
            }
        } else if (args.length == 3) {
            if (args[0].equals("set")) {
                String name = args[1];
                String uid = args[2];
                OfflinePlayer player = Bukkit.getOfflinePlayer(name);
                if (MySQL.ENABLED) {
                    MySQL.savePlayer(new Gson().toJson(DataUtil.initJson(uid)), player.getUniqueId().toString());
                    sender.sendMessage(color("&fB站账号设置成功!"));
                    return true;
                } else {
                    YAML.setData(player.getUniqueId(), new Gson().toJson(DataUtil.initJson(uid)));
                    sender.sendMessage(color("&fB站账号设置成功!"));
                    return true;
                }
            } else {
                sendHelp(sender);
            }
        } else {
            sendHelp(sender);
        }
        return false;
    }
}
