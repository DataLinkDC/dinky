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

package org.dinky.configure.schedule.Alert;

import org.dinky.alert.Alert;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;
import org.dinky.alert.Rules.CheckpointsRule;
import org.dinky.alert.Rules.ExceptionRule;
import org.dinky.assertion.Asserts;
import org.dinky.configure.schedule.BaseSchedule;
import org.dinky.context.FreeMarkerHolder;
import org.dinky.context.TenantContextHolder;
import org.dinky.data.dto.AlertRuleDTO;
import org.dinky.data.enums.Status;
import org.dinky.data.model.AlertGroup;
import org.dinky.data.model.AlertHistory;
import org.dinky.data.model.AlertInstance;
import org.dinky.data.model.JobInfoDetail;
import org.dinky.data.model.JobInstance;
import org.dinky.data.model.Task;
import org.dinky.job.FlinkJobTaskPool;
import org.dinky.service.impl.AlertGroupServiceImpl;
import org.dinky.service.impl.AlertHistoryServiceImpl;
import org.dinky.service.impl.AlertRuleServiceImpl;
import org.dinky.service.impl.TaskServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.spel.SpELCondition;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import freemarker.template.TemplateException;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class, JobAlerts, is responsible for scheduling and executing job alerts.
 * It checks for rule conditions and triggers alerts based on those conditions.
 */
@Configurable
@Component
@RequiredArgsConstructor
@Slf4j
public class JobAlerts extends BaseSchedule {

    /**
     * Service for managing alert history.
     */
    private final AlertHistoryServiceImpl alertHistoryService;

    /**
     * Service for managing alert groups.
     */
    private final AlertGroupServiceImpl alertGroupService;

    /**
     * Service for managing tasks.
     */
    private final TaskServiceImpl taskService;

    /**
     * Service for managing alert rules.
     */
    private final AlertRuleServiceImpl alertRuleService;

    /**
     * The pool containing Flink job tasks.
     * // TODO 任务刷新逻辑重购后记得修改这里逻辑
     */
    private final FlinkJobTaskPool taskPool = FlinkJobTaskPool.INSTANCE;

    /**
     * Rules for evaluating alert conditions.
     */
    private Rules rules;

    /**
     * Engine for evaluating rules.
     */
    private RulesEngine rulesEngine;

    /**
     * Holder for FreeMarker templates.
     */
    private FreeMarkerHolder freeMarkerHolder;

    private final Facts ruleFacts = new Facts();

    /**
     * Initializes the JobAlerts class by refreshing rules and setting up the scheduler.
     */
    @PostConstruct
    public void init() {
        refeshRulesData();
        addSchedule("JobAlert", this::check, new PeriodicTrigger(1000 * 30));
    }

    /**
     * checks for alert conditions for each job in the task pool.
     */
    protected void check() {
        TenantContextHolder.set(1);

        for (Map.Entry<String, JobInfoDetail> job : taskPool.entrySet()) {
            JobInfoDetail jobInfoDetail = job.getValue();
            String key = job.getKey();
            ruleFacts.put("", jobInfoDetail);
            ruleFacts.put("jobDetail", jobInfoDetail);
            ruleFacts.put("job", jobInfoDetail.getHistory());
            ruleFacts.put("key", key);
            ruleFacts.put("jobInstance", jobInfoDetail.getInstance());
            ruleFacts.put("checkPoints", jobInfoDetail.getJobHistory().getCheckpoints());
            ruleFacts.put("cluster", jobInfoDetail.getCluster());
            ruleFacts.put("exceptions", jobInfoDetail.getJobHistory().getExceptions());
            rulesEngine.fire(rules, ruleFacts);
        }
    }

    /**
     * Refreshes the alert rules and related data.
     */
    public void refeshRulesData() {

        ruleFacts.put("exceptionRule", new ExceptionRule());
        ruleFacts.put("checkpointRule", new CheckpointsRule());

        List<AlertRuleDTO> ruleDTOS = alertRuleService.getBaseMapper().selectWithTemplate();
        freeMarkerHolder = new FreeMarkerHolder();
        rulesEngine = new DefaultRulesEngine();
        rules = new Rules();

        ruleDTOS.forEach(ruleDto -> {
            if (ruleDto.getTemplateName() != null && !ruleDto.getTemplateName().isEmpty()) {
                freeMarkerHolder.putTemplate(ruleDto.getTemplateName(), ruleDto.getTemplateContent());
                ruleDto.setName(Status.findMessageByKey(ruleDto.getName()));
                ruleDto.setDescription(Status.findMessageByKey(ruleDto.getDescription()));
                rules.register(buildRule(ruleDto));
            } else {
                log.error("Alert Rule: {} has no template", ruleDto.getName());
            }
        });
    }

