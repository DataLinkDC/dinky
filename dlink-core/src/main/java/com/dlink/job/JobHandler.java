package com.dlink.job;

import com.dlink.exception.JobException;

import java.util.ServiceLoader;

/**
 * jobHandler
 *
 * @author wenmo
 * @since 2021/6/26 23:22
 */
public interface JobHandler  {
    boolean init();
    boolean ready();
    boolean running();
    boolean success();
    boolean failed();
    boolean callback();
    boolean close();
    static JobHandler build(){
        ServiceLoader<JobHandler> jobHandlers = ServiceLoader.load(JobHandler.class);
        for(JobHandler jobHandler : jobHandlers){
            return jobHandler;
        }
        throw new JobException("There is no corresponding implementation class for this interface!");
    }
}
