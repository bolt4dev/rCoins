package me.rin.coin.database.updater;

import me.rin.coin.CoinUser;
import me.rin.coin.CoinUserHandler;

import java.util.HashSet;
import java.util.Set;

public class CoinDatabaseUpdater {

    private static final Set<CoinUser> needUpdates = new HashSet<>();

    public static Set<CoinUser> getUpdatesSafe() {
        return new HashSet<>(CoinDatabaseUpdater.needUpdates);
    }

    public static void clearUpdates() {
        CoinDatabaseUpdater.needUpdates.clear();
    }

    public static void addToUpdate(CoinUser coinUserData) {
        CoinDatabaseUpdater.needUpdates.add(coinUserData);
    }

    public static void removeFromUpdate(CoinUser coinUserData) {
        CoinDatabaseUpdater.needUpdates.add(coinUserData);
    }

    public static void updateAll() {
        Set<CoinUser> needs = CoinDatabaseUpdater.getUpdatesSafe();
        CoinDatabaseUpdater.clearUpdates();
        CoinUserHandler.getDatabaseProvider().update(needs);
    }
}
