package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.History;
import com.dlink.service.HistoryService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * HistoryController
 *
 * @author wenmo
 * @since 2021/6/26 23:09
 */
@Slf4j
@RestController
@RequestMapping("/api/history")
public class HistoryController {
    @Autowired
    private HistoryService historyService;

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<History> listHistorys(@RequestBody JsonNode para) {
        return historyService.selectForProTable(para);
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
                if(!historyService.removeById(id)){
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
    public Result getOneById(@RequestBody History history) throws Exception {
        history = historyService.getById(history.getId());
        return Result.succeed(history,"获取成功");
    }

}
