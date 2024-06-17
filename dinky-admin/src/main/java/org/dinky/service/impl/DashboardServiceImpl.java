package org.dinky.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dinky.data.model.Dashboard;
import org.dinky.mapper.DashboardMapper;
import org.dinky.service.DashboardService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl extends ServiceImpl<DashboardMapper, Dashboard> implements DashboardService {
    @Override
    public boolean saveOrUpdate(Dashboard entity) {
        // 更新和保存，保证name不重复
        if (entity.getId() == null) {
            Dashboard dashboard = getOne(Wrappers.<Dashboard>lambdaQuery().eq(Dashboard::getName, entity.getName()));
            if (dashboard != null) {
                throw new RuntimeException("name already exists");
            }
        } else {
            Dashboard dashboard = getOne(Wrappers.<Dashboard>lambdaQuery().eq(Dashboard::getName, entity.getName()));
            if (dashboard != null && !dashboard.getId().equals(entity.getId())) {
                throw new RuntimeException("name already exists");
            }
        }
        return super.saveOrUpdate(entity);
    }
}
