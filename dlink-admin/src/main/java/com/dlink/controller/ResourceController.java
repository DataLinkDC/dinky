package com.dlink.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dlink.assertion.Asserts;
import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Resource;
import com.dlink.service.ResourceService;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@RestController
@RequestMapping("/api/resource")
public class ResourceController {
    @Autowired
    private ResourceService resourceService;

    /**
     * create or update role
     *
     * @return delete result code
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Resource resource) {
        Integer id = resource.getId();
        if (resourceService.saveOrUpdate(resource)) {
            return Result.succeed(Asserts.isNotNull(id) ? "修改成功" : "新增成功");
        } else {
            return Result.failed(Asserts.isNotNull(id) ? "修改失败" : "新增失败");
        }
    }

    /**
     * delete resource by id
     *
     * @return delete result code
     */
    @DeleteMapping()
    public Result deleteResourceById(@RequestBody JsonNode para) {
        return resourceService.deleteResourceById(para);
    }

    /**
     * query resource list
     */
    @PostMapping
    public ProTableResult<Resource> listResources(@RequestBody JsonNode para) {
        return resourceService.selectForProTable(para);
    }
}
