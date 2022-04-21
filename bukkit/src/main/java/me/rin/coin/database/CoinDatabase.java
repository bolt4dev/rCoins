package me.rin.coin.database;

import com.hakan.core.HCore;
import me.rin.coin.CoinUser;
import me.rin.coin.CoinUserHandler;

public class CoinDatabase {

    private final CoinUser coinUser;

    public CoinDatabase(CoinUser coinUser) {
        this.coinUser = coinUser;
    }

    public CoinUser getCoinUser() {
        return this.coinUser;
    }

    public void insert() {
        CoinUserHandler.getDatabaseProvider().insert(this.coinUser);
    }

    public void update() {
        CoinUserHandler.getDatabaseProvider().update(this.coinUser);
    }

    public void delete() {
        CoinUserHandler.getDatabaseProvider().delete(this.coinUser);
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
