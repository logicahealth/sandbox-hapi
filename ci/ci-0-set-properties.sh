#!/usr/bin/env bash

CONFIG_FILE_DSTU2="../aws/task-config-dstu2.json"
CONFIG_FILE_STU3="../aws/task-config-stu3.json"

export PROJECT_FULL_NAME="${PROJECT_NAME}-${ACTIVE_ENV}"

export PROJECT_VERSION=$(mvn -f ../pom.xml -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)

export PROJECT_PORT_DSTU2=$(cat ${CONFIG_FILE_DSTU2} | jq --raw-output '.config.port')

export PROJECT_PORT_DSTU2=$(cat ${CONFIG_FILE_STU3} | jq --raw-output '.config.port')

export DOCKER_PROJECT_REPO="hspconsortium"

export DOCKER_IMAGE_COORDINATES=${DOCKER_PROJECT_REPO}/${PROJECT_NAME}:${PROJECT_VERSION}

export AWS_REGION="us-west-2"

export AWS_CONTAINER_MEMORY_RESERVE_DSTU2=$(cat ${CONFIG_FILE_DSTU2} | jq --raw-output '.config.memory')
echo "AWS_CONTAINER_MEMORY_RESERVE_DSTU2: $AWS_CONTAINER_MEMORY_RESERVE_DSTU2"

export AWS_CONTAINER_MEMORY_RESERVE_STU3=$(cat ${CONFIG_FILE_STU3} | jq --raw-output '.config.memory')
echo "AWS_CONTAINER_MEMORY_RESERVE_STU3: $AWS_CONTAINER_MEMORY_RESERVE_STU3"