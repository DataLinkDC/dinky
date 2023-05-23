package com.zdpx.coder.operator.operators;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JoinOperatorTest {

    @Test
    void execute() {
        //<editor-fold desc="cep input string">
        String cep = "{\n" +
                "        \"activated\": true,\n" +
                "        \"id\": \"cepOperator\",\n" +
                "        \"code\": \"CepOperator\",\n" +
                "        \"parallelism\": 1,\n" +
                "        \"compatibility\": 1.0,\n" +
                "        \"expanded\": true,\n" +
                "        \"name\": \"CepOperator\",\n" +
                "        \"origin\": null,\n" +
                "        \"height\": 150,\n" +
                "        \"width\": 90,\n" +
                "        \"x\": 1,\n" +
                "        \"y\": 2,\n" +
                "        \"parameters\": [\n" +
                "          {\n" +
                "            \"partition\": \"taskId\",\n" +
                "            \"orderBy\": \"gbu_time\",\n" +
                "            \"patterns\": [\n" +
                "              {\n" +
                "                \"variable\": \"A\",\n" +
                "                \"quantifier\": \"{5}\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"skipStrategy\": {\n" +
                "              \"strategy\": \"LAST_ROW\",\n" +
                "              \"variable\": \"A\"\n" +
                "            },\n" +
                "            \"measures\": [\n" +
                "              {\n" +
                "                \"outName\": \"startTaskStatus\",\n" +
                "                \"functionName\": \"FIRST\",\n" +
                "                \"parameters\": [\n" +
                "                  \"A.taskStatus\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"outName\": \"endTaskStatus\",\n" +
                "                \"functionName\": \"LAST\",\n" +
                "                \"parameters\": [\n" +
                "                  \"A.taskStatus\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"outName\": \"gbuStartTime\",\n" +
                "                \"functionName\": \"FIRST\",\n" +
                "                \"parameters\": [\n" +
                "                  \"A.gbu_time\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"outName\": \"gbuEndTime\",\n" +
                "                \"functionName\": \"LAST\",\n" +
                "                \"parameters\": [\n" +
                "                  \"A.gbu_time\"\n" +
                "                ]\n" +
                "              },\n" +
                "              {\n" +
                "                \"outName\": \"va\",\n" +
                "                \"functionName\": \"LAST\",\n" +
                "                \"parameters\": [\n" +
                "                  \"A.va\"\n" +
                "                ]\n" +
                "              }\n" +
                "            ],\n" +
                "            \"defines\": [\n" +
                "              {\n" +
                "                \"variable\": \"A\",\n" +
                "                \"condition\": \"A.taskStatus = 0\"\n" +
                "              }\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      ";
        //</editor-fold>
        CepOperator cepOperator = new CepOperator();
        cepOperator.initialize();
    }
}