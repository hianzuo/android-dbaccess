package com.hianzuo.dbaccess.store;

import com.hianzuo.dbaccess.DBInterface;
import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.Dto;
import com.hianzuo.dbaccess.config.DBConfig;
import com.hianzuo.dbaccess.config.UpdateTableMethod;
import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.Table;
import com.hianzuo.dbaccess.throwable.DBRuntimeException;
import com.hianzuo.dbaccess.util.MD5Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Ryan
 * Date: 14-3-31
 * Time: 下午5:21
 */
@Table(ver = 4, name = "app_table_list")
public class SQLiteTable extends Dto implements DBTable {
    @Column(id = 1, unique = true) String className;
    @Column(id = 2, canull = true) String signer;
    @Column(id = 3) Integer ver;
    @Column(id = 4, def = "0") Boolean clearOnAddColumn = false;
    private List<SQLiteTableColumn> list = new ArrayList<SQLiteTableColumn>();
    private HashMap<Float, SQLiteTableColumn> map;
    private String createTableSQL;

    @Override
    public SQLiteTable create(String className, int ver, boolean clearOnAddColumn) {
        this.className = className;
        this.ver = ver;
        this.clearOnAddColumn = clearOnAddColumn;
        return this;
    }
    public void parseFields(List<Field> fields) {
        List<SQLiteTableColumn> list = this.list;
        if (list.size() > 0) {
            return;
        }
        for (Field field : fields) {
            Column column = getAnnotationColumn(field);
            if (null != column) {
                String name = column.name();
                Float cid = column.id();
                Integer len = column.len();
                boolean pk = column.pk();
                boolean aicr = column.aicr();
                boolean unique = column.unique();
                boolean canull = column.canull();
                String check = column.check();
                String def = column.def();
                String type = column.type();
                if (name.length() == 0) {
                    name = field.getName();
                }
                Class<?> fieldClass = field.getType();
                SQLiteTableColumn stc = new SQLiteTableColumn()
                        .update(cid, name, type, len, pk, aicr, canull, unique,
                                check, def, fieldClass);
                list.add(stc);
            }
        }
    }

    @Override
    public String getSigner() {
        if (null != signer) {
            return signer;
        }
        List<SQLiteTableColumn> list = this.list;
        StringBuilder sb = new StringBuilder();
        for (SQLiteTableColumn column : list) {
            sb.append(column.hashCode());
        }
        signer = MD5Utils.MD5(sb.toString());
        return signer;
    }

    private Column getAnnotationColumn(Field field) {
        try {
            return field.getAnnotation(Column.class);
        } catch (Exception e) {
            return null;
        }
    }

    public SQLiteTable update(String className, List<SQLiteTableColumn> list) {
        this.className = className;
        this.list = list;
        this.initMap(list);
        return this;
    }

    private void initMap(List<SQLiteTableColumn> list) {
        if (null == list) {
            return;
        }
        HashMap<Float, SQLiteTableColumn> map = new HashMap<Float, SQLiteTableColumn>();
        for (SQLiteTableColumn sct : list) {
            Float cid = sct.getCid();
            if (map.containsKey(cid)) {
                SQLiteTableColumn old = map.get(cid);
                throw new RuntimeException("@Column id must unique in table[" + className + "]. old :[" + old + "], new:[" + sct + "]");
            }
            map.put(cid, sct);
        }
        this.map = map;
    }

    public SQLiteTableColumn getColumn(Float cid) {
        if (null == map) {
            initMap(list);
        }
        if (null != map) {
            return map.get(cid);
        } else {
            throw new DBRuntimeException("list does not init.");
        }
    }

    @Override
    public String getClassName() {
        return className;
    }

    public List<SQLiteTableColumn> getList() {
        return list;
    }

    public boolean needUpdate(DBConfig config, Class<? extends Dto> tClass, SQLiteTable newTable) {
        UpdateTableMethod method = config.getUpdateTableMethod();
        if (method == UpdateTableMethod.AUTO_CHECK) {
            List<Field> fields = DBInterface.getColumnFields(tClass);
            newTable.parseFields(fields);
            return !this.signer.equals(newTable.getSigner());
        } else if (method == UpdateTableMethod.VERSION) {
            return !this.ver.equals(newTable.ver);
        } else {
            throw new DBRuntimeException("un support method[" + method + "].");
        }
    }

    public void updateColumnListTableId(Integer tableId) {
        if (null != list) {
            for (SQLiteTableColumn stc : list) {
                stc.setTid(tableId);
            }
            createTableSQL = null;
        }
    }

    public List<SQLiteTableColumn> readColumnList(Database db) {
        if (null == list || list.isEmpty()) {
            list = DBInterface.readByWhere(db, SQLiteTableColumn.class, "tid=?", getId());
        }
        initMap(list);
        return list;
    }

    public void removeColumn(List<SQLiteTableColumn> columns) {
        for (SQLiteTableColumn column : columns) {
            map.remove(column.getCid());
            list.remove(column);
        }
        if (!columns.isEmpty()) {
            createTableSQL = null;
        }
    }

    @Override
    public String makeCreateSQLFromColumns(String tableName, List<Field> fields) {
        if (null != createTableSQL) {
            return createTableSQL;
        }
        parseFields(fields);
        List<String> columns = new ArrayList<String>();
        for (SQLiteTableColumn column : list) {
            columns.add(column.makeColumnCreateTableSQL());
        }
        if (columns.size() <= 0) {
            throw new DBRuntimeException("没有可以Column存在.");
        } else {
            String createTableSQL = makeCreateTableByColumns(columns, tableName);
            this.createTableSQL = createTableSQL;
            return createTableSQL;
        }
    }

    private String makeCreateTableByColumns(List<String> columns, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append("(\n");
        for (String column : columns) {
            sb.append("    ").append(column).append(",\n");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append("\n);");
        return sb.toString();
    }

    public String joinAllColumnName(String delimiter) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (SQLiteTableColumn column : list) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(column.getName());
        }
        return sb.toString();
    }

    public void addColumn(SQLiteTableColumn... columns) {
        boolean changed = false;
        for (SQLiteTableColumn newCol : columns) {
            Float cid = newCol.getCid();
            SQLiteTableColumn column = getColumn(newCol.getCid());
            if (null != column) {
                throw new DBRuntimeException("can not add new column ,because cid[" + newCol.getCid() + "] exist.");
            } else {
                changed = true;
                list.add(newCol);
                map.put(cid, newCol);
            }
        }
        if (changed) {
            createTableSQL = null;
        }
    }

    public void modifyColumn(List<SQLiteTableColumn> columns) {
        for (SQLiteTableColumn newCol : columns) {
            SQLiteTableColumn column = getColumn(newCol.getCid());
            if (null == column) {
                throw new DBRuntimeException("can not getListType old column ,the cid is " + newCol.getCid());
            }
            column.copyFrom(newCol);
        }
        if (!columns.isEmpty()) {
            createTableSQL = null;
        }
    }

    @Override
    public void saveOrUpdateColumnList(Database db, int tid) {
        updateColumnListTableId(tid);
        DBInterface.deleteByWhere(db, SQLiteTableColumn.class, "tid=?", String.valueOf(tid));
        DBInterface.insertList(db, getList(), false);
    }

    public boolean isClearOnAddColumn() {
        if (null == clearOnAddColumn) {
            return false;
        }
        return clearOnAddColumn;
    }
}
