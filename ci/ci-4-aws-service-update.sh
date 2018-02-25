#!/usr/bin/env bash

export AWS_TASK_DEFINITION=$(aws ecs describe-task-definition --region ${AWS_REGION} --task-definition ${PROJECT_FULL_NAME})

export AWS_TASK_REVISION=$(echo $AWS_TASK_DEFINITION | jq --raw-output '.taskDefinition.revision')

echo $(aws ecs update-service --region ${AWS_REGION} --cluster ${TARGET_AWS_CLUSTER} --service ${PROJECT_FULL_NAME} --task-definition ${PROJECT_FULL_NAME}:${AWS_TASK_REVISION})
