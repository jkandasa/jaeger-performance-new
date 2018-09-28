#!/bin/bash

# checks mandatory fields and do login

if [ -z "${OS_URL}" ]; then
    echo "Field OS_URL is missing!"
    exit 1
fi

if [ -z "${OS_USERNAME}" ]; then
    echo "Field OS_USERNAME is missing!"
    exit 1
fi

if [ -z "${OS_PASSWORD}" ]; then
    echo "Field OS_PASSWORD is missing!"
    exit 1
fi

if [ -z "${OS_NAMESPACE}" ]; then
    echo "Field OS_NAMESPACE is missing!"
    exit 1
fi

# do oc login for further actions
oc login ${OS_URL} --username=${OS_USERNAME} --password=${OS_PASSWORD} -n ${OS_NAMESPACE} --insecure-skip-tls-verify=true
