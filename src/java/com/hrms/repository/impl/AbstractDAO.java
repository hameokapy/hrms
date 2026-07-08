
package com.hrms.repository.impl;

import com.hrms.core.config.DBContext;
import com.hrms.core.exception.technical.DatabaseException;
import java.sql.*;
import java.util.*;
import com.hrms.repository.GenericDAO;
import com.hrms.utils.DataMapper;

public abstract class AbstractDAO<T> implements GenericDAO<T> {
    
    @Override
    public <T> T querySingle(String sql, Class<T> clazz, Object... parameters) {
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return DataMapper.mapResultSetToObject(rs, clazz); 
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Query single failed: " + sql, e);
        }
        return null;
    }

    @Override
    public <T> List<T> queryList(String sql, Class<T> clazz, Object... parameters) {
        List<T> results = new ArrayList<>();
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    T object = DataMapper.mapResultSetToObject(rs, clazz); 
                    results.add(object);
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Query list failed: " + sql, e);
        }
        return results;
    }
    
    // queryList() và querySingle() phiên bản tự định nghĩa dùng RowMapper
    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
    
    @Override
    public <T> List<T> queryList(String sql, RowMapper<T> mapper, Object... parameters) {
        List<T> results = new ArrayList<>();
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    // Gọi hàm map tự customize của RowMapper ở đây
                    results.add(mapper.map(rs)); 
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Query list failed: " + sql, e);
        }
        return results;
    }
    
    @Override
    public <T> T querySingle(String sql, RowMapper<T> mapper, Object... parameters) {
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    // Gọi hàm map tự customize của RowMapper ở đây
                    return mapper.map(rs); 
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Query single failed: " + sql, e);
        }
        return null;
    }
    
    @Override
    public <K, V> Map<K, V> queryMap(String sql, RowMapper<K> keyMapper, RowMapper<V> valueMapper, Object... parameters) {
        Map<K, V> result = new HashMap<>();
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, parameters);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.put(keyMapper.map(rs), valueMapper.map(rs));
                }
            }
        } catch (Exception e) {
            throw new DatabaseException("Query map failed: " + sql, e);
        }
        return result;
    }
    
    private void setParameter(PreparedStatement statement, Object... parameters) {
        for (int i = 0; i < parameters.length; i++) {
            try {
                Object parameter = parameters[i];
                int index = i + 1;
                if (parameter != null) {
                    // JDBC tự động map: String -> NVARCHAR, Long -> BIGINT...
                    // Chiêu tránh hacker chơi trò SQL Injection
                    statement.setObject(index, parameter); 
                } else {
                    // Xử lý null riêng để DB biết đây là NULL của kiểu nào
                    statement.setNull(index, Types.NULL); 
                }
            } catch (SQLException e) {
                throw new DatabaseException("Set parameters failed at index: " + (i+1), e);
            }
        }
    }

    @Override
    public Long insert(String sql, Object... parameters) {
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParameter(statement, parameters);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                return resultSet.next() ? resultSet.getLong(1) : null;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Insert failed: " + sql, e);
        }
    }

    @Override
    public void update(String sql, Object... parameters) {
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setParameter(statement, parameters);
            statement.executeUpdate(); //đang prefer cách return boolean ở đây: return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DatabaseException("Update failed: " + sql, e);
        }
    }
    
    @Override
    public void executeBatch(String sql, List<Object[]> parametersList) {
        if (parametersList == null || parametersList.isEmpty()) return;
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Object[] parameters : parametersList) {
                setParameter(statement, parameters);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new DatabaseException("Batch execution failed: " + sql, e);
        }
    }

    @Override
    public int count(String sql, Object... parameters) {
        Connection connection = DBContext.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {            
            setParameter(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new DatabaseException("Count failed: " + sql, e);
        }
    }
}

/*
    BẢNG DATA TYPE BỌN REQUEST PARAM VÀ REQUEST BODY TRẢ VỀ
    Kiểu dữ liệu    Request Parameter (String)                                          Request Body (GSON Map<String, Object>)
    Number          Luôn là "123" (String).                                             Thường là 123.0 (Double). GSON mặc định số không có type là Double.
    Boolean         Luôn là "true"/"false" (String).                                    Là true/false (Kiểu Boolean thật sự của Java).
    String          Là "abc". Nếu để trống là "" (String).                              Là "abc". Nếu để trống là "" (String).
    Null            Nếu không gửi key, getParameter trả về null.                        Nếu gửi key: null trong JSON, Map.get() trả về null.
    Array           Gửi dạng ?id=1&id=2. Dùng getParameterValues trả về String[].	Trả về ArrayList<Object>. Các phần tử bên trong lại tuân theo quy tắc Double/String/Boolean ở trên.
*/
/*  
    BẢNG DATA TYPE TƯƠNG ĐƯƠNG: SQL - JDBC trả về kiểu java data type gì - JAVA data type mà project này cần
    SQL TYPE            JDBC TRẢ VỀ KIỂU JAVA?      JAVA DATA TYPE PROJECT NÀY CẦN?
    INT                 java.lang.Integer           Long
    NVARCHAR            java.lang.String            String
    DECIMAL(18,2)       java.math.BigDecimal        BigDecimal (ko dùng Double vì sai số do làm tròn)
    BIT                 java.lang.Boolean           Boolean
    DATETIME            java.sql.Timestamp          LocalDateTime
    DATE                java.sql.Date               LocalDate
    TIME(0)             java.sql.Time               LocalTime
    => Tóm lại là: Bên java mình vẫn dùng String, Boolean nên chỗ switch-case của setFieldValue() ko phải tính mấy case đó!
    Có hai switch-case về Integer<->Long vì đang cho hàm đấy convert đa năng Entity<->DTO, chứ ko chỉ mỗi ResultSet->Entity
*/
/*
    Luồng đi (Input): Request (String của RequestParam hoặc Map<String,Object> của RequestBody qua GSON) -> RequestParser -> Kiểu Java data type 
    mong muốn -> Service dùng data type đó xử lý các tác vụ -> Lúc truyền các biến đó vào ? ở tầng Repository, (ở setParameter() bên AbstractDAO)
    dùng tuyệt chiêu statement.setObject(index, parameter) thì JDBC Driver đủ thông minh để hiểu: "À, ông đưa tôi LocalDateTime, tôi sẽ tự chuyển nó thành DATETIME của SQL Server"
    
    Luồng về (Output): SQL Server data type -> JDBC bốc ResultSet và chuyển về JDBC java data type -> DataMapper -> Kiểu Java data type ta mong muốn để map vào Entity/DTO

    Đại đạo BA CHI giải quyết đống chuyển đổi data type: class RequestParser, class DataMapper, class AbstractDAO :)
*/