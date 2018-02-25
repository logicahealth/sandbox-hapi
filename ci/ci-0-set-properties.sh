#!/usr/bin/env bash

CONFIG_FILE="../aws/task-config.json"

export PROJECT_FULL_NAME="${PROJECT_NAME}-${ACTIVE_ENV}"

export PROJECT_VERSION=$(mvn -f ../pom.xml -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)

export PROJECT_PORT=$(cat ${CONFIG_FILE} | jq --raw-output '.config.port')

export DOCKER_PROJECT_REPO="hspconsortium"

export DOCKER_IMAGE_COORDINATES=${DOCKER_PROJECT_REPO}/${PROJECT_NAME}:${PROJECT_VERSION}

export AWS_REGION="us-west-2"

export AWS_CONTAINER_MEMORY_RESERVE=$(cat ${CONFIG_FILE} | jq --raw-output '.config.memory')

export SPRING_PROFILES_ACTIVE="${ACTIVE_ENV}"