package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.SysConfig;
import com.dlink.service.SysConfigService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * SysConfigController
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Slf4j
@RestController
@RequestMapping("/api/sysConfig")
public class SysConfigController {
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody SysConfig sysConfig) throws Exception {
        if(sysConfigService.saveOrUpdate(sysConfig)){
            return Result.succeed("新增成功");
        }else {
            return Result.failed("新增失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<SysConfig> listSysConfigs(@RequestBody JsonNode para) {
        return sysConfigService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size()>0){
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para){
                Integer id = item.asInt();
                if(!sysConfigService.removeById(id)){
                    error.add(id);
                }
            }
            if(error.size()==0) {
                return Result.succeed("删除成功");
            }else {
                return Result.succeed("删除部分成功，但"+error.toString()+"删除失败，共"+error.size()+"次失败。");
            }
        }else{
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody SysConfig sysConfig) throws Exception {
        sysConfig = sysConfigService.getById(sysConfig.getId());
        return Result.succeed(sysConfig,"获取成功");
    }

    /**
     * 获取所有配置
     */
    @GetMapping("/getAll")
    public Result getAll() {
        return Result.succeed(sysConfigService.getAll(),"获取成功");
    }

    /**
     * 批量更新配置
     */
    @PostMapping("/updateSysConfigByJson")
    public Result updateSysConfigByJson(@RequestBody JsonNode para) throws Exception {
        sysConfigService.updateSysConfigByJson(para);
        return Result.succeed("更新配置成功");
    }
}
