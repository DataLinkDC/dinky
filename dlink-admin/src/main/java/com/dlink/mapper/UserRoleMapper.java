package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.UserRole;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * user role mapper interface
 */
@Mapper
public interface UserRoleMapper extends SuperMapper<UserRole> {
    /**
     * @param userId userId
     * @return user role relation
     */
    List<UserRole> getUserRoleByUserId(@Param("userId") int userId);

    /**
     * delete user role relation
     *
     * @param userRoleList list
     * @return int
     */
    int deleteBathRelation(@Param("userRoleList") List<UserRole> userRoleList);

    /**
     * delete user role relation by role id
     *
     * @param roleId role id
     * @return
     */
    int deleteByRoleIds(@Param("roleIds") List<Integer> roleIds);
}