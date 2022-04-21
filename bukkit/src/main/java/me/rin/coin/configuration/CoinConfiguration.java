package me.rin.coin.configuration;

import com.hakan.core.utils.HYaml;
import me.rin.coin.rCoins;

public class CoinConfiguration {

    public static HYaml CONFIG;

    public static void initialize(rCoins plugin) {
        CONFIG = HYaml.create(plugin, "config.yml", "config.yml");
    }
}