package me.rin.coin;

import com.hakan.core.database.DatabaseObject;
import me.rin.coin.configuration.CoinConfiguration;
import me.rin.coin.database.CoinDatabase;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CoinUser implements DatabaseObject {

    private final UUID uid;
    private String name;
    private final CoinDatabase database;
    private int coin;

    public CoinUser(Player player) {
        this(player.getUniqueId(), player.getName());
    }

    public CoinUser(UUID uid, String name) {
        this.uid = uid;
        this.name = name;
        this.coin = CoinConfiguration.CONFIG.getInt("Settings.start-coin");
        this.database = new CoinDatabase(this);
    }

    public CoinUser(ResultSet resultSet) throws SQLException {
        this.uid = UUID.fromString(resultSet.getString("uid"));
        this.name = resultSet.getString("name");
        this.coin = resultSet.getInt("coins");
        this.database = new CoinDatabase(this);
    }

    public UUID getUID() {
        return this.uid;
    }

    public String getName() {
        return this.name;
    }

    public Integer getCoin() {
        return this.coin;
    }

    public CoinDatabase getDatabase() {
        return this.database;
    }


    /*
    HANDLERS
     */
    public void changeName(String name) {
        this.name = name;
        CoinDatabase.getProvider().addUpdateObject(this);
    }

    public void changeCoin(Integer coin) {
        this.coin = coin;
        CoinDatabase.getProvider().addUpdateObject(this);
    }
}