package me.rin.coin.listeners;

import me.rin.coin.CoinUser;
import me.rin.coin.CoinUserHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerConnectionListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        CoinUser coinUser = CoinUserHandler.getOrLoad(player.getUniqueId());
        if (!coinUser.getName().equals(player.getName())) {
            coinUser.changeName(player.getName());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        UUID uid = event.getPlayer().getUniqueId();
        CoinUserHandler.findByUID(uid)
                .ifPresent(coinUser -> coinUser.getDatabase().updateAsync());
    }
}
