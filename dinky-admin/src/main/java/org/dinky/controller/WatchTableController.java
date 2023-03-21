package org.dinky.controller;

import lombok.AllArgsConstructor;
import org.dinky.service.WatchTableService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@AllArgsConstructor
public class WatchTableController {

    private final WatchTableService watchTableService;

    @MessageMapping("/broadcast")
    public String broadcast(String message) {
        return "back" + message;
    }

    @SubscribeMapping("/subscribe/{id}/{table}")
    public String subscribe(@DestinationVariable Integer id, @DestinationVariable String table) {
        watchTableService.registerListenEntry(id, table);
        return "successful";
    }

    @SubscribeMapping("/unsubscribe/{id}/{table}")
    public String unsubscribe(@DestinationVariable Integer id, @DestinationVariable String table) {
        watchTableService.unRegisterListenEntry(id, table);
        return "successful";
    }

    @MessageMapping("/one")
    public void one(String message, Principal principal) {
    }

}
