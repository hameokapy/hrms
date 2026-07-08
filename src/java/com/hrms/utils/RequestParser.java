
package com.hrms.utils;

import com.hrms.core.exception.ValidationException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private final Map<String, Object> data;
    private final Map<String, String> errors = new HashMap<>();

    // Dùng lọc RequestParams của GET
    public RequestParser(HttpServletRequest request) {
        this.data = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> data.put(k, v));
    }

    // Dùng lọc RequestBody được GSON parse cho
    public RequestParser(Map<String, Object> body) {
        this.data = (body!=null) ? body : new HashMap<>();
    }
    
    public Integer getInteger(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required)
                errors.put(key, key + " is required.");
            return null;
        }
        try {
            Integer result = Double.valueOf(val.toString().trim()).intValue();
            if (result <= 0)
                errors.put(key, key + " must be a positive number.");
            return result;
        } catch (NumberFormatException e) {
            errors.put(key, key + " must be a valid number.");
            return null;
        }
    }

    public Long getLong(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null;
        }
        try {
            Long result = Double.valueOf(val.toString().trim()).longValue();
            if (result <= 0) 
                errors.put(key, key + " must be a positive number.");
            return result;
        } catch (NumberFormatException e) {
            errors.put(key, key + " must be a valid number.");
            return null;
        }
    }

    public Long getLongFromPath(String key, String value, boolean required) {
        if (value==null || value.isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null;
        }
        try {
            Long result = Double.valueOf(value.trim()).longValue();
            if (result <= 0)
                errors.put(key, key + " must be a positive number.");
            return result;
        } catch (NumberFormatException e) {
            errors.put(key, key + " must be a valid number.");
            return null;
        }
    }
    
    public String getString(String key, boolean required, int min, int max) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null; // Tính cả case isBlank() cx trả về NULL để dưới repo khỏi phải tạo đk search mấy field đó
        }
        String str = val.toString().trim();
        if (str.length()<min || str.length()>max)
            errors.put(key, String.format("%s length must be %d-%d characters.", key, min, max));
        return str;
    }

    public String getPassword(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null; 
        }
        String str = val.toString(); 
        if (str.length() < 6)
            errors.put(key, key + " must be at least 6 characters.");
        return str;
    }
    
    public String getUsername(String key, boolean required) {
        String val = getString(key, required, 4, 20); 
        if (val != null) {
            if (!val.matches("^[a-zA-Z0-9_.]+$")) 
                errors.put(key, key + " can only contain letters, numbers and underscores.");
        }
        return val;
    }

    public String getEmail(String key, boolean required) {
        String val = getString(key, required, 5, 100);
        if (val != null) {
            if (!val.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) 
                errors.put(key, key + " is invalid email format.");
        }
        return val;
    }

    public String getPhone(String key, boolean required) {
        String val = getString(key, required, 10, 10);
        if (val != null) {
            if (!val.matches("^0[3|5|7|8|9][0-9]{8}$")) 
                errors.put(key, key + " is invalid phone number.");
        }
        return val;
    }
    
    public Boolean getBoolean(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null;
        }
        String s = val.toString().trim().toLowerCase();
        if (s.equals("true") || s.equals("1")) 
            return true;
        if (s.equals("false") || s.equals("0")) 
            return false;
        errors.put(key, key + " must be a valid boolean (true/false or 1/0).");
        return null;
    }
    
    public java.math.BigDecimal getBigDecimal(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null|| val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null;
        }
        try {
            // BigDecimal xử lý được cả chuỗi dạng float kiểu: 1000.50
            java.math.BigDecimal result = new java.math.BigDecimal(val.toString().trim());
            if (result.compareTo(java.math.BigDecimal.ZERO) < 0) 
                errors.put(key, key + " cannot be negative.");
            return result;
        } catch (NumberFormatException e) {
            errors.put(key, key + " must be a valid decimal number.");
            return null;
        }
    }
    
    public LocalDate getLocalDate(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null;
        }
        try { 
            LocalDate date = LocalDate.parse(val.toString().trim()); // Format: yyyy-MM-dd
            if (date.getYear() <= 0)
                errors.put(key, key + " year must be positive.");
            return date; 
        } catch (Exception e) {
            errors.put(key, key + " format must be yyyy-MM-dd.");
            return null;
        }
    }

    public LocalDateTime getLocalDateTime(String key, boolean required) {
        Object val = getRawValue(key);
        if (val==null || val.toString().isBlank()) {
            if (required) 
                errors.put(key, key + " is required.");
            return null;
        }
        try {
            LocalDateTime dateTime = LocalDateTime.parse(val.toString().trim()); // Format: ISO (2023-10-27T10:15:30)
            if (dateTime.getYear() <= 0)
                errors.put(key, key + " year must be positive.");
            return dateTime;
        } catch (Exception e) {
            errors.put(key, key + " format must be ISO datetime.");
            return null;
        }
    }

    // Đang thiết kế dùng cho cái lấy data từ response body của PUT/POST thôi
    public List<Long> getListLong(String key, boolean required) {
        Object val = data.get(key);
        if (val==null || !(val instanceof List)) {
            if (required) 
                errors.put(key, key + " is required and must be an array of longs.");
            return Collections.emptyList(); // # new ArrayList<>() ở chỗ: nó immutable dù là empty list :)
        }
        List<?> list = (List<?>) val;
        List<Long> result = new ArrayList<>();
        for (Object item : list) {
            if (item==null || item.toString().isBlank()) {
                errors.put(key, "Found null or blank ID in the list " + key);
                return Collections.emptyList();
            }
            try {
                Long num = Double.valueOf(item.toString().trim()).longValue();
                if (num <= 0) {
                    errors.put(key, "Found ID of negative value or zero in " + key);
                    return Collections.emptyList();
                }
                result.add(num);
            } catch (NumberFormatException e) {
                errors.put(key, "Found ID of wrong number format in " + key);
                return Collections.emptyList();
            }
        }
        return result;
    }
    
    // Đang thiết kế dùng cho cái lấy data từ query link của GET thôi
    public List<String> getListString(String key, boolean required, int minLen, int maxLen) {
        Object val = data.get(key);
        List<String> result = new ArrayList<>();
        if (val == null) {
            if (required) 
                errors.put(key, key + " is required.");
            return Collections.emptyList();
        }
        if (val instanceof String[]) {
            for (String s : (String[]) val) {
                processAndAddToList(s, result);
            }
        } else if (val instanceof String) {
            processAndAddToList((String) val, result);
        }
        
        if (required && result.isEmpty()) {
            errors.put(key, key + " is required.");
        }
        return result;
    }

    private void processAndAddToList(String s, List<String> result) {
        if (s != null && !s.isBlank()) {
            String[] subs = s.split(",");
            for (String sub : subs) {
                if (!sub.trim().isEmpty()) {
                    result.add(sub.trim());
                }
            }
        }
    }
    
    private Object getRawValue(String key) {
        Object val = data.get(key);
        if (val instanceof String[]) {
            String[] arr = (String[]) val;
            return (arr.length > 0) ? arr[0] : null;
        }
        return val;
    }
    
    public void validate() {
        if (!errors.isEmpty())
            throw new ValidationException(errors);
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