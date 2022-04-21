package me.rin.coin.util.query.select;

import me.rin.coin.util.query.QueryBuilder;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SelectQuery extends QueryBuilder {

    private final List<String> values;
    private final Map<String, String> where;

    public SelectQuery(String table) {
        super(table);
        this.values = new LinkedList<>();
        this.where = new LinkedHashMap<>();
    }

    public SelectQuery from(String column) {
        String columnReplaced = column.replace(column, "`" + column + "`");
        this.values.add(columnReplaced);
        return this;
    }

    public SelectQuery fromAll() {
        this.values.add("*");
        return this;
    }

    public SelectQuery where(String column, Object value) {
        String columnReplaced = column.replace(column, "`" + column + "`");
        String valueReplaced = value.toString().replace("'", "''");
        this.where.put(columnReplaced, valueReplaced.replace(valueReplaced, "'" + valueReplaced + "'"));
        return this;
    }

    @Override
    public String build() {
        this.query.append("SELECT ");
        this.values.forEach((key) -> this.query.append(key).append(", "));
        this.query.delete(this.query.length() - 2, this.query.length());
        this.query.append(" FROM ").append(this.table);
        if (this.where.size() > 0) {
            this.query.append(" WHERE ");
            this.where.forEach((key, value) -> this.query.append(key).append(" = ").append(value).append(" AND "));
            this.query.delete(this.query.length() - 5, this.query.length());
        }
        return this.query.toString();
    }
}