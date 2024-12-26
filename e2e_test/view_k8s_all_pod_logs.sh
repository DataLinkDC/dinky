# 检查是否提供了命名空间参数
if [ -z "$1" ]; then
    echo "Usage: $0 <namespace>"
    exit 1
fi

NAMESPACE=$1

# 获取指定命名空间下的所有Pod
PODS=$(kubectl get pods -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}')

for POD in $PODS; do
    echo "================= Pod: $POD 信息 ================="
    kubectl describe pod $POD -n $NAMESPACE
    echo "================= Init Container 日志 ================="
    INIT_CONTAINERS=$(kubectl get pod $POD -n $NAMESPACE -o jsonpath='{.spec.initContainers[*].name}')
    for INIT_CONTAINER in $INIT_CONTAINERS; do
        echo "Init Container: $INIT_CONTAINER 的日志"
        kubectl logs -n $NAMESPACE $POD -c $INIT_CONTAINER --previous 2>&1
        kubectl logs -n $NAMESPACE $POD -c $INIT_CONTAINER  2>&1
        echo "----------------------------------------------------"
    done
    echo "================= 普通 Container 日志 ================="
    CONTAINERS=$(kubectl get pod $POD -n $NAMESPACE -o jsonpath='{.spec.containers[*].name}')
    for CONTAINER in $CONTAINERS; do
        echo "Container: $CONTAINER 的日志"
        kubectl logs -n $NAMESPACE $POD -c $CONTAINER --previous 2>&1
        kubectl logs -n $NAMESPACE $POD -c $CONTAINER  2>&1
        echo "----------------------------------------------------"
    done
    echo "====================================================="
done
