from time import sleep
from enum import Enum
import requests

import concurrent.futures

from requests import Response

from login import assertRespOk, url
from logger import log


class CatalogueTree:
    def __init__(self, id: int, name: str, task_id: int, children):
        self.id = id
        self.name = name
        self.task_id = task_id
        self.children: list[CatalogueTree] = children


class FlinkRunMode(Enum):
    LOCAL = "local"
    STANDALONE = "standalone"
    YARN_APPLICATION = "yarn-application"
    KUBERNETES_APPLICATION = "kubernetes-application"

    @staticmethod
    def getAllMode():
        # todo 这里暂时剔除 local，因为并发场景下，会出现接口卡住问题
        return [FlinkRunMode.STANDALONE, FlinkRunMode.YARN_APPLICATION, FlinkRunMode.KUBERNETES_APPLICATION]


class Task:
    def __init__(self, session: requests.Session, cluster_id: int, yarn_cluster_id: int, k8s_native_cluster_id: int,
                 parent_id: int):
        self.session = session
        self.cluster_id = cluster_id
        self.yarn_cluster_id = yarn_cluster_id
        self.k8s_native_cluster_id = k8s_native_cluster_id
        self.parent_id = parent_id
    def addBaseTask(self, params:dict):
        session = self.session
        add_parent_dir_resp = session.put(url("api/catalogue/saveOrUpdateCatalogueAndTask"), json=params)
        assertRespOk(add_parent_dir_resp, "Create a task")
        get_all_tasks_resp = session.post(url("api/catalogue/getCatalogueTreeData"), json={
            "sortValue": "",
            "sortType": ""
        })
        assertRespOk(get_all_tasks_resp, "Get job details")
        data_list: list[dict] = get_all_tasks_resp.json()['data']
        return getTask(data_list, params['name'])

    def addTask(self, name: str, parent_id: int = 0, dialect: str = "FlinkSql",
                statement: str = "", run_model: FlinkRunMode = FlinkRunMode.LOCAL) -> CatalogueTree:
        """
        en: Add a task
        zh: 添加一个任务
        :param name:  task name
        :param parent_id:  dir id
        :param statement:  statement
        :return CatalogueTree
        """
        model_str = run_model.value
        params = {
            "name": name,
            "type": dialect,
            "firstLevelOwner": 1,
            "task": {
                "savePointStrategy": 0,
                "parallelism": 1,
                "envId": -1,
                "step": 1,
                "alertGroupId": -1,
                "type": model_str,
                "dialect": dialect,
                "statement": statement,
                "firstLevelOwner": 1,
            },
            "isLeaf": False,
            "parentId": parent_id
        }
        if run_model == FlinkRunMode.STANDALONE:
            params["task"]["clusterId"] = self.cluster_id
        elif run_model == FlinkRunMode.YARN_APPLICATION:
            params["task"]["clusterConfigurationId"] = self.yarn_cluster_id
        elif run_model == FlinkRunMode.KUBERNETES_APPLICATION:
            params["task"]["clusterConfigurationId"] = self.k8s_native_cluster_id
        return self.addBaseTask(params)

    def getFlinkTaskStatus(self, jobInstanceId: int) -> str:
        """
        en:  Obtain the status of a Flink task
        zh:  获取Flink 任务状态
        :param jobInstanceId: job instance id
        :return: status
        """
        run_task_resp = self.session.get(url(f"api/jobInstance/refreshJobInfoDetail?id={jobInstanceId}&isForce=false"))
        assertRespOk(run_task_resp, "Get Task Status")
        return run_task_resp.json()['data']['instance']['status']

    def runTask(self, taskId: int) -> int:
        """
        en:Run a task
        zh:运行一个任务
        :param taskId: task id
        :return:
        """

        run_task_resp = self.session.get(url(f"api/task/submitTask?id={taskId}"))
        assertRespOk(run_task_resp, "Run Task")
        return run_task_resp.json()['data']['jobInstanceId']

    def runFlinkTask(self,statement: str, name: str,dialect:str="FlinkSql", modes: list[FlinkRunMode] = FlinkRunMode.getAllMode(), wait_time: int = 20,
                     is_async: bool = False ) -> CatalogueTree:
        parent_id = self.parent_id
        log.info(
            f"======================\nA Flink task is currently executed，name: {name}, statement: \n{statement}\n ======================")

        def taskFunc(mode: FlinkRunMode):
            flink_task_name = name + "-" + mode.value
            task = self.addTask(flink_task_name, parent_id, dialect, statement, mode)
            job_instance_id = self.runTask(task.task_id)
            sleep(wait_time)
            log.info(f"正在检查:{flink_task_name}任务状态")
            status = self.getFlinkTaskStatus(job_instance_id)
            assertFlinkTaskIsRunning(status, flink_task_name)
            self.stopTask(task.task_id)

        if is_async:
            with concurrent.futures.ThreadPoolExecutor() as executor:
                results = [executor.submit(taskFunc, model) for model in modes]
                for result in results:
                    result.result()
        else:
            for mode in modes:
                taskFunc(mode)

    def stopTask(self, taskId: int) -> None:
        resp: Response = self.session.get(url(f"api/task/cancel?id={taskId}&withSavePoint=false&forceCancel=true"))
        assertRespOk(resp, "StopTask")



def assertFlinkTaskIsRunning(status: str, name: str):
    # todo 这里应该判断flink是否有抛出异常，而不是只有状态
    if status != "RUNNING":
        error = f"Flink name:{name} is not RUNNING,current status:{status}"
        log.error(error)
        raise Exception(error)


def getTask(data_list: list[dict], name: str) -> CatalogueTree:
    for data in data_list:
        if data['name'] == name:
            return CatalogueTree(data['id'], data['name'], data['taskId'], data['children'])
        if len(data["children"]) > 0:
            result = getTask(data["children"], name)
            if result is not None:
                return result


def addCatalogue(session: requests.Session, name: str, isLeaf: bool = False, parentId: int = 0):
    add_parent_dir_resp = session.put(url("api/catalogue/saveOrUpdateCatalogue"), json={
        "name": name,
        "isLeaf": isLeaf,
        "parentId": parentId
    })
    assertRespOk(add_parent_dir_resp, "Create a dir")
    get_all_tasks_resp = session.post(url("api/catalogue/getCatalogueTreeData"), json={
        "sortValue": "",
        "sortType": ""
    })
    assertRespOk(get_all_tasks_resp, "Get job details")
    data_list: list[dict] = get_all_tasks_resp.json()['data']
    return getTask(data_list, name)
