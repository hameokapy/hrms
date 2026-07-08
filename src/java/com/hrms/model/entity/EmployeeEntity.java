
package com.hrms.model.entity;

public class EmployeeEntity extends BaseEntity {
    private String employeeCode;
    private String fullName;
    private String email;
    private String phone;
    private String status;
    // Employee-Department mqh 1:1
    private DepartmentEntity department;
    // Employee-Position mqh 1:1
    private PositionEntity position;

    public EmployeeEntity() {
    }

    public EmployeeEntity(Long id) {
        super(id);
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DepartmentEntity getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentEntity department) {
        this.department = department;
    }

    public PositionEntity getPosition() {
        return position;
    }

    public void setPosition(PositionEntity position) {
        this.position = position;
    }
    
    
}
