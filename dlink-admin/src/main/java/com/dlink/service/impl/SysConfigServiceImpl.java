package com.dlink.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dlink.assertion.Asserts;
import com.dlink.db.service.impl.SuperServiceImpl;
import com.dlink.mapper.SysConfigMapper;
import com.dlink.model.SysConfig;
import com.dlink.model.SystemConfiguration;
import com.dlink.service.SysConfigService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SysConfigServiceImpl
 *
 * @author wenmo
 * @since 2021/11/18
 **/
@Service
public class SysConfigServiceImpl extends SuperServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void initSysConfig() {
        List<SysConfig> sysConfigs = list();
        if(sysConfigs.size()==0){
            return;
        }
        Map<String,String> map = new HashMap<>();
        for(SysConfig item : sysConfigs){
            map.put(item.getName(),item.getValue());
        }
        SystemConfiguration.getInstances().setConfiguration(mapper.valueToTree(map));
    }

    @Override
    public void updateSysConfigByJson(JsonNode node) {
        if (node!=null&&node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String name = entry.getKey();
                String value = entry.getValue().asText();
                SysConfig config = getOne(new QueryWrapper<SysConfig>().eq("name", name));
                SysConfig newConfig = new SysConfig();
                newConfig.setValue(value);
                if(Asserts.isNull(config)){
                    newConfig.setName(name);
                    save(newConfig);
                }else {
                    newConfig.setId(config.getId());
                    updateById(newConfig);
                }
            }
        }
        SystemConfiguration.getInstances().setConfiguration(node);
    }
}
