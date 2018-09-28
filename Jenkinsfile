pipeline {
    agent any
    tools {
        maven 'M3'
        jdk 'JDK8'
    }
    options {
        disableConcurrentBuilds()
        /*timeout(time: 8, unit: 'HOURS')*/
    }
    environment {
        DEPLOYMENT_PARAMETERS="-pIMAGE_VERSION=latest -pCOLLECTOR_PODS=${COLLECTOR_PODS}"
        LOGS_COLLECTED="false"
        RUNNING_ON_OPENSHIFT="true"
    }
    parameters {
        string(name: 'JOB_REFERENCE', defaultValue: '', description: 'Jenkins job reference')

        string(name: 'OS_URL', defaultValue: '', description: 'Openshift url')
        string(name: 'OS_USERNAME', defaultValue: '', description: 'Openshift login username')
        password(name: 'OS_PASSWORD', defaultValue: '', description: 'Openshift login password')
        string(name: 'OS_NAMESPACE', defaultValue: 'jaeger-test2', description: 'Openshift namespace')
        booleanParam(name: 'INSTALL_JAEGER_SERVICES', defaultValue: true, description: 'Install Jaeger services')

        string(name: 'TEST_DURATION', defaultValue: '300', description: 'Test duration in seconds')
        string(name: 'NUMBER_OF_TRACERS', defaultValue: '10', description: 'Number of tracers')
        string(name: 'NUMBER_OF_SPANS', defaultValue: '80', description: 'Number of spans per tracer')
        string(name: 'QUERY_LIMIT', defaultValue: '3000', description: 'Maximum items limit on query execution')
        string(name: 'QUERY_SAMPLES', defaultValue: '5', description: 'Number of times the same should be executed')
        string(name: 'QUERY_INTERVAL', defaultValue: '-1', description: 'Query sets will be executed in this interval. If you want to only once at the end, pass it as -1')

        choice(choices: 'http\nudp', name: 'SENDER', description: 'In which mode spans should be sent. http - collector, udp - agent')
        
        choice(choices: 'elasticsearch\ncassandra',  name: 'STORAGE_TYPE', description: 'Type of backend should be used to store spans')
        choice(choices: 'storage\njaeger-query',  name: 'SPANS_COUNT_FROM', description: 'how to check spans count from backend. direct storage access or via jaeger query service?')
        string(name: 'STORAGE_HOST', defaultValue: 'elasticsearch', description: 'Storage hostname, for cassandra: cassandra')
        string(name: 'STORAGE_PORT', defaultValue: '9200', description: 'Storage host port number')
        string(name: 'STORAGE_KEYSPACE', defaultValue: 'jaeger_v1_dc1', description: 'Applicable for cassandra')

        string(name: 'JAEGER_QUERY_HOST', defaultValue: 'jaeger-query', description: 'Jaeger query service hostname')
        string(name: 'JAEGER_QUERY_PORT', defaultValue: '80', description: 'Jaeger query service host port number')
        string(name: 'JAEGER_COLLECTOR_HOST', defaultValue: 'jaeger-collector', description: 'Jaeger collector service hostname')
        string(name: 'JAEGER_COLLECTOR_PORT', defaultValue: '14268', description: 'Jaeger collector service host port number')
        string(name: 'JAEGER_AGENT_HOST', defaultValue: 'localhost', description: 'Jaeger agent service hostname')
        string(name: 'JAEGER_AGENT_PORT', defaultValue: '6831', description: 'Jaeger agent service host port number')
        string(name: 'JAEGER_AGENT_QUEUE_SIZE', defaultValue: '1000', description: 'Jaeger agent, length of the queue for the UDP server')
        string(name: 'JAEGER_AGENT_WORKERS', defaultValue: '10', description: 'Jaeger agent, how many workers the processor should run')
        
        string(name: 'JAEGER_FLUSH_INTERVAL', defaultValue: '200', description: 'Jaeger java client library flush interval')
        string(name: 'JAEGER_MAX_POCKET_SIZE', defaultValue: '0', description: 'Jaeger java client library max pocket size')
        string(name: 'JAEGER_SAMPLING_RATE', defaultValue: '1.0', description: '0.0 to 1.0 percent of spans to record')
        string(name: 'JAEGER_MAX_QUEUE_SIZE', defaultValue: '10000', description: 'Jaeger java client library max queue size')


        string(name: 'COLLECTOR_PODS', defaultValue: '1', description: 'The number of collector pods')
        string(name: 'COLLECTOR_QUEUE_SIZE', defaultValue: '2000', description: '--collector.queue-size')
        string(name: 'COLLECTOR_NUM_WORKERS', defaultValue: '50', description: '--collector.num-workers')

        string(name: 'QUERY_STATIC_FILES', defaultValue: '', description: '--query.static-files')

        string(name: 'ES_MEMORY', defaultValue: '1Gi', description: 'Memory for each elasticsearch pod')
        string(name: 'ES_BULK_SIZE', defaultValue: '5000000', description: '--es.bulk.size')
        string(name: 'ES_BULK_WORKERS', defaultValue: '1', description: '--es.bulk.workers')
        string(name: 'ES_BULK_FLUSH_INTERVAL', defaultValue: '200ms', description: '--es.bulk.flush-interval')


        string(name: 'ES_IMAGE', defaultValue: 'registry.centos.org/rhsyseng/elasticsearch:5.6.10', description: 'ElasticSearch image.')
        booleanParam(name: 'ES_IMAGE_INSECURE', defaultValue: false, description: 'If image location not-secured(HTTP), check this box')
        string(name: 'JAEGER_AGENT_IMAGE', defaultValue: 'jaegertracing/jaeger-agent:latest', description: 'Jaeger agent Image')
        string(name: 'JAEGER_COLLECTOR_IMAGE', defaultValue: 'jaegertracing/jaeger-collector:latest', description: 'Jaeger collector image')
        string(name: 'JAEGER_QUERY_IMAGE', defaultValue: 'jaegertracing/jaeger-query:latest', description: 'Jaeger query image')
        string(name: 'PERFORMANCE_TEST_IMAGE', defaultValue: 'jkandasa/jaeger-performance-test:latest', description: 'Jaeger performance test docker image')

        booleanParam(name: 'RUN_SMOKE_TESTS', defaultValue: false, description: 'Run smoke tests in addition to the performance tests')
        booleanParam(name: 'DELETE_JAEGER_AT_END', defaultValue: true, description: 'Delete Jaeger instance at end of the test')
        booleanParam(name: 'DELETE_JOB_AT_END', defaultValue: true, description: 'Delete test pods at end of the test')      
    }
    stages {
        stage('Set name and description') {
            steps {
                script {
                    displayName=params.SENDER + " " + params.COLLECTOR_PODS + " collectors pods"
                }
            }
        }
        stage('Mandatory fields check and login') {
            steps {
                sh '''
                    ./scripts/oc_login.sh
                '''
            }
        }
        stage('Delete Old Job') {
            steps {
                sh 'oc delete job,template,pod -l group=jaeger-performance-test -n ${OS_NAMESPACE} || true'
                sh 'oc delete job jaeger-standalone-performance-tests -n ${OS_NAMESPACE} || true'
            }
        }
        stage('Delete Jaeger') {
            when {
                expression { params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                sh 'oc delete all,template,daemonset,configmap -l jaeger-infra -n ${OS_NAMESPACE} || true'
                sh 'oc delete -f https://raw.githubusercontent.com/RHsyseng/docker-rhel-elasticsearch/5.x/es-cluster-deployment.yml --grace-period=1 -n ${OS_NAMESPACE} || true'
            }
        }
        stage('Cleanup, checkout, build') {
            steps {
                deleteDir()
                checkout scm
                sh 'ls -alF'
            }
        }
        stage('deploy Cassandra') {
            when {
                expression { params.STORAGE_TYPE == 'cassandra' && params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                sh '''
                    curl https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/production/cassandra.yml --output cassandra.yml
                    oc create --filename cassandra.yml -n ${OS_NAMESPACE}
                '''
            }
        }
        stage('deploy ElasticSearch') {
            when {
                expression { params.STORAGE_TYPE == 'elasticsearch' && params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                sh ' ./scripts/execute-es-cluster-deployment.sh ${OS_NAMESPACE}'
            }
        }
        stage('deploy Jaeger with Cassandra') {
            when {
                expression { params.STORAGE_TYPE == 'cassandra' && params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                sh '''
                    curl https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/production/configmap-cassandra.yml --output configmap-cassandra.yml
                    oc create -f ./configmap-cassandra.yml
                    curl https://raw.githubusercontent.com/jaegertracing/jaeger-openshift/master/production/jaeger-production-template.yml --output jaeger-production-template.yml
                    sed -i 's/jaegertracing\\/jaeger-collector:${IMAGE_VERSION}/'${JAEGER_COLLECTOR_IMAGE}'/g' jaeger-production-template.yml
                    sed -i 's/jaegertracing\\/jaeger-query:${IMAGE_VERSION}/'${JAEGER_QUERY_IMAGE}'/g' jaeger-production-template.yml
                    grep "image:" jaeger-production-template.yml
                    ./scripts/updateTemplateForCassandra.sh ${OS_NAMESPACE}
                    oc process  ${DEPLOYMENT_PARAMETERS} -f jaeger-production-template.yml  | oc create -n ${PROJECT_NAME} -f -
                '''
            }
        }
        stage('deploy Jaeger with ElasticSearch') {
            when {
                expression { params.STORAGE_TYPE == 'elasticsearch' && params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                sh './scripts/deploy_jaeger_elasticsearch.sh ${OS_NAMESPACE}'
            }
        }
        stage('Wait for Jaeger Deployment') {
            when {
                expression { params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                sh  '''
                    # NAMESPACE SERVICE_NAME MAX_WAIT_TIME
                    ./scripts/wait_for_service.sh ${OS_NAMESPACE} jaeger-query 60
                    ./scripts/wait_for_service.sh ${OS_NAMESPACE} jaeger-collector 60
                '''
            }
        }
        stage('Run performance tests') {
            steps{
                sh '''
                    # logs will be stored in this location
                    export LOGS_DIRECTORY=$PWD/logs
                    ./scripts/run_performance_test.sh
                    sleep 30  # wait for 30 seconds to create container
                    # wait_for_pods_status.sh NAMESPACE POD_FILTER WAIT_FOR_STATUS MAX_WAIT_TIME
                    # add extra 5 minutes on maximum wait time
                    ./scripts/wait_for_pods_status.sh ${OS_NAMESPACE} "app=jaeger-performance-test-job" "Running" `expr ${TEST_DURATION} + 300`
                    # copy log files
                    ./scripts/copy-log-file.sh  ${OS_NAMESPACE} "app=jaeger-performance-test-job" "jaeger-performance-test"
                    ./scripts/copy-log-file.sh  ${OS_NAMESPACE} "app=jaeger-performance-test-job" "jaeger-agent"
                    ls -lh logs/
                    mvn clean test
                '''
            }
        }
        stage('Collect logs'){
            steps{
                sh '''
                  ./scripts/collect_logs.sh ${OS_NAMESPACE}
                  export LOGS_COLLECTED="true"
                  '''
            }
        }
        stage('Delete Jaeger at end') {
            when {
                expression { params.DELETE_JAEGER_AT_END && params.INSTALL_JAEGER_SERVICES }
            }
            steps {
                script {
                    sh 'oc delete -f https://raw.githubusercontent.com/RHsyseng/docker-rhel-elasticsearch/5.x/es-cluster-deployment.yml --grace-period=1 -n ${OS_NAMESPACE} || true'
                    sh 'oc delete all,template,daemonset,configmap -l jaeger-infra -n ${OS_NAMESPACE} || true'
                }
            }
        }
        stage('Delete Job at end') {
            when {
                expression { params.DELETE_JOB_AT_END  }
            }
            steps {
                sh 'oc delete job,template,pod -l group=jaeger-performance-test -n ${OS_NAMESPACE}'
                sh 'oc delete job jaeger-standalone-performance-tests -n ${OS_NAMESPACE} || true'
            }
        }
    }

    post {
        always {
            script {
                if (env.LOGS_COLLECTED == 'false') {
                  sh './scripts/collect_logs.sh ${OS_NAMESPACE}'
                }
            }
            archiveArtifacts artifacts: 'logs/*.*'
        }
    }
}