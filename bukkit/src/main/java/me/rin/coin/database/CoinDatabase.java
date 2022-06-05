package me.rin.coin.database;

import com.hakan.core.HCore;
import com.hakan.core.database.DatabaseProvider;
import me.rin.coin.CoinUser;
import me.rin.coin.configuration.CoinConfiguration;
import me.rin.coin.database.providers.mysql.CoinMySQLProvider;
import me.rin.coin.database.providers.sqlite.CoinSQLiteProvider;
import me.rin.coin.rCoins;

import java.util.concurrent.TimeUnit;

public class CoinDatabase {

    public static void initialize() {
        try {
            String type = CoinConfiguration.CONFIG.getString("Database.type");
            String databaseName = CoinConfiguration.CONFIG.getString("Database.database-name");

            String ip = CoinConfiguration.CONFIG.getString("Database.auth.ip");
            String username = CoinConfiguration.CONFIG.getString("Database.auth.username");
            String password = CoinConfiguration.CONFIG.getString("Database.auth.password");
            int port = CoinConfiguration.CONFIG.getInt("Database.auth.port");

            DatabaseProvider<CoinUser> provider;
            switch (type) {
                case "mysql":
                    provider = new CoinMySQLProvider(ip, port, username, password, databaseName);
                    break;
                case "sqlite":
                    provider = new CoinSQLiteProvider(rCoins.getInstance().getDataFolder() + "/data/coins.db");
                    break;
                default:
                    throw new RuntimeException(type + " is not a valid database. Please use MySQL or SQLite");
            }

            provider.create();
            provider.updateEvery(10, TimeUnit.MINUTES);
            HCore.registerDatabaseProvider(CoinUser.class, provider);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DatabaseProvider<CoinUser> getProvider() {
        return HCore.getDatabaseProvider(CoinUser.class);
    }


    private final CoinUser coinUser;

    public CoinDatabase(CoinUser coinUser) {
        this.coinUser = coinUser;
    }

    public CoinUser getCoinUser() {
        return this.coinUser;
    }

    public void insert() {
        getProvider().insert(this.coinUser);
    }

    public void update() {
        getProvider().update(this.coinUser);
    }

    public void delete() {
        getProvider().delete(this.coinUser);
    }

    public void insertAsync() {
        HCore.asyncScheduler().run(this::insert);
    }

    public void updateAsync() {
        HCore.asyncScheduler().run(this::update);
    }

    public void deleteAsync() {
        HCore.asyncScheduler().run(this::delete);
    }
}
