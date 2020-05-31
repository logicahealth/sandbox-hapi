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


tag="hspconsortium/api:latest"
if [ $# -gt 0 ]; then
  tag=$1
fi

target_env="local"
if [ $# -gt 1 ]; then
  target_env=$2
fi

# files must be in a folder or subfolder
rm -rf target
mkdir -p target
cp ../reference-api-webapp/target/*.jar target

docker \
  build -t $tag \
  --build-arg TARGET_ENV=$target_env \
  .