    private Rule buildRule(AlertRuleDTO alertRuleDTO) {

        List<JSONObject> ruleItemList =
                JSONUtil.parseArray(alertRuleDTO.getRule()).toBean(List.class);
        List<String> conditionList = ruleItemList.stream()
                .map(r -> r.toBean(RuleItem.class).toString())
                .collect(Collectors.toList());
        String conditionContent = String.join(alertRuleDTO.getTriggerConditions(), conditionList);
        String condition = StrFormatter.format("#{{}}", conditionContent);

        log.info("Build Alert Rule: {}", condition);

        return new RuleBuilder()
                .name(alertRuleDTO.getName())
                .description(alertRuleDTO.getDescription())
                .priority(1)
                .when(new SpELCondition(condition))
                .then(f -> executeAlertAction(f, alertRuleDTO))
                .build();
    }

    /**
     * Executes the alert action when an alert condition is met.
     *
     * @param facts        The facts representing the job details.
     * @param alertRuleDTO Alert Rule Info.
     */
    private void executeAlertAction(Facts facts, AlertRuleDTO alertRuleDTO) {
        JobInfoDetail jobInfoDetail = facts.get("jobDetail");
        JobInstance jobInstance = jobInfoDetail.getInstance();
        Task task = taskService.getById(jobInfoDetail.getInstance().getTaskId());

        Map<String, Object> dataModel = new HashMap<>(facts.asMap());
        dataModel.put("task", task);
        dataModel.put("rule", alertRuleDTO);

        String alertContent;

        try {
            alertContent = freeMarkerHolder.buildWithData(alertRuleDTO.getTemplateName(), dataModel);
        } catch (IOException | TemplateException e) {
            log.error("Alert Error: ", e);
            return;
        }

        if (!Asserts.isNull(task.getAlertGroupId())) {
            AlertGroup alertGroup = alertGroupService.getAlertGroupInfo(task.getAlertGroupId());
            if (Asserts.isNotNull(alertGroup)) {
                for (AlertInstance alertInstance : alertGroup.getInstances()) {
                    if (alertInstance == null || !alertInstance.getEnabled()) {
                        continue;
                    }
                    sendAlert(
                            alertInstance,
                            jobInstance.getId(),
                            alertGroup.getId(),
                            alertRuleDTO.getName(),
                            alertContent);
                }
            }
        }
    }

    /**
     * Sends an alert based on the alert instance's configuration.
     *
     * @param alertInstance The alert instance to use for sending the alert.
     * @param jobInstanceId The ID of the job instance triggering the alert.
     * @param alertGid      The ID of the alert group.
     * @param title         The title of the alert.
     * @param alertMsg      The content of the alert message.
     */
    private void sendAlert(
            AlertInstance alertInstance, int jobInstanceId, int alertGid, String title, String alertMsg) {
        Map<String, String> params = org.dinky.utils.JSONUtil.toMap(alertInstance.getParams());
        AlertConfig alertConfig = AlertConfig.build(alertInstance.getName(), alertInstance.getType(), params);
        Alert alert = Alert.build(alertConfig);
        AlertResult alertResult = alert.send(title, alertMsg);

        AlertHistory alertHistory = new AlertHistory();
        alertHistory.setAlertGroupId(alertGid);
        alertHistory.setJobInstanceId(jobInstanceId);
        alertHistory.setTitle(title);
        alertHistory.setContent(alertMsg);
        alertHistory.setStatus(alertResult.getSuccessCode());
        alertHistory.setLog(alertResult.getMessage());
        alertHistoryService.save(alertHistory);
    }

    @Data
    public static class RuleItem {
        private String ruleKey;
        private String ruleOperator;
        //        private int rulePriority;
        private String ruleValue;

        @Override
        public String toString() {
            return StrFormatter.format(" #{} {} {} ", getRuleKey(), getRuleOperator(), getRuleValue());
        }
    }
}
