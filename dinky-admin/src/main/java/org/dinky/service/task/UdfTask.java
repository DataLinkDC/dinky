package org.dinky.service.task;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.dinky.config.Dialect;
import org.dinky.data.annotation.SupportDialect;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.model.Task;
import org.dinky.job.JobResult;
import org.dinky.service.impl.TaskServiceImpl;
import org.dinky.utils.UDFUtils;

@SupportDialect({Dialect.JAVA,Dialect.PYTHON,Dialect.SCALA})
public class UdfTask extends BaseTask{
    public UdfTask(TaskDTO task) {
        super(task);
    }

    @Override
    public JobResult execute() throws Exception {
        UDFUtils.taskToUDF(BeanUtil.toBean(task, Task.class));
        return null;
    }

    @Override
    public boolean stop() {
        return false;
    }
}
