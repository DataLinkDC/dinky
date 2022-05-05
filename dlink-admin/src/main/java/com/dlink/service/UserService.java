package com.dlink.service;

import com.dlink.common.result.Result;
import com.dlink.db.service.ISuperService;
import com.dlink.model.User;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * UserService
 *
 * @author wenmo
 * @since 2021/11/28 13:39
 */
public interface UserService extends ISuperService<User> {

    Result registerOrUpdateUser(JsonNode para);

    boolean modifyUser(User user);

    Result modifyPassword(String username, String password, String newPassword);

    boolean removeUser(Integer id);

    Result loginUser(String username, String password, boolean isRemember);

    User getUserByUsername(String username);
}
