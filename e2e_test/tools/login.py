import sys

import requests
from requests import Response

from httpUtil import assertRespOk, url
from logger import log


def login(session: requests.Session):
    log.info("Login to Dinky, Currently in use: admin")
    login_resp: Response = session.post(url("api/login"),
                                        json={"username": "admin", "password": "dinky123!@#", "ldapLogin": False,
                                              "autoLogin": True})
    assertRespOk(login_resp, "Login")

    log.info("Select the default tenant")
    choose_tenant_resp = session.post(url("api/chooseTenant?tenantId=1"))
    assertRespOk(choose_tenant_resp, "Choose Tenant")
    session.cookies.set("tenantId", '1')

def changeFlinkJobWaitTime(session: requests.Session):
    log.info("Change Flink Job Waiting Time: 120 s")
    resp: Response = session.post(url("api/sysConfig/modifyConfig"),
                                        json={
                                            "key": "sys.flink.settings.jobIdWait",
                                            "name": "Job 提交等待时间",
                                            "frontType": "number",
                                            "example": [],
                                            "note": "提交 Application 或 PerJob 任务时获取 Job ID 的最大等待时间（秒）",
                                            "defaultValue": 30,
                                            "value": "120",
                                            "index": 1
                                        })
    assertRespOk(resp, "ChangeFlinkJobWaitTime")
