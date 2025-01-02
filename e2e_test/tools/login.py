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
