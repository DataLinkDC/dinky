import sys
from logger import log

dinky_addr = sys.argv[1]
flink_version = sys.argv[2]
dinky_app_jar = 'dinky-app.jar'

# standalone
standalone_address = "jobmanager:8282"

#  yarn
yarn_flink_lib = "/opt/flink/lib"
yarn_flink_conf = "/opt/flink/conf"
yarn_hadoop_conf = "/opt/flink/conf"
yarn_dinky_app_jar = "/dinky/jar"

log.info(f"""
====================================================    
        all config dinky address: {dinky_addr} 
        flink version: {flink_version}
====================================================
""")
