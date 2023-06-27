package org.dinky.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.data.dto.TreeNodeDTO;
import org.dinky.data.result.Result;
import org.dinky.service.ResourcesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourcesService resourcesService;
    /*
       CREATE TABLE `dinky_resources` (
     `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
     `alias` varchar(64) DEFAULT NULL COMMENT 'alias',
     `file_name` varchar(64) DEFAULT NULL COMMENT 'file name',
     `description` varchar(255) DEFAULT NULL,
     `user_id` int(11) DEFAULT NULL COMMENT 'user id',
     `type` tinyint(4) DEFAULT NULL COMMENT 'resource type,0:FILE，1:UDF',
     `size` bigint(20) DEFAULT NULL COMMENT 'resource size',
     `pid` int(11) DEFAULT NULL,
     `full_name` varchar(128) DEFAULT NULL,
     `is_directory` tinyint(4) DEFAULT NULL,
       `create_time` datetime DEFAULT NULL COMMENT 'create time',
     `update_time` datetime DEFAULT NULL COMMENT 'update time',
     PRIMARY KEY (`id`),
     UNIQUE KEY `dinky_resources_un` (`full_name`,`type`)
   ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE = utf8_bin;

        */
    @PostMapping("/createFolder")
    public Result<Void> createFolder(Integer pid, String fileName, String desc){
        resourcesService.createFolder(pid, fileName, desc);
        return Result.succeed();
    }
    @PostMapping("/rename")
    public Result<Void> rename(Integer id, String fileName, String desc){
        resourcesService.rename(id, fileName, desc);
        return Result.succeed();
    }
    @GetMapping("/showByTree")
    public Result<List<TreeNodeDTO>> showByTree(Integer pid, Integer showFloorNum){
        return Result.succeed(resourcesService.showByTree(pid, showFloorNum));
    }

    @GetMapping("/getContentByResourceId")
    public Result<String> getContentByResourceId(@RequestParam Integer id){
        return Result.data(resourcesService.getContentByResourceId(id));
    }


//    @PostMapping("/uploadFile")
//    public Result<Void> uploadFile (String path,String desc){
//
//    }
//    @PostMapping("/createFile")
//    public Result<Void> createFile (String path,String desc,String content){
//
//    }
}
