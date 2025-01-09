package org.dinky.connector.mock.source;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.functions.OpenContext;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.table.data.RowData;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dinky.assertion.Asserts;
import org.dinky.data.exception.DinkyException;
import org.dinky.utils.JsonUtils;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class MockSourceFunction extends RichSourceFunction<RowData> {

    private final String hostname;

    private final Integer taskId;
    private final String tableName;
    private final String port;
    private final DeserializationSchema<RowData> deserializer;
    private final List<RowData> testCaseRowData;
    private final String TEST_CASE_OPEN_API_TEMPLATE = "http://{0}:{1}/openapi/getTableTestCase?taskId={2}&tableName={3}";


    public MockSourceFunction(String hostname,
                              String port,
                              Integer taskId,
                              String tableName,
                              DeserializationSchema<RowData> deserializer) {
        this.hostname = hostname;
        this.port = port;
        this.taskId = taskId;
        this.tableName = tableName;
        this.deserializer = deserializer;
        this.testCaseRowData = new LinkedList<>();
    }

    @Override
    public void open(OpenContext parameters) throws Exception {
        super.open(parameters);
        // generate test case for current table by open api
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(MessageFormat.format(TEST_CASE_OPEN_API_TEMPLATE, hostname, port, taskId, tableName));
        // http execute
        CloseableHttpResponse response = httpClient.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode();
        HttpEntity entity = response.getEntity();
        //check response
        if (statusCode == 200 && Asserts.isNotNull(entity)) {
            ObjectNode jsonNodes = JsonUtils.parseObject(EntityUtils.toString(entity));
            ArrayNode arrayNode = (ArrayNode) jsonNodes.get("data").get("rowData");
            for (JsonNode jsonNode : arrayNode) {
                //deserialize test case
                testCaseRowData.add(deserializer.deserialize(jsonNode.toString().getBytes()));
            }
        } else {
            throw new DinkyException("Get test case for table " + tableName + " failed, response " + response);
        }
    }


    @Override
    public void run(SourceContext<RowData> sourceContext) throws Exception {
        testCaseRowData.forEach(sourceContext::collect);
    }

    @Override
    public void cancel() {
        // do nothing
    }
}
