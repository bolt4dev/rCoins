package me.rin.coin;

import com.hakan.core.HCore;
import com.hakan.core.database.DatabaseProvider;
import com.hakan.core.listener.HListenerAdapter;
import me.rin.coin.commands.CoinCommand;
import me.rin.coin.configuration.CoinConfiguration;
import me.rin.coin.database.providers.mysql.CoinMySQLProvider;
import me.rin.coin.database.providers.sqlite.CoinSQLiteProvider;
import me.rin.coin.database.updater.CoinDatabaseUpdater;
import me.rin.coin.listeners.PlayerConnectionListeners;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

public class CoinUserHandler {

    private static Map<UUID, CoinUser> coinUsers;
    private static DatabaseProvider<CoinUser> databaseProvider;
    private static boolean log;


    /*
    INITIALIZE
     */
    public static void initialize(rCoins plugin) {

        //CONFIGURATION
        CoinConfiguration.initialize(plugin);


        //DATABASE
        coinUsers = new HashMap<>();
        databaseProvider = createDatabaseProvider();
        databaseProvider.create();
        log = CoinConfiguration.CONFIG.getBoolean("Settings.log");
        if (log) {
            try {
                Files.createDirectories(Paths.get("plugins/rCoins/log"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //TASKS
        HCore.asyncScheduler()
                .after(12000).every(12000)
                .run(CoinDatabaseUpdater::updateAll);

        HCore.syncScheduler()
                .after(6000).every(6000)
                .run(() -> coinUsers.values().forEach(coinUser -> {
                    if (Bukkit.getPlayer(coinUser.getUID()) == null)
                        coinUsers.remove(coinUser.getUID());
                }));


        //BUKKIT
        HListenerAdapter.register(new PlayerConnectionListeners(plugin));
        HCore.registerCommands(new CoinCommand("rcoin", CoinConfiguration.CONFIG.getStringList("Settings.commands")));
    }

    public static void uninitialize() {
        CoinDatabaseUpdater.updateAll();
    }


    /*
    PROVIDERS
     */
    public static DatabaseProvider<CoinUser> createDatabaseProvider() {
        try {
            String type = CoinConfiguration.CONFIG.getString("Database.type");
            String databaseName = CoinConfiguration.CONFIG.getString("Database.database-name");

            String ip = CoinConfiguration.CONFIG.getString("Database.auth.ip");
            int port = CoinConfiguration.CONFIG.getInt("Database.auth.port");
            String username = CoinConfiguration.CONFIG.getString("Database.auth.username");
            String password = CoinConfiguration.CONFIG.getString("Database.auth.password");

            switch (type) {
                case "mysql":
                    return new CoinMySQLProvider(ip, port, username, password, databaseName);
                case "sqlite":
                    return new CoinSQLiteProvider(rCoins.getInstance().getDataFolder() + "/data/coins.db");
                default:
                    throw new RuntimeException(type + " is not a valid database. Please use MySQL or SQLite");
            }


        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    public static DatabaseProvider<CoinUser> getDatabaseProvider() {
        return CoinUserHandler.databaseProvider;
    }

    /*
    HANDLERS
     */
    public static Map<UUID, CoinUser> getContentSafe() {
        return new HashMap<>(coinUsers);
    }

    public static Map<UUID, CoinUser> getContent() {
        return coinUsers;
    }

    public static Collection<CoinUser> getValuesSafe() {
        return new ArrayList<>(coinUsers.values());
    }

    public static Collection<CoinUser> getValues() {
        return coinUsers.values();
    }

    public static Optional<CoinUser> findByUID(UUID uid) {
        return Optional.ofNullable(coinUsers.get(uid));
    }

    public static CoinUser getByUID(UUID uid) {
        return findByUID(uid).orElseThrow(() -> new NullPointerException("there is no coin data with (" + uid + ")"));
    }

    public static CoinUser getOrLoad(UUID uuid) {
        CoinUser coinUser = coinUsers.get(uuid);
        if (coinUser != null)
            return coinUser;

        CoinUser coinUserDB = databaseProvider.getValue("uid", uuid);

        if (coinUserDB != null) {
            coinUsers.put(coinUserDB.getUID(), coinUserDB);
            return coinUserDB;
        }

        CoinUser newCoinUser = new CoinUser(Bukkit.getPlayer(uuid));
        newCoinUser.getDatabase().insertAsync();
        coinUsers.put(newCoinUser.getUID(), newCoinUser);
        return newCoinUser;
    }


    public static void transferCoin(CoinUser sender, CoinUser target, int value) {
        sender.changeCoin(sender.getCoin() - value);
        target.changeCoin(target.getCoin() + value);

        if (log) {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm dd.MM.yyyy");
            String date = formatter.format(new Date());

            String line = "[" + date + "]" + sender.getName() + " successfully sent " + value + " coins to " + target.getName();
            try (FileWriter logWriter = new FileWriter("plugins/rCoins/log/log.txt", true);
                 BufferedWriter bufferedLogWriter = new BufferedWriter(logWriter)) {
                bufferedLogWriter.write(line);
                bufferedLogWriter.newLine();
            } catch (IOException e) {
                System.out.println("An error appeared while logging. Please contact with developer.");
                e.printStackTrace();
            }
        }
    }
}