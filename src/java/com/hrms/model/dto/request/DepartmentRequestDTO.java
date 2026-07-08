
package com.hrms.model.dto.request;

public class DepartmentRequestDTO { 
    // Lý do cần 1: đơn giản là để giảm tham số của 1 hàm <= 3 (quy tắc trong cuốn Clean Code)
    // Lý do cần 2: bảo mật hơn = cách chỉ chứa những field đc phép sửa gửi xuống BE
    
    private Long id;
    private String code;
    private String name;
    private Long managerId;
    private String location;
    private String status;    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
}
