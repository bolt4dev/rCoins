package me.rin.coin.util.query.create;

import me.rin.coin.util.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class CreateQuery extends QueryBuilder {

    private final Map<String, String> values;

    public CreateQuery(String table) {
        super(table);
        this.values = new LinkedHashMap<>();
    }

    public CreateQuery value(String column, String type) {
        this.values.put(column, type);
        return this;
    }

    @Override
    public String build() {
        StringBuilder query = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        query.append(this.table);
        query.append("(");
        for (Map.Entry<String, String> entry : this.values.entrySet()) {
            query.append(entry.getKey());
            query.append(" ");
            query.append(entry.getValue());
            query.append(", ");
        }
        query.delete(query.length() - 2, query.length());
        query.append(")");
        return query.toString();
    }
}
