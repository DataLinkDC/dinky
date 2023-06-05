/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.dinky.sse;

import org.dinky.context.GitBuildContextHolder;
import org.dinky.data.model.GitProject;
import org.dinky.data.result.StepResult;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Opt;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author ZackYoung
 * @since 0.8.0
 */
@Slf4j
public abstract class StepSse {
    protected final String name;
    protected final int sleep;
    protected int step = 0;
    protected final List<SseEmitter> emitterList;
    protected final Dict params;
    protected final AtomicInteger msgId;
    protected final AtomicInteger stepAtomic;
    protected final ExecutorService cachedThreadPool;
    protected final LinkedList<String> msgList = new LinkedList<>();
    protected StepSse nexStepSse = null;

    /** 0-fail , 1-process , 2-success */
    protected int status = 0;

    public StepSse(
            String name,
            int sleep,
            List<SseEmitter> emitterList,
            Dict params,
            AtomicInteger msgId,
            AtomicInteger stepAtomic,
            ExecutorService cachedThreadPool) {
        this.name = name;
        this.sleep = sleep;
        this.emitterList = emitterList;
        this.params = params;
        this.msgId = msgId;
        this.stepAtomic = stepAtomic;
        this.cachedThreadPool = cachedThreadPool;
    }

    public abstract void exec();

    public synchronized void addMsg(String msg) {
        msgList.add(msg);
    }

    public synchronized void sendMsg(Object msg) {
        List<SseEmitter> loseLise = new ArrayList<>();
        Opt.ofEmptyAble(CollUtil.removeNull(emitterList))
                .ifPresent(
                        x -> {
                            x.forEach(
                                    emitter -> {
                                        try {
                                            emitter.send(
                                                    SseEmitter.event()
                                                            .id(
                                                                    String.valueOf(
                                                                            msgId
                                                                                    .getAndIncrement()))
                                                            .data(msg));
                                        } catch (IllegalStateException | IOException e) {
                                            loseLise.add(emitter);
                                        }
                                    });
                            emitterList.removeAll(loseLise);
                        });
    }

    public synchronized void addFileMsg(Object msg) {
        FileUtil.appendUtf8String(Convert.toStr(msg), getLogFile());
    }

    public synchronized void addFileMsgCusLog(String msg) {
        String content = "\n=============    " + Convert.toStr(msg) + "   =============\n";
        addMsg(content);
        FileUtil.appendUtf8String(content, getLogFile());
    }

    public synchronized void addFileMsgLog(String msg) {
        addMsg(msg);
        FileUtil.appendUtf8String(msg, getLogFile());
    }

    public synchronized void addFileLog(List<?> data) {
        sendMsg(getList(data));
        FileUtil.appendString(StrUtil.join("\n", data), getLogFile(), StandardCharsets.UTF_8);
    }

    protected File getLogFile() {
        File logDir = (File) params.get("logDir");
        return new File(logDir, getStep() + ".log");
    }

    public synchronized void sendSync() {
        if (CollUtil.isNotEmpty(msgList)) {
            sendMsg(getLogObj(msgList));
            msgList.clear();
        }
    }

    public void send() {
        cachedThreadPool.execute(this::sendSync);
    }

    public void main() {
        this.status = 1;
        getStep();
        FileUtil.del(getLogFile());
        FileUtil.touch(getLogFile());

        cachedThreadPool.execute(
                () -> {
                    while (status == 1) {
                        ThreadUtil.sleep(sleep);
                        send();
                    }
                });
        try {
            addFileMsgCusLog("step " + getStep() + ": " + name + " start");
            exec();
            setFinish(true);
        } catch (Exception e) {
            addFileMsgLog(ExceptionUtil.stacktraceToString(e));
            send();
            setFinish(false);
        }
    }

    public void setFinish(boolean status) {
        this.status = status ? 2 : 0;
        addFileMsgCusLog(
                "step " + getStep() + ": " + name + " " + (status ? "finished" : "failed"));

        sendSync();
        sendMsg(getEndLog());

        GitProject gitProject = (GitProject) params.get("gitProject");

        sendMsg(StepResult.genFinishInfo(getStep(), status ? 2 : 0, DateUtil.date()));
        if (!status) {
            gitProject.setBuildState(2);
            close();
        } else if (nexStepSse == null) {
            gitProject.setBuildState(3);
            close();
        }

        gitProject.setBuildStep(getStep());
        gitProject.updateById();

        if (status && nexStepSse != null) {
            gitProject.setBuildState(1);
            gitProject.setBuildStep(nexStepSse.getStep());
            gitProject.updateById();
            nexStepSse.main();
        }
    }

    private void close() {
        CollUtil.removeNull(emitterList)
                .forEach(
                        emitter -> {
                            try {
                                emitter.complete();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
        // Manual GC is required here to release file IO(此处需要手动GC，释放文件IO)
        GitProject gitProject = (GitProject) params.get("gitProject");

        GitBuildContextHolder.remove(gitProject.getId());
        System.gc();
    }

    public int getStep() {
        if (step != 0) {
            return step;
        }
        this.step = stepAtomic.getAndIncrement();
        return step;
    }

    public void setNexStepSse(StepSse nexStepSse) {
        this.nexStepSse = nexStepSse;
    }

    protected Dict getLogObj(Object data) {
        //        {
        //            "type":"2",
        //                "currentStep":1,
        //                "resultType":"1", 1日志 2list
        //            "data":"log",
        //                "status":1  # 2完成  1进行中 0失败
        //
        //        }

        Object dataResult =
                (data instanceof List) ? StrUtil.join("\n", data) : JSONUtil.toJsonStr(data);
        return Dict.create()
                .set("type", 1)
                .set("currentStep", getStep())
                .set("resultType", 1)
                .set("data", dataResult)
                .set("currentStepName", name)
                .set("status", status);
    }

    protected Dict getEndLog() {
        return Dict.create()
                .set("type", 1)
                .set("currentStep", getStep())
                .set("resultType", 1)
                .set("currentStepName", name)
                .set("status", status);
    }

    protected Dict getList(List<?> dataList) {
        return Dict.create()
                .set("type", 2)
                .set("currentStep", getStep())
                .set("resultType", 2)
                .set("data", dataList)
                .set("currentStepName", name)
                .set("status", status);
    }

    public void getStatus(int step, int status, List<Dict> data) {
        Dict result = new Dict().set("step", getStep()).set("name", name).set("status", -1);
        if (getStep() <= step) {
            Instant instant =
                    FileUtil.getAttributes(getLogFile().toPath(), true).creationTime().toInstant();

            result.set(
                            "startTime",
                            new DateTime(instant).toString(DatePattern.NORM_DATETIME_PATTERN))
                    .set("status", getStep() < step ? 2 : status);
        } else {
            result.set("startTime", null);
        }
        data.add(result);
        if (nexStepSse != null) {
            nexStepSse.getStatus(step, status, data);
        }
    }
}
