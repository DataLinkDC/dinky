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
import com.dlink.model.Namespace;
import com.dlink.service.NamespaceService;
import com.fasterxml.jackson.databind.JsonNode;

@Slf4j
@RestController
@RequestMapping("/api/namespace")
public class NamespaceController {
    @Autowired
    private NamespaceService namespaceService;

    /**
     * create or update namespace
     *
     * @return delete result code
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Namespace namespace) {
        Integer id = namespace.getId();
        if (namespaceService.saveOrUpdate(namespace)) {
            return Result.succeed(Asserts.isNotNull(id) ? "修改成功" : "新增成功");
        } else {
            return Result.failed(Asserts.isNotNull(id) ? "修改失败" : "新增失败");
        }
    }

    /**
     * delete namespace by id
     *
     * @return delete result code
     */
    @DeleteMapping()
    public Result deleteNamespaceById(@RequestBody JsonNode para) {
        return namespaceService.deleteNamespaceById(para);
    }

    /**
     * query namespace list
     */
    @PostMapping
    public ProTableResult<Namespace> listNamespaces(@RequestBody JsonNode para) {
        return namespaceService.selectForProTable(para);
    }
}
