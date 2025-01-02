from typing import Optional

from requests import Session
import urllib.parse as urlparse
from hdfs.client import Client
import os
from config import *
from httpUtil import assertRespOk, url
from logger import log


def addStandaloneCluster(session: Session) -> int:
    """
    en: Add a cluster instance
    zh: 添加一个集群实例
    :param session:  requests.Session
    :return:  clusterId
    """
    name = 'flink-standalone'
    add_cluster_resp = session.put(url("api/cluster"), json={
        "name": name,
        "type": "standalone",
        "hosts": standalone_address
    })
    assertRespOk(add_cluster_resp, "Add cluster")
    get_data_list = session.get(url(f"api/cluster/list?searchKeyWord={urlparse.quote(name)}&isAutoCreate=false"))
    assertRespOk(get_data_list, "Get cluster list")
    for data in get_data_list.json()['data']:
        if data['name'] == name:
            return data['id']
    raise Exception(f"Cluster {name} not found")


def addApplicationCluster(session: Session, params: dict) -> Optional[int]:
    name = params['name']
    test_connection_yarn_resp = session.post(url("api/clusterConfiguration/testConnect"), json=params)
    assertRespOk(test_connection_yarn_resp, "Test yarn connectivity")
    test_connection_yarn_resp = session.put(url("api/clusterConfiguration/saveOrUpdate"), json=params)
    assertRespOk(test_connection_yarn_resp, "Add Yarn Application Cluster")
    get_app_list = session.get(url(f"api/clusterConfiguration/list?keyword={name}"), json=params)
    assertRespOk(get_app_list, "Get Yarn Application Cluster")
    for data in get_app_list.json()["data"]:
        if data["name"] == name:
            return data['id']


def addYarnCluster(session: Session) -> Optional[int]:
    client = Client("http://namenode:9870")
    flink_lib_path = yarn_flink_lib
    client.makedirs(flink_lib_path)
    # Traverse the specified path and upload the file to HDFS
    for root, dirs, files in os.walk(flink_lib_path):
        for file in files:
            filepath = os.path.join(root, file)
            client.upload(flink_lib_path + "/" + file, filepath)
    client.makedirs(yarn_dinky_app_jar)
    dinky_app_hdfs_jar_path = yarn_dinky_app_jar + "/" + dinky_app_jar
    client.upload(dinky_app_hdfs_jar_path, dinky_app_hdfs_jar_path)
    name = "yarn-test"
    params = {
        "type": "yarn-application",
        "name": name,
        "enabled": True,
        "config": {
            "clusterConfig": {
                "hadoopConfigPath": yarn_hadoop_conf,
                "flinkLibPath": "hdfs://" + flink_lib_path,
                "flinkConfigPath": yarn_flink_conf
            },
            "flinkConfig": {
                "configuration": {
                    "jobmanager.memory.process.size": "1024m",
                    "taskmanager.memory.process.size": "1024m",
                    "taskmanager.numberOfTaskSlots": "1",
                    "state.savepoints.dir": "hdfs:///flink/savepoint",
                    "state.checkpoints.dir": "hdfs:///flink/ckp"
                }
            },
            "appConfig": {
                "userJarPath": "hdfs://" + dinky_app_hdfs_jar_path
            }
        }
    }
    log.info(f"Adding yarn application cluster, parameters:{params}")
    return addApplicationCluster(session, params)


def addK8sNativeCluster(session: Session) -> Optional[int]:
    with open('/kube/k3s.yaml', 'r') as f:
        kube_config_content = f.read()
    name = "k8s-native-test"
    params = {
        "type": "kubernetes-application",
        "name": name,
        "enabled": True,
        "config": {
            "kubernetesConfig": {
                "configuration": {
                    "kubernetes.rest-service.exposed.type": "NodePort",
                    "kubernetes.namespace": "dinky",
                    "kubernetes.service-account": "dinky",
                    "kubernetes.container.image": f"dinky/flink:flink"
                },
                "ingressConfig": {
                    "kubernetes.ingress.enabled": False
                },
                "kubeConfig": kube_config_content,
                "podTemplate": podTemplate,
            },
            "clusterConfig": {
                "flinkConfigPath": "/opt/flink/conf"
            },
            "flinkConfig": {
                "flinkConfigList": [
                    {
                        "name": "user.artifacts.raw-http-enabled",
                        "value": "true"
                    },
                    {
                        "name": "kubernetes.flink.conf.dir",
                        "value": "/opt/flink/conf"
                    },
                    {
                        "name": "kubernetes.container.image.pull-policy",
                        "value": "Never"
                    }
                ],
                "configuration": {
                    "jobmanager.memory.process.size": "1024mb",
                    "taskmanager.memory.process.size": "1024mb"
                }
            },
            "appConfig": {
                "userJarPath": "local:/opt/flink/usrlib/" + dinky_app_jar,
            }
        }
    }
    log.info(f"Adding k8s native application cluster, parameters:{params}")
    return addApplicationCluster(session, params)
