
package com.hrms.model.dto.response;

import java.io.Serializable;

public class DashboardMgtDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private long totalEmployees; // all emp mà ko bị inactive
    private long totalDepartments; // all dept mà ko bị inactive
    private long pendingLeaveRequests; // all pending leave requests

    private long deptsWithoutManager; // số active dept mà ko có manager
    private long empsWithoutActiveContract; // số non-inactive emp mà ko có contract active
    private long empsWithoutUserAccount; // số active + on_leave emp mà ko có user acc active   

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public long getPendingLeaveRequests() {
        return pendingLeaveRequests;
    }

    public void setPendingLeaveRequests(long pendingLeaveRequests) {
        this.pendingLeaveRequests = pendingLeaveRequests;
    }

    public long getDeptsWithoutManager() {
        return deptsWithoutManager;
    }

    public void setDeptsWithoutManager(long deptsWithoutManager) {
        this.deptsWithoutManager = deptsWithoutManager;
    }

    public long getEmpsWithoutActiveContract() {
        return empsWithoutActiveContract;
    }

    public void setEmpsWithoutActiveContract(long empsWithoutActiveContract) {
        this.empsWithoutActiveContract = empsWithoutActiveContract;
    }

    public long getEmpsWithoutUserAccount() {
        return empsWithoutUserAccount;
    }

    public void setEmpsWithoutUserAccount(long empsWithoutUserAccount) {
        this.empsWithoutUserAccount = empsWithoutUserAccount;
    }

    
}
