package me.rin.coin.commands;


import com.hakan.core.command.HCommandAdapter;
import com.hakan.core.command.executors.base.BaseCommand;
import com.hakan.core.command.executors.sub.SubCommand;
import com.hakan.core.utils.ColorUtil;
import me.rin.coin.CoinUser;
import me.rin.coin.CoinUserHandler;
import me.rin.coin.configuration.CoinConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@BaseCommand(
        name = "rcoin",
        description = "Coin command",
        usage = "coin <command>",
        aliases = {"coin", "rcoins"}
)
public class CoinCommand implements HCommandAdapter {

    @SubCommand
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            CoinUser user = CoinUserHandler.getOrLoad(player.getUniqueId());
            player.sendMessage(ColorUtil.colored(CoinConfiguration.CONFIG.getString("Messages.coin-you-have")
                    .replace("%coin%", user.getCoin().toString())));
        } else if (args.length == 1) {
            Player targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer != null) {
                CoinUser target = CoinUserHandler.getOrLoad(targetPlayer.getUniqueId());
                player.sendMessage(ColorUtil.colored("&aPlayer &f" + args[0] + " &ahas &f" + target.getCoin() + " &acoins."));
            } else {
                player.sendMessage(ColorUtil.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[0]));
            }
        }
    }

    @SubCommand(
            args = "help"
    )
    public void help(CommandSender sender, String[] args) {
        if (sender.isOp() || sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ColorUtil.colored("&a/coin <player> &8-> &f shows player's coins"));
            sender.sendMessage(ColorUtil.colored("&a/coin admin set <player> <value> &8-> &f sets player's coin to value"));
            sender.sendMessage(ColorUtil.colored("&a/coin admin add <player> <value> &8-> &f adds value to player's coins"));
            sender.sendMessage(ColorUtil.colored("&a/coin admin remove <player> <value> &8-> &f removes value from player's coins"));
            sender.sendMessage(ColorUtil.colored("&a/coin admin reload &8-> &f reloads the config file"));
        } else {
            CoinConfiguration.CONFIG.getStringList("Messages.help")
                    .forEach(key -> sender.sendMessage(ColorUtil.colored(key)));
        }
    }

    @SubCommand(
            args = "transfer"
    )
    public void transfer(Player player, String[] args) {
        if (args.length == 3) {
            CoinUser user = CoinUserHandler.getOrLoad(player.getUniqueId());

            if (!args[1].equals(player.getName()) && Bukkit.getPlayer(args[1]) != null) {
                if (CoinConfiguration.CONFIG.getBoolean("Account-Protection.enabled")) {
                    if (user.getCoin() < CoinConfiguration.CONFIG.getInt("Account-Protection.minimum")) {
                        player.sendMessage(ColorUtil.colored(CoinConfiguration.CONFIG.getString("Messages.account-error").replace("%coin%", String.valueOf(CoinConfiguration.CONFIG.getInt("Account-Protection.minimum")))));
                        return;
                    }
                }

                int coinToSend = Integer.parseInt(args[2]);
                if (user.getCoin() >= coinToSend) {
                    CoinUser target = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[1]).getUniqueId());

                    CoinUserHandler.transferCoin(user, target, coinToSend);
                    player.sendMessage(ColorUtil.colored(CoinConfiguration.CONFIG.getString("Messages.transfer-success").replace("%coins%", args[2]).replace("%target%", args[1])));
                } else {
                    player.sendMessage(ColorUtil.colored(CoinConfiguration.CONFIG.getString("Messages.transfer-failed").replace("%coin%", String.valueOf(Integer.parseInt(args[2]) - user.getCoin()))));
                }
            } else {
                player.sendMessage(ColorUtil.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[1]));
            }
        } else {
            player.sendMessage(ColorUtil.colored("&cUsage: /coin transfer <player> <value>"));
        }
    }


    @SubCommand(
            args = {"admin", "reload"},
            permission = "rcoins.admin",
            permissionMessage = "§cYou don't have permission to use this command."
    )
    public void adminReload(CommandSender sender, String[] args) {
        CoinConfiguration.CONFIG.reload();
        sender.sendMessage(ColorUtil.colored("&aCoin system reloaded"));
    }

    @SubCommand(
            args = {"admin", "set"},
            permission = "rcoins.admin",
            permissionMessage = "§cYou don't have permission to use this command."
    )
    public void adminSet(CommandSender sender, String[] args) {
        if (args.length == 4) {
            CoinUser targetUser = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[2]).getUniqueId());
            targetUser.changeCoin(Integer.valueOf(args[3]));
            sender.sendMessage(ColorUtil.colored("&aYou successfully set &f" + args[2] + "&f's&a coin value to &f" + args[3]));
        } else {
            sender.sendMessage(ColorUtil.colored("&cUsage: /coin admin set <player> <value>"));
        }
    }

    @SubCommand(
            args = {"admin", "add"},
            permission = "rcoins.admin",
            permissionMessage = "§cYou don't have permission to use this command."
    )
    public void adminAdd(CommandSender sender, String[] args) {
        if (args.length == 4) {
            CoinUser targetUser = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[2]).getUniqueId());
            int value = Integer.parseInt(args[3]);
            targetUser.changeCoin(targetUser.getCoin() + value);
            sender.sendMessage(ColorUtil.colored("&aYou successfully added &f" + value + "&a coins to &f" + targetUser.getName()));
        } else {
            sender.sendMessage(ColorUtil.colored("&cUsage: /coin admin add <player> <value>"));
        }
    }

    @SubCommand(
            args = {"admin", "remove"},
            permission = "rcoins.admin",
            permissionMessage = "§cYou don't have permission to use this command."
    )
    public void adminRemove(CommandSender sender, String[] args) {
        if (args.length == 4) {
            CoinUser targetUser = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[2]).getUniqueId());
            int value = Integer.parseInt(args[3]);
            targetUser.changeCoin(Math.max(0, targetUser.getCoin() - value));
            sender.sendMessage(ColorUtil.colored("&aYou successfully removed &f" + value + "&a coins from &f" + targetUser.getName() + ". &aNow &f" + targetUser.getName() + " &a has only &f " + targetUser.getCoin() + " &acoins."));
        } else {
            sender.sendMessage(ColorUtil.colored("&cUsage: /coin admin remove <player> <value>"));
        }
    }
}