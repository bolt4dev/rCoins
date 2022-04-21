package me.rin.coin.util.query;

public abstract class QueryBuilder {

    protected final String table;
    protected final StringBuilder query;

    public QueryBuilder(String table) {
        this.table = table;
        this.query = new StringBuilder();
    }

    public abstract String build();

    @Override
    public String toString() {
        return this.build();
    }
}
