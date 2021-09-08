package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.DocumentMapper;
import com.dlink.model.Document;
import com.dlink.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DocumentServiceImpl
 *
 * @author wenmo
 * @since 2021/6/3 14:36
 **/
@Service
public class DocumentServiceImpl extends SuperServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Override
    public List<Document> getFillAllByVersion(String version) {
        return baseMapper.selectList(new QueryWrapper<Document>().eq("version",version).eq("enabled",1));
    }
}
