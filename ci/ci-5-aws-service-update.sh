#!/usr/bin/env bash
#
#  * #%L
#  *
#  * %%
#  * Copyright (C) 2014-2019 Healthcare Services Platform Consortium
#  * %%
#  * Licensed under the Apache License, Version 2.0 (the "License");
#  * you may not use this file except in compliance with the License.
#  * You may obtain a copy of the License at
#  *
#  *      http://www.apache.org/licenses/LICENSE-2.0
#  *
#  * Unless required by applicable law or agreed to in writing, software
#  * distributed under the License is distributed on an "AS IS" BASIS,
#  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  * See the License for the specific language governing permissions and
#  * limitations under the License.
#  * #L%
#


# get the most recent task definition (assumes the task definition was just added)
export AWS_TASK_DEFINITION=$(aws ecs describe-task-definition --region us-west-2 --task-definition ${PROJECT_FULL_NAME})

export AWS_TASK_REVISION=$(echo ${AWS_TASK_DEFINITION} | jq --raw-output '.taskDefinition.revision')

# update the service to this task definition
echo $(aws ecs update-service --region us-west-2 --cluster hspc-${TARGET_ENV} --service ${PROJECT_FULL_NAME} --task-definition ${PROJECT_FULL_NAME}:${AWS_TASK_REVISION})
