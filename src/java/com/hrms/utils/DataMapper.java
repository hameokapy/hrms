
package com.hrms.utils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.sql.*;
import java.util.HashMap;

public class DataMapper {
    private DataMapper() {}
    
    // Lưu Cache Field để ko phải quét Reflection liên tục
    private static final Map<Class<?>, Map<String, Field>> fieldCache = new ConcurrentHashMap<>();

    // 1. Map từ ResultSet sang Entity (dùng tầng repo)
    public static <T> T mapResultSetToObject(ResultSet rs, Class<T> clazz) {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i); // # .getColumnName()
                Object value = rs.getObject(columnName);
                String propertyName = convertToCamelCase(columnName);
                setFieldValue(object, propertyName, value);
            }
            return object; 
        } catch (Exception e) { 
            return null; 
        }
    }
    
    private static String convertToCamelCase(String snakeCase) {
        if (snakeCase==null || !snakeCase.contains("_")) 
            return snakeCase;
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (char c : snakeCase.toLowerCase().toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                result.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return result.toString();
    }

    private static void setFieldValue(Object target, String propertyName, Object value) {
        if (value == null) 
            return; // Nếu DB trả về null thì ko cần set làm gì
        if ("serialVersionUID".equals(propertyName)) 
            return;
        Field field = null;
        try {
            Map<String, Field> fields = fieldCache.computeIfAbsent(target.getClass(), clazz -> DataMapper.getAllFields(clazz));
            field = fields.get(propertyName);
        // ======= CÁCH NÀY KO DÙNG ĐC, COI LÀ PSEUDOCODE ĐỂ HIỂU CONVERT NHỮNG GÌ =======
        //if (field != null) {
        //    field.setAccessible(true);
        //    Class<?> fieldType = field.getType();
        //    Object convertedValue = switch (fieldType.getName()) {
        //        case "java.time.LocalDateTime" -> (value instanceof java.sql.Timestamp t) ? t.toLocalDateTime() : value;
        //        case "java.time.LocalDate" -> (value instanceof java.sql.Date d) ? d.toLocalDate() : value;
        //        case "java.time.LocalTime" -> (value instanceof java.sql.Time d) ? d.toLocalTime(): value;
        //        // Hai cái dưới để instanceof Number cho đa năng, cover đc cả case Long/Integer/BigDecimal
        //        case "java.lang.Integer" -> (value instanceof Number l) ? l.intValue() : value;
        //        case "java.lang.Long" -> (value instanceof Number i) ? i.longValue() : value;
        //        default -> value;
        //    };
        //    field.set(target, convertedValue);
        //} 
        // ===============================================================================
            if (field == null) 
                return;
            field.setAccessible(true);
            Class<?> fieldType = field.getType();         
            Object convertedValue = value;
            if (fieldType == Long.class || fieldType == long.class) {
                convertedValue = (value instanceof Number) ? ((Number) value).longValue() 
                        : Long.valueOf(value.toString().trim());
            } else if (fieldType == Integer.class || fieldType == int.class) {
                convertedValue = (value instanceof Number) ? ((Number) value).intValue() 
                        : Integer.valueOf(value.toString().trim());
            } else if (fieldType == java.math.BigDecimal.class) {
                if (value instanceof java.math.BigDecimal) 
                    convertedValue = value;
                else if (value instanceof Number) 
                    convertedValue = java.math.BigDecimal.valueOf(((Number) value).doubleValue());
                else 
                    convertedValue = new java.math.BigDecimal(value.toString().trim());
            } else if (fieldType == Boolean.class || fieldType == boolean.class) {
                if (value instanceof Boolean) {
                    convertedValue = value;
                } else if (value instanceof Number) {
                    convertedValue = ((Number) value).intValue() != 0;
                } else {
                    String str = value.toString().trim().toLowerCase();
                    convertedValue = str.equals("true") || str.equals("1");
                }
            } else if (fieldType == String.class) {
                convertedValue = value.toString();
            } else if (fieldType == java.time.LocalDateTime.class) {
                if (value instanceof java.sql.Timestamp) 
                    convertedValue = ((java.sql.Timestamp) value).toLocalDateTime();
                else if (value instanceof java.time.LocalDateTime) 
                    convertedValue = value;
                else 
                    convertedValue = java.time.LocalDateTime.parse(value.toString());
            } else if (fieldType == java.time.LocalDate.class) {
                if (value instanceof java.sql.Date)
                    convertedValue = ((java.sql.Date) value).toLocalDate();
                else if (value instanceof java.time.LocalDate)
                    convertedValue = value;
                else
                    convertedValue = java.time.LocalDate.parse(value.toString());
            } else if (fieldType == java.time.LocalTime.class) {
                if (value instanceof java.sql.Time)
                    convertedValue = ((java.sql.Time) value).toLocalTime();
                else if (value instanceof java.time.LocalTime)
                    convertedValue = value;
                else
                    convertedValue = java.time.LocalTime.parse(value.toString());
            } else {
                field.set(target, value);
            }
            field.set(target, convertedValue);
        } catch (Exception e) { 
            System.err.println("Mapper Error: Field [" + propertyName + "] requires " 
            + field.getType().getSimpleName() + " but got " + value.getClass().getSimpleName());
        }
    } 

    private static Map<String, Field> getAllFields(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        while (clazz!=null && clazz!=Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                fieldMap.putIfAbsent(field.getName(), field);
            }
            clazz = clazz.getSuperclass();
        }
        return fieldMap;
    }

    // 2. Map từ Entity sang DTO (dùng tầng service)
    public static <T> T mapObjectToObject(Object source, Class<T> targetClass) {
        try {
            T target = targetClass.getDeclaredConstructor().newInstance();
            Map<String, Field> targetFields = getAllFields(targetClass);
            Map<String, Field> sourceFields = getAllFields(source.getClass());   
            for (String fieldName : targetFields.keySet()) {
                if (sourceFields.containsKey(fieldName)) {
                    Field sourceField = sourceFields.get(fieldName);
                    sourceField.setAccessible(true);
                    Object value = sourceField.get(source);
                    setFieldValue(target, fieldName, value);
                }
            }
            return target;
        } catch (Exception e) { 
            return null; 
        }
    }
    
    // 3. Vá not null field của requestDTO sang existing Entity (dùng tầng service)
    public static void copyPropertiesIgnoreNull(Object source, Object target) {
        if (source==null || target==null) 
            return;
        Map<String, Field> sourceFields = fieldCache.computeIfAbsent(source.getClass(), DataMapper::getAllFields);
        Map<String, Field> targetFields = fieldCache.computeIfAbsent(target.getClass(), DataMapper::getAllFields);
        for (Map.Entry<String, Field> entry : sourceFields.entrySet()) {
            String fieldName = entry.getKey();
            Field sourceField = entry.getValue();
            if (targetFields.containsKey(fieldName)) {
                try {
                    sourceField.setAccessible(true);
                    Object value = sourceField.get(source);
                    if (value != null)
                        setFieldValue(target, fieldName, value);
                } catch (Exception e) {
                    System.err.println("Copy Error: Field [" + fieldName + "] - " + e.getMessage());
                }
            }
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