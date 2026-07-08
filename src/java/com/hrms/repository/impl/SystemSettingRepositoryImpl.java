
package com.hrms.repository.impl;

import com.hrms.core.config.SystemConfig;
import java.util.Map;

public class SystemSettingRepositoryImpl extends AbstractDAO<Object> {
    
    public Map<String, String> getAllSettingMappings() {
        String sql = "SELECT setting_key, setting_value FROM system_settings";
        return queryMap(sql, rs -> rs.getString("setting_key"), rs -> rs.getString("setting_value"));
    }
    
    public void updateYearConfig(int year) {
        String sql = "UPDATE system_settings SET setting_value = ?, updated_at = GETDATE() WHERE setting_key = ?";
        update(sql, String.valueOf(year), SystemConfig.Key.LAST_GEN_YEAR.getValue());
    }
}
