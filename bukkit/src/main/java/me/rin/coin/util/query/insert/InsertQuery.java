package me.rin.coin.util.query.insert;

import me.rin.coin.util.query.QueryBuilder;

import java.util.HashMap;
import java.util.Map;

public class InsertQuery extends QueryBuilder {

    private final Map<String, String> values;

    public InsertQuery(String table) {
        super(table);
        this.values = new HashMap<>();
    }

    public InsertQuery value(String column, Object value) {
        String columnReplaced = column.replace(column, "`" + column + "`");
        String valueReplaced = value.toString().replace("'", "''");
        this.values.put(columnReplaced, valueReplaced.replace(valueReplaced, "'" + valueReplaced + "'"));
        return this;
    }

    @Override
    public String build() {
        this.query.append("INSERT INTO ").append(this.table);
        this.query.append(" (");
        this.query.append(String.join(", ", this.values.keySet()));
        this.query.append(") VALUES (");
        this.query.append(String.join(", ", this.values.values()));
        this.query.append(")");
        return this.query.toString();
    }
}