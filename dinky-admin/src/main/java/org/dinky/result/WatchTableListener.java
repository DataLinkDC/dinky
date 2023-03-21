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

package org.dinky.result;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;



@Slf4j
@Service
public class WatchTableListener extends Thread {

    private final SimpMessagingTemplate messagingTemplate;

    public static final int PORT = 7125;
    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[4096];

    public WatchTableListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        try {
            this.socket = new DatagramSocket(PORT);
            start();
        } catch (SocketException e) {
            log.error(e.getMessage());
        }
    }

    public void run() {
        running = true;
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                log.error(e.getMessage());
            }

            String received = new String(packet.getData(), 0, packet.getLength());
            this.messagingTemplate.convertAndSend("/topic/broadcast", received);
        }

        socket.close();
    }

    public void stopThread() {
        running = false;
    }
}
