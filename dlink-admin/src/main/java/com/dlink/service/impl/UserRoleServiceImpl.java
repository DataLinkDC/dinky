package com.dlink.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.UserRoleMapper;
import com.dlink.model.UserRole;
import com.dlink.service.UserRoleService;

@Service
public class UserRoleServiceImpl extends SuperServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
    @Override
    public int delete(int userId) {
        LambdaQueryWrapper<UserRole> wrapper = Wrappers.lambdaQuery();
        wrapper.eq(UserRole::getUserId, userId);
        return baseMapper.delete(wrapper);
    }
}
