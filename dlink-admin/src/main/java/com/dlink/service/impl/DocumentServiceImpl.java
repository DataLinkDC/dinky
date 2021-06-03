package com.dlink.service.impl;

import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.DocumentMapper;
import com.dlink.model.Document;
import com.dlink.service.DocumentService;
import org.springframework.stereotype.Service;

/**
 * DocumentServiceImpl
 *
 * @author wenmo
 * @since 2021/6/3 14:36
 **/
@Service
public class DocumentServiceImpl extends SuperServiceImpl<DocumentMapper, Document> implements DocumentService {

}
