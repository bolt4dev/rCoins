package me.rin.coin.util.query.delete;

import me.rin.coin.util.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeleteQuery extends QueryBuilder {

    private final Map<String, String> where;

    public DeleteQuery(String table) {
        super(table);
        this.where = new LinkedHashMap<>();
    }

    public DeleteQuery where(String column, Object value) {
        String columnReplaced = column.replace(column, "`" + column + "`");
        String valueReplaced = value.toString().replace("'", "''");
        this.where.put(columnReplaced, valueReplaced.replace(valueReplaced, "'" + valueReplaced + "'"));
        return this;
    }

    @Override
    public String build() {
        this.query.append("DELETE FROM ").append(this.table);
        if (this.where.size() > 0) {
            this.query.append(" WHERE ");
            this.where.forEach((key, value) -> this.query.append(key).append(" = ").append(value).append(" AND "));
            this.query.delete(this.query.length() - 5, this.query.length());
        }
        return this.query.toString();
    }
}