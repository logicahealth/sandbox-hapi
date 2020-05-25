#!/usr/bin/env bash
#
#  * #%L
#  *
#  * %%
#  * Copyright (C) 2014-2020 Healthcare Services Platform Consortium
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


profile="stu3"
sandbox_name="hspc8"

if [ $# -gt 0 ]; then
    case "$1" in
        dstu2)
            profile=$1
            sandbox_name="hspc8"
            ;;
        stu3)
            profile=$1
            sandbox_name="hspc9"
            ;;
        r4)
            profile=$1
            sandbox_name="hspc10"
            ;;
        *)
            echo "Usage: $0 {dstu2|stu3|r4} {default|multitenant}"
            exit 1
    esac
fi

echo "using $profile profile..."

if [ $# -gt 1 ]; then
    tenant=$2
    jar="reference-api-webapp/target/hspc-reference-api-webapp-*.jar"
else
    tenant="multitenant"
    jar="reference-api-webapp/target/hspc-reference-api-webapp-*.jar"
fi
jenv local 1.8
set -x

echo "running..."
java \
  -Dspring.profiles.active=${profile},${tenant},local \
  -Xms256M \
  -Xmx1024M \
  -Dhspc.platform.api.sandbox.name=${sandbox_name} \
  -jar ${jar}
