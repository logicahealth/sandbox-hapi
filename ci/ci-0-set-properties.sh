#!/usr/bin/env bash

export PROJECT_NAME="api"

export PROJECT_FULL_NAME="${PROJECT_NAME}-v5-${FHIR_VERSION}-${TARGET_ENV}"

export PROJECT_VERSION=$(mvn -f ../pom.xml -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)

export DOCKER_IMAGE_COORDINATES="hspconsortium/${PROJECT_NAME}:${PROJECT_VERSION}"

export SPRING_PROFILES_ACTIVE="${TARGET_ENV},multitenant,${FHIR_VERSION}"

export TEMPLATE_FILE="../aws/task-definition-${FHIR_VERSION}.json"

export VERSION_SNAPSHOT_REGEX="^[0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT$"

export VERSION_RELEASE_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
