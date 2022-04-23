package me.rin.coin.database.providers.mysql;

import com.hakan.core.database.DatabaseProvider;
import com.hakan.core.utils.query.create.CreateQuery;
import com.hakan.core.utils.query.delete.DeleteQuery;
import com.hakan.core.utils.query.insert.InsertQuery;
import com.hakan.core.utils.query.select.SelectQuery;
import com.hakan.core.utils.query.update.UpdateQuery;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.rin.coin.CoinUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class CoinMySQLProvider implements DatabaseProvider<CoinUser> {

    private final Connection connection;
    private final String databaseName;

    public CoinMySQLProvider(String ip, int port, String username, String password, String databaseName) throws SQLException, ClassNotFoundException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + ip + ":" + port);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        this.connection = new HikariDataSource(config).getConnection();
        this.databaseName = databaseName;
    }

    @Override
    public void create() {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS " + this.databaseName);
            statement.execute("USE " + this.databaseName);

            CreateQuery createQuery = new CreateQuery("coins");
            Arrays.asList(CoinMySQLField.values()).
                    forEach(field -> createQuery.value(field.getPath(), field.getValue()));
            statement.executeUpdate(createQuery.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nonnull
    @Override
    public List<CoinUser> getValues() {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("USE " + this.databaseName);

            List<CoinUser> coinDatas = new ArrayList<>();

            SelectQuery query = new SelectQuery("coins").fromAll();
            ResultSet resultSet = statement.executeQuery(query.build());
            while (resultSet.next())
                coinDatas.add(new CoinUser(resultSet));

            return coinDatas;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public CoinUser getValue(@Nonnull String s, @Nonnull Object o) {
        try (Statement statement = this.connection.createStatement()) {
            statement.execute("USE " + this.databaseName);

            SelectQuery query = new SelectQuery("coins").where(s, o).fromAll();
            ResultSet resultSet = statement.executeQuery(query.build());
            resultSet.next();
            return new CoinUser(resultSet);
        } catch (SQLException e) {
            return null;
        }
    }

    @Override
    public void insert(@Nonnull CoinUser coinUser) {
        this.insert(Collections.singletonList(coinUser));
    }

    @Override
    public void update(@Nonnull CoinUser coinUser) {
        this.update(Collections.singletonList(coinUser));
    }

    @Override
    public void delete(@Nonnull CoinUser coinUser) {
        this.delete(Collections.singletonList(coinUser));
    }

    @Override
    public void insert(@Nonnull Collection<CoinUser> datas) {
        if (datas.size() == 0)
            return;

        try (Statement statement = this.connection.createStatement()) {
            statement.execute("USE " + this.databaseName);

            for (CoinUser coinUser : datas)
                statement.execute(this.toInsertSQL(coinUser));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(@Nonnull Collection<CoinUser> datas) {
        if (datas.size() == 0)
            return;

        try (Statement statement = this.connection.createStatement()) {
            statement.execute("USE " + this.databaseName);

            for (CoinUser coinUser : datas)
                statement.execute(this.toUpdateSQL(coinUser));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(@Nonnull Collection<CoinUser> datas) {
        if (datas.size() == 0)
            return;

        try (Statement statement = this.connection.createStatement()) {
            statement.execute("USE " + this.databaseName);

            for (CoinUser coinUser : datas)
                statement.execute(this.toDeleteSQL(coinUser));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /*
    CONVERTERS
     */
    private String toInsertSQL(CoinUser coinUser) {
        InsertQuery query = new InsertQuery("coins");
        Arrays.asList(CoinMySQLField.values())
                .forEach(field -> query.value(field.getPath(), field.getValue(coinUser)));
        return query.build();
    }

    private String toUpdateSQL(CoinUser coinUser) {
        UpdateQuery query = new UpdateQuery("coins");
        query.where(CoinMySQLField.OWNER.getPath(), CoinMySQLField.OWNER.getValue(coinUser));
        Arrays.asList(CoinMySQLField.values())
                .forEach(field -> query.value(field.getPath(), field.getValue(coinUser)));
        return query.build();
    }

    private String toDeleteSQL(CoinUser coinUser) {
        DeleteQuery query = new DeleteQuery("coins");
        query.where(CoinMySQLField.OWNER.getPath(), CoinMySQLField.OWNER.getValue(coinUser));
        return query.build();
    }
}