package com.dlink.controller;

import com.dlink.api.FlinkAPI;
import com.dlink.assertion.Asserts;
import com.dlink.cluster.FlinkClusterInfo;
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
        cluster.setAutoRegisters(false);
        clusterService.registersCluster(cluster);
        return Result.succeed(Asserts.isNotNull(cluster.getId()) ? "修改成功" : "新增成功");
    }

    /**
     * 启用和禁用
     */
    @PutMapping("/enable")
    public Result enableCluster(@RequestBody Cluster cluster) throws Exception {
        clusterService.enableCluster(cluster);
        return Result.succeed("修改成功");
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
        if (para.size() > 0) {
            List<Integer> error = new ArrayList<>();
            for (final JsonNode item : para) {
                Integer id = item.asInt();
                if (!clusterService.removeById(id)) {
                    error.add(id);
                }
            }
            if (error.size() == 0) {
                return Result.succeed("删除成功");
            } else {
                return Result.succeed("删除部分成功，但" + error.toString() + "删除失败，共" + error.size() + "次失败。");
            }
        } else {
            return Result.failed("请选择要删除的记录");
        }
    }

    /**
     * 获取指定ID的信息
     */
    @PostMapping("/getOneById")
    public Result getOneById(@RequestBody Cluster cluster) throws Exception {
        cluster = clusterService.getById(cluster.getId());
        return Result.succeed(cluster, "获取成功");
    }

    /**
     * 获取可用的集群列表
     */
    @GetMapping("/listEnabledAll")
    public Result listEnabledAll() {
        List<Cluster> clusters = clusterService.listEnabledAll();
        return Result.succeed(clusters, "获取成功");
    }

    /**
     * 获取可用的集群列表
     */
    @GetMapping("/listSessionEnable")
    public Result listSessionEnable() {
        List<Cluster> clusters = clusterService.listSessionEnable();
        return Result.succeed(clusters, "获取成功");
    }

    /**
     * 全部心跳监测
     */
    @PostMapping("/heartbeats")
    public Result heartbeat(@RequestBody JsonNode para) {
        List<Cluster> clusters = clusterService.listEnabledAll();
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            clusterService.registersCluster(cluster);
        }
        return Result.succeed("状态刷新完成");
    }

    /**
     * 回收过期集群
     */
    @GetMapping("/clear")
    public Result clear() {
        return Result.succeed(clusterService.clearCluster(), "回收完成");
    }

}
