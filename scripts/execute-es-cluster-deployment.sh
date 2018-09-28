# Unable to add multiline sed replace with Jenkins pipeline file, hence created this script as a workaround,
# and moved all the commands to this file.

set -x
curl https://raw.githubusercontent.com/RHsyseng/docker-rhel-elasticsearch/5.x/es-cluster-deployment.yml --output es-cluster-deployment.yml
sed -i 's/512Mi/'${ES_MEMORY}'/g' es-cluster-deployment.yml
sed -i 's/registry.centos.org\/rhsyseng\/elasticsearch:5.6.10/'${ES_IMAGE//\//\\/}'  \
    importPolicy: \
      insecure: '${ES_IMAGE_INSECURE}'/g' es-cluster-deployment.yml
oc create -f es-cluster-deployment.yml -n ${OS_NAMESPACE}
while true; do
    replicas=$(oc get statefulset/elasticsearch -o=jsonpath='{.status.readyReplicas}' -n ${OS_NAMESPACE})
    ((replicas > 1)) && break
    sleep 1
 done
