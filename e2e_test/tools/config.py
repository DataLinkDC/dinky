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

podTemplate="""
apiVersion: v1
kind: Pod
metadata:
  name: jobmanager-pod-template
spec:
  initContainers:
    - name: artifacts-fetcher-dinky
      image: library/busybox:latest
      imagePullPolicy: Never
      # Use wget or other tools to get user jars from remote storage
      command: [ 'wget', 'http://172.28.0.1:9001/dinky-app.jar', '-O', '/flink-usrlib/dinky-app.jar' ]
      volumeMounts:
        - mountPath: /flink-usrlib
          name: flink-usrlib
    - name: artifacts-fetcher-mysql
      image: library/busybox:latest
      imagePullPolicy: Never
      # Use wget or other tools to get user jars from remote storage
      command: [ 'wget', 'http://172.28.0.1:9001/mysql-connector-java-8.0.30.jar', '-O', '/flink-usrlib/mysql-connector-java-8.0.30.jar' ]
      volumeMounts:
        - mountPath: /flink-usrlib
          name: flink-usrlib

  containers:
    # Do not change the main container name
    - name: flink-main-container
      resources:
        requests:
          ephemeral-storage: 2048Mi
        limits:
          ephemeral-storage: 2048Mi
      volumeMounts:
        - mountPath: /opt/flink/usrlib
          name: flink-usrlib
  volumes:
    - name: flink-usrlib
      emptyDir: { }

"""

log.info(f"""
====================================================    
        all config dinky address: {dinky_addr} 
        flink version: {flink_version}
====================================================
""")
