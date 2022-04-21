package me.rin.coin.util.query.update;

import me.rin.coin.util.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class UpdateQuery extends QueryBuilder {

    private final Map<String, String> values;
    private final Map<String, String> where;

    public UpdateQuery(String table) {
        super(table);
        this.values = new LinkedHashMap<>();
        this.where = new LinkedHashMap<>();
    }

    public UpdateQuery value(String column, Object value) {
        String columnReplaced = column.replace(column, "`" + column + "`");
        String valueReplaced = value.toString().replace("'", "''");
        this.values.put(columnReplaced, valueReplaced.replace(valueReplaced, "'" + valueReplaced + "'"));
        return this;
    }

    public UpdateQuery where(String column, Object value) {
        String columnReplaced = column.replace(column, "`" + column + "`");
        String valueReplaced = value.toString().replace("'", "''");
        this.where.put(columnReplaced, valueReplaced.replace(valueReplaced, "'" + valueReplaced + "'"));
        return this;
    }

    @Override
    public String build() {
        this.query.append("UPDATE ").append(this.table).append(" SET ");
        this.values.forEach((k, v) -> this.query.append(k).append(" = ").append(v).append(", "));
        this.query.delete(this.query.length() - 2, this.query.length());
        if (this.where.size() > 0) {
            this.query.append(" WHERE ");
            this.where.forEach((k, v) -> this.query.append(k).append(" = ").append(v).append(" AND "));
            this.query.delete(this.query.length() - 5, this.query.length());
        }
        return this.query.toString();
    }
}
