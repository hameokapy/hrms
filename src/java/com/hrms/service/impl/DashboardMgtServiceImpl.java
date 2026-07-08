
package com.hrms.service.impl;

import com.hrms.model.dto.response.DashboardMgtDTO;
import com.hrms.repository.impl.DashboardMgtRepositoryImpl;

public class DashboardMgtServiceImpl {
    private final DashboardMgtRepositoryImpl repo = new DashboardMgtRepositoryImpl();

    public DashboardMgtDTO getStats() {
        return repo.getDashboardStats();
    }
}
