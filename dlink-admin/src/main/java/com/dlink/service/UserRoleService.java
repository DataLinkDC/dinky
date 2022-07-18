package com.dlink.service;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.dlink.db.service.ISuperService;
import com.dlink.model.UserRole;


public interface UserRoleService extends ISuperService<UserRole> {
    /**
     * delete user role relation by user id
     *
     * @param userId user id
     * @return delete row num
     */
    int delete(int userId);

    /**
     * query user role relation by userId
     *
     * @param userId user id
     * @return delete row num
     */
    List<UserRole> getUserRoleByUserId(int userId);

    /**
     * delete user role relation by userId  and roleId
     *
     * @param userRoleList
     * @return
     */
    int deleteBathRelation(List<UserRole> userRoleList);
}

