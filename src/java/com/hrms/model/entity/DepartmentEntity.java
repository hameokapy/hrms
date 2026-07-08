
package com.hrms.model.entity;

public class DepartmentEntity extends BaseEntity {
    private String code;
    private String name;
    private String location;
    private String status;    
    // Circular FK nên phải chơi transient
    private transient EmployeeEntity manager;

    public DepartmentEntity() {
    }

    public DepartmentEntity(Long id) {
        super(id);
    }
    
    public EmployeeEntity getManager() {
        return manager;
    }

    public void setManager(EmployeeEntity manager) {
        this.manager = manager;
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
