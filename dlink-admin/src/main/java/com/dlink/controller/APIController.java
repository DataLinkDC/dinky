package com.dlink.controller;

import com.dlink.common.result.Result;
import com.dlink.dto.APIExecuteSqlDTO;
import com.dlink.job.JobResult;
import com.dlink.service.APIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * APIController
 *
 * @author wenmo
 * @since 2021/12/11 21:44
 */
@Slf4j
@RestController
@RequestMapping("/openapi")
public class APIController {

    @Autowired
    private APIService apiService;
    @PostMapping("/executeSql")

    public Result executeSql(@RequestBody APIExecuteSqlDTO apiExecuteSqlDTO)  {
        return Result.succeed(apiService.executeSql(apiExecuteSqlDTO),"执行成功");
    }
}
