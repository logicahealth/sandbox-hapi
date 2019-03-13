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


profile="dstu2"
sandbox_name="hspc5"
profile=$1
environment=$2
sandbox_name=$3
jasypt_password=$4 || ""

echo "using $1 profile..."

jar="hspc-reference-api-webapp-*.jar"

set -x

if [[ "$environment" == "local" ]]; then
    java \
        -Dspring.profiles.active=${profile},multitenant \
        -Xms256M \
        -Xmx512M \
        -DnameForShutdown=1 \
        -Dhspc.platform.api.sandbox.name=${sandbox_name} \
        -jar ${jar} &> output.log &
else
    java \
      -Dspring.profiles.active=${profile},multitenant,${environment} \
      -Xms256M \
      -Xmx512M \
      -DnameForShutdown=1 \
      -Dhspc.platform.api.sandbox.name=${sandbox_name} \
      -Djasypt.encryptor.password=${jasypt_password} \
      -jar ${jar} &> output.log &
fi



