package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.model.Task;
import com.dlink.result.RunResult;
import com.dlink.service.StudioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * StudioController
 *
 * @author wenmo
 * @since 2021/5/30 11:05
 */
@Slf4j
@RestController
@RequestMapping("/api/studio")
public class StudioController {

    @Autowired
    private StudioService studioService;

    /**
     * 执行Sql
     */
    @PostMapping("/executeSql")
    public Result executeSql(@RequestBody StudioExecuteDTO studioExecuteDTO) throws Exception {
        RunResult runResult = studioService.executeSql(studioExecuteDTO);
        return Result.succeed(runResult,"执行成功");
    }
}
