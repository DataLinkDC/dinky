package org.dinky.ws.handler;

import java.util.HashMap;
import java.util.Map;

public abstract class ManualMessageEventHandler extends WsBaseMessageEventHandler {
    @Override
    public Map<String, Object> autoMessageSend() {
        return new HashMap<>();
    }



    @Override
    public void run() {

    }
}
