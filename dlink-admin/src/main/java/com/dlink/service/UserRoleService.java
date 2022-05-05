package com.dlink.service;

import com.dlink.db.service.ISuperService;
import com.dlink.model.UserRole;


public interface UserRoleService extends ISuperService<UserRole> {
    public int delete(int userId);
}
