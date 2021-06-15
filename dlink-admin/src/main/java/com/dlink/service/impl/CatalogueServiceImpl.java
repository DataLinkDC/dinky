package com.dlink.service.impl;

import com.dlink.assertion.Assert;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.dto.CatalogueTaskDTO;
import com.dlink.mapper.CatalogueMapper;
import com.dlink.model.Catalogue;
import com.dlink.model.Task;
import com.dlink.service.CatalogueService;
import com.dlink.service.StatementService;
import com.dlink.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CatalogueServiceImpl
 *
 * @author wenmo
 * @since 2021/5/28 14:02
 **/
@Service
public class CatalogueServiceImpl extends SuperServiceImpl<CatalogueMapper, Catalogue> implements CatalogueService {

    @Autowired
    private TaskService taskService;
    @Autowired
    private StatementService statementService;

    @Override
    public List<Catalogue> getAllData() {
        return this.list();
    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public Catalogue createCatalogueAndTask(CatalogueTaskDTO catalogueTaskDTO) {
        Task task = new Task();
        task.setName(catalogueTaskDTO.getName());
        task.setAlias(catalogueTaskDTO.getAlias());
        taskService.saveOrUpdateTask(task);
        Catalogue catalogue = new Catalogue();
        catalogue.setName(catalogueTaskDTO.getAlias());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.save(catalogue);
        return catalogue;
    }

    @Transactional(rollbackFor=Exception.class)
    @Override
    public boolean toRename(Catalogue catalogue) {
        Catalogue oldCatalogue = this.getById(catalogue.getId());
        if(oldCatalogue==null){
            return false;
        }else{
            Task task = new Task();
            task.setId(oldCatalogue.getTaskId());
            task.setAlias(catalogue.getName());
            taskService.updateById(task);
            this.updateById(catalogue);
            return true;
        }
    }

    @Override
    public boolean removeCatalogueAndTaskById(Integer id) {
        Catalogue catalogue = this.getById(id);
        if(catalogue==null){
            return false;
        }else{
            if(catalogue.getTaskId()!=null) {
                taskService.removeById(catalogue.getTaskId());
                statementService.removeById(catalogue.getTaskId());
            }
            this.removeById(id);
            return true;
        }
    }
}
