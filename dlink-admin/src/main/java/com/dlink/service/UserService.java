package com.dlink.service;

import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.User;

/**
 * UserService
 *
 * @author wenmo
 * @since 2021/11/28 13:39
 */
public interface UserService extends ISuperService<User> {

    Result registerUser(User user);

    boolean modifyUser(User user);

    boolean removeUser(Integer id);

    Result loginUser(String username, String password,boolean isRemember);

    User getUserByUsername(String username);
}
