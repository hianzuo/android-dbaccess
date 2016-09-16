package com.hianzuo.dbaccess.controller;

import com.hianzuo.dbaccess.DBInterface;
import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.Dto;
import com.hianzuo.dbaccess.config.DBHelper;
import com.hianzuo.dbaccess.sql.builder.WhereBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User: Ryan
 * Date: 14-3-22
 * Time: 下午3:36
 */
public class DtoController<T extends Dto> {
    protected Class<T> tClass;
    private final static HashMap<Class<? extends Dto>, DtoController<? extends Dto>>
            controllers = new HashMap<Class<? extends Dto>, DtoController<? extends Dto>>();

    public DtoController(Class<T> clazz) {
        this.tClass = clazz;
    }

    public static <T extends Dto> DtoController<T> get(Class<T> clazz) {
        //noinspection unchecked
        DtoController<T> controller = (DtoController<T>) controllers.get(clazz);
        if (null == controller) {
            DtoControllerCreator creator = DBHelper.getDBConfig().getDtoControllerCreator();
            controller = creator.create(clazz);
            controllers.put(clazz, controller);
        }
        return controller;
    }

    /**
     * 把数据保存到数据库中
     */
    public <M extends Dto> void insert(M t) {
        final M finalT = t;
        insertList(new ArrayList<M>() {
            {
                add(finalT);
            }
        });
    }

    public void saveList(List<T> list) {
        insertList(list);
    }

    public void insertOrUpdateList(List<T> list) {
        saveOrUpdateList(list);
    }

    /**
     * 把List数据保存到数据库中
     *
     * @param list 需要保存到表的数据
     */
    public <M extends Dto> List<M> insertList(List<M> list) {
        return DBInterface.insertList(list);
    }


    public final void saveOrUpdateDtoList(List<? extends Dto> list) {
        List<T> tList = new ArrayList<>();
        for (Dto dto : list) {
            //noinspection unchecked
            tList.add((T) dto);
        }
        saveOrUpdateList(tList);
    }

    public void saveOrUpdateList(List<T> list) {
        DBInterface.saveOrUpdateList(list);
    }

    /**
     * 根据ID更新数据
     *
     * @param dto 需要更新的数据类
     * @param id  数据条目ID
     * @return 更新成功返回大于0 否则返回式－1
     */
    public <M extends Dto> int update(M dto, String id) {
        return DBInterface.update(dto, id);
    }

    /**
     * 根据ID删除类的数据
     *
     * @param id 需要删除条目的ID
     * @return 删除成功返回大于0 否则返回式－1
     */
    public int delete(String id) {
        return DBInterface.delete(tClass, id);
    }

    /**
     * 删除类的全部数据
     *
     * @return 删除成功返回大于0 否则返回式－1
     */
    public int deleteAll() {
        return DBInterface.deleteAll(tClass);
    }

    /**
     * 删除ID最小的那条数据
     */
    public void deleteByMinId() {
        DBInterface.deleteByMinId(tClass);
    }

    /**
     * 读取ID最大的那条数据
     *
     * @return 返回读取到的数据
     */
    public T readByMaxId() {
        return DBInterface.readByMaxId(tClass);
    }

    /**
     * 根据ID读取数据
     *
     * @param id 数据条目的ID
     * @return 读取到的Dto对象
     */
    public T findById(String id) {
        return DBInterface.read(tClass, id);
    }

    /**
     * 根据ID读取数据
     *
     * @param id 数据条目的ID
     * @return 读取到的Dto对象
     */
    public T findById(Integer id) {
        return DBInterface.read(tClass, String.valueOf(id));
    }

    /**
     * 根据ID读取数据
     *
     * @param listId 数据条目的ID
     * @return 读取到的Dto对象
     */
    public List<T> findByListId(Integer... listId) {
        String[] ids = new String[listId.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = String.valueOf(listId[i]);
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String ignored : ids) {
            if (first) {
                sb.append("id=?");
                first = false;
            } else {
                sb.append(" or id=?");
            }
        }
        return readByWhere(sb.toString(), ids);
    }

    /**
     * 读取类型的全部数据
     *
     * @return 读取到相应类型的List对象
     */
    public List<T> readAll() {
        return DBInterface.readAll(tClass);
    }

    /**
     * 读取类型的全部数据
     *
     * @return 读取到相应类型的List对象
     */
    public <M> List<M> readAll(Class<M> clz) {
        return DBInterface.readAll(tClass, clz);
    }

    public <M> List<M> readByWhere(Database db, Class<M> mClass, String where, String... params) {
        return DBInterface.readByWhere(db, tClass, mClass, 1, Integer.MAX_VALUE, where, params);
    }

