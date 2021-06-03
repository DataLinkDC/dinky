package com.dlink.service;

import com.dlink.dto.StudioDDLDTO;
import com.dlink.dto.StudioExecuteDTO;
import com.dlink.result.RunResult;

/**
 * StudioService
 *
 * @author wenmo
 * @since 2021/5/30 11:07
 */
public interface StudioService {
    RunResult executeSql(StudioExecuteDTO studioExecuteDTO);

    RunResult executeDDL(StudioDDLDTO studioDDLDTO);
}
