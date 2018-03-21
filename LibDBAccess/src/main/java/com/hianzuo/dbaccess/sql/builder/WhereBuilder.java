package com.hianzuo.dbaccess.sql.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ryan on 14/11/19.
 */
public class WhereBuilder implements Cloneable {
    private List<SQLBuilder.KVItem> whereList;
    private List<SQLBuilder.KVListItem> whereInList;
    private List<SQLBuilder.SQLItem> beforeItemList;
    private List<SQLBuilder.SQLItem> afterItemList;

    public WhereBuilder addWhere(SQLBuilder.KVItem... items) {
        if (null == items) {
            return this;
        }
        for (SQLBuilder.KVItem item : items) {
            if (item instanceof SQLBuilder.KVListItem) {
                addWhereIn((SQLBuilder.KVListItem) item);
            } else {
                addWhere(item);
            }
        }
        return this;
    }

    public WhereBuilder addWhere(List<SQLBuilder.KVItem> items) {
        if (null == items) {
            return this;
        }
        for (SQLBuilder.KVItem item : items) {
            addWhere(item);
        }
        return this;
    }

    public WhereBuilder addWhere(SQLBuilder.KVItem item) {
        if (null == item) {
            return this;
        }
        if (null == whereList) {
            whereList = new ArrayList<>();
        }
        whereList.add(item);
        return this;
    }

    public WhereBuilder addWhere(String name, Integer value) {
        return addWhere(name, String.valueOf(value));
    }

    public WhereBuilder addWhere(String name, String operator, Object value) {
        return addWhere(new SQLBuilder.KVItem(name, operator, value));
    }

    public WhereBuilder addWhere(String name, Object value) {
        return addWhere(new SQLBuilder.KVItem(name, value));
    }


    public WhereBuilder addWhereBetween(String name, Object start, Object end) {
        addWhere(name, ">=", start);
        addWhere(name, "<=", end);
        return this;
    }

    public WhereBuilder addWhereIn(SQLBuilder.KVListItem item) {
        if (null == whereInList) {
            whereInList = new ArrayList<SQLBuilder.KVListItem>();
        }
        whereInList.add(item);
        return this;
    }

    public WhereBuilder addWhereIn(String name, Object... args) {
        addWhereIn(new SQLBuilder.KVListItem(name, args));
        return this;
    }

    public WhereBuilder before(String sql, Object... args) {
        if (null == beforeItemList) {
            beforeItemList = new ArrayList<SQLBuilder.SQLItem>();
        }
        beforeItemList.add(new SQLBuilder.SQLItem(sql, args));
        return this;
    }

    public WhereBuilder after(String sql, Object... args) {
        if (null == afterItemList) {
            afterItemList = new ArrayList<SQLBuilder.SQLItem>();
        }
        afterItemList.add(new SQLBuilder.SQLItem(sql, args));
        return this;
    }

    public String sql() {
        StringBuilder sb = new StringBuilder();
        if (null != beforeItemList && !beforeItemList.isEmpty()) {
            for (SQLBuilder.SQLItem item : beforeItemList) {
                sb.append(item.sql).append(SQLBuilder.KG);
            }
        }
        if (null != whereList && !whereList.isEmpty()) {
            for (SQLBuilder.KVItem item : whereList) {
                 sb.append(item.sql());
            }
        }
        if (null != whereInList && !whereInList.isEmpty()) {
            for (SQLBuilder.KVListItem item : whereInList) {
                sb.append(item.name)
                        .append(SQLBuilder.KG)
                        .append(SQLBuilder.IN)
                        .append(SQLBuilder.K_LEFT);
                int count = item.value.length;
                while (count > 0) {
                    count--;
                    sb.append(SQLBuilder.QU_MARK);
                    sb.append(SQLBuilder.COMMA);
                }
                if (item.value.length > 0) {
                    sb.delete(sb.length() - 1, sb.length());
                }
                sb.append(SQLBuilder.K_RIGHT)
                        .append(SQLBuilder.KG)
                        .append(SQLBuilder.AND)
                        .append(SQLBuilder.KG)
                ;
            }
        }
        if (sb.length() > 5) {
            sb.delete(sb.length() - 5, sb.length());
        }
        if (null != afterItemList && !afterItemList.isEmpty()) {
            for (SQLBuilder.SQLItem item : afterItemList) {
                sb.append(SQLBuilder.KG).append(item.sql);
            }
        }
        return sb.toString();
    }

    public String[] params() {
        String[] beforeItemParams = beforeItemParams();
        String[] whereParams = whereParams();
        String[] whereInParams = whereInParams();
        String[] afterItemParams = afterItemParams();
        int allLen = beforeItemParams.length + whereParams.length +
                whereInParams.length + afterItemParams.length;
        String[] params = new String[allLen];
        int i = 0;
        for (String param : beforeItemParams) {
            params[i++] = param;
        }
        for (String param : whereParams) {
            params[i++] = param;
        }
        for (String param : whereInParams) {
            params[i++] = param;
        }
        for (String param : afterItemParams) {
            params[i++] = param;
        }
        return params;
    }

    private String[] beforeItemParams() {
        if (null != beforeItemList && !beforeItemList.isEmpty()) {
            List<String> list = new ArrayList<String>();
            for (SQLBuilder.SQLItem item : beforeItemList) {
                if (null != item.args) {
                    for (Object arg : item.args) {
                        list.add(arg.toString());
                    }
                }
            }
            return list.toArray(new String[list.size()]);
        } else {
            return new String[0];
        }
    }

    private String[] whereParams() {
        if (null != whereList && !whereList.isEmpty()) {
            String[] params = new String[whereList.size()];
            int i = 0;
            for (SQLBuilder.KVItem item : whereList) {
                if (null != item.value) {
                    params[i++] = item.value.toString();
                }
            }
            if (params.length != i) {
                String[] params1 = new String[i];
                System.arraycopy(params, 0, params1, 0, i);
                params = params1;
            }
            return params;
        } else {
            return new String[0];
        }
    }

    private String[] whereInParams() {
        if (null != whereInList && !whereInList.isEmpty()) {
            List<String> list = new ArrayList<String>();
            for (SQLBuilder.KVListItem item : whereInList) {
                for (Object val : item.value) {
                    list.add(val.toString());
                }
            }
            return list.toArray(new String[list.size()]);
        } else {
            return new String[0];
        }
    }

    private String[] afterItemParams() {
        if (null != afterItemList && !afterItemList.isEmpty()) {
            List<String> list = new ArrayList<String>();
            for (SQLBuilder.SQLItem item : afterItemList) {
                if (null != item.args) {
                    for (Object arg : item.args) {
                        list.add(arg.toString());
                    }
                }
            }
            return list.toArray(new String[list.size()]);
        } else {
            return new String[0];
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public WhereBuilder copy() {
        try {
            return (WhereBuilder) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("can not clone Where builder", e);
        }
    }


}
