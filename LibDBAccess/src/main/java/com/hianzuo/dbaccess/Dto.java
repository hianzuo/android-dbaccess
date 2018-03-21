package com.hianzuo.dbaccess;

import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.Table;
import com.hianzuo.dbaccess.throwable.DBRuntimeException;
import com.hianzuo.dbaccess.util.ClassFieldUtil;
import com.flyhand.core.utils.ClazzUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * User: Ryan
 * Date: 14-3-21
 * Time: 下午12:20
 */
@Table(ver = 3)
public abstract class Dto extends ExtendField implements Serializable, Cloneable {
    @Column(id = -1, aicr = true, pk = true)
    public Integer id;
    @Column(id = -2, len = 32)
    public String status = Status.NEW.toString();

    public Dto() {
    }

    public Integer getIdInteger() {
        return id;
    }

    public String getId() {
        if (null == id) {
            return null;
        } else {
            return String.valueOf(id);
        }
    }

    public void setId(String id) {
        this.id = Integer.valueOf(id);
    }

    public int save() {
        return save(null);
    }

    public int save(Database database) {
        if (null != id) {
            throw new RuntimeException("id is not null ,can not save.");
        }
        int result = insert(database);
        if (result == -1) {
            throw new RuntimeException("save entity failure[" + this + "].");
        } else {
            return result;
        }
    }

    public int insert() {
        return insert(null);
    }

    public int insert(Database database) {
        return DBInterface.insert(database, this);
    }

    public int insertWithoutEqualsMaxId() {
        if (null != id) {
            throw new RuntimeException("id is not null ,can not save.");
        }
        return DBInterface.insertWithoutEqualsMaxId(this);
    }

    public int update() {
        return update(null);
    }

    public int updateIncludeNull(Database db) {
        return update(db, true);
    }

    public int update(Database db) {
        return update(db, false);
    }

    public int update(Database db, boolean includeNull) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        Dto where = Dto.createNullFieldDto(getClass());
        where.setId(this.id);
        this.id = null;
        return updateByWhere(db, where, includeNull);
    }

    public <T extends Dto> int updateByWhere(Database db, T where, boolean includeNullValue) {
        //noinspection unchecked
        return DBInterface.updateByWhere(db, (Class<Dto>) getClass(), this, where, includeNullValue);
    }

    public void saveOrUpdate() {
        DBInterface.saveOrUpdate(this);
    }

    public void saveOrUpdate(Database database) {
        DBInterface.saveOrUpdate(database, this);
    }

    /**
     * 根据条件更新或者插入数据
     *
     * @param where  更新条件
     * @param params 参数
     */

    public boolean updateOrInsertByWhere(String where, String... params) {
        return DBInterface.updateOrInsertByWhere(this, where, params);
    }

    public int delete() {
        if (null == id) {
            throw new RuntimeException("id is null ,can not delete.");
        }
        return delete(String.valueOf(id));
    }

    public int delete(String id) {
        return DBInterface.delete(this.getClass(), id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void nullId() {
        this.id = null;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status.toString();
    }

    public static <T extends Dto> T createNullFieldDto(Class<T> clz) {
        T t = ClazzUtil.newInstance(clz);
        List<Field> fields = DBInterface.getColumnFields(clz);
        for (Field field : fields) {
            try {
                boolean isStatic = Modifier.isStatic(field.getModifiers());
                boolean isFinal = Modifier.isFinal(field.getModifiers());
                boolean isShadow$ = field.getName().startsWith("shadow$");
                if (isFinal || isStatic || isShadow$) {
                } else {
                    field.setAccessible(true);
                    field.set(t, null);
                }
            } catch (Exception e) {
                throw new DBRuntimeException(e);
            }
        }
        return t;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public <T extends Dto> T copyFrom(T dto) {
        if (this != dto) {
            List<Field> fields = ClassFieldUtil.getAccessibleFields(dto.getClass());
            for (Field field : fields) {
                try {
                    field.set(this, field.get(dto));
                } catch (Exception e) {
                    throw new RuntimeException("can not copy dto filed[" + field.getName() + "].", e);
                }
            }
        }
        return (T) this;
    }

    public String getStatusLabel() {
        try {
            return Status.valueOf(getStatus()).getLabel();
        } catch (Exception e) {
            String status = getStatus();
            if (null == status) {
                return "null";
            } else {
                return status;
            }
        }
    }
}
