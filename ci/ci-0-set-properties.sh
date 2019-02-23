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


export PROJECT_NAME="api"

export PROJECT_FULL_NAME="${PROJECT_NAME}-v5-${FHIR_VERSION}-${TARGET_ENV}-temp"

export PROJECT_VERSION=$(mvn -f ../pom.xml -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive exec:exec)

export DOCKER_IMAGE_COORDINATES="hspconsortium/${PROJECT_NAME}:${PROJECT_VERSION}"

export SPRING_PROFILES_ACTIVE="${TARGET_ENV},multitenant,${FHIR_VERSION}"

export TEMPLATE_FILE="../aws/task-definition-${FHIR_VERSION}.json"

export VERSION_SNAPSHOT_REGEX="^[0-9]+\.[0-9]+\.[0-9]+-SNAPSHOT$"

export VERSION_RELEASE_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
