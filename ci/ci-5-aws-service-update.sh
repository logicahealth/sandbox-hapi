#!/usr/bin/env bash

# get the most recent task definition (assumes the task definition was just added)
export AWS_TASK_DEFINITION=$(aws ecs describe-task-definition --region us-west-2 --task-definition ${PROJECT_FULL_NAME})

export AWS_TASK_REVISION=$(echo ${AWS_TASK_DEFINITION} | jq --raw-output '.taskDefinition.revision')

# update the service to this task definition
echo $(aws ecs update-service --region us-west-2 --cluster hspc-${TARGET_ENV} --service ${PROJECT_FULL_NAME} --task-definition ${PROJECT_FULL_NAME}:${AWS_TASK_REVISION})
