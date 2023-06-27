package org.dinky.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Opt;
import lombok.RequiredArgsConstructor;
import org.dinky.data.annotation.PublicInterface;
import org.dinky.service.MonitorService;
import org.dinky.sse.SseEmitterUTF8;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseController {
    private final MonitorService monitorService;
    @GetMapping(value = "/getLastUpdateData", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @CrossOrigin("*")
    @PublicInterface
    public SseEmitter getLastUpdateData(Long lastTime) {
        SseEmitter emitter = new SseEmitterUTF8(TimeUnit.MINUTES.toMillis(30));
        return monitorService.sendLatestData(
                emitter, DateUtil.date(Opt.ofNullable(lastTime).orElse(DateUtil.date().getTime())));
    }
}
