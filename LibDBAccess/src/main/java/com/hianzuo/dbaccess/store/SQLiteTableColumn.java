package com.hianzuo.dbaccess.store;

import com.hianzuo.dbaccess.Dto;
import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.Table;
import com.hianzuo.dbaccess.lang.DbList;
import com.hianzuo.dbaccess.throwable.DBRuntimeException;
import com.hianzuo.dbaccess.util.StringUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * User: Ryan
 * Date: 14-3-31
 * Time: 下午5:21
 */
@Table(name = "app_table_columns")
public class SQLiteTableColumn extends Dto {
    @Column(id = 10)
    Integer tid; //表ID
    @Column(id = 13)
    Float cid; //字段唯一值
    @Column(id = 14, name = "_name")
    String name;//字段名
    @Column(id = 15, name = "_type")
    String type;//类型
    @Column(id = 16, canull = true)
    Integer len;//长度
    @Column(id = 17)
    boolean pk = false;//是否主键
    @Column(id = 18)
    boolean aicr = false;//是否自增长
    @Column(id = 19)
    boolean canull = false;//能否NULL
    @Column(id = 20, name = "_unique")
    boolean unique = false;//是否唯一
    @Column(id = 21, name = "_check", canull = true)
    String check;//检查
    @Column(id = 22, canull = true)
    String def;//默认值
    private Class<?> fieldClass;//Dto属性类型
    private String typeAndLen;//like varchar(32)
    private String mCreateTableSQL;

    public SQLiteTableColumn update(Float cid, String name, String type, Integer len,
                                    boolean pk, boolean aicr, boolean canull, boolean unique,
                                    String check, String def, Class<?> fieldClass) {
        this.cid = cid;
        this.name = name;
        this.pk = pk;
        this.aicr = aicr;
        this.canull = canull;
        this.unique = unique;
        this.check = check;
        this.def = def;
        this.fieldClass = fieldClass;
        if (null == type || type.length() == 0) {
            this.type = getSQLType();
        } else {
            this.type = type;
        }
        if (len == 0) {
            this.len = getDefaultTypeLength();
        } else {
            this.len = len;
        }
        if (StringUtil.isEmpty(check)) {
            if (BigDecimal.class.equals(fieldClass)) {
                this.check = name + "< 10000000000";
            }
        }
        return this;
    }


    public void setTid(Integer tid) {
        this.tid = tid;
    }

    public Integer getTid() {
        return tid;
    }

    public Float getCid() {
        return cid;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Integer getLen() {
        return len;
    }

    public boolean isPk() {
        return pk;
    }

    public boolean isAicr() {
        return aicr;
    }

    public boolean isCanull() {
        return canull;
    }

    public boolean isUnique() {
        return unique;
    }

    public String getCheck() {
        return check;
    }

    public String getDef() {
        return def;
    }

    public void copyFrom(SQLiteTableColumn newCol) {
        if (!this.cid.equals(newCol.cid)) {
            throw new DBRuntimeException("you cannot copy newColumn[" + newCol.cid + "] to column[" + this.cid + "],because the cid is not equals.");
        }
        this.name = newCol.name;
        this.type = newCol.type;
        this.len = newCol.len;
        this.pk = newCol.pk;
        this.aicr = newCol.aicr;
        this.canull = newCol.canull;
        this.unique = newCol.unique;
        this.check = newCol.check;
        this.def = newCol.def;
        this.mCreateTableSQL = null;
    }

    public String makeColumnCreateTableSQL() {
        if (null != mCreateTableSQL) {
            return mCreateTableSQL;
        }
        StringBuilder sb = new StringBuilder("");
        sb.append(name).append(" ");
        String typeAndLen = getTypeAndLen();
        sb.append(typeAndLen).append(" ");
        if (pk) {
            sb.append("PRIMARY KEY").append(" ");
        }
        if (aicr) {
            sb.append("AUTOINCREMENT").append(" ");
        }
        if (unique) {
            sb.append("UNIQUE").append(" ");
        }
        if (null != check && check.trim().length() > 0) {
            sb.append("CHECK(").append(check).append(")").append(" ");
        }
        if (!canull) {
            sb.append("NOT NULL ");
        }
        if (!"null".equals(def)) {
            sb.append("DEFAULT ").append(def);
        }
        mCreateTableSQL = sb.toString().trim();
        return mCreateTableSQL;
    }

    private String getTypeAndLen() {
        if (null == this.typeAndLen) {
            String typeAndLen = getSQLTypeAndLength();
            String type = typeAndLen;
            Integer len = null;
            int s = typeAndLen.indexOf('(');
            int e = typeAndLen.indexOf(')');
            if (typeAndLen.matches("[a-zA-Z]+\\([0-9]+\\)")) {
                len = Integer.valueOf(typeAndLen.substring(s + 1, e));
                type = typeAndLen.substring(0, s);
            }
            if (null != len) {
                typeAndLen = type + "(" + len + ")";
            }
            this.type = type;
            this.len = len;
            this.typeAndLen = typeAndLen;
        }
        return typeAndLen;
    }

    private String getSQLTypeAndLength() {
        if (null != len && len > 0) {
            return type + "(" + len + ")";
        } else {
            return type;
        }
    }

    private String getSQLType() {
        Class<?> type = fieldClass;
        if (Integer.class.equals(type) || "int".equals(type.getName())) {
            return "integer";
        } else if (String.class.equals(type)) {
            return "varchar";
        } else if (Long.class.equals(type) || "long".equals(type.getName())) {
            return "integer";
        } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
            return "varchar";
        } else if (Double.class.equals(type) || "double".equals(type.getName())) {
            return "double";
        } else if (Float.class.equals(type) || "float".equals(type.getName())) {
            return "float";
        } else if (BigDecimal.class.equals(type)) {
            return "decimal(12,5)";
        } else if (BigInteger.class.equals(type)) {
            return "integer";
        } else if (Enum.class.isAssignableFrom(type)) {
            return "varchar";
        } else if (DbList.class.isAssignableFrom(type)) {
            return "text";
        } else if (List.class.isAssignableFrom(type)) {
            return "text";
        } else {
            throw new RuntimeException("can't support for type " + type.getName());
        }
    }

    private int getDefaultTypeLength() {
        Class<?> type = fieldClass;
        if (String.class.equals(type)) {
            if ("text".equals(this.type)) {
                return 0;
            } else {
                return 255;
            }
        } else if (Boolean.class.equals(type)
                || "boolean".equals(type.getName())) {
            return 10;
        } else if (Enum.class.isAssignableFrom(type)) {
            return 32;
        } else {
            return 0;
        }
    }


    public String makeAddColumnSQL(String tableName) {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + makeColumnCreateTableSQL() + ";";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof SQLiteTableColumn
                && o.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        int result = cid != null ? cid.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (len != null ? len.hashCode() : 0);
        result = 31 * result + (pk ? 1 : 0);
        result = 31 * result + (aicr ? 1 : 0);
        result = 31 * result + (canull ? 1 : 0);
        result = 31 * result + (unique ? 1 : 0);
        result = 31 * result + (check != null ? check.hashCode() : 0);
        result = 31 * result + (def != null ? def.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SQLiteTableColumn{" +
                "def='" + def + '\'' +
                ", tid=" + tid +
                ", cid=" + cid +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", len=" + len +
                ", pk=" + pk +
                ", aicr=" + aicr +
                ", canull=" + canull +
                ", unique=" + unique +
                ", check='" + check + '\'' +
                '}';
    }
}
