package com.hianzuo.dbaccess.sql.builder;

import com.hianzuo.dbaccess.util.StringUtil;

/**
 * Created by Ryan on 14/11/19.
 */
public class SQLBuilder {
    private Action action;
    private String tableName;
    private String selected;
    private WhereBuilder whereBuilder;
    static final String KG = " ";
    static final String FROM = "FROM";
    static final String WHERE = "WHERE";
    static final String AND = "AND";
    static final String IN = "IN";
    static final String COMMA = ",";
    static final String K_LEFT = "(";
    static final String K_RIGHT = ")";
    static final char EQ = '=';
    static final char QU_MARK = '?';

    private SQLBuilder(Action action, String tableName) {
        this.action = action;
        this.tableName = tableName;
    }

    public static SQLBuilder createSelect(String tableName) {
        return new SQLBuilder(Action.SELECT, tableName);
    }

    public static SQLBuilder createUpdate(String tableName) {
        return new SQLBuilder(Action.SELECT, tableName);
    }

    private WhereBuilder getWhereBuilder() {
        if (null == whereBuilder) {
            whereBuilder = new WhereBuilder();
        }
        return whereBuilder;
    }

    public SQLBuilder addWhere(KVItem item) {
        getWhereBuilder().addWhere(item);
        return this;
    }

    public SQLBuilder addWhere(String name, Integer value) {
        getWhereBuilder().addWhere(name, value);
        return this;
    }

    public SQLBuilder addWhere(String name, String operator, Object value) {
        getWhereBuilder().addWhere(name, operator, value);
        return this;
    }

    public SQLBuilder addWhere(String name, Object value) {
        getWhereBuilder().addWhere(name, value);
        return this;
    }

    public SQLBuilder addWhereBetween(String name, Object start, Object end) {
        getWhereBuilder().addWhereBetween(name, start, end);
        return this;
    }

    public SQLBuilder addWhereIn(KVListItem item) {
        getWhereBuilder().addWhereIn(item);
        return this;
    }


    public SQLBuilder addWhereIn(String name, Object... value) {
        getWhereBuilder().addWhereIn(name, value);
        return this;
    }

    public SQLBuilder appendWhere(String sql, Object... args) {
        getWhereBuilder().after(sql, args);
        return this;
    }

    public SQLBuilder setSelect(String str) {
        this.selected = str;
        return this;
    }

    public SQLBuilder addSelect(String str) {
        if (isEmpty(str)) {
            return this;
        }
        if (isEmpty(this.selected)) {
            setSelect(str);
        } else {
            this.selected += "," + str;
        }
        return this;
    }

    public SQLBuilder addSelect(String... list) {
        addSelect(join(list, ","));
        return this;
    }


    public String sql() {
        StringBuilder sb = new StringBuilder();
        sb.append(action).append(KG);
        if (Action.SELECT == action) {
            buildSelect(sb);
        } else {
            buildUpdate(sb);
        }
        return sb.toString();
    }

    public String[] params() {
        return whereBuilder.params();
    }

    private void buildSelect(StringBuilder sb) {
        if (isEmpty(selected)) {
            sb.append("*").append(KG).append(FROM).append(KG);
        } else {
            sb.append(selected).append(KG).append(FROM).append(KG);
        }
        sb.append(tableName).append(KG);
        buildWhere(sb);
    }

    private void buildUpdate(StringBuilder sb) {
    }


    private void buildWhere(StringBuilder sb) {
        if (null != whereBuilder) {
            sb.append(SQLBuilder.WHERE).append(SQLBuilder.KG);
            sb.append(whereBuilder.sql());
        }
    }

    public static enum Action {
        SELECT, UPDATE
    }

    public static class SQLItem {
        String sql;
        Object[] args;

        public SQLItem(String sql, Object[] args) {
            this.sql = sql;
            this.args = args;
        }
    }

    public static class SQL {
        String sql;
        String[] params;

        public SQL(String sql, String[] params) {
            this.sql = sql;
            this.params = params;
        }

        public String getSql() {
            return sql;
        }

        public String[] getParams() {
            return params;
        }

        @Override
        public String toString() {
            return sql;
        }
    }

    public static class KVItem {
        String name;
        String operator;
        String value;

        public KVItem(String name) {
            this.name = name;
        }

        public KVItem(String name, String value) {
            this(name, null, value);
        }

        public KVItem(String name, Object value) {
            this(name, null, value.toString());
        }
        public KVItem(String name, String operator, String value) {
            this.name = name;
            this.operator = operator;
            this.value = value;
        }

        public KVItem(String name, String operator, Object value) {
            this(name, operator, value.toString());
        }

        public KVItem copy(String alias) {
            alias = StringUtil.isEmpty(alias) ? "" : (alias.endsWith(".") ? alias : alias + ".");
            return new KVItem(alias + name, operator, value);
        }

        public String getValue() {
            return value;
        }

        public String sql() {
            StringBuilder sb = new StringBuilder("");
            sb.append(name);
            sb.append(SQLBuilder.KG);
            if (null != value) {
                if (SQLBuilder.isEmpty(operator)) {
                    sb.append(SQLBuilder.EQ);
                } else {
                    sb.append(operator);
                }
                sb.append(SQLBuilder.KG);
                sb.append(SQLBuilder.QU_MARK)
                        .append(SQLBuilder.KG)
                        .append(SQLBuilder.AND)
                        .append(SQLBuilder.KG)
                ;
            } else {
                sb.append(SQLBuilder.AND)
                        .append(SQLBuilder.KG);
            }
            return sb.toString();
        }
    }

    public static class KVListItem extends KVItem {
        Object[] value;

        public KVListItem(String name, Object... value) {
            super(name);
            this.value = value;
        }
    }


    static boolean isEmpty(String s) {
        return null == s || s.length() == 0;
    }

    static String join(String[] strings, String token) {
        StringBuilder sb = new StringBuilder();
        for (Object string : strings) {
            sb.append(string.toString()).append(token);
        }
        if (strings.length > 0) {
            int sb_len = sb.length(), token_len = token.length();
            sb.delete(sb_len - token_len, sb_len);
        }
        return sb.toString();
    }
}
