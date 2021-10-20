package com.dlink.db.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.dlink.db.properties.MybatisPlusFillProperties;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * DateMetaObjectHandler
 *
 * @author wenmo
 * @since 2021/5/25
 **/
public class DateMetaObjectHandler implements MetaObjectHandler {
    private MybatisPlusFillProperties mybatisPlusFillProperties;

    public DateMetaObjectHandler(MybatisPlusFillProperties mybatisPlusFillProperties) {
        this.mybatisPlusFillProperties = mybatisPlusFillProperties;
    }

    @Override
    public boolean openInsertFill() {
        return mybatisPlusFillProperties.getEnableInsertFill();
    }

    @Override
    public boolean openUpdateFill() {
        return mybatisPlusFillProperties.getEnableUpdateFill();
    }

    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = getFieldValByName(mybatisPlusFillProperties.getCreateTimeField(), metaObject);
        Object updateTime = getFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), metaObject);
        Object alias = getFieldValByName(mybatisPlusFillProperties.getAlias(), metaObject);
        Object name = getFieldValByName(mybatisPlusFillProperties.getName(), metaObject);
        if (createTime == null) {
            setFieldValByName(mybatisPlusFillProperties.getCreateTimeField(), LocalDateTime.now(), metaObject);
        }
        if (updateTime == null) {
            setFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), LocalDateTime.now(), metaObject);
        }
        if (alias == null) {
            setFieldValByName(mybatisPlusFillProperties.getAlias(), name, metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        setFieldValByName(mybatisPlusFillProperties.getUpdateTimeField(), LocalDateTime.now(), metaObject);
    }
}