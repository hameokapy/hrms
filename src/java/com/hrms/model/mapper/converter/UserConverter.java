
package com.hrms.model.mapper.converter;

import com.hrms.model.dto.response.UserSummaryDTO;
import com.hrms.model.entity.UserEntity;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.hrms.utils.DataMapper;

public class UserConverter {
    
    public static <T> T toUserDTO(UserEntity entity, Class<T> targetClass, Map<Long, Set<String>> roleMap) {
        if(entity==null)
            return null;
        T dto = DataMapper.mapObjectToObject(entity, targetClass);
        if(dto instanceof UserSummaryDTO){
            // Anything extra thì tự map tay ở dưới
            UserSummaryDTO sdto = (UserSummaryDTO) dto;
            if(roleMap != null) {
                sdto.setRoleNames(roleMap.getOrDefault(entity.getId(), new HashSet<>()));
            }
            if(entity.getEmployee() != null){
                String displayName = entity.getEmployee().getFullName() + " (" + entity.getEmployee().getEmployeeCode() + ")";
                String dept = entity.getEmployee().getDepartment()!=null ? entity.getEmployee().getDepartment().getName() : "N/A";
                String posi = entity.getEmployee().getPosition()!=null ? entity.getEmployee().getPosition().getName() : "N/A";
                sdto.setEmployeeDisplayName(displayName);
                sdto.setDepartmentAndPosition(dept + " (" + posi + ")");
            }
        }
        return dto;
    }
}
