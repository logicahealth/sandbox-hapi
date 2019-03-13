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


set -x

# set values provided at build time
export TARGET_ENV=test

export FHIR_VERSION=dstu2
. ci-0-set-properties.sh
. ci-1-prepare-sources.sh

export FHIR_VERSION=stu3
. ci-0-set-properties.sh
. ci-1-prepare-sources.sh

. ci-2-build-project.sh

export FHIR_VERSION=dstu2
. ci-3-docker-image.sh
. ci-4-aws-task-update.sh
. ci-5-aws-service-update.sh

export FHIR_VERSION=stu3
. ci-3-docker-image.sh
. ci-4-aws-task-update.sh
. ci-5-aws-service-update.sh
