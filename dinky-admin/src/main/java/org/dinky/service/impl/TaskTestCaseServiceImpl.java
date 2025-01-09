package org.dinky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dinky.assertion.Asserts;
import org.dinky.data.dto.TaskTestCaseListDTO;
import org.dinky.data.model.TableTestCase;
import org.dinky.executor.ExecutorConfig;
import org.dinky.explainer.lineage.LineageBuilder;
import org.dinky.explainer.lineage.LineageColumn;
import org.dinky.explainer.lineage.LineageTable;
import org.dinky.mapper.TaskTestCaseMapper;
import org.dinky.mybatis.service.impl.SuperServiceImpl;
import org.dinky.service.TaskService;
import org.dinky.service.TaskTestCaseService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskTestCaseServiceImpl extends SuperServiceImpl<TaskTestCaseMapper, TableTestCase> implements TaskTestCaseService {

    private final TaskService taskService;

    @Override
    public TaskTestCaseListDTO getTestCaseBasedOnStatement(TaskTestCaseListDTO taskTestCaseListDTO) {
        // generate source tables and schemes
        List<LineageTable> tableschemeParsedFromstatementlist = LineageBuilder.getSourceTablesByLogicalPlan(taskTestCaseListDTO.getStatement(), ExecutorConfig.DEFAULT);

        // Merged test case list
        List<TableTestCase> mergedTestCaseList = new ArrayList<>();

        // get existed test cases from database
        List<TableTestCase> testCaseFromDbList = baseMapper.selectList(
                new LambdaQueryWrapper<TableTestCase>().eq(TableTestCase::getTaskId, taskTestCaseListDTO.getTaskId()));
        Map<String, TableTestCase> testCaseFromDbMap = new HashMap<>();
        testCaseFromDbList.forEach((testCase) -> testCaseFromDbMap.put(testCase.getTableName(), testCase));

        // ergodic every table and fetch test case row data as much as possible
        for (LineageTable tableScheme : tableschemeParsedFromstatementlist) {
            // table parsed from the latest statement
            String tableName = tableScheme.getName();
            TableTestCase mergedTableTestCase = TableTestCase
                    .builder()
                    .tableName(tableName)
                    .columns(tableScheme.getColumns().stream().map(LineageColumn::getName).collect(Collectors.toList()))
                    .rowData(new ArrayList<>())
                    .build();
            mergedTestCaseList.add(mergedTableTestCase);
            // try to reuse test cases
            if (testCaseFromDbMap.containsKey(tableName)) {
                Set<String> latestColumnSet = new HashSet<>(mergedTableTestCase.getColumns());
                List<Map<String, String>> rowDataListFromDb = testCaseFromDbMap.get(tableName).getRowData();
                // handle every row data
                mergedTableTestCase.getRowData().addAll(getRowDataCanBeUsedInNewScheme(latestColumnSet, rowDataListFromDb));
            }
        }

        return TaskTestCaseListDTO.builder().taskTableInputList(mergedTestCaseList).build();
    }

    @Override
    public void savOrUpdateTestCase(TaskTestCaseListDTO taskTestCaseListDTO) {
        List<TableTestCase> testCaseList = taskTestCaseListDTO.getTaskTableInputList();

        Map<String, TableTestCase> tableNameTestCaseMap = new HashMap<>();
        if (Asserts.isNotNull(testCaseList)) {
            // task id init
            testCaseList.forEach((tableTestCase -> {
                tableTestCase.setTaskId(taskTestCaseListDTO.getTaskId());
                tableNameTestCaseMap.put(tableTestCase.getTableName(), tableTestCase);
            }));
            // update id in order to update existed records
            List<TableTestCase> existedTestCaselist = baseMapper.selectList(new LambdaQueryWrapper<TableTestCase>().eq(TableTestCase::getTaskId, taskTestCaseListDTO.getTaskId()));
            // delete test cases that do not exist in latest input source tables
            List<Integer> deleteTestCaseIdlist = new ArrayList<>();

            for (TableTestCase existedTestCase : existedTestCaselist) {
                String existedTableName = existedTestCase.getTableName();
                if (tableNameTestCaseMap.containsKey(existedTableName)) {
                    // based on existed id, existed table test case will be updated
                    TableTestCase tableTestCase = tableNameTestCaseMap.get(existedTableName);
                    tableTestCase.setId(existedTestCase.getId());
                } else {
                    deleteTestCaseIdlist.add(existedTestCase.getId());
                }
            }

            if (Asserts.isNotNullCollection(testCaseList)) {
                baseMapper.insertOrUpdate(testCaseList);
            }
            if (Asserts.isNotNullCollection(deleteTestCaseIdlist)) {
                baseMapper.deleteByIds(deleteTestCaseIdlist);
            }

        }

    }

    @Override
    public Boolean checkTaskOperatePermission(Integer taskId) {
        return taskService.checkTaskOperatePermission(taskId);
    }

    @Override
    public TableTestCase getTestCaseByTaskIdAndTableName(Integer taskId, String tableName) {
        return baseMapper.selectOne(new LambdaQueryWrapper<TableTestCase>().eq(TableTestCase::getTaskId, taskId).eq(TableTestCase::getTableName, tableName));
    }

    private List<Map<String, String>> getRowDataCanBeUsedInNewScheme
            (Set<String> latestColumnSet, List<Map<String, String>> existingList) {
        List<Map<String, String>> mergedRowDataList = new ArrayList<>();
        // handle every row data
        for (Map<String, String> rowData : existingList) {
            Map<String, String> mergedRowData = new HashMap<>();
            for (Map.Entry<String, String> entry : rowData.entrySet()) {
                String existedColumnName = entry.getKey();
                String existedColumnData = entry.getValue();
                // keep the column data that still contains in latest scheme
                if (latestColumnSet.contains(existedColumnName)) {
                    mergedRowData.put(existedColumnName, existedColumnData);
                }
            }
            if (Asserts.isNotNullMap(mergedRowData)) {
                mergedRowDataList.add(mergedRowData);
            }
        }
        return mergedRowDataList;
    }
}
