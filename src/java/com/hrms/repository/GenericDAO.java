
package com.hrms.repository;

import java.util.List; 
import java.util.Map;
import com.hrms.repository.impl.AbstractDAO;

public interface GenericDAO<T> {
    <T> List<T> queryList(String sql, Class<T> clazz, Object... parameters);
    <T> List<T> queryList(String sql, AbstractDAO.RowMapper<T> mapper, Object... parameters);
    <T> T querySingle(String sql, Class<T> clazz, Object... parameters);
    <T> T querySingle(String sql, AbstractDAO.RowMapper<T> mapper, Object... parameters);
    <K, V> Map<K, V> queryMap(String sql, AbstractDAO.RowMapper<K> keyMapper, AbstractDAO.RowMapper<V> valueMapper, Object... parameters);
    Long insert(String sql, Object... parameters);
    void update(String sql, Object... parameters);
    void executeBatch(String sql, List<Object[]> parametersList);
    int count(String sql, Object... parameters);
}
