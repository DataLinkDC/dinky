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

package com.dlink.explainer.trans;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * AbstractTrans
 *
 * @author wenmo
 * @since 2021/6/22
 **/
public abstract class AbstractTrans {
    protected Integer id;
    protected Integer parentId;
    protected String name;
    protected String type;
    protected String text;
    protected Integer stage;
    protected String contents;
    protected String pact;
    protected Integer parallelism;
    protected List<Predecessor> predecessors;

    public void build(JsonNode node) {
        id = node.get("id").asInt();
        text = node.toPrettyString();
        stage = id;
        type = matchType(node.get("type").asText());
        name = matchType(node.get("type").asText());
        pact = node.get("pact").asText();
        contents = matchContents(node.get("contents").asText());
        translate();
        parallelism = node.get("parallelism").asInt();
        predecessors = new ArrayList<>();
        if (node.has("predecessors")) {
            JsonNode predecessornodes = node.get("predecessors");
            for (JsonNode predecessor : predecessornodes) {
                predecessors.add(new Predecessor(predecessor.get("id").asInt(), predecessor.get("ship_strategy").asText(), predecessor.get("side").asText()));
            }
        }
    }

    abstract void translate();

    public static String matchType(String str) {
        Pattern p = Pattern.compile("(.*?)\\(");
        Matcher m = p.matcher(str);
        String type = null;
        if (m.find()) {
            type = m.group(0).replaceAll("\\(", "").trim();
        } else {
            type = str;
        }
        return type;
    }

    public static String matchPact(String str) {
        Pattern p = Pattern.compile(": (.*?)$");
        Matcher m = p.matcher(str);
        String pact = null;
        if (m.find()) {
            pact = m.group(0).replaceAll(": ", "").trim();
        } else {
            pact = str;
        }
        return pact;
    }

    public static String matchContents(String str) {
        Pattern p = Pattern.compile("\\((.*?)$");
        Matcher m = p.matcher(str);
        String contents = null;
        if (m.find()) {
            contents = m.group(0).replaceFirst("\\(", "").trim();
            contents = contents.substring(0, contents.lastIndexOf(")"));
        } else {
            contents = str;
        }
        return contents;
    }

    public static String matchStage(String str) {
        Pattern p = Pattern.compile("Stage (.*?) :");
        Matcher m = p.matcher(str);
        String type = null;
        if (m.find()) {
            type = m.group(0).replaceFirst("Stage ", "").replaceFirst(" :", "").trim();
        }
        return type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getStage() {
        return stage;
    }

    public void setStage(Integer stage) {
        this.stage = stage;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getPact() {
        return pact;
    }

    public void setPact(String pact) {
        this.pact = pact;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public List<Predecessor> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<Predecessor> predecessors) {
        this.predecessors = predecessors;
    }
}
