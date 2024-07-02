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

package org.dinky.job.handler;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.compress.utils.Lists;
import org.dinky.alert.Alert;
import org.dinky.alert.AlertConfig;
import org.dinky.alert.AlertResult;
import org.dinky.alert.dingtalk.DingTalkConstants;
import org.dinky.alert.sms.SmsConstants;
import org.dinky.assertion.Asserts;
import org.dinky.context.FreeMarkerHolder;
import org.dinky.context.SpringContextUtils;
import org.dinky.daemon.pool.FlinkJobThreadPool;
import org.dinky.data.dto.AlertRuleDTO;
import org.dinky.data.dto.TaskDTO;
import org.dinky.data.enums.JobLifeCycle;
import org.dinky.data.enums.Status;
import org.dinky.data.enums.TaskOwnerAlertStrategyEnum;
import org.dinky.data.exception.DinkyException;
import org.dinky.data.model.Configuration;
import org.dinky.data.model.SystemConfiguration;
import org.dinky.data.model.alert.AlertGroup;
import org.dinky.data.model.alert.AlertHistory;
import org.dinky.data.model.alert.AlertInstance;
import org.dinky.data.model.ext.JobAlertData;
import org.dinky.data.model.ext.JobInfoDetail;
import org.dinky.data.model.rbac.User;
import org.dinky.data.options.JobAlertRuleOptions;
import org.dinky.service.AlertHistoryService;
import org.dinky.service.TaskService;
import org.dinky.service.UserService;
import org.dinky.service.impl.AlertRuleServiceImpl;
import org.dinky.utils.JsonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.RuleBuilder;
import org.jeasy.rules.spel.SpELCondition;
import org.springframework.context.annotation.DependsOn;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@DependsOn("springContextUtils")
public class JobAlertHandler {

    private static final AlertHistoryService alertHistoryService;
    private static final TaskService taskService;
    private static final AlertRuleServiceImpl alertRuleService;
    private static final SystemConfiguration systemConfiguration = SystemConfiguration.getInstances();
    private static final UserService userService;

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

    /**
     * 缓存告警发送记录，用于防止单位时间内频繁发送重复告警 key为任务实例id，
     * value为告警发送记录map，key为告警id，value为触发次数 |
     * Cache alert sending records to prevent frequent sending of duplicate alerts within a unit time.
     * The key is the task instance ID, the value is the alert sending record map,
     * the key is the alert ID, and the value is the number of triggers
     */
    private static LoadingCache<Integer, Map<Integer, Integer>> alertCache;

    private static LoadingCache<Integer, User> userCache;

    private static volatile JobAlertHandler defaultJobAlertHandler;

