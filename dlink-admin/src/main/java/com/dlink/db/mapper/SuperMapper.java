package com.dlink.db.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * SuperMapper
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public interface SuperMapper<T> extends BaseMapper<T> {

    List<T> selectForProTable(Page<T> page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper, @Param("param") Map<String, Object> param);

}
