package com.dlink.controller;

import com.dlink.common.result.ProTableResult;
import com.dlink.common.result.Result;
import com.dlink.model.Cluster;
import com.dlink.result.SubmitResult;
import com.dlink.service.ClusterService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ClusterController
 *
 * @author wenmo
 * @since 2021/5/28 14:03
 **/
@Slf4j
@RestController
@RequestMapping("/api/cluster")
public class ClusterController {
    @Autowired
    private ClusterService clusterService;

    /**
     * 新增或者更新
     */
    @PutMapping
    public Result saveOrUpdate(@RequestBody Cluster cluster) throws Exception {
        checkHealth(cluster);
        if(clusterService.saveOrUpdate(cluster)){
            return Result.succeed("新增成功");
        }else {
            return Result.failed("新增失败");
        }
    }

    /**
     * 动态查询列表
     */
    @PostMapping
    public ProTableResult<Cluster> listClusters(@RequestBody JsonNode para) {
        return clusterService.selectForProTable(para);
    }

    /**
     * 批量删除
     */
    @DeleteMapping
    public Result deleteMul(@RequestBody JsonNode para) {
        if (para.size()>0){
            boolean isAdmin = false;
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para){
                Integer id = item.asInt();
                if(!clusterService.removeById(id)){
                    error.add(id);
                }
            }
            if(error.size()==0&&!isAdmin) {
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
    public Result getOneById(@RequestBody Cluster cluster) throws Exception {
        cluster = clusterService.getById(cluster.getId());
        return Result.succeed(cluster,"获取成功");
    }

    /**
     * 全部心跳监测
     */
    @PostMapping("/heartbeats")
    public Result heartbeat(@RequestBody JsonNode para) {
        List<Cluster> clusters = clusterService.listEnabledAll();
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            checkHealth(cluster);
            clusterService.updateById(cluster);
        }
        return Result.succeed("状态刷新完成");
    }

    private void checkHealth(Cluster cluster){
        String jobManagerHost = clusterService.checkHeartBeat(cluster.getHosts(), cluster.getJobManagerHost());
        if(jobManagerHost==null){
            cluster.setJobManagerHost("");
            cluster.setStatus(0);
        }else{
            cluster.setJobManagerHost(jobManagerHost);
            cluster.setStatus(1);
        }
    }
}
