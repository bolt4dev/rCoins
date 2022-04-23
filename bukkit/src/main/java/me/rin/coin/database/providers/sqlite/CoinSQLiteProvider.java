package me.rin.coin.database.providers.sqlite;

import com.hakan.core.database.DatabaseProvider;
import com.hakan.core.utils.HYaml;
import com.hakan.core.utils.query.create.CreateQuery;
import com.hakan.core.utils.query.delete.DeleteQuery;
import com.hakan.core.utils.query.insert.InsertQuery;
import com.hakan.core.utils.query.select.SelectQuery;
import com.hakan.core.utils.query.update.UpdateQuery;
import me.rin.coin.CoinUser;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.*;
import java.util.*;

public class CoinSQLiteProvider implements DatabaseProvider<CoinUser> {

    private final Connection connection;

    public CoinSQLiteProvider(String location) throws SQLException {
        HYaml.createFile(location);
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + location);
    }

    @Override
    public void create() {
        try (Statement statement = this.connection.createStatement()) {
            CreateQuery createQuery = new CreateQuery("coins");
            Arrays.asList(CoinSQLiteField.values()).
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
        Arrays.asList(CoinSQLiteField.values())
                .forEach(field -> query.value(field.getPath(), field.getValue(coinUser)));
        return query.build();
    }

    private String toUpdateSQL(CoinUser coinUser) {
        UpdateQuery query = new UpdateQuery("coins");
        query.where(CoinSQLiteField.OWNER.getPath(), CoinSQLiteField.OWNER.getValue(coinUser));
        Arrays.asList(CoinSQLiteField.values())
                .forEach(field -> query.value(field.getPath(), field.getValue(coinUser)));
        return query.build();
    }

    private String toDeleteSQL(CoinUser coinUser) {
        DeleteQuery query = new DeleteQuery("coins");
        query.where(CoinSQLiteField.OWNER.getPath(), CoinSQLiteField.OWNER.getValue(coinUser));
        return query.build();
    }
}