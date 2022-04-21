package me.rin.coin;

import com.hakan.core.HCore;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;


public class rCoins extends JavaPlugin {

    private static rCoins instance;

    public static rCoins getInstance() {
        return instance;
    }


    /*
    ROOT
     */
    @Override
    public void onEnable() {
        instance = this;
        HCore.initialize(this);
        CoinUserHandler.initialize(this);

        Logger logger = this.getLogger();
        logger.info("rCoins Enabled");
        logger.info("Version: " + getDescription().getVersion());
        logger.info("Developer: RIN#8198");
    }

    @Override
    public void onDisable() {
        CoinUserHandler.uninitialize();
    }
}