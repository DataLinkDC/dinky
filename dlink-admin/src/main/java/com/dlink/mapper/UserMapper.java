package com.dlink.mapper;

import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserMapper
 *
 * @author wenmo
 * @since 2021/11/28 13:36
 */
@Mapper
public interface UserMapper extends SuperMapper<User> {
}
