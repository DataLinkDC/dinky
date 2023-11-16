---
position: 1
id: dinky_setting
title: Dinky 环境配置
---




**参数配置说明:**


- **Python 环境变量:** pyflink任务需要设置此参数。
- **Dinky 地址:** Dinky后端服务地址，用于application和prejob模式下任务状态回传，这样就不会出现0.7那样任务结束后dinky页面变为unknown或者还是running的情况，所以这个参数务必为容器内与dinky可以互通的地址。
:::warning 注意

  dinky有自动获取ip地址，但是大多数情况下服务器可能不止一个网卡，需要用户手动检查，并修改。
:::