#!/usr/bin/env bash

set -x

[[ -z "$1" ]] && { echo "usage: $0 {project} {test|prod}"; exit 1; }
[[ -z "$2" ]] && { echo "usage: $0 {project} {test|prod}"; exit 1; }

export AWS_REGION=us-east-1
export AWS_VPC_ID=vpc-858379fe
export SUBNET_1="subnet-5c84cb73"
export SUBNET_2="subnet-c9302782"
export SUBNETS="$SUBNET_1,$SUBNET_2"
export SECURITY_GROUPS="sg-46337531"
export AWS_TASK_EXECUTION_ROLE_ESCAPED="arn:aws:iam::657600230790:role\/ecsTaskExecutionRole"
export AWS_ECS_LAUNCH_TYPE=FARGATE
export AWS_CLUSTER_NAME="$1-$2"
export AWS_LOAD_BALANCER_NAME="$1-$2"
export AWS_TARGET_GROUP_NAME="$1-$2"
export AWS_TARGET_GROUP_PORT=80
export AWS_SERVICE_NAME="$1-$2"
export AWS_TASK_NAME="$1-$2"
export AWS_TASK_CPU_UNIT=512
export AWS_TASK_MEMORY_UNIT=1024
export AWS_CONTAINER_NAME="$1-$2"
export AWS_CONTAINER_PORT=8075
export AWS_CONTAINER_CPU_UNIT=512
export AWS_CONTAINER_MEMORY_UNIT=1024
export AWS_HEALTH_CHECK_PATH="/"
export AWS_HEALTH_CHECK_INTERVAL=30
export AWS_HEALTH_CHECK_HEALTHY_COUNT=5
export AWS_HEALTH_CHECK_UNHEALTHY_COUNT=5
export IMAGE_COORDINATES="hspconsortium/hspc-reference-api-webapp-multitenant:1.10.9-SNAPSHOT"
export IMAGE_COORDINATES_ESCAPED="hspconsortium\/hspc-reference-api-webapp-multitenant:1\.10\.9-SNAPSHOT"
