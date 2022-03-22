package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
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

import static com.dlink.assertion.Asserts.isNotNull;
import static com.dlink.assertion.Asserts.isNull;

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

    @Override
    public Catalogue findByParentIdAndName(Integer parent_id, String name) {
        return baseMapper.selectOne(Wrappers.<Catalogue>query().eq("parent_id", parent_id).eq("name", name));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Catalogue createCatalogueAndTask(CatalogueTaskDTO catalogueTaskDTO) {
        Task task = new Task();
        task.setName(catalogueTaskDTO.getName());
        task.setAlias(catalogueTaskDTO.getAlias());
        task.setDialect(catalogueTaskDTO.getDialect());
        taskService.saveOrUpdateTask(task);
        Catalogue catalogue = new Catalogue();
        catalogue.setName(catalogueTaskDTO.getAlias());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setType(catalogueTaskDTO.getDialect());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.save(catalogue);
        return catalogue;
    }

    @Override
    public Catalogue createCatalogAndFileTask(CatalogueTaskDTO catalogueTaskDTO, String ment) {
        Task task = new Task();
        task.setName(catalogueTaskDTO.getName());
        task.setAlias(catalogueTaskDTO.getAlias());
        task.setDialect(catalogueTaskDTO.getDialect());
        task.setStatement(ment);
        task.setEnabled(true);
        taskService.saveOrUpdateTask(task);
        Catalogue catalogue = new Catalogue();
        catalogue.setName(catalogueTaskDTO.getAlias());
        catalogue.setIsLeaf(true);
        catalogue.setTaskId(task.getId());
        catalogue.setType(catalogueTaskDTO.getDialect());
        catalogue.setParentId(catalogueTaskDTO.getParentId());
        this.save(catalogue);
        return catalogue;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean toRename(Catalogue catalogue) {
        Catalogue oldCatalogue = this.getById(catalogue.getId());
        if (isNull(oldCatalogue)) {
            return false;
        } else {
            Task task = new Task();
            task.setId(oldCatalogue.getTaskId());
            task.setName(catalogue.getName());
            task.setAlias(catalogue.getName());
            taskService.updateById(task);
            this.updateById(catalogue);
            return true;
        }
    }

    @Override
    public boolean removeCatalogueAndTaskById(Integer id) {
        Catalogue catalogue = this.getById(id);
        if (isNull(catalogue)) {
            return false;
        } else {
            if (isNotNull(catalogue.getTaskId())) {
                taskService.removeById(catalogue.getTaskId());
                statementService.removeById(catalogue.getTaskId());
            }
            this.removeById(id);
            return true;
        }
    }

    @Override
    public boolean moveCatalogue(Integer id, Integer parentId) {
        Catalogue catalogue = this.getById(id);
        if (isNull(catalogue)) {
            return false;
        } else {
            catalogue.setParentId(parentId);
            return updateById(catalogue);
        }
    }
}
