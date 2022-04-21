package me.rin.coin.database.providers.sqlite;

import me.rin.coin.CoinUser;

import javax.annotation.Nonnull;

public enum CoinSQLiteField {

    OWNER("uid", "VARCHAR(36) NOT NULL"),
    NAME("name", "TEXT NOT NULL"),
    COINS("coins", "INT NOT NULL"),
    ;

    private final String path;
    private final String value;

    CoinSQLiteField(String path, String value) {
        this.path = path;
        this.value = value;
    }

    @Nonnull
    public String getPath() {
        return this.path;
    }

    @Nonnull
    public String getValue() {
        return this.value;
    }

    @Nonnull
    public String getValue(CoinUser coinUser) {
        switch (this) {
            case OWNER:
                return coinUser.getUID().toString();
            case NAME:
                return coinUser.getName();
            case COINS:
                return String.valueOf(coinUser.getCoin());
            default:
                return "";
        }
    }
}
