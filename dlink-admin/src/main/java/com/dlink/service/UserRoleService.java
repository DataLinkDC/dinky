package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.UserRole;


public interface UserRoleService extends ISuperService<UserRole> {
    /**
     * delete user role relation by user id
     * @param userId user id
     * @return delete row num
     */
    public int delete(int userId);
}
