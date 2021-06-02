package com.dlink.mapper;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.dlink.db.mapper.SuperMapper;
import com.dlink.model.Statement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * StatementMapper
 *
 * @author wenmo
 * @since 2021/5/28 13:41
 **/
@Mapper
public interface StatementMapper extends SuperMapper<Statement> {

    int insert(Statement statement);

}