    public <M> List<M> readByWhere(Class<M> mClass, String where, String... params) {
        return DBInterface.readByWhere(null, tClass, mClass, 1, Integer.MAX_VALUE, where, params);
    }

    public <M> List<M> readByWhere(Class<M> mClass, Integer pageIndex, Integer pageCount,
                                   String where, String... params) {
        return DBInterface.readByWhere(null, tClass, mClass, pageIndex, pageCount, where, params);
    }

    /**
     * 根据条件读取
     *
     * @param where  读取条件
     * @param params 参数
     * @return 读取到相应类型的List对象
     */

    public List<T> readByWhere(String where, String... params) {
        Database db = null;
        return readByWhere(db, where, params);
    }

    /**
     * 根据条件读取
     *
     * @param where  读取条件
     * @param params 参数
     * @return 读取到相应类型的List对象
     */

    public List<T> readByWhere(Database db, String where, String... params) {
        return DBInterface.readByWhere(db, tClass, where, params);
    }

    /**
     * 根据条件读取
     *
     * @param builder 条件
     * @return 读取到相应类型的List对象
     */
    public List<T> readByWhere(WhereBuilder builder) {
        return readByWhere(builder.sql(), builder.params());
    }

    /**
     * 根据条件读取
     *
     * @param where     读取条件
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @param params    参数
     * @return 读取到相应类型的List对象
     */
    public List<T> readByWhere(String where, Integer pageIndex, Integer pageCount, String... params) {
        return DBInterface.readByWhere(tClass, where, pageIndex, pageCount, params);
    }

    /**
     * 根据条件读取
     *
     * @param builder   条件
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @return 读取到相应类型的List对象
     */
    public List<T> readByWhere(WhereBuilder builder, Integer pageIndex, Integer pageCount) {
        return readByWhere(builder.sql(), pageIndex, pageCount, builder.params());
    }

    /**
     * 根据条件读取
     *
     * @param where  读取条件
     * @param params 参数
     * @return 读取到相应类型的List对象
     */

    public List<T> readNeedByWhere(String need, String where, String... params) {
        return DBInterface.readNeedByWhere(tClass, need, where, params);
    }

    /**
     * 根据条件读取
     *
     * @param where  读取条件
     * @param params 参数
     * @return 读取到相应类型的List对象
     */

    public List<T> readNeedByWhere(String need, String where, Integer pageIndex, Integer pageCount, String... params) {
        return DBInterface.readNeedByWhere(tClass, need, where, pageIndex, pageCount, params);
    }

    /**
     * 根据条件判断数据是否存在
     *
     * @param where  需要判断的条件
     * @param params 需要判断的条件值
     * @return 是否存在
     */

    public boolean existByWhere(String where, String[] params) {
        return DBInterface.existByWhere(tClass, where, params);
    }

    /**
     * 读取相应的类的数据
     *
     * @param sql    SQL语句
     * @param params 参数
     * @return 读取到相应类型的List对象
     */
    @SuppressWarnings("unchecked")
    public List<T> readBySQL(String sql, String... params) {
        return DBInterface.readBySQL(tClass, sql, params);
    }

    /**
     * 获取相应的类有多少行数据
     *
     * @return 多少行数据
     */
    public int getRowCount() {
        return DBInterface.getRowCount(tClass);
    }


    /**
     * 删除数据根据传入的Where条件
     *
     * @param where   where条件
     * @param objects 参数
     */
    public int deleteByWhere(String where, Object... objects) {
        return DBInterface.deleteByWhere(tClass, where, objects);
    }

    public boolean isTableExist() {
        return DBInterface.isTableExist(tClass);
    }

    public int updateByWhere(Dto update, Dto where) {
        return updateByWhere(null, update, where);
    }

    public int updateByWhere(Database db, Dto update, Dto where) {
        return DBInterface.updateByWhere(db, tClass, update, where);
    }

    public int updateByWhere(String update, String where, String... params) {
        return updateByWhere(null, update, where, params);
    }

    public int updateByWhere(Database db, String update, String where, String... params) {
        return DBInterface.updateByWhere(null, tClass, update, where, params);
    }

    public void updateListByWhere(Database db, List<? extends Dto> updates, List<? extends Dto> wheres) {
        DBInterface.updateListByWhere(db, tClass, updates, wheres);
    }

    public void updateListByWhere(List<? extends Dto> updates, List<? extends Dto> wheres) {
        DBInterface.updateListByWhere(tClass, updates, wheres);
    }

    public Class<T> getDtoClass() {
        return tClass;
    }


}