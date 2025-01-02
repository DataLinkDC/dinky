from json import JSONDecodeError

from requests import Response

from config import dinky_addr


def url(path: str):
    return rf"http://{dinky_addr}/{path}"


def assertRespOk(resp: Response, api_name: str):
    if resp.status_code != 200:
        raise AssertionError("api name:{api_name} request failed")
    else:
        try:
            resp_json = resp.json()
            if not resp_json["success"]:
                raise AssertionError(f"api name:{api_name} request failed.Error: {resp_json['msg']}")
        except JSONDecodeError:
            raise AssertionError(f"api name:{api_name} request failed.Error: {resp.content.decode()}")
