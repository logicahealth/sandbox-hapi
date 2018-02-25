#!/usr/bin/env bash

../aws/build-template.sh $PROJECT_FULL_NAME $DOCKER_IMAGE_COORDINATES $PROJECT_PORT $AWS_CONTAINER_MEMORY_RESERVE

echo $(aws ecs register-task-definition --region ${AWS_REGION} --family ${PROJECT_FULL_NAME} --cli-input-json file://../aws/task-definition.json)