    static {
        taskService = SpringContextUtils.getBean("taskServiceImpl", TaskService.class);
        alertHistoryService = SpringContextUtils.getBean("alertHistoryServiceImpl", AlertHistoryService.class);
        alertRuleService = SpringContextUtils.getBean("alertRuleServiceImpl", AlertRuleServiceImpl.class);
        userService = SpringContextUtils.getBean("userServiceImpl", UserService.class);

        Configuration<Integer> jobReSendDiffSecond = systemConfiguration.getJobReSendDiffSecond();
        jobReSendDiffSecond.addChangeEvent((c) -> {
            alertCache = CacheBuilder.newBuilder()
                    // 与expireAfterAccess不同，写入后定时过期 | Different from expireAfterAccess, it expires after writing
                    .expireAfterWrite(c, TimeUnit.SECONDS)
                    .build(CacheLoader.from(() -> new HashMap<>()));
        });
        jobReSendDiffSecond.runChangeEvent();

        userCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<Integer, User>() {
                    @Override
                    public User load(Integer id) { // no checked exception
                        return userService.getById(id);
                    }
                });
    }

    public static JobAlertHandler getInstance() {
        if (defaultJobAlertHandler == null) {
            synchronized (FlinkJobThreadPool.class) {
                if (defaultJobAlertHandler == null) {
                    defaultJobAlertHandler = new JobAlertHandler();
                }
            }
        }
        return defaultJobAlertHandler;
    }

    public JobAlertHandler() {
        refreshRulesData();
    }

    /**
     * checks for alert conditions for each job in the task pool.
     */
    public void check(JobInfoDetail jobInfoDetail) {
        Facts ruleFacts = new Facts();
        JobAlertData jobAlertData = JobAlertData.buildData(jobInfoDetail);
        JsonUtils.toMap(jobAlertData).forEach((k, v) -> {
            if (v == null) {
                throw new DinkyException(StrFormatter.format(
                        "When deal alert job data, the key [{}] value is null, its maybe dinky bug,please report", k));
            }
            ruleFacts.put(k, v);
        });
        rulesEngine.fire(rules, ruleFacts);
    }

    /**
     * Refreshes the alert rules and related data.
     */
    public void refreshRulesData() {
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
    private void executeAlertAction(Facts facts, AlertRuleDTO alertRuleDTO) throws Exception {
        int jobInstanceId = facts.get(JobAlertRuleOptions.FIELD_JOB_INSTANCE_ID);
        int taskId = facts.get(JobAlertRuleOptions.FIELD_TASK_ID);

        // 进行是否需要告警判断 | Determine whether an alert is required
        Map<Integer, Integer> map = alertCache.get(jobInstanceId);
        Integer ruleId = alertRuleDTO.getId();
        if (!map.containsKey(ruleId)) {
            // 初始化触发次数 | Initialize the number of triggers
            map.put(ruleId, 1);
        }
        // 触发次数+1 | Trigger count +1
        Integer maxSendCount = systemConfiguration.getDiffMinuteMaxSendCount().getValue();
        // 判断是否超过最大发送次数，超过则不再发送，等待缓存过期 | Determine whether the maximum number of sends has been exceeded,
        if (map.get(ruleId) > maxSendCount) {
            return;
        }
        map.put(ruleId, map.get(ruleId) + 1);

        TaskDTO task = taskService.getTaskInfoById(taskId);
        if (!Objects.equals(task.getStep(), JobLifeCycle.PUBLISH.getValue())) {
            // Only publish job can be alerted
            return;
        }
        Map<String, Object> dataModel = new HashMap<>(facts.asMap());
        dataModel.put(JobAlertRuleOptions.OPTIONS_JOB_ALERT_RULE, alertRuleDTO);
        String alertContent = freeMarkerHolder.buildWithData(alertRuleDTO.getTemplateName(), dataModel);

        if (!Asserts.isNull(task.getAlertGroup())) {
            //获取任务的责任人和维护人对应的用户信息|Get the responsible person and maintainer of the task
            User ownerInfo = userCache.get(task.getFirstLevelOwner());
            List<User> maintainerInfo = task.getSecondLevelOwners().stream().map(id -> {
                try {
                    return userCache.get(id);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
            AlertGroup alertGroup = task.getAlertGroup();
            alertGroup.getInstances().stream()
                    .filter(Objects::nonNull)
                    .filter(AlertInstance::getEnabled)
                    .forEach( alertInstance -> {
                        List<String> extraMobileList = Lists.newArrayList();
                        TaskOwnerAlertStrategyEnum value = SystemConfiguration.getInstances().getTaskOwnerAlertStrategy().getValue();
                        switch (value){
                            case OWNER:
                                if(ownerInfo!=null&&ownerInfo.getMobile()!=null){
                                    extraMobileList.add(ownerInfo.getMobile());
                                }
                                break;
                            case OWNER_AND_MAINTAINER:
                                if(ownerInfo!=null&&ownerInfo.getMobile()!=null){
                                    extraMobileList.add(ownerInfo.getMobile());
                                }
                                extraMobileList.addAll(maintainerInfo.stream().filter(user -> Objects.nonNull(user)&&StrUtil.isNotBlank(user.getMobile()))
                                        .map(User::getMobile)
                                        .collect(Collectors.toList()));
                                break;
                            case NONE:
                                break;
                            default:
                                log.error("Alert Strategy Type: {} is not supported",value);
                        }
                        //获取告警实例的配置参数|Get the configuration parameters of the alert instance
                        Map<String, Object> alertInstanceParams = alertInstance.getParams();
                        switch (alertInstance.getType()) {
                            case DingTalkConstants.TYPE:
                                Boolean atAll = (Boolean)alertInstanceParams.getOrDefault( DingTalkConstants.ALERT_TEMPLATE_AT_ALL,false);
                                if(!atAll){
                                    // 重新构告警实例的告警人员|Rebuild the alert personnel of the alert instance
                                    List<String> atMobiles = (List<String>) alertInstanceParams.get(DingTalkConstants.ALERT_TEMPLATE_AT_MOBILES);
                                    atMobiles.addAll(extraMobileList.stream().filter(mobile->!atMobiles.contains(mobile)).collect(Collectors.toList()));
                                    alertInstanceParams.put(DingTalkConstants.ALERT_TEMPLATE_AT_MOBILES, atMobiles);
                                    alertInstance.setParams(alertInstanceParams);
                                }
                                break;
                            case SmsConstants.TYPE:
                                // 重新构告警实例的告警人员|Rebuild the alert personnel of the alert instance
                                List<String> phoneNumbers = (List<String>) alertInstanceParams.get(SmsConstants.PHONE_NUMBERS);
                                phoneNumbers.addAll(extraMobileList.stream().filter(mobile->!phoneNumbers.contains(mobile)).collect(Collectors.toList()));
                                alertInstanceParams.put(DingTalkConstants.ALERT_TEMPLATE_AT_MOBILES, phoneNumbers);
                                alertInstance.setParams(alertInstanceParams);
                                break;
                            default:
                        }
                        sendAlert(
                                alertInstance, jobInstanceId, alertGroup.getId(), alertRuleDTO.getName(), alertContent);
                    });
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
        AlertConfig alertConfig =
                AlertConfig.build(alertInstance.getName(), alertInstance.getType(), alertInstance.getParams());
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
    private static class RuleItem {
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
