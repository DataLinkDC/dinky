import os

import requests

from env import addStandaloneCluster, addYarnCluster, addK8sNativeCluster
from login import login, changeFlinkJobWaitTime
from task import addCatalogue, Task


def traverse_files(directory) -> list[str]:
    for root, dirs, files in os.walk(directory):
        return files


if __name__ == '__main__':
    session = requests.session()
    login(session)
    changeFlinkJobWaitTime(session)
    clusterId = addStandaloneCluster(session)
    yarn_cluster_id = addYarnCluster(session)
    k8s_native_cluster_id = addK8sNativeCluster(session)
    catalogue = addCatalogue(session, "flink-sql-task")

    flink_sql_datagen_test = Task(session, clusterId, yarn_cluster_id, k8s_native_cluster_id, catalogue.id)
    flink_sql_task_path = "dinky_task/flink_sql"
    for file_name in traverse_files(flink_sql_task_path):
        sql = open(os.path.join(flink_sql_task_path, file_name)).read()
        task_name = file_name.split(".")[0]
        flink_sql_datagen_test.runFlinkTask(sql, task_name, is_async=True)

    # flink_jar_sql_task_path = "dinky_task/flink_jar_sql"
    # for file_name in traverse_files(flink_jar_sql_task_path):
    #     sql = open(os.path.join(flink_jar_sql_task_path, file_name)).read()
    #     task_name = file_name.split(".")[0]
    #     flink_sql_datagen_test.runFlinkTask(sql, task_name,"FlinkJar", is_async=True)
