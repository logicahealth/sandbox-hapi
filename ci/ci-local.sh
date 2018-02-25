#!/usr/bin/env bash

set -x

# set values provided at build time
export ACTIVE_ENV=test
export PROJECT_NAME=api
export TARGET_AWS_CLUSTER=hspc-test
export ENC_PW_TEST=changeme

# DSTU2
export PROJECT_NAME=api-v5-dstu2
export SPRING_PROFILES_ACTIVE="test,multitenant,dstu2"
. ci-0-set-properties.sh
. ci-1-prepare-sources.sh
export PROJECT_PORT=${PROJECT_PORT_DSTU2}
export AWS_CONTAINER_MEMORY_RESERVE=${AWS_CONTAINER_MEMORY_RESERVE_DSTU2}
. ci-3-aws-task-update.sh
. ci-4-aws-service-update.sh

# STU3
export PROJECT_NAME=api-v5-stu3
export SPRING_PROFILES_ACTIVE="test,multitenant,stu3"
. ci-0-set-properties.sh
. ci-1-prepare-sources.sh
export PROJECT_PORT=${PROJECT_PORT_STU3}
export AWS_CONTAINER_MEMORY_RESERVE=${AWS_CONTAINER_MEMORY_RESERVE_STU3}
. ci-3-aws-task-update.sh
. ci-4-aws-service-update.sh
