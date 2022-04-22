package me.rin.coin.commands;


import com.hakan.core.command.HCommandExecutor;
import me.rin.coin.CoinUser;
import me.rin.coin.CoinUserHandler;
import me.rin.coin.configuration.CoinConfiguration;
import me.rin.coin.util.CoinUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

public class CoinCommand extends HCommandExecutor {

    public CoinCommand(String command, String... aliases) {
        super(command, null, aliases);
        super.subCommand("admin").subCommand("reload");
        super.subCommand("transfer");
        super.subCommand("admin").subCommand("set");
        super.subCommand("admin").subCommand("add");
        super.subCommand("admin").subCommand("remove");
    }

    public CoinCommand(String command, List<String> aliases) {
        this(command, aliases.toArray(new String[0]));
    }

    @Override
    public void onCommand(@Nonnull CommandSender sender, @Nonnull String[] args) {
        if (sender instanceof Player) {

            Player player = (Player) sender;
            CoinUser user = CoinUserHandler.getOrLoad(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.coin-you-have").replace("%coin%", user.getCoin().toString())));
                return;
            } else if (args.length == 1) {
                if (args[0].equals("admin")) {
                    if (sender.isOp()) {
                        player.sendMessage(CoinUtils.colored("&a/coin <player> &8-> &f shows player's coins"));
                        player.sendMessage(CoinUtils.colored("&a/coin admin set <player> <value> &8-> &f sets player's coin to value"));
                        player.sendMessage(CoinUtils.colored("&a/coin admin add <player> <value> &8-> &f adds value to player's coins"));
                        player.sendMessage(CoinUtils.colored("&a/coin admin remove <player> <value> &8-> &f removes value from player's coins"));
                        player.sendMessage(CoinUtils.colored("&a/coin admin reload &8-> &f reloads the config file"));
                        return;
                    }
                } else if (args[0].equals("help")) {

                    player.sendMessage(CoinUtils.colored("&a/coin &8-> &f shows your coins"));
                    player.sendMessage(CoinUtils.colored("&a/coin transfer <player> <value> &8-> &f sends value of coins to another player"));
                    player.sendMessage(CoinUtils.colored("&a/coin admin &8-> &f shows admin commands"));
                    return;
                } else {
                    if (player.isOp()) {
                        if (Bukkit.getPlayer(args[0]) != null) {
                            CoinUser target = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[0]).getUniqueId());
                            player.sendMessage(CoinUtils.colored("&aPlayer &f" + args[0] + " &ahas &f" + target.getCoin() + " &acoins."));
                        } else {
                            player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[0]));
                        }
                        return;
                    }
                }
            } else if (args.length == 2) {
                if (args[1].equals("reload")) {
                    if (player.isOp()) {
                        CoinConfiguration.CONFIG.reload();
                        player.sendMessage(CoinUtils.colored("&aCoin system reloaded"));
                    } else {
                        player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.insufficient-permission")));
                    }
                    return;
                }
            } else if (args.length == 3) {
                if (args[0].equals("transfer")) {
                    if (!args[1].equals(player.getName()) && Bukkit.getPlayer(args[1]) != null) {
                        if (CoinConfiguration.CONFIG.getBoolean("Account-Protection.enabled")) {
                            if (user.getCoin() < CoinConfiguration.CONFIG.getInt("Account-Protection.minimum")) {
                                player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.account-error").replace("%coin%", String.valueOf(CoinConfiguration.CONFIG.getInt("Account-Protection.minimum")))));
                                return;
                            }
                        }

                        int coinToSend = Integer.parseInt(args[2]);
                        if (user.getCoin() >= coinToSend) {
                            CoinUser target = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[1]).getUniqueId());

                            CoinUserHandler.transferCoin(user, target, coinToSend);
                            player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.transfer-success").replace("%coins%", args[2]).replace("%target%", args[1])));
                        } else {
                            player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.transfer-failed").replace("%coin%", String.valueOf(Integer.parseInt(args[2]) - user.getCoin()))));
                        }
                    } else {
                        player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[1]));
                    }
                    return;
                }
            } else if (args.length == 4) {
                if (args[0].equals("admin")) {
                    if (player.isOp()) {
                        if (args[1].equals("set")) {
                            if (Bukkit.getPlayer(args[2]) == null) {
                                player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[2]));
                                return;
                            }
                            CoinUser target = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[2]).getUniqueId());
                            target.changeCoin(Integer.valueOf(args[3]));
                            player.sendMessage(CoinUtils.colored("&aYou successfully set &f" + args[2] + "&f's&a coin value to &f" + args[3]));
                            return;
                        } else if (args[1].equals("add")) {
                            if (Bukkit.getPlayer(args[2]) == null) {
                                player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[2]));
                                return;
                            }
                            CoinUser target = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[2]).getUniqueId());
                            int value = Integer.parseInt(args[3]);
                            target.changeCoin(target.getCoin() + value);
                            player.sendMessage(CoinUtils.colored("&aYou successfully added &f" + value + "&a coins to &f" + target.getName()));
                            return;
                        } else if (args[1].equals("remove")) {
                            if (Bukkit.getPlayer(args[2]) == null) {
                                player.sendMessage(CoinUtils.colored(CoinConfiguration.CONFIG.getString("Messages.player-not-found")).replace("%name%", args[2]));
                                return;
                            }
                            CoinUser target = CoinUserHandler.getOrLoad(Bukkit.getPlayer(args[2]).getUniqueId());
                            int value = Integer.parseInt(args[3]);
                            if (value > target.getCoin()) {
                                target.changeCoin(0);
                            } else {
                                target.changeCoin(target.getCoin() - value);
                            }

                            player.sendMessage(CoinUtils.colored("&aYou successfully removed &f" + value + "&a coins from &f" + target.getName() + ". &aNow &f" + target.getName() + " &a has only &f " + target.getCoin() + " &acoins."));
                            return;
                        }
                    }
                }
            }

            player.sendMessage(CoinUtils.colored("&c there's no such a command"));
            player.sendMessage(CoinUtils.colored("&f----------------------------"));
            player.sendMessage(CoinUtils.colored("&a/coin &8-> &f shows your coins"));
            player.sendMessage(CoinUtils.colored("&a/coin transfer <player> <value> &8-> &f sends value of coins to another player"));
            player.sendMessage(CoinUtils.colored("&a/coin admin &8-> &f shows admin commands"));
        }
    }
}