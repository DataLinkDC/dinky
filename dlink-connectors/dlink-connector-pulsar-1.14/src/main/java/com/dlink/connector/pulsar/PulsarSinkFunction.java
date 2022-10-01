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

package com.dlink.connector.pulsar;

import com.dlink.connector.pulsar.util.PulsarConnectionHolder;
import com.dlink.connector.pulsar.util.PulsarProducerHolder;
import com.dlink.utils.JSONUtil;

import org.apache.flink.annotation.Internal;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.runtime.state.FunctionSnapshotContext;
import org.apache.flink.streaming.api.checkpoint.CheckpointedFunction;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.SerializableObject;
import org.apache.pulsar.PulsarVersion;
import org.apache.pulsar.client.api.ClientBuilder;
import org.apache.pulsar.client.api.CompressionType;
import org.apache.pulsar.client.api.HashingScheme;
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.ProducerBuilder;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.TypedMessageBuilder;
import org.apache.pulsar.client.impl.PulsarClientImpl;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * The sink function for Pulsar.
 *
 * @author DarrenDa
 * * @version 1.0
 * * @Desc:
 */
@Internal
public class PulsarSinkFunction<T> extends RichSinkFunction<T>
    implements CheckpointedFunction {

    private static final long serialVersionUID = 1L;
    private final Logger log = LoggerFactory.getLogger(PulsarSinkFunction.class);

    private final String topic;
    private final String serviceUrl;
    private final Properties pulsarProducerProperties;
    private final Properties pulsarClientProperties;
    private SerializationSchema<T> runtimeEncoder;
    private transient Producer producer;
    private transient volatile boolean closed = false;

    /**
     * Flag indicating whether to accept failures (and log them), or to fail on failures. Default is False.
     */
    protected boolean logFailuresOnly;

    /**
     * If true, the producer will wait until all outstanding records have been send to the broker. Default is True.
     */
    protected boolean flushOnCheckpoint = true;

    /**
     * The callback than handles error propagation or logging callbacks.
     */
    protected transient BiConsumer<MessageId, Throwable> sendCallback;

    /**
     * Errors encountered in the async producer are stored here.
     */
    protected transient volatile Exception asyncException;

    /**
     * Lock for accessing the pending records.
     */
    protected final SerializableObject pendingRecordsLock = new SerializableObject();

    /**
     * Number of unacknowledged records.
     */
    protected long pendingRecords;

    public PulsarSinkFunction(
        String topic,
        String serviceUrl,
        Properties pulsarProducerProperties,
        Properties pulsarClientProperties,
        SerializationSchema<T> runtimeEncoder
    ) {
        this.topic = topic;
        this.serviceUrl = serviceUrl;
        this.pulsarProducerProperties = pulsarProducerProperties;
        this.pulsarClientProperties = pulsarClientProperties;
        this.runtimeEncoder = runtimeEncoder;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        log.info("start open ...");
        try {
            RuntimeContext ctx = getRuntimeContext();

            log.info("Starting FlinkPulsarProducer ({}/{}) to produce into (※) pulsar topic {}",
                ctx.getIndexOfThisSubtask() + 1, ctx.getNumberOfParallelSubtasks(), topic);

            this.producer = createReusedProducer();
            log.info("Pulsar producer has been created.");

        } catch (IOException ioe) {
            log.error("Exception while creating connection to Pulsar.", ioe);
            throw new RuntimeException("Cannot create connection to Pulsar.", ioe);
        } catch (Exception ex) {
            log.error("Exception while creating connection to Pulsar.", ex);
            throw new RuntimeException("Cannot create connection to Pulsar.", ex);
        }

        if (flushOnCheckpoint
            && !((StreamingRuntimeContext) this.getRuntimeContext()).isCheckpointingEnabled()) {
            log.warn("Flushing on checkpoint is enabled, but checkpointing is not enabled. Disabling flushing.");
            flushOnCheckpoint = false;
        }

        if (logFailuresOnly) {
            this.sendCallback =
                (t, u) -> {
                    if (u != null) {
                        log.error("Error while sending message to Pulsar: {}", ExceptionUtils.stringifyException(u));
                    }
                    acknowledgeMessage();
                };
        } else {
            this.sendCallback =
                (t, u) -> {
                    if (asyncException == null && u != null) {
                        asyncException = new Exception(u);
                    }
                    acknowledgeMessage();
                };
        }
        log.info("end open.");
    }

    @Override
    public void invoke(T value, Context context) throws Exception {
        log.info("start to invoke, send pular message.");

        //propagate asynchronous errors
        checkErroneous();

        byte[] serializeValue = runtimeEncoder.serialize(value);
        String strValue = new String(serializeValue);
        TypedMessageBuilder<byte[]> typedMessageBuilder = producer.newMessage();
        typedMessageBuilder.value(serializeValue);
        typedMessageBuilder.key(getKey(strValue));

        if (flushOnCheckpoint) {
            synchronized (pendingRecordsLock) {
                pendingRecords++;
            }
        }

        //异步发送
        CompletableFuture<MessageId> messageIdCompletableFuture = typedMessageBuilder.sendAsync();
        messageIdCompletableFuture.whenComplete(sendCallback);
    }

    @Override
    public void close() throws Exception {
        //采用pulsar producer复用的方式，close方法不要具体实现，否则producer会被关闭
        log.error("PulsarProducerBase Class close function called");
        checkErroneous();
    }

    @Override
    public void snapshotState(FunctionSnapshotContext context) throws Exception {
        if (flushOnCheckpoint) {
            synchronized (pendingRecordsLock) {
                if (pendingRecords != 0) {
                    try {
                        log.info("等待notify");
                        pendingRecordsLock.wait();
                        checkErroneous();
                        flush();
                        log.info("等待waite之后");
                    } catch (InterruptedException e) {
                        //this can be interrupted when the Task has been cancelled.
                        //by throwing an exception, we ensure that this checkpoint doesn't get
                        //confirmed
                        throw new IllegalStateException("Flushing got interrupted while checkpointing", e);
                    }
                }
            }
        }
    }

    @Override
    public void initializeState(FunctionInitializationContext context) throws Exception {
        //nothing to do.
    }

    public String getKey(String strValue) {
        //JSONObject jsonObject = JSONObject.parseObject(strValue);
        //JSONObject jsonObject = JSONUtil.parseObject(strValue);
        //String key = jsonObject.getString("key");
        ObjectNode jsonNodes = JSONUtil.parseObject(strValue);
        String key = String.valueOf(jsonNodes.get("key"));
        return key == null ? "" : key;
    }

    //获取Pulsar Producer
    public Producer createProducer() throws Exception {
        log.info("current pulsar version is {}", PulsarVersion.getVersion());

        ClientBuilder builder = PulsarClient.builder();
        ProducerBuilder producerBuilder = builder.serviceUrl(serviceUrl)
            .maxNumberOfRejectedRequestPerConnection(50)
            .loadConf((Map) pulsarClientProperties)
            .build()
            .newProducer()
            .topic(topic)
            .blockIfQueueFull(Boolean.TRUE)
            .compressionType(CompressionType.LZ4)
            .hashingScheme(HashingScheme.JavaStringHash)
            //.batchingMaxPublishDelay(100, TimeUnit.MILLISECONDS)
            .loadConf((Map) pulsarProducerProperties);//实现配置透传功能
        Producer producer = producerBuilder.create();
        return producer;
    }

    //获取复用的Pulsar Producer
    public Producer createReusedProducer() throws Exception {
        log.info("now create client, serviceUrl is : {}", serviceUrl);
        PulsarClientImpl client = PulsarConnectionHolder.getProducerClient(serviceUrl, pulsarClientProperties);

        log.info("current pulsar version is {} , topic is : {}", PulsarVersion.getVersion(), topic);

        return PulsarProducerHolder.getProducer(topic, pulsarProducerProperties, client);
    }

    /**
     * Defines whether the producer should fail on errors, or only log them. If this is set to true,
     * then exceptions will be only logged, if set to false, exceptions will be eventually thrown
     * and cause the streaming program to fail (and enter recovery).
     *
     * @param logFailuresOnly The flag to indicate logging-only on exceptions.
     */
    public void setLogFailuresOnly(boolean logFailuresOnly) {
        this.logFailuresOnly = logFailuresOnly;
    }

    /**
     * If set to true, the Flink producer will wait for all outstanding messages in the Pulsar
     * buffers to be acknowledged by the Pulsar producer on a checkpoint. This way, the producer can
     * guarantee that messages in the Pulsar buffers are part of the checkpoint.
     *
     * @param flush Flag indicating the flushing mode (true = flush on checkpoint)
     */
    public void setFlushOnCheckpoint(boolean flush) {
        this.flushOnCheckpoint = flush;
    }

    protected void checkErroneous() throws Exception {
        Exception e = asyncException;
        if (e != null) {
            //prevent double throwing
            asyncException = null;
            throw new Exception("Failed to send data to Pulsar: " + e.getMessage(), e);
        }
    }

    private void acknowledgeMessage() {
        if (flushOnCheckpoint) {
            synchronized (pendingRecordsLock) {
                log.info("pendingRecords：{}", pendingRecords);
                pendingRecords--;
                if (pendingRecords == 0) {
                    pendingRecordsLock.notifyAll();
                    log.info("notify完成");
                }
            }
        }
    }

    /**
     * Flush pending records.
     */
    protected void flush() throws Exception {
        producer.flush();
    }

}
