package com.dlink.daemon.task;

import com.dlink.daemon.constant.FlinkTaskConstant;
import com.dlink.model.JobStatus;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

@Data
public class FlinkDaemonTask {
    private static final Logger log = LoggerFactory.getLogger(FlinkDaemonTask.class);

    public static Random random = new Random(5);
    private String id;
    private JobStatus status;
    private long preDealTime;
    private int count;


//    @Override
    public DaemonTask setConfig(DaemonTaskConfig config) {
        return null;
    }

//    @Override
    public String getType() {
        return null;
    }


//    @Override
    public void dealTask() {
        long gap = 0;
        if (this.preDealTime != 0L) {
            gap = System.currentTimeMillis() - this.preDealTime;
        }
        preDealTime = System.currentTimeMillis();

        int i = random.nextInt(10);
        if(i > 5){
            log.info("deal FlinkTask id:" + id + " status: finished count:"+ count + " gap:"+ gap + "ms");
        }else {
            log.info("deal FlinkTask id:" + id + " status: running count:" +count + " gap:"+ gap + "ms");
            //加入等待下次检测
//            DefaultThreadPool.getInstance().execute(this);
        }
        count++;
        if(gap < FlinkTaskConstant.TIME_SLEEP){
            try {
                Thread.sleep(FlinkTaskConstant.TIME_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public FlinkDaemonTask() {

    }

    public FlinkDaemonTask(String id) {
        this.id = id;
    }

    public FlinkDaemonTask(String id, JobStatus status) {
        this.id = id;
        this.status = status;
    }

}
