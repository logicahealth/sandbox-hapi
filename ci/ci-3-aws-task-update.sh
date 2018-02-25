#!/usr/bin/env bash

echo "PROJECT_FULL_NAME: $PROJECT_FULL_NAME"
echo "DOCKER_IMAGE_COORDINATES: $DOCKER_IMAGE_COORDINATES"
echo "PROJECT_PORT: $PROJECT_PORT"
echo "AWS_CONTAINER_MEMORY_RESERVE: $AWS_CONTAINER_MEMORY_RESERVE"

../aws/build-template.sh $PROJECT_FULL_NAME $DOCKER_IMAGE_COORDINATES $PROJECT_PORT $AWS_CONTAINER_MEMORY_RESERVE

echo $(aws ecs register-task-definition --region ${AWS_REGION} --family ${PROJECT_FULL_NAME} --cli-input-json file://../aws/task-definition.json)
